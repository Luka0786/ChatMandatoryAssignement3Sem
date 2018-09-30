/*import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

class ClientHandler extends Thread {
    private Socket sock;
    private DataOutputStream remoteOut;
    private MultiChatServer server;
    private DataInputStream remoteIn;
    private String user;

    ClientHandler(Socket sock, DataOutputStream remoteOut, String user,
                  MultiChatServer server) throws IOException
    {
        this.sock = sock;
        this.remoteOut = remoteOut;
        this.server = server;
        this.user = user;
        remoteIn = new DataInputStream(sock.getInputStream());
        broadcast( "User " + user + " connected");
    }

    public void run() {
        String s;

        try {
            while (true) {
                s = remoteIn.readUTF();
                broadcast(s);
            }
        }
        catch (IOException e) {
            broadcast("User " + user + " disconnected");
            server.removeFromClients(remoteOut);
        }
        finally {
            try { cleanUp(); }
            catch (IOException x) { }
        }
    }

    // Send the message to all the sockets connected to the server.
    private void broadcast(String s) {
        List clients = server.getClients();
        DataOutputStream dataOut = null;

        for (Iterator i = clients.iterator(); i.hasNext(); ) {
            dataOut = (DataOutputStream)(i.next());

            try { dataOut.writeUTF(s); }
            catch (IOException x) {
                System.out.println(x.getMessage() +
                        ": Failed to broadcast to client.");
                server.removeFromClients(dataOut);
            }
        }
    }

    private void cleanUp() throws IOException {
        if (remoteOut != null) {
            server.removeFromClients(remoteOut);
            remoteOut.close();
        }

        if (remoteIn != null) {
            remoteIn.close();
        }

        if (sock != null) {
            sock.close();
        }
    }
}
*/