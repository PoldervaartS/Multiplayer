package tankGame;


public class ArtificialPlayer extends Player {
	
	private int difficulty;

	public ArtificialPlayer(Tank t, int difficulty) {
		super(t);
		setDifficulty(difficulty);
		// TODO make AI actually work??
	}

	//simulate the player input, going to be a lot of work
	public void updateKeys() {
		setKey("SPACE",true);
		if(difficulty == 1) {
			setKey("A",true);
		}else if( difficulty == 2){
			setKey("D",true);
		}else {
			
		}
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

}
