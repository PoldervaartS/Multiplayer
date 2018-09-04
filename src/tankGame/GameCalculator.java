package tankGame;

import java.awt.Color;
import java.util.*;
import javax.swing.Timer;

public class GameCalculator {

	// width of the canvas, height of the canvas
	int cWidth, cHeight;
	Cell[][] cells;
	// ArrayList containing all current players
	volatile ArrayList<Player> players;
	private boolean resetting = false, newScores = true, roundOver = false;
	final long tAfterRound = 3000;
	long tRoundEnds;

	public GameCalculator(int numPlayers, int numAI, int difficulty, int w, int h) {
		players = new ArrayList<>();
		//add the number of ai first
		for(int i = 0; i < numAI; i++) {
			addAI(difficulty);
		}
		cWidth = w;
		cHeight = h;
		cells = new Cell[h / 100][w / 100];
		// generateWalls(); currently broken
		Chrono chrono = new Chrono(this);
		new Timer(10, chrono).start();
	}

	public void calculate() {
		collision();
		updateAI(players);
		moveTanks();
		moveBullets();
		//check surviving players, if 1 or less left round is over
		if (players.size() > 0 && !roundOver) {
			int numAlive = 0;
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getTank().isLive())
					numAlive++;
			}
			//give time after round ends for players to still die
			if ((numAlive <= 1 && players.size() > 1) || (numAlive == 0 && players.size() > 0)) {
				roundOver = true;
				tRoundEnds = System.currentTimeMillis();
			}
		}
		if (roundOver && (System.currentTimeMillis() - tRoundEnds) > tAfterRound) {
			roundOver = false;
			tRoundEnds = 0;
			newRound();
		}
	}

	public void addPlayer() {
		// new player with random tank
		//somehow the players are added before AI on the list???
		players.add(0,new Player(randTank()));

	}

	public void addAI(int difficulty) {
		
		players.add(new ArtificialPlayer(randTank(),difficulty));
	}
	
	public Tank randTank() {
		return new Tank(Math.random() * (cWidth - Tank.width), Math.random() * (cHeight - Tank.height), randColor());
	}
	public Color randColor() {
		return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
	}

	public void moveTanks() {
		// loop through the players and get their tanks and key statuses then
		// change their position
		for (int i = 0; i < players.size(); i++) {
			Map<String, Boolean> keys = players.get(i).getKeys();
			Tank t = players.get(i).getTank();
			if (t.isLive()) {
				if (keys.get("W")) {
					// needs players.get(i).getTank() so i don't change the instance variable
					players.get(i).getTank().setPos(t.getX() + Math.cos(Math.toRadians(t.getTheta())) * t.speed,
							t.getY() + Math.sin(Math.toRadians(t.getTheta())) * t.speed, t.getTheta());
				}
				if (keys.get("A")) {
					players.get(i).getTank().setTheta(t.getTheta() - t.speed);
				}
				if (keys.get("S")) {
					players.get(i).getTank().setPos(t.getX() - Math.cos(Math.toRadians(t.getTheta())) * t.speed,
							t.getY() - Math.sin(Math.toRadians(t.getTheta())) * t.speed, t.getTheta());
				}
				if (keys.get("D")) {
					players.get(i).getTank().setTheta(t.getTheta() + t.speed);
				}
			}
		}
	}

	public void moveBullets() {
		for (int i = 0; i < players.size(); i++) {
			Map<String, Boolean> keys = players.get(i).getKeys();
			Tank t = players.get(i).getTank();
			long lastSpawn = players.get(i).getLastSpawn();
			ArrayList<Bullet> bullets = t.getBullets();
			if (keys.get("SPACE") && t.isLive()) {
				if (bullets.size() < Tank.maxBullets) {
					if (System.currentTimeMillis() - lastSpawn > 150) {
						int centerX = (int) t.getX() + Tank.width / 2;
						int centerY = (int) t.getY() + Tank.height / 2;
						// calculate from the center
						// don't actually need height only using width
						t.addBullet(centerX + Math.cos(Math.toRadians(t.getTheta())) * (Tank.width / 2 + Bullet.r),
								centerY + Math.sin(Math.toRadians(t.getTheta())) * (Tank.width / 2 + Bullet.r),
								System.currentTimeMillis(), t.getTheta());
						players.get(i).setLastSpawn(System.currentTimeMillis());
					}
				}
			}
			// Moving bullets
			for (int j = 0; j < bullets.size(); j++) {
				Bullet b = bullets.get(j);
				if (bullets.get(j).checkLife(System.currentTimeMillis())) {
					b.setX(b.getX() + Math.cos(Math.toRadians(b.getTheta())) * b.speed);
					b.setY(b.getY() + Math.sin(Math.toRadians(b.getTheta())) * b.speed);
				} else {
					bullets.remove(j);
				}
			}
		}
	}

	public void collision() {
		for (int i = 0; i < players.size(); i++) {
			Tank t = players.get(i).getTank();
			for (int j = 0; j < players.size(); j++) {
				ArrayList<Bullet> bullets = players.get(j).getTank().getBullets();
				for (int k = 0; k < bullets.size(); k++) {
					if (t.isLive() && bulletInTank(t, bullets.get(k))) {
						// check if they killed themselves
						if (j != i) {
							players.get(j).setScore(players.get(j).getScore() + 1);
							System.out.println("Player " + i + " hit by player " + j);
						} else {
							System.out.println("Player " + i + " commited sudoku");
						}
						t.setLive(false);
						newScores = true;
					}
				}
			}
		}
	}

	public boolean changeFromOld() {
		if (resetting) {
			return true;
		}
		for (int i = 0; i < players.size(); i++) {
			Map<String, Boolean> keys = players.get(i).getKeys();
			for (String k : keys.keySet()) {
				if (keys.get(k)) {
					return true;
				}
			}
			if (players.get(i).getTank().getBullets().size() != 0) {
				return true;
			}
		}
		return false;
	}

	//specific collision test for bullets and tanks
	public boolean bulletInTank(Tank t, Bullet b) {
		int centerX = (int) t.getX() + Tank.width / 2;
		int centerY = (int) t.getY() + Tank.height / 2;
		if (Math.abs(centerX - b.getX()) < Math.abs(Tank.width * Math.cos(Math.toRadians(t.getTheta()))) / 2
				+ Bullet.r / 2) {
			if (Math.abs(centerY - b.getY()) < Math.abs(Tank.width * Math.sin(Math.toRadians(t.getTheta()))) / 2
					+ Bullet.r / 2) {
				return true;
			}
		}
		// math.abs from center
		return false;
	}

	//TODO OVERHAUL ENTIRE DATA OUT PROCESS, possibly pass the outsocket and just write from there using bytes
	public synchronized ArrayList<String> dataOut() {
		// arraylist that will be sent to server with details
		ArrayList<String> sentArray = new ArrayList<>();
		//the info that will be in the arraylist
		//reset for resetting
		// p playerNum
		//	x y theta of tank
		//b numLiveBullets playerNum
		// 	for(numLiveBullets)
		//		b bulletNum  x  y  theta
		//scores numPlayers
		// for(numPlayers)
		//		score playerNum playersScore
		//dead kills the player specified
		if (resetting) {
			resetting = false;
			sentArray.add("reset");
			return sentArray;
		}
		// says player positions and corresponding bullets for that player
		for (int i = 0; i < players.size(); i++) {
			Tank t = players.get(i).getTank();
			sentArray.add("p " + i);
			sentArray.add((int) t.getX() + " " + (int) t.getY() + " " + (int) t.getTheta());
			ArrayList<Bullet> bulletsTemp = t.getBullets();
			sentArray.add("b " + bulletsTemp.size() + " " + i);
			for (int j = 0; j < bulletsTemp.size(); j++) {
				Bullet b = bulletsTemp.get(j);
				sentArray.add((int) b.getX() + " " + (int) b.getY() + " " + (int) b.getTheta());
			}
		}
		// the scores
		if (newScores) {
			sentArray.add("scores " + players.size());
			for (int i = 0; i < players.size(); i++) {
				sentArray.add(players.get(i).getScore() + "");
			}
			// all the dead players
			sentArray.add("dead ");
			for (int i = 0; i < players.size(); i++) {
				if (!players.get(i).getTank().isLive()) {
					sentArray.set(sentArray.size() - 1, sentArray.get(sentArray.size() - 1) + i + " ");
				}
			}
		}
		return sentArray;
	}

	public void dataIn(String s) {
		String[] data = s.split(" ");
		// playerNum keyPressed keyStatus
		players.get(Integer.parseInt(data[0])).setKey(data[1].toUpperCase(), Boolean.parseBoolean(data[2]));
	}

	public void generateWalls() {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (Math.random() < .25) {
					cells[i][j].setBottomW(true);
				}
				if (Math.random() < .25) {
					cells[i][j].setRightW(true);
				}
			}

		}
	}

	// TODO make randomized maps
	public ArrayList<String> sendMap() {
		ArrayList<String> mapData = new ArrayList<>();

		for (int i = 0; i < cells.length; i++) {

			for (int j = 0; j < cells[i].length; i++) {

				if (j == cells[i].length - 1) {

				}
			}
		}
		return mapData;
	}

	public void reset() {
		resetting = true;
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setScore(0);
			players.get(i).setTank(new Tank((Math.random() * (cWidth - (Tank.width * 2))),
					Math.random() * (cHeight - (Tank.width * 2))));
		}
	}

	public void newRound() {
		resetting = true;
		// randomizes the tank positions
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setTank(new Tank((Math.random() * (cWidth - (Tank.width * 2))),
					Math.random() * (cHeight - (Tank.width * 2))));
		}
	}

	public void removePlayer(int playerNum) {
		// TODO fix whenever a player disconnects
		// players.remove(playerNum);
	}
	
	//No idea if this is a good idea or not.
	public void updateAI(ArrayList<Player> playerList) {
		ArrayList<Player> gameState = playerList;
		for(int i = 0; i < gameState.size(); i++) {
			if(gameState.get(i) instanceof ArtificialPlayer) {
				ArtificialPlayer aI = (ArtificialPlayer)gameState.get(i);
				aI.updateKeys();
			}
		}
	}

}
