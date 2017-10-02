package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gui.BoardGui;

public class Game {
	private final Tile[][] board;
	private final Server server;
	private final BoardGui boardGui;
	private Sync sync;
	private final Player me;
	private final int processId = 2;
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

		sync = new Sync(server, processId);
		
		server.sendCommand(new NameCommand(me));
	}
	
	public void handleMove(MoveCommand command) {
		try {
			sync.request(() -> {
				movePlayer(command);
				server.sendCommand(command);
			});
		} catch (Exception e) {}
	}

	private void handleCommand(Command command) throws Exception {
		if (command instanceof SyncCommand) {
			sync.handleSyncCommand((SyncCommand)command);
		}
		else if (command instanceof NameCommand) {
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
		boardGui.setScores(players);
	}

	public void movePlayer(MoveCommand command) throws Exception {
		Player player = null;
		for (Player p : players) {
			if (p.name.equals(command.name)) {
				player = p;
			}
		}
		if (player == null) {
			throw new Exception("player not found: " + command.name);
		}

		player.direction = command.dir;
		int x = player.posX, y = player.posY;

		int deltaX = 0;
		int deltaY = 0;

		switch (command.dir) {
		case UP:
			deltaY -= 1;
			break;
		case DOWN:
			deltaY += 1;
			break;
		case LEFT:
			deltaX -= 1;
			break;
		case RIGHT:
			deltaX += 1;
			break;
		default: break;
		}

		if (board[x + deltaX][y + deltaY] == Tile.WALL) {
			player.point -= 1;
		} else {
			Player p = getPlayerAt(x + deltaX, y + deltaY);
			if (p != null) {
				player.point += 10;
				p.point -= 10;
			} else {
				Tile hero = getHeroTileFromDirection(player.direction);
				player.point += 1;

				board[x][y] = Tile.FLOOR;
				boardGui.setTile(x, y, Tile.FLOOR);

				board[x + deltaX][y + deltaY] = hero;
				boardGui.setTile(x + deltaX, y + deltaY, hero);

				player.posX += deltaX;
				player.posY += deltaY;
			}
		}

		boardGui.setScores(players);
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

		for (int y = 0; y < stringBoard.length; y++) {
			String tiles = stringBoard[y];
			for (int x = 0; x < tiles.length(); x++) {
				if (tiles.charAt(x) == 'w') {
					board[x][y] = Tile.WALL;
				}
					
				else
					board[x][y] = Tile.FLOOR;
			}
		}
		
		int i = 0;
	}

	public Tile[][] getBoard() {
		return board;
	}
}
