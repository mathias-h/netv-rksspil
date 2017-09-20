package game2017;

class MoveCommand extends Command {
	String name;
	int deltaX;
	int deltaY;
	Direction dir;
	public MoveCommand(String name, int deltaX, int deltaY, Direction dir) {
		this.name = name;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.dir = dir;
	}
	
	
}