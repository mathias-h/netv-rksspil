package model;

import java.util.ArrayList;
import java.util.List;

import gui.BoardGui;

public class Game {
	private final Tile[][] board;
	private final Server server;
	private final BoardGui boardGui;
	private final Player me;
	private final List<Player> players = new ArrayList<>();
	public final int width;
	public final int height;

	public Game(int width, int height, int port, Player me, BoardGui boardGui) throws Exception {
		this.width = width;
		this.height = height;
		this.boardGui = boardGui;
		this.me = me;
		this.server = new Server(port, this::handleCommand);
		board = new Tile[width][height];

		setBoard();
	}

	public void init() throws Exception {
		addPlayer(new NameCommand(me));
		setScores();
	}

	public void connectToClients() throws Exception {
		server.connectToClients();

		server.sendCommand(new NameCommand(me));
	}

	public void handleCommand(Command command) throws Exception {
		if (command instanceof NameCommand) {
			addPlayer((NameCommand) command);
		} else if (command instanceof MoveCommand) {
			movePlayer((MoveCommand) command);
		} else {
			throw new Exception("invalid command type");
		}
	}

	private void setScores() {
		boardGui.setScores(players);
	}

	private void addPlayer(NameCommand command) throws Exception {
		Player player = command.player;

		players.add(player);

		Tile hero = getHeroTileFromDirection(player.direction);
		
		board[player.posX][player.posY] = hero;
		boardGui.setTile(player.posX, player.posY, hero);
	}

	public void movePlayer(MoveCommand command) throws Exception {
		// Player player = null;
		// for (Player p : players) {
		// if (p.name.equals(command.name)) {
		// player = p;
		// }
		// }
		// if (player == null) {
		// throw new Exception("player not found: " + name);
		// }
		//
		// player.direction = command.dir;
		// int x = player.getXpos(), y = player.getYpos();
		//
		// if (board[y + command.deltaY].charAt(x + command.deltaX) == 'w') {
		// player.addPoints(-1);
		// } else {
		// Player p = getPlayerAt(x + command.deltaX, y + command.deltaY);
		// if (p != null) {
		// player.addPoints(10);
		// p.addPoints(-10);
		// } else {
		// player.addPoints(1);
		//
		// fields[x][y].setGraphic(new ImageView(image_floor));
		// x += command.deltaX;
		// y += command.deltaY;
		//
		// switch (command.dir) {
		// case UP:
		// fields[x][y].setGraphic(new ImageView(hero_up));
		// break;
		// case DOWN:
		// fields[x][y].setGraphic(new ImageView(hero_down));
		// break;
		// case LEFT:
		// fields[x][y].setGraphic(new ImageView(hero_left));
		// break;
		// case RIGHT:
		// fields[x][y].setGraphic(new ImageView(hero_right));
		// break;
		// default:
		// break;
		// }
		//
		// player.setXpos(x);
		// player.setYpos(y);
		// }
		// }
		// scoreList.setText(getScoreList());
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.posX == x && p.posY == y) {
				return p;
			}
		}
		return null;
	}

	private Tile getHeroTileFromDirection(Direction direction) {
		switch (direction) {
		case UP:
			return Tile.HERO_UP;
		case DOWN:
			return Tile.HERO_DOWN;
		case LEFT:
			return Tile.HERO_LEFT;
		case RIGHT:
			return Tile.HERO_RIGHT;
		default:
			return null;
		}
	}

	private void setBoard() {
		String[] stringBoard = { // 20x20
				"wwwwwwwwwwwwwwwwwwww", "w        ww        w", "w w  w  www w  w  ww", "w w  w   ww w  w  ww",
				"w  w               w", "w w w w w w w  w  ww", "w w     www w  w  ww", "w w     w w w  w  ww",
				"w   w w  w  w  w   w", "w     w  w  w  w   w", "w ww ww        w  ww", "w  w w    w    w  ww",
				"w        ww w  w  ww", "w         w w  w  ww", "w        w     w  ww", "w  w              ww",
				"w  w www  w w  ww ww", "w w      ww w     ww", "w   w   ww  w      w", "wwwwwwwwwwwwwwwwwwww" };

		for (int y = 0; y < stringBoard.length; y++) {
			String[] tiles = stringBoard[y].split("");
			for (int x = 0; x < tiles.length; x++) {
				if (tiles[x].equals("w"))
					board[x][y] = Tile.WALL;
				else
					board[x][y] = Tile.FLOOR;
			}
		}
	}

	public Tile[][] getBoard() {
		return board;
	}
}
