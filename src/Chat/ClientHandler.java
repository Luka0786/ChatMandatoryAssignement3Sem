package Chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    Scanner scanner = new Scanner(System.in);
    private String name;
    Scanner input;
    PrintWriter output;
    Socket socket;
    boolean isLoggedIn;

    public ClientHandler(Socket socket, String name, Scanner input, PrintWriter output) {
        this.socket = socket;
        this.name = name;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        String received = input.nextLine();;
        while(!received.equals("CLOSE")) {
            try {
                System.out.println(received);

                output.println("Message received: " + received);

                received = input.nextLine();

            }
            finally {
            try {
                this.isLoggedIn = false;
                this.socket.close();

                System.out.println(
                        "\n* Closing connection... *"
                );

            }
            catch (IOException ioE){
                System.out.println("UNABLE TO DISCONNECT");
                System.exit(1);
            }
            }
        }
    }



    /*
        try {
        int numMessages = 0;
        String message = input.nextLine();
        while(!message.equals("***CLOSE***"))
        {
            System.out.println("Message received.");
            numMessages++;
            output.println("Message " + numMessages
                    + ": " + message);

            message = input.nextLine();
        }
        output.println(numMessages + " messages received.");
    }catch (IOException ioE)
    {
        ioE.printStackTrace();
    }finally {
        try {
            System.out.println(
                    "\n* Closing connection... *"
            );
            link.close();
        }catch (IOException ioE){
            System.out.printf("UNABLE TO DISCONNECT!");
            System.exit(1);
        }
    }
    */
}
