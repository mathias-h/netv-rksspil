package game2017;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class Main extends Application {

    public static final String name = "Mathias";
    public static final int posX = 1;
    public static final int posY = 1;
    public static final Direction dir = Direction.UP;
    public static final int size = 20;
    public static final int scene_height = size * 20 + 100;
    public static final int scene_width = size * 20 + 200;

    public static Image image_floor;
    public static Image image_wall;
    public static Image hero_right, hero_left, hero_up, hero_down;

    public static Player me;
    public static List<Player> players = new ArrayList<Player>();

    private Label[][] fields;
    private TextArea scoreList;

    private String[] board = { // 20x20
        "wwwwwwwwwwwwwwwwwwww",
        "w        ww        w",
        "w w  w  www w  w  ww",
        "w w  w   ww w  w  ww",
        "w  w               w",
        "w w w w w w w  w  ww",
        "w w     www w  w  ww",
        "w w     w w w  w  ww",
        "w   w w  w  w  w   w",
        "w     w  w  w  w   w",
        "w ww ww        w  ww",
        "w  w w    w    w  ww",
        "w        ww w  w  ww",
        "w         w w  w  ww",
        "w        w     w  ww",
        "w  w              ww",
        "w  w www  w w  ww ww",
        "w w      ww w     ww",
        "w   w   ww  w      w",
        "wwwwwwwwwwwwwwwwwwww" };

    // -------------------------------------------
    // | Maze: (0,0) | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1) | scorelist |
    // | | (1,1) |
    // -------------------------------------------

    @Override
    public void start(Stage primaryStage) throws Exception {
        Server server = new Server(this);

        try {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 0, 10));

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            scoreList = new TextArea();

            GridPane boardGrid = new GridPane();

            image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size,
                false, false);
            image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size,
                false, false);

            hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size,
                size, false, false);
            hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size,
                false, false);
            hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size,
                false, false);
            hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size,
                false, false);

            fields = new Label[20][20];
            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (board[j].charAt(i)) {
                    case 'w':
                        fields[i][j] = new Label("", new ImageView(image_wall));
                        break;
                    case ' ':
                        fields[i][j] = new Label("", new ImageView(image_floor));
                        break;
                    default:
                        throw new Exception("Illegal field value: " + board[j].charAt(i));
                    }
                    boardGrid.add(fields[i][j], i, j);
                }
            }
            scoreList.setEditable(false);

            Button b = new Button("connect");
            b.setOnAction(e -> {
                try {
                    server.connectToClients();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
            grid.add(b, 0, 2);

            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(scoreList, 1, 1);

            Scene scene = new Scene(grid, scene_width, scene_height);
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                MoveCommand c = null;
                try {
                    switch (event.getCode()) {
                    case UP:
                        c = new MoveCommand(me.name, 0, -1, Direction.UP);
                        break;
                    case DOWN:
                        c = new MoveCommand(me.name, 0, +1, Direction.DOWN);
                        break;
                    case LEFT:
                        c = new MoveCommand(me.name, -1, 0, Direction.LEFT);
                        break;
                    case RIGHT:
                        c = new MoveCommand(me.name, +1, 0, Direction.RIGHT);
                        break;
                    default:
                        break;
                    }

                    if (c != null) {
                        server.sendCommand(c);
                        playerMoved(c);
                    }
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

            // Setting up standard players

            me = addPlayer(name, posX, posY, Direction.UP);

            scoreList.setText(getScoreList());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player addPlayer(String name, int posX, int podY, Direction dir) {
        Player p = new Player(name, posX, podY, dir);
        Image heroImage = null;
        players.add(p);

        switch (dir) {
        case UP:
            heroImage = hero_up;
            break;
        case DOWN:
            heroImage = hero_down;
            break;
        case LEFT:
            heroImage = hero_left;
            break;
        case RIGHT:
            heroImage = hero_right;
            break;
        default:
            break;
        }

        fields[posX][podY].setGraphic(new ImageView(heroImage));
        return p;
    }

    public void playerMoved(MoveCommand command) throws Exception {
        Player player = null;
        for (Player p : players) {
            if (p.name.equals(command.name)) {
                player = p;
            }
        }
        if (player == null) {
            throw new Exception("player not found: " + name);
        }

        player.direction = command.dir;
        int x = player.getXpos(), y = player.getYpos();

        if (board[y + command.deltaY].charAt(x + command.deltaX) == 'w') {
            player.addPoints(-1);
        }
        else {
            Player p = getPlayerAt(x + command.deltaX, y + command.deltaY);
            if (p != null) {
                player.addPoints(10);
                p.addPoints(-10);
            }
            else {
                player.addPoints(1);

                fields[x][y].setGraphic(new ImageView(image_floor));
                x += command.deltaX;
                y += command.deltaY;

                switch (command.dir) {
                case UP:
                    fields[x][y].setGraphic(new ImageView(hero_up));
                    break;
                case DOWN:
                    fields[x][y].setGraphic(new ImageView(hero_down));
                    break;
                case LEFT:
                    fields[x][y].setGraphic(new ImageView(hero_left));
                    break;
                case RIGHT:
                    fields[x][y].setGraphic(new ImageView(hero_right));
                    break;
                default:
                    break;
                }

                player.setXpos(x);
                player.setYpos(y);
            }
        }
        scoreList.setText(getScoreList());
    }

    public String getScoreList() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : players) {
            b.append(p + "\r\n");
        }
        return b.toString();
    }

    public Player getPlayerAt(int x, int y) {
        for (Player p : players) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
