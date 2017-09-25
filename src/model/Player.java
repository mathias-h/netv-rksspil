package model;

import java.io.Serializable;

public class Player implements Serializable {
	public String name;
	public int posX;
	public int posY;
	public int point;
	public Direction direction;

	public Player(String name, int posX, int posY, Direction direction) {
		this.name = name;
		this.posX = posX;
		this.posY = posY;
		this.direction = direction;
		this.point = 0;
	}
	public String toString() {
		return name+":   "+point;
	}
}
