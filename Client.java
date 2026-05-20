
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** Client class
 *
 * @author Kerly Titus
 */

public class Client implements Runnable {

    private static int numberOfTransactions;        /* Number of transactions to process */
    private static int maxNbTransactions;            /* Maximum number of transactions */
    private static Transactions[] transaction;    /* Transactions to be processed */
    private static Network objNetwork;            /* Client object to handle network operations */
    private String clientOperation;                    /* sending or receiving */

    /**
     * Constructor method of Client class
     *
     * @param operation "sending" or "receiving"
     */
    public Client(String operation) {
        System.out.println("[CLIENT] Creating new Client with operation: " + operation);

        if (operation.equals("sending")) {
            System.out.println("[CLIENT-SEND] Initializing client sending application ...");
            numberOfTransactions = 0;
            maxNbTransactions = 100;
            transaction = new Transactions[maxNbTransactions];
            objNetwork = new Network("client");
            clientOperation = operation;
            System.out.println("[CLIENT-SEND] Reading transactions from file ...");
            readTransactions();
            System.out.println("[CLIENT-SEND] Connecting to network ...");
            String cip = objNetwork.getClientIP();
            System.out.println("[CLIENT-SEND] Client IP: " + cip);
            if (!(objNetwork.connect(cip))) {
                System.out.println("[CLIENT-SEND] ERROR: Network unavailable!");
                System.exit(0);
            }
            System.out.println("[CLIENT-SEND] ✓ Successfully connected to network");
        } else if (operation.equals("receiving")) {
            System.out.println("[CLIENT-RECEIVE] Initializing client receiving application ...");
            clientOperation = operation;
            System.out.println("[CLIENT-RECEIVE] Ready to receive transactions");
        }
    }

    /**
     * Accessor method of Client class
     *
     * @param
     * @return numberOfTransactions
     */
    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    /**
     * Mutator method of Client class
     *
     * @param nbOfTrans
     * @return
     */
    public void setNumberOfTransactions(int nbOfTrans) {
        numberOfTransactions = nbOfTrans;
    }

    /**
     * Accessor method of Client class
     *
     * @param
     * @return clientOperation
     */
    public String getClientOperation() {
        return clientOperation;
    }

    /**
     * Mutator method of Client class
     *
     * @param operation
     * @return
     */
    public void setClientOperation(String operation) {
        clientOperation = operation;
    }

    /**
     * Reading of the transactions from an input file
     *
     * @param
     * @return
     */
    public void readTransactions() {
        System.out.println("[CLIENT-SEND] === Starting readTransactions() ===");
        Scanner inputStream = null;     /* Transactions input file stream */
        int i = 0;                      /* Index of transactions array */

        try {
            inputStream = new Scanner(new FileInputStream("transaction.txt"));
            System.out.println("[CLIENT-SEND] Successfully opened transaction.txt");
        } catch (FileNotFoundException e) {
            System.out.println("[CLIENT-SEND] ERROR: File transaction.txt was not found");
            System.out.println("[CLIENT-SEND] ERROR: Could not be opened.");
            System.exit(0);
        }
        while (inputStream.hasNextLine()) {
            try {
                transaction[i] = new Transactions();
                transaction[i].setAccountNumber(inputStream.next());            /* Read account number */
                transaction[i].setOperationType(inputStream.next());            /* Read transaction type */
                transaction[i].setTransactionAmount(inputStream.nextDouble());  /* Read transaction amount */
                transaction[i].setTransactionStatus("pending");                 /* Set current transaction status */

                System.out.println("[CLIENT-SEND] Transaction[" + i + "]: Account=" + transaction[i].getAccountNumber()
                        + ", Type=" + transaction[i].getOperationType()
                        + ", Amount=" + transaction[i].getTransactionAmount()
                        + ", Status=" + transaction[i].getTransactionStatus());
                i++;
            } catch (InputMismatchException e) {
                System.out.println("[CLIENT-SEND] ERROR: Line " + i + " in transaction.txt has invalid input");
                System.exit(0);
            }

        }
        setNumberOfTransactions(i);        /* Record the number of transactions processed */
        System.out.println("[CLIENT-SEND] === Finished readTransactions() ===");
        System.out.println("[CLIENT-SEND] Total transactions loaded: " + getNumberOfTransactions());

        inputStream.close();

    }

    /************************************************************************************************************************************************
     * TO DO : if the network input buffer is full then yield the cpu using the Java method Thread.yield(); first try with busy-waiting				*
     ************************************************************************************************************************************************/

