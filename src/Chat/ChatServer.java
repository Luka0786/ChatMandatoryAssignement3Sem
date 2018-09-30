package Chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatServer {
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;
    private static List<ClientHandler> chatClients = new ArrayList<>();
    private static int clientCounter = 0;

    public static void main(String[] args) throws IOException {

        System.out.println("Opening port");

        serverSocket = new ServerSocket(PORT);

        Socket socket;

        while(true) {
            socket = serverSocket.accept();

            System.out.println("Client request received " + socket);

            Scanner input =
                    new Scanner(socket.getInputStream());
            PrintWriter output =
                    new PrintWriter(
                            socket.getOutputStream(),true);

            System.out.println("Handling new client:");

            ClientHandler threadChatClient = new ClientHandler(socket, "client " + clientCounter,input, output);
            Thread thread = new Thread(threadChatClient);

            System.out.println("Adding the new client to list");

            chatClients.add(threadChatClient);

            thread.start();

            clientCounter++;
        }
    }
}