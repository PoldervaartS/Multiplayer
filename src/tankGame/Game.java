package tankGame;

import java.awt.*;
import java.util.*;
import javax.swing.Timer;
import java.awt.image.BufferStrategy;

@SuppressWarnings("serial")
public class Game extends Canvas {
	
	volatile ArrayList<Tank> players = new ArrayList<>();
	boolean repaintInProgress = false;
	int numPlayers,width,height;
	
	public Game(int numPlayers, int width, int height) {
		this.width = width;
		this.height = height;
		this.numPlayers = numPlayers;
		setIgnoreRepaint(true);
		tankGame.Chrono chrono = new tankGame.Chrono(this);
		new Timer(10, chrono).start();
	}

	public void myRepaint() {
		
		if(getBufferStrategy() == null){
			createBufferStrategy(2);
			return;
		}
		
		if (repaintInProgress)
			return;
		
		repaintInProgress = true;
		
		BufferStrategy strategy = getBufferStrategy();
		Graphics g = strategy.getDrawGraphics();
		Graphics2D g2 = (Graphics2D)g;

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);
		
		//for displaying scores
		int startY = 30;
		int startX = 30;
		for(int i = 0; i < players.size(); i++){
			Tank t = players.get(i);
			g2.setColor(t.getColor());
			//actually displaying the scores
			g2.drawString("Player" + i + " score: " + t.getScore(), startX, startY);
			startX += 100;
			if(t.isLive()){
				//Rotate around center of the tank
				g2.rotate(Math.toRadians(t.getTheta()), t.getX() + Tank.width/2, t.getY() + Tank.height/2);
				g2.fillRect((int)t.getX(), (int)t.getY(), Tank.width, Tank.height);
				g2.rotate(-Math.toRadians(t.getTheta()), t.getX() +  Tank.width/2, t.getY() + Tank.height/2);
			}
			
			ArrayList<Bullet> bullets = t.getBullets();
			//TODO fix bullet spawning
			for(int j = 0; j < bullets.size(); j++){
				Bullet b = bullets.get(j);
				g2.setColor(Color.BLACK);
				g2.rotate(Math.toRadians(b.getTheta()), b.getX()+ Bullet.r/2, b.getY() + Bullet.r/2);
				g2.fillOval((int)b.getX(), (int)b.getY(), Bullet.r, Bullet.r);
				g2.rotate(-Math.toRadians(b.getTheta()), b.getX()+ Bullet.r/2, b.getY() + Bullet.r/2);
			}
		}

		if (g2 != null) {
			g2.dispose();
			g.dispose();
		}
		strategy.show();
		Toolkit.getDefaultToolkit().sync();
		repaintInProgress = false;
	}
	
	public void killPlayer(int playerNum){
		players.get(playerNum).setLive(false);
	}
	
	public Color randColor(){
		return new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
	}

	public void setTank(int playerNum, int x, int y, int theta){
		if(playerNum >= players.size()){
			players.add(new Tank(x,y,theta));
			players.get(playerNum).setColor(randColor());
		}
		players.get(playerNum).setPos(x, y, theta);
	}
	
	public void setBullet(int playerNum,int bulletNum,int x,int y,int theta){
		ArrayList<Bullet> bullets = players.get(playerNum).getBullets();
		if(bulletNum >= players.get(playerNum).getBullets().size()){
			players.get(playerNum).getBullets().add(new Bullet(x,y,theta));
		}else{
			bullets.get(bulletNum).setX(x);
			bullets.get(bulletNum).setY(y);
			bullets.get(bulletNum).setTheta(theta);
		}
		
	}
	
	//returns the number of bullets a player has
	public int getNumBullets(int playerNum){
		return players.get(playerNum).getBullets().size();
	}
	
	//removes bullets from start to the end of the bullets arraylist for specified player
	public void removeBulletsPast(int playerNum, int start){
		ArrayList<Bullet> bullets = players.get(playerNum).getBullets();
		int bulletSize = bullets.size();
		for(int i = start; i < bulletSize; i++){
			bullets.remove(start);
		}
	}
	
	//reset the game on client side
	public void reset(){
		for(int i = 0; i < players.size(); i++){
			//clear all bullets
			removeBulletsPast(i,0);
			players.get(i).setLive(true);
		}
	}
	
	public void setPlayerScore(int playerNum, int score){
		players.get(playerNum).setScore(score);
	}
}
