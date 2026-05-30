
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
    public static final boolean USE_YIELD_MODE = false; // true => use Thread.yield(), false => busy-wait
    public static final boolean DEBUG_LOGS_ENABLED = true; // enable/disable debug

    /* Busy-waiting generally results in longer overall running times and higher CPU usage because threads
    continuously consume CPU cycles while waiting. Yield allows waiting threads to give up the CPU, reducing wasted
    processing time and improving overall system efficiency.
     */

    /** 
     * main class
     * @param args the command line arguments
     */
    public static void main(String[] args) {

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
        clientInThread.start();

        try {
            clientOutThread.join();
            clientInThread.join();
            serverThread.join();
            networkThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
