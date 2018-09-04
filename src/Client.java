import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Client extends JFrame implements KeyListener{

	// bufferedreader for reading server info outstream for sending to server
	private BufferedReader inDataStream;
	private DataOutputStream outDataStream;
	// declare socket
	private Socket connection = null;
	// declare attribute to hold details of remote machine and port
	private String remoteMachine;
	private int port;
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	
	//requirements for game stuff
	tankGame.Game g;
	Map<Integer, Boolean> keys;
	
	public static void main (String args[]){
		// 127.0.0.1 for own computer
		//Server says the ipv4 address
	    new Client("192.168.1.83", 8900);
	}


	public Client(String remoteIn, int portIn){
		remoteMachine = remoteIn;
		port = portIn;
        
		//used so no unnecessary sending of information
		keys = new TreeMap<Integer,Boolean>();
		keys.put(87, false); // W
		keys.put(65, false); // A
		keys.put(83, false); // S
		keys.put(68, false); // D
		keys.put(32, false); // SPACE
		//add Game
		 g = new tankGame.Game(0,WIDTH,HEIGHT);
		//add components
		add(g);
        addKeyListener(this);        
        // configure the frame
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, 0);
		g.setFocusable(false);
        setVisible(true);
        //if start getting crash due to buffer strategy, do g.createStrategy(2);
        startClient();
	}
	
	
	public void startClient(){
		String fromServer = "";
        String[] split = null;
        String bulletInfo = "";
		String[] nSplit = null;
		try{
			// attempt to create a connection to the server
            connection = new Socket(remoteMachine,port);
            // create an input stream from the server
            inDataStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            //create output stream to the server
            outDataStream = new DataOutputStream(connection.getOutputStream());
            
            while(true){
            	fromServer = inDataStream.readLine();
            	//reading the different outputs from the server, documentation in server.dataOut()
            	//TODO OVERHAUL THE ENTIRE SENDING & RECIEVING OF DATA
            	split = fromServer.split(" ");
            	if(split[0].equals("p")){
            		g.setTank(Integer.parseInt(split[1]), Integer.parseInt(split[2]),
            				Integer.parseInt(split[3]), Integer.parseInt(split[4]));
            	}else if(split[0].equals("b")){
            		if(Integer.parseInt(split[1]) < g.getNumBullets(Integer.parseInt(split[2]))){
            			g.removeBulletsPast(Integer.parseInt(split[2]), Integer.parseInt(split[1]));
            		}
            		for(int i = 0; i < Integer.parseInt(split[1]); i++){
            			 bulletInfo = inDataStream.readLine();
            			 nSplit = bulletInfo.split(" ");
            			g.setBullet(Integer.parseInt(nSplit[2]), Integer.parseInt(nSplit[1]),
            					Integer.parseInt(nSplit[3]), Integer.parseInt(nSplit[4]), Integer.parseInt(nSplit[5]));
            		}
            	}else if(split[0].equals("dead")){
            		for(int i = 1; i < split.length; i++){
            			g.killPlayer(Integer.parseInt(split[i]));
            		}
            	}else if(split[0].equals("score")){
            		g.setPlayerScore(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            	}else if(split[0].equals("reset")){
            		//RESET MUST ALWAYS BE LAST
            		g.reset();
            	}
            }
		}catch(IOException e){
			System.out.println(fromServer);
			e.printStackTrace();
			startClient();
		}catch(Exception e){
			System.out.println(fromServer);
			System.out.println(bulletInfo);
			e.printStackTrace();
			startClient();
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {
		try{
			//checks if the key has already been sent at least once
			if(!keys.get(e.getKeyCode())) {
				outDataStream.writeBytes(KeyEvent.getKeyText(e.getKeyCode()) + " true \n");
			}
		}catch(IOException f){
			System.out.println("Error in Client.keypressed");
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		try{
			//sets the key to false
			keys.put(e.getKeyCode(), false);
			outDataStream.writeBytes(KeyEvent.getKeyText(e.getKeyCode()) + " false \n");
		}catch(Exception f){
			System.out.println("Error in Client.keyreleased");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//unused
	}
}
