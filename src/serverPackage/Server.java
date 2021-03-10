package serverPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private ServerSocket myServerSocket;
    private Thread mainThread;
    private AtomicBoolean shutDown = new AtomicBoolean(false);
    private ArrayList<ClientHandler> clientHandlersList = new ArrayList<>();

    public Server() {

        try {
            myServerSocket = new ServerSocket(5005);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainThread = new Thread(() -> {
            while (!myServerSocket.isClosed()) {
                try {
                    Socket s = myServerSocket.accept();
                    ClientHandler client = new ClientHandler(s);
                    clientHandlersList.add(client);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        });
        mainThread.start();
    }
    public void stopServer() throws IOException {
        try {
            myServerSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i =0; i < clientHandlersList.size(); i++) {
            clientHandlersList.get(i).connectionClosed();
        }
    }
}
