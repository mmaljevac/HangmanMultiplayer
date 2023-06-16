package hr.tvz.hangman;

import hr.tvz.hangman.model.GameState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import static hr.tvz.hangman.networking.Server.HOST;
import static hr.tvz.hangman.networking.Server.PORT;

public class HangmanController {
    private static final Integer MAX_LIVES = 6;

    private static final String SAVE_GAME_FILE_NAME = "hangmanSave.bin";
    private static Boolean gameOver;
    private static Integer lives;
    @FXML
    private Text wordText;
    @FXML
    private Text guessedWordText;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField letterField;
    @FXML
    private Text livesText;
    @FXML
    public static TextArea chatArea;
    @FXML
    public static TextField messageField;
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static String username = "Player 1";
    @FXML
    private Label usernameLabel;
    private static Boolean isConn = false;


    public void initialize() {
        newGame();
        new Thread(() -> startClientThread()).start();

        usernameLabel.setText(username);
    }

    private void startClientThread() {
        try {
            Socket socket = new Socket(HOST, PORT);

            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            sendMessage();
            listenForMessage();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void newGame() {
        gameOver = false;
        lives = MAX_LIVES;
        livesText.setText(lives.toString());

        imageView.setImage(new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + lives + ".png")));

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Enter username:");

        Optional<String> usernameOptional = dialog.showAndWait();
        username = usernameOptional.orElse("");

//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Input Dialog");
//        dialog.setHeaderText("Enter a word:");
//        dialog.setContentText("Word:");
//
//        Optional<String> wordDialog = dialog.showAndWait();
//        String word = wordDialog.orElse("");
        // TODO temporary word
        String word = "Waiting...";

        wordText.setText(word.toUpperCase());

//        StringBuilder secretWord = new StringBuilder();
//        for (int i = 0; i < word.length(); i++) {
//            if (word.charAt(i) == ' ') {
//                secretWord.append(" ");
//            } else {
//                secretWord.append("*");
//            }
//        }
        guessedWordText.setText(word.toString());
    }

    @FXML
    public void enterLetter(ActionEvent event) {
        if (letterField.getText().isEmpty()) return;

        if (!gameOver) {
            checkLetter();
            checkWin();
        }

        if (lives == 0) {
            gameOver = true;
            showConfirmation("You lost!", "Start a new game?");
        }
        letterField.clear();
    }

    public void checkLetter() {

        if (letterField.getText().length() != 1) {
            showMessage("You can enter 1 letter only!", "");
            letterField.clear();
            return;
        }

        char letter = letterField.getText().toUpperCase().charAt(0);
        String word = wordText.getText();

        if (guessedWordText.getText().indexOf(letter) != -1) {
            showMessage("You already guessed this letter!", "");
            letterField.clear();
            return;
        }

        if (word.indexOf(letter) != -1) {
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == letter) {
                    String newSecretWord = guessedWordText.getText();
                    newSecretWord = newSecretWord.substring(0, i) + letter + newSecretWord.substring(i + 1);
                    guessedWordText.setText(newSecretWord);
                }
            }
        } else {
            if (lives > 0) {
                lives--;
                livesText.setText(lives.toString());
                imageView.setImage(
                        new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + lives + ".png")));
            }
        }
    }

    public void checkWin() {
        if (wordText.getText().equals(guessedWordText.getText())) {
            gameOver = true;
            showConfirmation("You won!", "Start a new game?");
        }
    }

    public void saveGame() {
        GameState currGameState = new GameState();
        currGameState.setGameOver(gameOver);
        currGameState.setLives(lives);
        currGameState.setGuessedWord(guessedWordText.getText());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_GAME_FILE_NAME));

            oos.writeObject(currGameState);

            showMessage("Game saved!", "");
        } catch (IOException e) {
            showMessage("Error :(", "");
            throw new RuntimeException(e);
        }
    }

    public void loadGame() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_GAME_FILE_NAME));
            if (ois.readObject() instanceof GameState gs) {
                gameOver = gs.getGameOver();
                lives = gs.getLives();
                livesText.setText(lives.toString());
                guessedWordText.setText(gs.getGuessedWord());
                imageView.setImage(
                        new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + lives + ".png")));
            }
            showMessage("Game loaded!", "");
        } catch (IOException | ClassNotFoundException e) {
            showMessage("Error :(", "");
            throw new RuntimeException(e);
        }
    }

    public void showMessage(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showConfirmation(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (alert.showAndWait().get() == ButtonType.OK) {
            newGame();
        }
    }

    public void generateDocumentation(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("documentation-view.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Documentation");
        stage.setScene(new Scene(root, 600, 600));
        stage.show();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(){
        new Thread(() -> {
            try{
                bufferedWriter.write(username);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                Scanner scanner = new Scanner(System.in);
                while (socket.isConnected()){
                    String messageToSend = scanner.nextLine();

                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader,bufferedWriter);
            }
        }).start();
    }

    public void sendConnectedMessage(){
        try{
            bufferedWriter.write("connSend");
            bufferedWriter.newLine();
            bufferedWriter.flush();

        }catch (IOException e){
            closeEverything(socket, bufferedReader,bufferedWriter);
        }
    }

    private void processConnection() throws IOException {
        if (!isConn) {
            sendConnectedMessage();
            isConn = true;
        }
    }

    public void listenForMessage() {

        new Thread(() -> {
            String messageFromGroup;
            while (socket.isConnected()){
                try{
                    messageFromGroup = bufferedReader.readLine();

                    String temp;
                    switch (messageFromGroup) {
                        case "conn":
                            sendConnectedMessage();
                            break;

                        case "connSend":
                            processConnection();
                            break;

                        default:
                            System.out.println(messageFromGroup);
                            break;
                    }

                }catch (IOException e){
                    closeEverything(socket, bufferedReader,bufferedWriter);
                }
            }
        }).start();
    }
}