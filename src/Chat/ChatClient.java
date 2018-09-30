package Chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {

    final static int ServerPort = 1234;

    public static void main(String[] args) throws UnknownHostException, IOException {



        Scanner scanner = new Scanner(System.in);

        InetAddress ip = InetAddress.getByName("localhost");

        Socket socket = new Socket(ip,ServerPort);

        Scanner input = new Scanner(socket.getInputStream());
        PrintWriter output = new PrintWriter(socket.getOutputStream());

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String message = scanner.nextLine();

                    output.write(message);
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String message = input.nextLine();
                    System.out.println(message);

                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
       /* private static InetAddress host;
        private static final int PORT = 1234;

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
            Socket link = null;

            try {
                link = new Socket(host,PORT);

                Scanner input =
                        new Scanner(link.getInputStream());

                PrintWriter output =
                        new PrintWriter(
                                link.getOutputStream(),true);

                Scanner userEntry = new Scanner(System.in);

                String message, response;

                do {
                    System.out.println("Enter message: ");
                    message = userEntry.nextLine();
                    output.println(message);
                    response = input.nextLine();
                    System.out.println("\nSERVER> " + response);
                }while (!message.equals("***CLOSE***"));

            }catch (IOException ioE){
                ioE.printStackTrace();
            }
            finally {
                try{
                    System.out.println(
                            "\n* Closing connection... *");
                    link.close();
                }
                catch (IOException ioE){
                    System.out.println("Unable to disconnect!");
                    System.exit(1);
                }
            }
        }
    }
    */

}
