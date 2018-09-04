import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

@SuppressWarnings("serial")
public class Server extends JFrame implements KeyListener{
	
	private JTextArea textWindow= new JTextArea();
	//number of AI players and difficulty
	private int numAI, difficulty;
    private int port;
    public int numPlayers;
    //dimentions of the server window
    final int WIDTH = 400;
    final int HEIGHT = 300;
    //dimentions of the client window, if change 1 change other
    final int CWIDTH = 1000;
    final int CHEIGHT = 600;
    //calculating all the stuff
    public tankGame.GameCalculator game;
    //necessary for the server
    Socket connection = null;
    ServerSocket listenSocket;
    BufferedReader inDataStream;
    DataOutputStream outDataStream;
    
    public Server(int portIn){
    	port = portIn;
    	addAI();
    	numPlayers = 0;
    	textWindow.setFocusable(false);
    	setTitle("Addition Sever");
        add("Center",textWindow);
        addKeyListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setVisible(true);
        game = new tankGame.GameCalculator(numPlayers, numAI, difficulty, CWIDTH, CHEIGHT);
        startServer();
    }
    
	public void startServer(){
		try {
			//Used to get the ip address of the server
			InetAddress ip = Inet4Address.getLocalHost();
			textWindow.append("Server ip : " + ip.getHostAddress() + "\n");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        try {
            listenSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
        	try{
        		textWindow.append("Listening on port "+ port +"\n");
        		//listen for a connection from the client 
                connection = listenSocket.accept();
                game.addPlayer();
                //possible place for ai input
                ServerThread st = new ServerThread(connection, numPlayers++, game);
                st.start();
                textWindow.append("Connection esablished\n ");
        	}catch(IOException e){
        		
            }
        	
        }
    }
	
    public void addAI() {
		// should do the numAI in game calculator so it can be changed on reset, but i don't know shit.
		Scanner reader = new Scanner(System.in); // Reading from System.in
		System.out.println("Enter number of AI: ");
		numAI = reader.nextInt();
		System.out.println("Enter difficulty 1, 2 or 3 :");
		difficulty = reader.nextInt();
		reader.close();
		//first players are the ai
	}

	//IP of own computer is 127.0.0.1
	public static void main(String arg[]){
		 new Server(8900);
	}
	

	@SuppressWarnings("static-access") @Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == (e.VK_R)){
			textWindow.append("Resetting game \n");
			game.reset();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// unused
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// unused
		
	}
	
}

class ServerThread extends Thread{  

    String line=null;
    BufferedReader  br=null;
    PrintWriter writer=null;
    Socket s=null;
    int playerNum = 0;
    tankGame.GameCalculator g;

    public ServerThread(Socket s, int playerNum, tankGame.GameCalculator g){
        this.s=s;
        this.playerNum = playerNum;
        this.g = g;
    }

    public void run() {
    try{
        br= new BufferedReader(new InputStreamReader(s.getInputStream()));
        writer=new PrintWriter(s.getOutputStream());

    }catch(IOException e){
        System.out.println("IO error in server thread");
    }

    try {
        line=br.readLine();
        while(line.compareTo("QUIT")!=0){
        	g.dataIn(playerNum + " " + line);
        	//done so that the player is getting new info even if no input from them
        	while(!br.ready() && g.changeFromOld()){
        		sendData();
        	}
        	sendData();
            line=br.readLine();
        }
        if(line.compareTo("QUIT") == 0){
        	g.removePlayer(playerNum);
        	System.out.println("player " + playerNum + " left");
        }
    } catch (IOException e) {
    	g.removePlayer(playerNum);
        line=this.getName(); //reused String line for getting thread name
        System.out.println("IO Error/ Client "+line+" terminated abruptly");
    }
    catch(NullPointerException e){
        line=this.getName(); //reused String line for getting thread name
        System.out.println("Client "+line+" Closed");
    }
     finally{
    	try{
    		System.out.println("Connection Closing..");
    		if (br!=null){
    			br.close(); 
    			System.out.println(" Socket Input Stream Closed");
    		}

    		if(writer!=null){
    			writer.close();
    			System.out.println("Socket Out Closed");
    		}
    		if (s!=null){
    			s.close();
    			System.out.println("Socket Closed");
    		}
        	}
    	catch(IOException ie){
    		System.out.println("Socket Close Error");
    	}
     }//end finally
    }
    
    public void sendData(){
    	ArrayList<String> toClient = g.dataOut();
    	while(!toClient.isEmpty()){
    		String s = toClient.remove(0);
    		String[] split = s.split(" ");
    		//the info from the arraylist is split and sent entry by entry
    		if(split[0].equals("reset")){
    			toClient.clear();
    			writer.println(split[0]);
    			writer.flush();
    		}
    		// p playerNum
    		//	x y theta of tank
    		else if(split[0].equals("p")){
    			writer.println(s + " " + toClient.remove(0));
    			writer.flush();
    		} 
    		//b numLiveBullets playerNum
    		// 	for(numLiveBullets)
    		//		b bulletNum  x  y  theta
    		else if(split[0].equals("b") && Integer.parseInt(split[1]) != 0){
    			writer.println(s);
    			writer.flush();
    			for(int i = 0; i < Integer.parseInt(split[1]); i++){
    				String send = split[0] + " " + i + " " + split[2] + " " + toClient.remove(0);
    				writer.println(send);
    				writer.flush();
    			}
    		}    		
    		//scores numPlayers
    		// for(numPlayers)
    		//		score playerNum playersScore
    		else if(split[0].equals("scores")){
    			for(int i = 0; i < Integer.parseInt(split[1]); i++){
    				String send = "score " + i + " " + toClient.remove(0);
    				writer.println(send);
    				writer.flush();
    			}
    		}
    		//dead kills the player specified
    		else if(split[0].equals("dead")){
    			writer.println(s);
    			writer.flush();
    		}
    	}
    }
    
}
