
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

        if (operation.equals("sending")) {
            numberOfTransactions = 0;
            maxNbTransactions = 100;
            transaction = new Transactions[maxNbTransactions];
            objNetwork = new Network("client");
            clientOperation = operation;
            readTransactions();
            String cip = objNetwork.getClientIP();
            if (!(objNetwork.connect(cip))) {
                System.exit(0);
            }
        } else if (operation.equals("receiving")) {
            clientOperation = operation;
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
        Scanner inputStream = null;     /* Transactions input file stream */
        int i = 0;                      /* Index of transactions array */

        try {
            try {
                inputStream = new Scanner(new FileInputStream("src/transaction.txt"));
            } catch (FileNotFoundException firstFailure) {
                inputStream = new Scanner(new FileInputStream("transaction.txt"));
            }
        } catch (FileNotFoundException e) {
            System.exit(0);
        }
        while (inputStream.hasNextLine()) {
            try {
                transaction[i] = new Transactions();
                transaction[i].setAccountNumber(inputStream.next());            /* Read account number */
                transaction[i].setOperationType(inputStream.next());            /* Read transaction type */
                transaction[i].setTransactionAmount(inputStream.nextDouble());  /* Read transaction amount */
                transaction[i].setTransactionStatus("pending");                 /* Set current transaction status */

                i++;
            } catch (InputMismatchException e) {
                System.exit(0);
            }

        }
        setNumberOfTransactions(i);        /* Record the number of transactions processed */

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
        int i = 0;     /* index of transaction array */

        while (i < getNumberOfTransactions()) {

            // Wait for buffer space based on configured threading mode
            while (objNetwork.getInBufferStatus().equals("full")) {
                if (Driver.USE_YIELD_MODE) {
                    Thread.yield();
                }
            }

            transaction[i].setTransactionStatus("sent");   /* Set current transaction status */

            objNetwork.send(transaction[i]);

            i++;
        }
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
        int i = 0;     /* Index of transaction array */

        while (i < getNumberOfTransactions()) { // TODO: only for debugging

            // Wait for transaction if buffer is empty
            while (objNetwork.getOutBufferStatus().equals("empty")) {
                if (Driver.USE_YIELD_MODE) {
                    Thread.yield();
                }
            }

            objNetwork.receive(transact);
            System.out.println(transact);
            i++;
        }
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
        Transactions transact = new Transactions();
        long sendClientStartTime = 0, sendClientEndTime = 0, receiveClientStartTime = 0, receiveClientEndTime = 0;

        if (clientOperation.equals("sending")) {
            if (numberOfTransactions != 0) {
                sendClientStartTime = System.currentTimeMillis();
                sendTransactions();
                sendClientEndTime = System.currentTimeMillis();
                objNetwork.disconnect(objNetwork.getClientIP());
                System.out.println();
            }
        } else if (clientOperation.equals("receiving")) {
            receiveClientStartTime = System.currentTimeMillis();
            receiveTransactions(transact);
            receiveClientEndTime = System.currentTimeMillis();
            objNetwork.disconnect(objNetwork.getClientIP());
        }
    }
}
