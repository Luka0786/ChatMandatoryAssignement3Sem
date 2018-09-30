

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class MultiChatServer {
    private static Socket[] clientSocketArray = new Socket[5];
    private static int counter = 0;
    private static ServerSocket serverSocket;
    private static final int PORT = 1237;

    /*------- Troels*/
    static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws IOException {
        System.out.println("Opening port");
        try {
            serverSocket = new ServerSocket(PORT);
            // Socket client = serverSocket.accept();
            while(true) {

                clientSocketArray[counter] = serverSocket.accept();

                System.out.println(clientSocketArray[counter]);

                System.out.println("New client accepted!");

                HandleClient handler = new HandleClient(clientSocketArray[counter]);

                counter++;

                handler.start();
            }

        } catch (IOException ioE) {
            System.out.println("Error: " + ioE);
            System.exit(1);
        }
    }

    static class HandleClient extends Thread {
        private Socket client;
        private Scanner inputClient;
        private PrintWriter outputClient;

        public HandleClient(Socket socket) throws IOException{
            this.client = socket;
            //clientSocketArray[counter] = serverSocket.accept();
            //counter++;

            try {
                inputClient = new Scanner(client.getInputStream());
                //outputClient = new PrintWriter(client.getOutputStream(),true);

                /*------- Troels*/
                writers.add(new PrintWriter(client.getOutputStream(), true));
            }
            catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }




        public void sendAll(String message) {
            for (Socket s : clientSocketArray) {
                if(s != null){
                    System.out.println(s);
                    try {
                        outputClient = new PrintWriter(s.getOutputStream(),true);
                        outputClient.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void run() {
            String message;

            do {
                message = inputClient.nextLine();

                //sendAll(message);
               /* ------- Troels*/
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE : " + message);
                }
                // outputClient.println("ECHO: " + message);
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
