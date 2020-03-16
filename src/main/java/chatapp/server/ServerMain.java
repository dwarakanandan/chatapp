package chatapp.server;

import chatapp.SharedUtil;

public class ServerMain {

    public static String TAG = "[SERVER] :";
    public static void main(String[] args) {
        Server server = new Server(SharedUtil.SERVER_PORT);
        server.startServer();
    }
}
