package hr.tvz.hangman.chatyt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerYT {
    public static final String HOST = "localhost";
    public static final int PORT = 1234;

    private final ServerSocket serverSocket;

    public ServerYT(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandlerYT clientHandler = new ClientHandlerYT(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ServerYT server = new ServerYT(serverSocket);
        server.startServer();
    }
}
