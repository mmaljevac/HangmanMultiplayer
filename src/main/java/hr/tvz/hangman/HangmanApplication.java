package hr.tvz.hangman;

import hr.tvz.hangman.model.ClientData;
import hr.tvz.hangman.networking.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class HangmanApplication extends Application {
    public static String clientName;

    public static Integer port;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HangmanApplication.class.getResource("hangman-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle(clientName);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        if(Optional.ofNullable(args[0]).isPresent()
//                && Optional.ofNullable(args[1]).isPresent()) {
//            System.out.println("Player name: " + args[0]);
//            clientName = args[0];
//            port = Integer.parseInt(args[1]);
//        }
//        sendRequest();

        launch();
    }

//    private static void sendRequest() {
//        // Closing socket will also close the socket's InputStream and OutputStream.
//        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)){
//            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
//
//            //sendPrimitiveRequest(clientSocket);
//            sendSerializableRequest(clientSocket);
//
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void sendSerializableRequest(Socket client) throws IOException, ClassNotFoundException {
//        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
//        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
//        ClientData newClientData = new ClientData(clientName, port);
//        oos.writeObject(newClientData);
//        System.out.println("Server confirmation: " + ois.readObject());
//    }
}