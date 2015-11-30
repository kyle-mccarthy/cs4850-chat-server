import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Student: Kyle McCarthy
 * Student ID: 1807388
 * Student Pawprint: KJMD54
 * Date: 11/23/15
 */

public class Server
{
    protected int port;
    protected ServerSocket listener;
    protected int clientCount;
    protected int clientIDCount;
    protected int maxClients;
    protected HashMap <String,String> credentials;
    protected HashSet<String> users;
    protected HashMap <String,PrintWriter> clients;

    /**
     * Server constructor, take in the port from the main application and set the version and reset client count.
     * @param port - int - the port that the server will run on
     */
    public Server(int port)
    {
        this.port = port;
        this.clientCount = 0;
        this.clientIDCount = 0;
        this.maxClients = 3;
        this.users = new HashSet<>();
        this.clients = new HashMap<>();
        this.credentials = new HashMap<>();
        this.loadCredentials();
    }

    public void loadCredentials()
    {
        this.credentials.put("Tom", "Tom11");
        this.credentials.put("David", "David22");
        this.credentials.put("Beth", "Beth33");
        this.credentials.put("John", "John44");
    }

    /**
     * Start the ServerSocket on the desired port, increment the clientCount and create the Thread to handle comms.
     * @throws IOException
     */
    public void run() throws IOException
    {
        this.listener = new ServerSocket(this.port);
        try {
            // run the loop infinitely and process new clients attempting to join the channel
            while (true) {
                // @todo limit the max connections
                this.clientCount++;
                this.clientIDCount++;
                new Handler(this.listener.accept(), clientIDCount).start();
            }
        } finally {
            // called when the process exits (client disconnects), close the listener and then also decrement the counter
            // so other people have the ability to connect to the chat room
            // decrement the client counter and close the connection
            this.clientCount--;
            this.listener.close();
        }
    }

    /**
     * Try to start the server on the default port defined in the instructions.  If there is an issue try to inform the
     * user of the most likely issue and output the stack trace.
     * @param args
     */
    public static void main (String[] args)
    {
        Server server = new Server(17388);
        try {
            server.run();
        } catch (IOException e) {
            System.err.println("Error: Could not bind to port.  This could be the result of the server process" +
                    " exiting abnormally when last run.  View the README file to find how to close the process " +
                    " that is blocking the port.  The stack trace follows: \n");
            e.printStackTrace();
        }
    }

    private class Handler extends Thread
    {
        protected Socket socket;
        protected int clientID;
        protected BufferedReader input;
        protected PrintWriter output;

        /**
         * Thread to handle the requests from a client application to the server.
         * @param socket - Socket from the server object
         * @param id - int - ID of the client running on the handler
         */
        public Handler(Socket socket, int id)
        {
            this.socket = socket;
            this.clientID = id;
        }

        /**
         * Controls the thread for a client, allows for client to input a string that is then sent to the server.
         */
        public void run() {
            try {
                // create the BufferedReader and PrintWriter for input and output respectively
                this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                this.output = new PrintWriter(this.socket.getOutputStream(), true);

                // when a client connects send the following instructions, the client will need to handle these
                // and print them to the CLI at start or all the messages will be delay
                String welcome = "My chat room client.  Version Two.";
                welcome += "Your client has connected to the server and has been assigned the temporary id " +
                        "#" + this.clientID + ".";
                welcome += "Please execute a command or enter help for a list of commands.";
                this.output.println(welcome);

                while (true) {
                    // get the command that is sent to the server and then process it according to the assignment
                    String input = this.input.readLine();

                    // @todo remove debugging
                    // @todo process the commands from the client to the server
                    // direct the user to the readme if they don't know what to do
                    if (input.startsWith("help")) {
                        this.output.println("Please view the readme for a list of commands.");
                    } else if (input.startsWith("login")) {
                        // attempt to login a user with the credentials passed
                        this.output.println("@todo");
                    } else if (input.startsWith("send all")) {
                        // send a message to all the users connected to the server
                        this.output.println("@todo");
                    } else if (input.startsWith("send")) {
                        // send a message to the user specified
                        this.output.println("@todo");
                    } else if (input.startsWith("who")) {
                        // output a list of all the clients connected to the server
                        this.output.println("@todo");
                    } else if (input.startsWith("logout")) {
                        // logout the client from the server
                        // @todo make the client cleaner when logged out somehow
                        this.output.println("Logging out...");
                        break;
                    } else {
                        // catch invalid commands and direct the user to the readme
                        this.output.println("Command not found.  View a list of possible commands in the readme.");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // try to close the connection between handler and server
                try {
                    this.socket.close();
                    System.out.println("Connection with client closed.");
                } catch (IOException e) {
                    System.out.println("Error: exception occurred when closing the socket.  Possibly already closed?");
                }
            }
        }
    }
}
