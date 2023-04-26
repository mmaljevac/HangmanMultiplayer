package hr.tvz.hangman.tcp;

import hr.tvz.hangman.model.ClientData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ServerToClientThread implements Runnable {
    private Socket clientSocket;

    private ClientData connectedClientData;

    public ServerToClientThread(Socket clientSocket, ClientData connectedClientData) {
        this.clientSocket = clientSocket;
        this.connectedClientData = connectedClientData;
    }

    @Override
    public void run() {
        while(true) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try (Socket serverToClientSocket = new Socket(Server.HOST, connectedClientData.getPort())) {
                System.err.println("Server to client is connecting to " + serverToClientSocket.getInetAddress() +
                        ":" + serverToClientSocket.getPort());

                System.out.println("Server timestamp: " + LocalDateTime.now());

                //sendPrimitiveRequest(clientSocket);
                sendSerializableRequest(clientSocket);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private static void sendSerializableRequest(Socket client) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(LocalDate.now().toString());
        System.out.println("Received: " + ois.readObject());
    }
}
