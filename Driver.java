
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kerly Titus
 */
public class Driver {

    /** 
     * main class
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        AppConfig.displayThreadingModeMenu();
        
    	Network network = new Network("network");
    	Thread networkThread = new Thread(network);

        Server server = new Server();
        Thread serverThread = new Thread(server);

        Client clientOut = new Client("sending");
        Thread clientOutThread = new Thread(clientOut);

        Client clientIn = new Client("receiving");
        Thread clientInThread = new Thread(clientIn);

        networkThread.start();
        serverThread.start();
        clientOutThread.start();
//        clientInThread.start();

    	 /*******************************************************************************************************************************************
    	  * TODO : implement all the operations of main class - activate network and start client (sending & receiving) and server 																					*
    	  ******************************************************************************************************************************************/
        
    	
    }
}
