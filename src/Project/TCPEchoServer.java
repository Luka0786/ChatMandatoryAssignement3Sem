package Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class TCPEchoServer {

    private static ServerSocket serverSocket;
    private static final int PORT = 1237;

    public static void main(String[] args) throws IOException {
        System.out.println("Opening port");
        try {
            serverSocket = new ServerSocket(PORT);
        }catch (IOException ioE)
        {
            System.out.println("Error: " + ioE);
            System.exit(1);
        }
        do {
            Socket client = serverSocket.accept();

            System.out.println("New client accepted!");

            HandleClient handler = new HandleClient(client);

            handler.start();
        }while (true);

    }

    static class HandleClient extends Thread {
        private Socket client;
        private Scanner inputClient;
        private PrintWriter outputClient;
        //private Socket[] clientSocketArray = new Socket[5];
        //private int counter = 0;

        public HandleClient(Socket socket) throws IOException{
            this.client = socket;
            //clientSocketArray[counter] = serverSocket.accept();
            //counter++;

            try {
                inputClient = new Scanner(client.getInputStream());
                outputClient = new PrintWriter(client.getOutputStream(),true);
            }
            catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }

            public void run() {
                String message;

                do {
                    message = inputClient.nextLine();
                    /*
                    for (Socket s : clientSocketArray) {
                        if(s != null) {
                            outputClient = null;
                        }
                        */


                    outputClient.println("ECHO: " + message);


                }
                while (!message.equals("QUIT"));

                try {
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


