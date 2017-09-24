package model;

public class MoveCommand extends Command {
	String name;
	Direction dir;
	public MoveCommand(String name, Direction dir) {
		this.name = name;
		this.dir = dir;
	}
	
	@Override
	public String toString() {
		return "MoveCommand(" + name + ", " + dir + ")";
	}
}