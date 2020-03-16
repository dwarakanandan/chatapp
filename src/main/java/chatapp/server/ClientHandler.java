package chatapp.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import chatapp.SharedUtil;

public class ClientHandler implements Runnable {

    private Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;

    ConcurrentHashMap<Long, ClientInfo> clientSet;
    ClientInfo clientInfo;
    ClientInfo friendClientInfo;

    public ClientHandler(Socket socket, ConcurrentHashMap<Long, ClientInfo> clientSet) {
        this.socket = socket;
        this.clientSet = clientSet;
    }

    private String getTag() {
        return ServerMain.TAG + "[Thread-" + Thread.currentThread().getId() + "] :";
    }

    @Override
    public void run() {
        try {
            System.out.println(getTag() + "Connection accepted");
            clientInfo = new ClientInfo();
            clientInfo.setClientHandler(this);
            clientInfo.setBusy(false);

            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());
            clientInfo.setOutputStream(outputStream);
            clientSet.put(Thread.currentThread().getId(), clientInfo);

            String line = "";

            while (true) {
                line = inputStream.readUTF();
                System.out.println(getTag() + line);

                if (line.startsWith(SharedUtil.CONTROL_MESSAGE)) {
                    controlMessageRouter(line);
                } else {
                    friendClientInfo.getOutputStream().writeUTF(line);
                }

            }

        } catch (IOException e) {
            System.out.println(getTag() + "Connection dropped");
            clientSet.remove(Thread.currentThread().getId());
            exitChat();
        } finally {
            try {
                socket.close();
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void controlMessageRouter(String line) throws IOException {
        if (line.contains(SharedUtil.NAME_REQUEST)) {
            setSelfClientInfo(line);
        }
        if (line.contains(SharedUtil.CLIENT_LIST_REQUEST)) {
            sendClientList();
        }
        if (line.contains(SharedUtil.FRIEND_REQUEST)) {
            setFriendClientInfo(line);
        }
        if (line.contains(SharedUtil.EXIT_CHAT)) {
            exitChat();
        }
    }

    public void notifyFriendRequest(ClientInfo friendClientInfo) throws IOException {
        this.friendClientInfo = friendClientInfo;
        this.clientInfo.setBusy(true);
        outputStream.writeUTF(SharedUtil.CONTROL_MESSAGE+"_"+SharedUtil.FRIEND_CONNECTED+"_"+friendClientInfo.getClientName());
    }

    private void exitChat() {
        this.clientInfo.setBusy(false);
        if (this.friendClientInfo != null) {
            try {
                friendClientInfo.getOutputStream()
                        .writeUTF(SharedUtil.CONTROL_MESSAGE + "_" + SharedUtil.FRIEND_DISCONNECTED);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void setSelfClientInfo(String line) {
        String[] split = line.split("_");
        clientInfo.setClientName(split[split.length-1]);
    }

    private void setFriendClientInfo(String line) throws IOException {
        String[] split = line.split("_");
        String friendName = split[split.length-1];
        for (Long handlerThread: clientSet.keySet()) {
            ClientInfo tempClientInfo = clientSet.get(handlerThread);
            if (tempClientInfo.getClientName().contains(friendName)) {
                if (tempClientInfo.getBusy()) {
                    outputStream.writeUTF(SharedUtil.CONTROL_MESSAGE+"_"+SharedUtil.CLIENT_BUSY);
                } else {
                    tempClientInfo.getClientHandler().notifyFriendRequest(this.clientInfo);
                    this.friendClientInfo = tempClientInfo;
                    this.clientInfo.setBusy(true);
                    outputStream.writeUTF(SharedUtil.CONTROL_MESSAGE+"_"+SharedUtil.FRIEND_CONNECTED+"_"+friendClientInfo.getClientName());
                }
                return;
            }
        }
        
    }

    private void sendClientList() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Number of clients online: " + clientSet.size() + "\n");
        for (Long handlerThread: clientSet.keySet()) {
            ClientInfo tempClientInfo = clientSet.get(handlerThread);
            String statusString = "free";
            if (tempClientInfo.getBusy()) {
                statusString = "busy";
            }
            stringBuilder.append("*" + tempClientInfo.getClientName() + " [" +statusString + "]\n");
        }
        outputStream.writeUTF(stringBuilder.toString());
    }

}