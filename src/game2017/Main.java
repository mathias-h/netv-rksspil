package game2017;

import gui.BoardGui;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import model.Direction;
import model.MoveCommand;
import model.Player;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class Main extends Application {
	public static Player me = new Player("Mathias", 1, 1, Direction.LEFT);
	public static final int port = 1024;
	public static BoardGui boardGui;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));
			
			Scene scene = new Scene(boardGui);
			
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				MoveCommand c = null;
				try {
					switch (event.getCode()) {
					case UP:
						c = new MoveCommand(me.name, Direction.UP);
						break;
					case DOWN:
						c = new MoveCommand(me.name, Direction.DOWN);
						break;
					case LEFT:
						c = new MoveCommand(me.name, Direction.LEFT);
						break;
					case RIGHT:
						c = new MoveCommand(me.name, Direction.RIGHT);
						break;
					default:
						break;
					}

					if (c != null) {
						boardGui.game.handleMove(c);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		boardGui = new BoardGui(me, port);
		launch(args);
	}
}
