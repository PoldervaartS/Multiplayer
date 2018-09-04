package tankGame;

public class Cell {
	
	static final int side = 100;
	private int x, y;	
	private boolean rightW, bottomW;
	
	public Cell(int x, int y){
		setX(x);
		setY(y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean bottomW() {
		return bottomW;
	}

	public void setBottomW(boolean bottomW) {
		this.bottomW = bottomW;
	}

	public boolean rightW() {
		return rightW;
	}

	public void setRightW(boolean rightW) {
		this.rightW = rightW;
	}
}
