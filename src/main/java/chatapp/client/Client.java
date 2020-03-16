package chatapp.client;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import chatapp.SharedUtil;

public class Client {

    private String address;
    private int port;
    private Socket socket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private AtomicBoolean friendConnected;
    private String clientName;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void startClient() {

        try {
            friendConnected = new AtomicBoolean();
            friendConnected.set(false);

            socket = new Socket(address, port);
            inputStream  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());


            System.out.println(ClientMain.TAG + "Connected to server!");
            System.out.print(ClientMain.TAG + "Enter your name: ");
            BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.clientName = inputBufferedReader.readLine();
            outputStream.writeUTF(SharedUtil.CONTROL_MESSAGE + "_" +SharedUtil.NAME_REQUEST + "_" + this.clientName);
            
            System.out.println();
            System.out.println(ClientMain.TAG + "To retrive client list:  Type CLIENT_LIST");
            System.out.println(ClientMain.TAG + "To connect to a client:  Type FRIEND_<CLIENT_NAME>");
            System.out.println(ClientMain.TAG + "To disconnect from chat:  Type EXIT_CHAT");
            System.out.println();
            
            DataInputThread dataInputThread = new DataInputThread(this, outputStream);
            DataOutputThread dataOutputThread = new DataOutputThread(this, inputStream);

            ExecutorService threadPool = Executors.newCachedThreadPool();
            threadPool.submit(dataInputThread);
            threadPool.submit(dataOutputThread);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    String getClientName() {
        return this.clientName;
    }

    boolean getFriendConnected() {
        return this.friendConnected.get();
    }
    void setFriendConnected(boolean value) {
        this.friendConnected.set(value);
    }
    
}