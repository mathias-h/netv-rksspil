package gui;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.Game;
import model.Player;
import model.Tile;

public class BoardGui extends GridPane {
	public final Game game;
	private final int size = 20;
	private final int width = 20;
	private final int height = 20;

	private final Label[][] fields;
	private final TextArea scoreList = new TextArea();
	private final Image imageFloor, imageWall, heroRight, heroLeft, heroUp, heroDown;

	public BoardGui(Player me, int port) throws Exception {

		game = new Game(width, height, port, me, this);
		fields = new Label[width][height];

		imageFloor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);
		imageWall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
		heroRight = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
		heroLeft = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
		heroUp = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
		heroDown = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

		initContent();
		game.init();
	}

	private void initContent() {
		GridPane boardGrid = new GridPane();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				switch (game.getBoard()[x][y]) {
				case WALL:
					fields[x][y] = new Label("", new ImageView(imageWall));
					break;
				case FLOOR:
					fields[x][y] = new Label("", new ImageView(imageFloor));
					break;
				default:
					continue;
				}
				boardGrid.add(fields[x][y], x, y);
			}
		}
		this.add(boardGrid, 0, 0);

		scoreList.setEditable(false);
		scoreList.setMaxWidth(200);
		this.add(scoreList, 1, 0);

		Button b = new Button("connect");
		b.setOnAction(e -> {
			try {
				game.connectToClients();
				b.setDisable(true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		this.add(b, 0, 1);
	}

	public void setTile(int x, int y, Tile tile) throws Exception {
		Platform.runLater(() -> {
			ImageView graphic = null;

			switch (tile) {
			case WALL:
				graphic = new ImageView(imageWall);
				break;
			case FLOOR:
				graphic = new ImageView(imageFloor);
				break;
			case HERO_UP:
				graphic = new ImageView(heroUp);
				break;
			case HERO_DOWN:
				graphic = new ImageView(heroDown);
				break;
			case HERO_LEFT:
				graphic = new ImageView(heroLeft);
				break;
			case HERO_RIGHT:
				graphic = new ImageView(heroRight);
				break;
			}

			if (graphic == null) return;

			fields[x][y].setGraphic(graphic);
		});
	}

	public void setScores(List<Player> players) {
		StringBuilder b = new StringBuilder(1000);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		scoreList.setText(b.toString());
	}
}
