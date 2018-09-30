package Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TCPEchoClient {
    private static InetAddress host;
    private static final int PORT = 1237;
    private static int incrementingPort = 1237;

    public static void main(String[] args) {

        try{

            host = InetAddress.getLocalHost();

        }catch (UnknownHostException uhE) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        accessServer();

    }

    private static void accessServer() {
        Socket socket = null;

        try {
            socket = new Socket(host,incrementingPort);

            Scanner input =
                    new Scanner(socket.getInputStream());

            PrintWriter output =
                    new PrintWriter(
                            socket.getOutputStream(),true);

            Scanner userEntry = new Scanner(System.in);

            String message, response;

            incrementingPort++;

            do {
                System.out.println("Enter message: ");
                message = userEntry.nextLine();
                output.println(message);
                response = input.nextLine();
                System.out.println("\nSERVER> " + response);
                input.nextLine();


            }while (!message.equals("QUIT"));

        }catch (IOException ioE){
            ioE.printStackTrace();
        }
        finally {
            try{
                System.out.println(
                        "\n* Closing connection... *");
                socket.close();
            }
            catch (IOException ioE){
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }
}
