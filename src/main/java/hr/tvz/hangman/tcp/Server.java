package hr.tvz.hangman.tcp;

import hr.tvz.hangman.model.ClientData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final String HOST = "localhost";
    public static final int PORT = 1989;

    public static void main(String[] args) {
        acceptRequests();
    }

    private static void acceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                // outer try catch blocks cannot handle the anonymous implementations
                //new Thread(() ->  processPrimitiveClient(clientSocket)).start();
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void processSerializableClient(Socket clientSocket) {

        ClientData connectedClientData = null;

        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){

            if(ois.readObject() instanceof ClientData clientData) {
                connectedClientData = clientData;
                System.out.println("Player name arrived: " + clientData.getPlayerName());
                oos.writeObject(clientData + " welcome!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //tu stavi thread
        ServerToClientThread serverToClientThread = new ServerToClientThread(clientSocket,
                connectedClientData);

        new Thread(serverToClientThread).start();
    }
}
