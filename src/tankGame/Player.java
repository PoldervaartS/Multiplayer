package tankGame;

import java.util.TreeMap;

public class Player {
	private Tank tank;
	private TreeMap<String,Boolean> keys;
	private long lastSpawn;
	private boolean isNew;
	private int score;
	
	public Player(Tank t){
		keys = new TreeMap<String,Boolean>();
		keys.put("W", false);
		keys.put("A", false);
		keys.put("S", false);
		keys.put("D", false);
		keys.put("SPACE", false);
		setLastSpawn(0);
		setScore(0);
		this.tank = t;
	}
	
	public TreeMap<String,Boolean> getKeys(){
		return keys;
	}
	
	public Tank getTank() {
		return tank;
	}

	public long getLastSpawn() {
		return lastSpawn;
	}

	public boolean isNew() {
		return isNew;
	}

	public int getScore() {
		return score;
	}

	public void setKey(String s, Boolean b){
		keys.put(s, b);
	}

	public void setTank(Tank tank) {
		this.tank = tank;
	}

	public void setLastSpawn(long lastSpawn) {
		this.lastSpawn = lastSpawn;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	
	public void setScore(int score) {
		this.score = score;
	}

}
