package Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class TCPEchoServer {
    //Variables
    private static Socket[] clientSocketArray = new Socket[5];
    private static int counter = 0;
    private static ServerSocket serverSocket;
    private static final int PORT = 9000;
    private static String[] clientNames = new String[5];


    public static void main(String[] args) throws IOException {
        // Declaring printwriter
        System.out.println("Opening port");
        PrintWriter output;
        try {
            // Initializing a new serversocket which takes PORT as a parameter
            serverSocket = new ServerSocket(PORT);

            // While loop
            while(true) {
                // Adding the current serverSocket.accept(); to an array of sockets on the array index of counter which is an incrementing int
                clientSocketArray[counter] = serverSocket.accept();

                // Printing to the server that a new client has been accepted
                System.out.println("New client accepted!");


                // Initializing a Scanner which takes the socket in clientsockerArray at index of counter and get the inputStream of that specific socket
                Scanner socketNameScanner = new Scanner(clientSocketArray[counter].getInputStream());

                // We then initialize a printwriter which takes the currently created socket, and send a J_OK message back to the client if the connection is successful
                output = new PrintWriter(clientSocketArray[counter].getOutputStream(),true);

               String names = "";

                for (int i = 0; i < clientNames.length ; i++) {
                    if(clientNames[i] != null){
                    names += clientNames[i] + " ";}
                }

                System.out.println(names);
                output.println(names);
                output.println("J_OK");

                // Getting the socket name from the Client, in the client after connecting we send the name using the output variable.
                String socketName = socketNameScanner.nextLine();

                // We then add the name to an array we defined which contains only the names for that specific socket, so it is added in the same order as the sockets
                // This means if we have index 7 in clientSocketArray we also have the name that was added when creating this socket in the client
                // This is made possible by using the same incrementing int (counter)
                clientNames[counter] = socketName;

                // Initializing a new HandleClient which takes a socket as a parameter
                HandleClient handler = new HandleClient(clientSocketArray[counter]);

                List<String> tempListOfNames = new ArrayList<>();

                for (int i = 0; i < clientNames.length; i++) {

                    if (clientNames[i] != null) {
                        tempListOfNames.add(clientNames[i]);


                    }

                }


                handler.sendAll("LIST " + tempListOfNames.toString()
                        .replace(",", "")
                        .replace("[", "")
                        .replace("]", ""));



                // Incrementing the counter
                counter++;

                // Starting the thread that we initialized above
                handler.start();}

        }
        // A catch clause that will print to the client if connection fails and exit the process
        catch (Exception e) {
            output = new PrintWriter(clientSocketArray[counter].getOutputStream(),true);
            output.println("J_ER: CODE: " + e.getCause() + " Message: " + e.getMessage());
            System.exit(1);

        }
    }

    // HandleClient class which extends Thread
    static class HandleClient extends Thread {
        // Variables
        private Socket client;
        private Scanner inputClient;
        private PrintWriter outputClient;

        // Constructor which takes a socket as a parameter and initializews a new Scanner object which takes our sockets inputstream as a parameter
        public HandleClient(Socket socket) {
            this.client = socket;

            try {
                inputClient = new Scanner(client.getInputStream());

            }
            catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }



        // Method that sends a message to all active clients
        public void sendAll(String message) {
            // For each loop that says, for each Socket in clientSocketArray do this
            for (Socket s : clientSocketArray) {
                if(s != null){
                    try {
                        // If the socket is != null we initialize a new printwriter and use that sockets outputstream as a parameter and then sends the message out to that current client
                        outputClient = new PrintWriter(s.getOutputStream(),true);
                        outputClient.println(message);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // HandleClients run method
        public void run() {
            // Variables
            String message;
            String tempName = "";

            do {
                // Initializing message as inputClient.nextline();
                message = inputClient.nextLine();

                // Setting the name for each socket equal to the index which matches this is explained a bit more in depth above
                for (int i = 0; i < clientSocketArray.length ; i++) {
                    if(client == clientSocketArray[i]){
                        tempName = clientNames[i];
                    }
                }

                // Calling the method sendAll() we add tempName in front of the message so that we can see who sent the message
                sendAll("DATA " + tempName + ": " + message);
            }
            //Loop Will continue running until the message QUIT is typed
            while (!message.equals("QUIT"));

            try {
                // If socket is unequal to null the socket will close and we will print out a closing message
                if (client!=null) {
                    System.out.println("Closing down connection...");
                    client.close();
                }
            }
            catch (IOException ioEx) {
                System.out.println("Unable to disconnect!");
            }
        }
    }


}