    /**
     * Sending the transactions to the server
     *
     * @param
     * @return
     */
    public void sendTransactions() {
        System.out.println("[CLIENT-SEND] === Starting sendTransactions() ===");
        int i = 0;     /* index of transaction array */

        while (i < getNumberOfTransactions()) {
            System.out.println("[CLIENT-SEND] Processing transaction " + i + " of " + getNumberOfTransactions());

            // Wait for buffer space based on configured threading mode
            while (objNetwork.getInBufferStatus().equals("full")) {
                System.out.println("[CLIENT-SEND] Buffer is FULL, waiting...");
                if (AppConfig.getThreadingMode().equals(ThreadingMode.BUSY_WAIT)) {
                    System.out.println("[CLIENT-SEND] ... using BUSY WAIT mode");
                } else if (AppConfig.getThreadingMode().equals(ThreadingMode.YIELD)) {
                    System.out.println("[CLIENT-SEND] ... using YIELD mode, giving CPU to other threads");
                    Thread.yield();
                }
            }

            System.out.println("[CLIENT-SEND] Buffer has space, sending transaction...");
            transaction[i].setTransactionStatus("sent");   /* Set current transaction status */

            System.out.println("[CLIENT-SEND] >> Sending transaction " + i + " for account " + transaction[i].getAccountNumber()
                    + " (Operation: " + transaction[i].getOperationType() + ", Amount: " + transaction[i].getTransactionAmount() + ")");

            objNetwork.send(transaction[i]);                            /* Transmit current transaction */
            System.out.println("[CLIENT-SEND] >> Transaction " + i + " sent successfully");
            i++;
        }
        System.out.println("[CLIENT-SEND] === Finished sendTransactions() ===");
        System.out.println("[CLIENT-SEND] All " + getNumberOfTransactions() + " transactions sent");
    }

    /************************************************************************************************************************************************
     * TO DO : if the network output buffer is full then yield the cpu using the Java method Thread.yield(); first try with busy-waiting			 *
     *************************************************************************************************************************************************/

    /**
     * Receiving the completed transactions from the server
     *
     * @param transact
     * @return
     */
    public void receiveTransactions(Transactions transact) {
        System.out.println("[CLIENT-RECEIVE] === Starting receiveTransactions() ===");
        int i = 0;     /* Index of transaction array */

        while (i < getNumberOfTransactions()) {
            System.out.println("[CLIENT-RECEIVE] Waiting for transaction " + i + " from network...");

            // Wait for transaction if buffer is empty
            while (objNetwork.getOutBufferStatus().equals("empty")) {
                System.out.println("[CLIENT-RECEIVE] Output buffer is EMPTY, waiting...");
                if (AppConfig.getThreadingMode().equals(ThreadingMode.YIELD)) {
                    Thread.yield();
                }
            }

            System.out.println("[CLIENT-RECEIVE] << Receiving transaction " + i);
            objNetwork.receive(transact);                                /* Receive updated transaction from the network buffer */

            System.out.println("[CLIENT-RECEIVE] << Transaction " + i + " received for account " + transact.getAccountNumber()
                    + " with new balance: " + transact.getTransactionBalance());

            System.out.println("[CLIENT-RECEIVE] Transaction details: " + transact);
            i++;
        }
        System.out.println("[CLIENT-RECEIVE] === Finished receiveTransactions() ===");
        System.out.println("[CLIENT-RECEIVE] All " + getNumberOfTransactions() + " transactions received");
    }

    /**
     * Create a String representation based on the Client Object
     *
     * @param
     * @return String representation
     */
    public String toString() {
        return ("\n client IP " + objNetwork.getClientIP() + " Connection status" + objNetwork.getClientConnectionStatus() + "Number of transactions " + getNumberOfTransactions());
    }

    /* *********************************************************************************************************************************************
     * TODO : implement the method Run() to execute the sending and receiving threads and also record the running times 							*
     * *********************************************************************************************************************************************/

    /**
     * Code for the run method
     *
     * @param
     * @return
     */
    public void run() {
        System.out.println("[CLIENT] === Client thread started ===");
        System.out.println("[CLIENT] Client operation mode: " + clientOperation);

        Transactions transact = new Transactions();
        long sendClientStartTime = 0, sendClientEndTime = 0, receiveClientStartTime = 0, receiveClientEndTime = 0;

        if (clientOperation.equals("sending")) {
            System.out.println("[CLIENT-SEND] Entering SEND mode");
            if (numberOfTransactions != 0) {
                sendClientStartTime = System.currentTimeMillis();
                System.out.println("[CLIENT-SEND] Start time: " + sendClientStartTime);
                sendTransactions();
                sendClientEndTime = System.currentTimeMillis();
                System.out.println("[CLIENT-SEND] End time: " + sendClientEndTime);
                System.out.println("[CLIENT-SEND] Total time: " + (sendClientEndTime - sendClientStartTime) + " ms");
            } else {
                System.out.println("[CLIENT-SEND] No transactions to send!");
            }
        } else if (clientOperation.equals("receiving")) {
            System.out.println("[CLIENT-RECEIVE] Entering RECEIVE mode");
            receiveClientStartTime = System.currentTimeMillis();
            System.out.println("[CLIENT-RECEIVE] Start time: " + receiveClientStartTime);
            receiveTransactions(transact);
            receiveClientEndTime = System.currentTimeMillis();
            System.out.println("[CLIENT-RECEIVE] End time: " + receiveClientEndTime);
            System.out.println("[CLIENT-RECEIVE] Total time: " + (receiveClientEndTime - receiveClientStartTime) + " ms");
        }

        System.out.println("[CLIENT] === Client thread finished ===");
    }
}
