package model;

public class PointCommand extends Command {
	String name;
	int points;
	@Override
	public String toString() {
		return "PointCommand(" + name + ", " + points + ")";
	}
}
