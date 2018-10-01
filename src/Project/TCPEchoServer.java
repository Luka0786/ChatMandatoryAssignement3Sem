package Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class TCPEchoServer {
    private static Socket[] clientSocketArray = new Socket[5];
    private static int counter = 0;
    private static ServerSocket serverSocket;
    private static final int PORT = 1237;
    private static String[] clientNames = new String[5];

    public static void main(String[] args) throws IOException {
        System.out.println("Opening port");
        PrintWriter output;
        try {
            serverSocket = new ServerSocket(PORT);
            // Socket client = serverSocket.accept();
            while(true) {

            clientSocketArray[counter] = serverSocket.accept();

            System.out.println(clientSocketArray[counter]);

            System.out.println("New client accepted!");
            Scanner socketNameScanner = new Scanner(clientSocketArray[counter].getInputStream());
            String socketName = socketNameScanner.nextLine();
            clientNames[counter] = socketName;


            output = new PrintWriter(clientSocketArray[counter].getOutputStream(),true);
            output.println("J_OK");

            HandleClient handler = new HandleClient(clientSocketArray[counter]);

            counter++;


            handler.start();}

        } catch (Exception e) {
            output = new PrintWriter(clientSocketArray[counter].getOutputStream(),true);
            output.println("J_ER: CODE:" + e.getCause() + " Message: " + e.getMessage());
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
                String tempName = "";

                do {
                    message = inputClient.nextLine();

                    for (int i = 0; i < clientSocketArray.length ; i++) {
                        if(client == clientSocketArray[i]){
                            tempName = clientNames[i];
                        }
                    }


                    sendAll(tempName + ": " + message);
                    /*
                    for (Socket s : clientSocketArray) {
                        if(s != null) {
                            outputClient = null;
                        }
                        */


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


