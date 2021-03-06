package Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class TCPEchoClient {
    // Attributes
    private static InetAddress host;
    private static int PORT = 0; //1237
    private static Scanner scanner = new Scanner(System.in);
    private static String name;
    private static String hostName;

    public static void main(String[] args)  {
        try {
            // Method call
            connectToServer();

            // Setting the InetAddress
            host = InetAddress.getByName(hostName);

        }
        catch (IOException uhE) {
            // Printing the error message
            System.out.println("J_ER CLIENT NOT ACCEPTED");

            // Closing the system after error occurs
            System.exit(1);
        }
        // Method call
        accessServer();

    }

    // Method that makes the client access the server
    private static void accessServer() {
        // Declaring new socket
        Socket socket;

        try {
            // The socket equals the InetAdress and Port number
            socket = new Socket(host,PORT);

            // Initializing a BufferedReader, that takes the sockets InputStream as a parameter
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Initializing a PrintWriter, that takes the sockets OutputStream as a parameter
            PrintWriter output =
                    new PrintWriter(
                            socket.getOutputStream(),true);


            // Initializing a BufferedReader, that reads the clients input
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));

            String names = input.readLine();
            String[] tempArray = names.split("[\\s]");

            List<String> tempNames = new ArrayList(Arrays.asList(tempArray));

            // Only if our tempNames contains the name input from the client. The client must then choose another name
            while (tempNames.contains(name)){
                System.out.println("Duplicate name please try again: ");
                Scanner scanner = new Scanner(System.in);

                name = scanner.nextLine();
            }

            // Sending the clients name to the sockets OutputStream
            output.println(name);

            // Sending a message in a new thread every 60 second
            Thread IMAV = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String IMAVmessage = "IMAV";
                            output.println(IMAVmessage);
                            Thread.sleep(60000);
                        } catch (InterruptedException ieE) {
                            ieE.printStackTrace();
                        }
                    }
                }
            });
            IMAV.start();

            // Declaring two Strings
            String response;
            System.out.println("Enter message: ");
            do {
                // Thread that reads the message written by the client
                Thread readMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (true){
                            try {
                                // The clients input
                                String message = userEntry.readLine();

                                while(message.length() > 250){
                                    System.out.println("Messages can only be 250 characters");
                                    message = userEntry.readLine();
                                }
                                // Sending the input to the sockets OutputStream
                                output.println(message);

                                // If the client type QUIT the system closes
                                if (message.equals("QUIT")) {
                                    System.exit(1);
                                }
                            }
                            // Catching the IOExeption and printing the error code
                            catch (IOException e) {
                                System.out.println("J_ER INPUT/OUTPUT ERROR");
                            }
                        }
                    }
                });
                // Starting the Thread
                readMessage.start();

                // Setting the response equals the sockets InputStreams readLine method
                response = input.readLine();

                // Printing out the response (J_OK)
                System.out.println(response);
            }
            // Infinite loop
            while (true);
        }
        // Catching the IOExeption and printing the error code
        catch (IOException ioE){
            System.out.println("J_ER INPUT/OUTPUT ERROR");
        }
    }

    // Method that connects the client to the server, if the InetAdress and port matches the server
    private static void connectToServer() {
        // Welcome message
        System.out.println("Welcome to the chat. To enter the chat room, please type:\n" +
                "JOIN user_name, server_ip:server_port");

        // Giving an example
        System.out.println("EXAMPLE: JOIN Lukas, localhost:9000");

        // The clients input to join the server
        String join = scanner.nextLine();

        // Splits the clients input into a String array, and splitting by (space, comma and colon)
        String[] parts = join.split("[\\s,:]+", 200);

        // The name is equal the first index of the String array
        name = parts[1];

        while (name.length() > 12 || !name.matches("[a-åA-Å0-9_æøÆØ-]+")){
            System.out.println("Invalid name. Can only contain 12 characters, numbers and the signs _ and -");
            System.out.println("Please try again: ");
            Scanner temp = new Scanner(System.in);

            name = temp.nextLine();
        }

        // The InetAdress is equal the second index of the String array
        hostName = parts[2];

        // The Port number is equal the third index of the String array, and parsed to an integer, since the input is a String
        PORT = Integer.parseInt(parts[3]);
    }
}
