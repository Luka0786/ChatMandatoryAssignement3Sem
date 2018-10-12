package Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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

                handler.sendAll("LIST " + getNamesList(clientNames).toString()
                        .replace(",", "")
                        .replace("[", "")
                        .replace("]", ""));

                // Incrementing the counter
                counter++;

                // Starting the thread that we initialized above
                handler.start();
            }
        }
        // A catch clause that will print to the client if connection fails and exit the process
        catch (Exception e) {
            output = new PrintWriter(clientSocketArray[counter].getOutputStream(),true);
            output.println("J_ER CONNECTION FAILED");
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
                System.out.println("J_ER INPUT/OUTPUT ERROR");
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
                        System.out.println("J_ER INPUT/OUTPUT ERROR");
                    }
                }
            }
        }

        // HandleClients run method
        public void run() throws NoSuchElementException {
            // Variables
            String message;
            String tempName = "";
            List<String> messagesList = new ArrayList<>();

            //Setting newTime equal to current time in milliseconds plus 120000 so that it is 2 minutes ahead
            long newTime = System.currentTimeMillis() + 120000; // 1538989906527  // 1538989967167

            do {
                // Setting tempname = getName with the parameters tempName and client, getName gets the specific name that corresponds with that client socket
                // getting the name from the current client
                tempName = getName(tempName,client);
                // Initializing message as inputClient.nextline();
                message = inputClient.nextLine();
                //making it so only the current client can use his own username like so Kasper cant write messages for Lukas
                if(!message.contains("***IMAV***") && message.startsWith("DATA " + tempName + ":")){

                    String finalMessage = message.replace("DATA ", "");
                    // Calling the method sendAll() we add tempName in front of the message so that we can see who sent the message
                    sendAll(finalMessage);
                }else if(message.contains("***IMAV***")){
                    // adding the current message to the messagesList
                    messagesList.add(message);}
                else if(!message.contains("QUIT")){
                    outputClient.println("J_ER BAD COMMAND");
                }

                // Below fori loop is for visual presentation of the messages sent from the client.
                        /*
                        for (int i = 0; i < messagesList.size(); i++) {
                            System.out.println(i + " : " + messagesList.get(i));
                            }
                        */

                //Creating a long that contains current time in milliseconds
                long currentTime = System.currentTimeMillis();
                //if currentTime which is the currentTime in milliseconds is equal to the newTime
                if (currentTime >= newTime) {
                    //If the list of messages contains ***IMAV***
                    if (messagesList.contains("***IMAV***")) {
                        //tempName = getName(tempName, client) so we get the name that corresponds to the clients socket
                        tempName = getName(tempName, client);

                        //Printing out that the client is ALive for debugging purposes
                        System.out.println(tempName + " IS ALIVE");

                        //Clearing the list every time we check so that it wont contain IMAV from before we ran it.
                        messagesList.clear();
                    } else {
                        //This happends if the list does not contain ***IMAV***
                        //We print out that the client is closing with the clients name and explains that no heartbeat was received
                        System.out.println("Closing client: " + tempName + ". No heartbeat received");
                        // We then clear the list
                        messagesList.clear();
                        try {
                            //We set the clients Socket and name = null so they wont be shown in either of the lists
                            for (int i = 0; i < clientSocketArray.length; i++) {
                                if (client == clientSocketArray[i]) {
                                    clientSocketArray[i] = null;
                                    clientNames[i] = null;
                                }
                            }
                            //We then wrap up by closing the client
                            client.close();

                        } catch (IOException e) {
                            System.out.println("J_ER INPUT/OUTPUT ERROR");
                        }
                    }
                    //Making it so another 2 minutes will pass before running this code again
                    newTime = System.currentTimeMillis() + 120000;
                }
            }

            //Loop Will continue running until the message QUIT is typed
            while (!message.equals("QUIT"));

            try {
                // If socket is unequal to null the socket will close and we will print out a closing message
                if (client != null) {
                    for (int i = 0; i < clientSocketArray.length; i++) {
                        if (client == clientSocketArray[i]) {
                            //Initializing the quitMessage with the clients name
                            String quitMessage = clientNames[i] + " Has left the chat";
                            //Visualising in the server, who left the chat
                            System.out.println(quitMessage);
                            //sends quit message to all active clients
                            sendAll(quitMessage);

                            //Setting the clients socket and name = null, so when we print out those lists the null value will not be shown
                            clientSocketArray[i] = null;
                            clientNames[i] = null;

                            //Send updated list of names which does not contain the client that was just removed
                            sendAll("LIST " + getNamesList(clientNames).toString()
                                    .replace(",", "")
                                    .replace("[", "")
                                    .replace("]", ""));
                        }
                    }
                    client.close();
                }
            } catch (IOException ioEx) {
                System.out.println("J_ER INPUT/OUTPUT ERROR");
            }
        }

        //Method that returns the name that corresponds to the speficic socket
        public String getName(String name, Socket client) {
            for (int i = 0; i < clientSocketArray.length; i++) {
                if (client == clientSocketArray[i]) {
                    name = clientNames[i];
                }
            }
            return name;
        }
    }

    //method that returns a list of names, this list contains all names that arent null
    private static List getNamesList(String[] array){
        List<String> listOfNames = new ArrayList<>();
        for (int j = 0; j < array.length; j++) {

            if (array[j] != null) {
                listOfNames.add(array[j]);
            }
        }
        return listOfNames;
    }
}

