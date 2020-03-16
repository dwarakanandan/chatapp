package chatapp.client;

import java.io.*;

import chatapp.SharedUtil;

public class DataInputThread implements Runnable {

    Client client;
    private DataOutputStream outputStream = null;

    public DataInputThread(Client client, DataOutputStream outputStream) {
        this.client = client;
        this.outputStream = outputStream;
    }

    private String getTag() {
        return ClientMain.TAG + "[Thread-" + Thread.currentThread().getId() + "] :";
    }

    @Override
    public void run() {
        BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(getTag() + "Moinitoring STANDARD_INPUT");
        System.out.println();

        try {
            while (true) {
                String userInput = inputBufferedReader.readLine();
                
                if (userInput != null) {
                    if (userInput.startsWith("CLIENT_LIST")) {
                        outputStream.writeUTF(SharedUtil.CONTROL_MESSAGE + "_" + SharedUtil.CLIENT_LIST_REQUEST);
                        continue;
                    }
                    if (userInput.startsWith("FRIEND_")) {
                        if (userInput.contains(this.client.getClientName())) {
                            System.out.println("Enter a valid client name!");
                            continue;
                        }
                        outputStream.writeUTF(SharedUtil.CONTROL_MESSAGE + "_" + userInput);
                        continue;
                    }
                    if (userInput.startsWith("EXIT_CHAT")) {
                        outputStream.writeUTF(SharedUtil.CONTROL_MESSAGE + "_" + SharedUtil.EXIT_CHAT);
                        client.setFriendConnected(false);
                        continue;
                    }
                    if (! client.getFriendConnected()) {
                        System.out.println("Not connected to any friends!");
                        continue;
                    }
                    outputStream.writeUTF(userInput);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}