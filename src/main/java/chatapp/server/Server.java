package chatapp.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

    private int port;
    private ServerSocket server = null;
    private ConcurrentHashMap<Long, ClientInfo> clientSet;

    public Server(int port) {
        this.port = port;
        clientSet = new ConcurrentHashMap<>();
    }

    public void startServer() {

        try {
            server = new ServerSocket(port);
            System.out.println(ServerMain.TAG + "Server started");
            ExecutorService threadPool = Executors.newCachedThreadPool();
            while(true) {
                Socket socket = server.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clientSet);
                threadPool.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}