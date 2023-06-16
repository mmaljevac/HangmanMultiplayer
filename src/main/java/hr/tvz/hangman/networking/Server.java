package hr.tvz.hangman.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final String HOST = "localhost";
    public static final int PORT = 1234;

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new player has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);

                new Thread(clientHandler).start();
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
        System.err.println("Server listening on port: " + serverSocket.getLocalPort());
        Server server = new Server(serverSocket);
        server.startServer();
    }

}
