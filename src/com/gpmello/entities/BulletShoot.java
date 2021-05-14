package com.gpmello.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gpmello.main.Game;
import com.gpmello.world.Camera;

public class BulletShoot extends Entity {
	
	private double dx;
	private double dy;
	private double spd = 4;
	
	private int life = 30, currentLife = 0; //bullet life time => how many ticks to disappear

	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}
	
	public void tick() {
		x += dx * spd;
		y += dy * spd;
		currentLife++;
		if(currentLife == life) {
			Game.bullets.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
	
}