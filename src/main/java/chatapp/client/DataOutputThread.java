package chatapp.client;

import java.io.*;

import chatapp.SharedUtil;

public class DataOutputThread implements Runnable {

    private DataInputStream inputStream = null;
    Client client;
    private String friendTag;

    public DataOutputThread(Client client, DataInputStream inputStream) {
        this.client = client;
        this.inputStream = inputStream;
    }

    private String getTag() {
        return ClientMain.TAG + "[Thread-" + Thread.currentThread().getId() + "] :";
    }

    @Override
    public void run() {
        System.out.println(getTag() + "Moinitoring SOCKET_INPUT");
        
        try {
            while (true) {
                String line = inputStream.readUTF();
                if (line.startsWith(SharedUtil.CONTROL_MESSAGE)) {
                    controlMessageRouter(line);
                } else {
                    System.out.println("[" + friendTag +"] :" + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void controlMessageRouter(String line) {
        if (line.contains(SharedUtil.FRIEND_CONNECTED)) {
            this.client.setFriendConnected(true);
            String[] split = line.split("_");
            friendTag = split[split.length-1];
            System.out.println(getTag() + SharedUtil.FRIEND_CONNECTED + " [" + friendTag +"]");
        }
        if (line.contains(SharedUtil.FRIEND_DISCONNECTED)) {
            this.client.setFriendConnected(false);
            System.out.println(getTag() + SharedUtil.FRIEND_DISCONNECTED);
        }
        if (line.contains(SharedUtil.CLIENT_BUSY)) {
            System.out.println("Client busy! Try connecting to someone else.");
        }
    }

}