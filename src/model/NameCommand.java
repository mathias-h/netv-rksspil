package model;

public class NameCommand extends Command {
	Player player;
	
	public NameCommand(Player player) {
		this.player = player;
	}
}