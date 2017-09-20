package game2017;

class NameCommand extends Command {
	String name;
	int posX;
	int posY;
	Direction dir;
	
	public NameCommand(String name, int posX, int podY, Direction dir) {
		this.name = name;
		this.posX = posX;
		this.posY = podY;
		this.dir = dir;
	}
}