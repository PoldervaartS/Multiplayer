package tankGame;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Chrono implements ActionListener{
	private Game g = null;
	private GameCalculator server = null;
	
	public Chrono(GameCalculator server){
		this.server = server;
	}
	
	public Chrono(Game g){
		this.g = g;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if(g != null){
			g.myRepaint();
		}
		if(server != null){
			server.calculate();
		}
	}
}
