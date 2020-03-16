package chatapp.server;

import java.io.*;

public class ClientInfo {
    private boolean busy;
    private String clientName;
    private DataOutputStream outputStream;
    private ClientHandler clientHandler;

    public ClientInfo() {
    }

    public boolean getBusy() {
        return this.busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public DataOutputStream getOutputStream() {
        return this.outputStream;
    }

    public void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public ClientHandler getClientHandler() {
        return this.clientHandler;
    }
    
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

}