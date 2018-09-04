package tankGame;


import java.awt.BorderLayout;
import javax.swing.*;


@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	GameFrame() {
		
		// frame description
		super("Tank Game WIP");
		// our Canvas
		tankGame.Game canvas = new Game(2,1000,600);
		add(canvas, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// set it's size and make it visible
		setSize(1000, 600);
		setVisible(true);
		// now that is visible we can tell it that we will use 2 buffers to do the repaint
		// befor being able to do that, the Canvas as to be visible
		canvas.createBufferStrategy(2);
	}
	// just to start the application
	public static void main(String[] args) {
		// instance of our stuff
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new tankGame.GameFrame();
			}
		});
	}
}
