package tankGame;


public class Bullet {
	private double x,y,theta;
	final int speed = 2;
	final static int r = 8;
	long spawnT;
	int life = 10000;
	private boolean isLive = true;
	
	public Bullet(double x, double y, long spawnT, double theta){
		this.setX(x);
		this.setY(y);
		this.spawnT = spawnT;
		this.setTheta(theta);
	}
	
	public Bullet(double x, double y, double theta){
		this.setX(x);
		this.setY(y);
		this.setTheta(theta);
	}
	public Bullet(boolean b){
		
	}
	
	public boolean checkLife(long l){
		if(isLive() == false){
			return false;
		}
		return !(l - spawnT > life);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}
}
