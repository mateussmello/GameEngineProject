package com.gpmello.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
//import java.util.ArrayList;

//import com.gpmello.graficos.Spritesheet;
import com.gpmello.main.Game;
import com.gpmello.world.Camera;
import com.gpmello.world.World;

public class Player extends Entity{
	
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 0.8; //Parâmetro
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	
	private boolean hasGun = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	
	public double maxLife = 100, life = 100;
	
	public int mouseX, mouseY;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16); //Parâmetro animação
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16); //Parâmetro animação
		}
	}
	
	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x + speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x+= speed;
		}else if(left && World.isFree((int)(x - speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x-= speed;
		}
		if(up && World.isFree(this.getX(), (int)(y - speed))) {
			moved = true;
			y-= speed;
		}else if(down && World.isFree(this.getX(), (int)(y + speed))) {
			moved = true;
			y+= speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionGun();
		updateCamera();
		
		if(isDamaged){
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shoot) {
			shoot = false;
			//create projectile and shoot
			if(hasGun && ammo > 0) {
				ammo--;
				
				//offset variables => position with player and gun
				int dx = 0;
				int px = 0;
				int py = 6;
				if(dir == right_dir) {
					dx = 1;
					px = 18;
				} else {
					dx = -1;
					px = -8;
				}
				
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
				Game.bullets.add(bullet);
			}
			
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			//create projectile and shoot
			if(hasGun && ammo > 0) {
				ammo--;
				
				//offset variables => position with player and gun
				int px = 0;
				int py = 6;
				double angle = 0;
				
				if(dir == right_dir) {
					px = 18;
					angle = Math.atan2(
							mouseY - (this.getY() + py - Camera.y), 
							mouseX - (this.getX() + px - Camera.x)
							);
				} else {
					px = -8;
					angle = Math.atan2(
							mouseY - (this.getY() + py - Camera.y), 
							mouseX - (this.getX() + px - Camera.x)
							);
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				BulletShoot bullet = new BulletShoot(
						this.getX() + px, 
						this.getY() + py, 
						3, 3, null, dx, dy
						);
				Game.bullets.add(bullet);
			}
		}
		
		if(life <= 0) {
			//Game Over
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDHT * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	public void checkCollisionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Weapon) {
				if(Entity.isColliding(this, e)) {
					hasGun = true;
					Game.entities.remove(i);
				}
			}
		}
	}
	
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Bullet) {
				if(Entity.isColliding(this, e)) {
					ammo += 10;
					Game.entities.remove(i);
				}
			}
		}
	}
	
	public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof LifePack) {
				if(Entity.isColliding(this, e)) {
					life += 10; //Parâmetro
					if(life >= 100)
						life = 100;
					Game.entities.remove(i);
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) {
					//gun to right
					g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 8, this.getY() - Camera.y, null);
				}
			}else if(dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) {
					//gun to left
					g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 8, this.getY() - Camera.y, null);
				}
			}
		}
		else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				if(dir == left_dir) {
					g.drawImage
					(Entity.GUN_DAMAGE_LEFT, this.getX() - 8 - Camera.x, this.getY() - Camera.y, null);
				} else {
					g.drawImage
					(Entity.GUN_DAMAGE_RIGHT, this.getX() + 8 - Camera.x, this.getY() - Camera.y, null);
				}
			}
		}
		
	}

}
