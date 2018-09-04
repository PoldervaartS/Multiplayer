package tankGame;

import java.awt.Color;
import java.util.ArrayList;

public class Tank {
	private ArrayList<Bullet> bullets;
	final int speed = 3; static final int width = 40; static final int height = 15;
	private double x,y,theta;
	private boolean isLive = true;
	private Color c;
	static final int maxBullets = 10;
	private int score = 0;
	
	public Tank(double x, double y){
		this.x = x;
		this.y = y;
		theta = 0;
		setLive(true);
		setColor(randColor());
		setBullets(new ArrayList<Bullet>());
	}
	
	public Tank(double x, double y, double theta){
		setX(x);
		setY(y);
		setTheta(theta);
		setLive(true);
		setColor(randColor());
		setBullets(new ArrayList<Bullet>());
	}
	
	public Tank(double x, double y, Color c){
		this.x = x;
		this.y = y;
		setLive(true);
		setColor(c);
		setBullets(new ArrayList<Bullet>());
	}
	
	private Color randColor(){
		return new Color((int)Math.random()*256,(int)Math.random()*256,(int)Math.random()*256);
	}
	
	public void addBullet(double x, double y, long spawnT, double theta){
		bullets.add(new Bullet(x,y,spawnT,theta));
	}

	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}

	public double getTheta() {
		return theta;
	}

	public Color getColor() {
		return c;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public boolean isLive() {
		return isLive;
	}

	public int getScore() {
		return score;
	}

	public void setPos(double x, double y,double theta){
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
	
	public void setX(double x){
		this.x = x;
	}

	public void setY(double y){
		this.y = y;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public void setColor(Color c) {
		this.c = c;
	}

	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}
	
	//Unused??
	public void setBullet(int bulletNum, int x, int y, int theta){
		
	}
	
	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
