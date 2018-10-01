package Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class TCPEchoClient {
    private static InetAddress host;
    private static int PORT = 0; //1237
    private static Scanner scanner = new Scanner(System.in);
    private static String name;
    private static String hostName;

    public static void main(String[] args) {

        try{
            connectToServer();
            host = InetAddress.getByName(hostName);


        }catch (UnknownHostException uhE) {
            System.out.println("Client not accepted." + uhE.getMessage());
            System.exit(1);
        }
        accessServer();

    }

    private static void accessServer() {
        Socket socket = null;

        try {
            socket = new Socket(host,PORT);

            BufferedReader input =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter output =
                    new PrintWriter(
                            socket.getOutputStream(),true);

            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));

            output.println(name);
            String message = "", response;
            boolean isRunning = true;


            do {
                System.out.println("Enter message: ");

                Thread readMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            try {
                                String message = userEntry.readLine();;
                                output.println(message);
                                if (message.equals("QUIT")) {
                                    System.exit(1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                readMessage.start();

                response = input.readLine();

                System.out.println(response);



            }while (isRunning);


        }catch (IOException ioE){
            ioE.printStackTrace();
        }
        finally {
            try{
                System.out.println(
                        "\n* Closing connection ");
                socket.close();
            }
            catch (IOException ioE){
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }

    private static void connectToServer() {
        System.out.println("Welcome to the chat. To enter the chat room, please type:\n" +
                "JOIN user_name, server_ip:server_port");

        System.out.println("EXAMPLE: JOIN Lukas, localhost:1237");


        String join = scanner.nextLine();
        System.out.println(join);
        String[] parts = join.split("[\\s,:]+", 200);
        name = parts[1];
        hostName = parts[2];
        PORT = Integer.parseInt(parts[3]);



    }
}
