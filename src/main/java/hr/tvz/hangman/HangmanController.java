package hr.tvz.hangman;

import hr.tvz.hangman.rmi.ChatMessage;
import hr.tvz.hangman.model.GameState;
import hr.tvz.hangman.rmi.RefreshChatMessagesThread;
import hr.tvz.hangman.rmi.RemoteService;
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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
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
    private Text letterLabel;
    @FXML
    private TextField letterField;
    @FXML
    private Button submitButton;
    @FXML
    private Text livesText;
    @FXML
    public TextArea chatArea;
    @FXML
    public TextField messageField;
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static String username;
    @FXML
    private Label usernameLabel;
    private static Boolean isConn = false;
    @FXML
    private TextField setWordField;
    @FXML
    private Label wordToGuessLabel;

    private RemoteService service;
    @FXML
    private Button sendMsgButton;


    public void initialize() {

        gameOver = false;
        lives = MAX_LIVES;
        livesText.setText(lives.toString());

        wordText.setVisible(false);

        letterLabel.setVisible(false);
        letterField.setVisible(false);
        submitButton.setVisible(false);

        imageView.setImage(new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + lives + ".png")));

        String word = "Waiting...";

        wordText.setText(word.toUpperCase());
        guessedWordText.setText(word.toString());

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Enter username:");

        Optional<String> usernameOptional = dialog.showAndWait();
        username = usernameOptional.orElse("");

        usernameLabel.setText(username);

        new Thread(() -> startClientThread()).start();

        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry("localhost", 1746);
            service = (RemoteService) registry.lookup(RemoteService.REMOTE_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

        new Thread(new RefreshChatMessagesThread(chatArea)).start();
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

    public void sendChatMessage() throws RemoteException {
        String chatMessageString = messageField.getText();
        ChatMessage newChatMessage = new ChatMessage(
                username,
                LocalDateTime.now(),
                chatMessageString);
        service.sendMessage(newChatMessage);
        messageField.setText("");
    }

    public void newGame() {
        try {
            bufferedWriter.write("newGame");
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

        gameOver = false;
        lives = MAX_LIVES;
        livesText.setText(lives.toString());

        imageView.setImage(new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + lives + ".png")));

        String word = "Waiting...";

        wordText.setText(word.toUpperCase());
        guessedWordText.setText(word.toString());

        setWordField.setVisible(true);
        wordToGuessLabel.setVisible(true);

        letterLabel.setVisible(false);
        letterField.setVisible(false);
        submitButton.setVisible(false);
    }

    public void checkLetter(String enteredLetter) {
        System.out.println(enteredLetter + " in checkLetter");
        if (enteredLetter.length() != 1) {
            showMessage("You can enter 1 letter only!", "");
            letterField.clear();
            return;
        }

        char letter = enteredLetter.toUpperCase().charAt(0);
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
            Platform.runLater(() -> showConfirmation("You won!", "Start a new game?"));
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
            bufferedWriter.write("loadGame");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

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
        } catch (IOException | ClassNotFoundException e) {
            showMessage("Error :(", "");
            throw new RuntimeException(e);
        }
    }

    public void showMessage(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
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
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }

    public void sendConnectedMessage() {
        try {
            bufferedWriter.write("connSend");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void handleConnection() throws IOException {
        if (!isConn) {
            isConn = true;
            sendConnectedMessage();
        }
    }

    private void handleEnteredWord() throws IOException {
        setWordField.setVisible(false);
        wordToGuessLabel.setVisible(false);

        var enteredWordTemp = bufferedReader.readLine();
        StringBuilder secretWord = new StringBuilder();
        for (int i = 0; i < enteredWordTemp.length(); i++) {
            if (enteredWordTemp.charAt(i) == ' ') {
                secretWord.append(" ");
            } else {
                secretWord.append("*");
            }
        }
        wordText.setText(enteredWordTemp.toUpperCase());
        guessedWordText.setText(secretWord.toString().toUpperCase());

        letterLabel.setVisible(true);
        letterField.setVisible(true);
        submitButton.setVisible(true);
    }

    private void handleEnteredLetter() throws IOException {
        var enteredLetterTemp = bufferedReader.readLine();
        if (enteredLetterTemp.isEmpty()) return;

        if (!gameOver) {
            checkLetter(enteredLetterTemp);
            checkWin();
        }

        if (lives == 0) {
            gameOver = true;
            Platform.runLater(() -> showConfirmation("You lost!", "Start a new game?"));
        }

        System.out.println(enteredLetterTemp + " in handleEnteredLetter()");

        letterField.clear();
    }

    private void handleNewGame() {
        gameOver = false;
        lives = MAX_LIVES;
        livesText.setText(lives.toString());

        imageView.setImage(new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + lives + ".png")));

        String word = "Waiting...";

        wordText.setText(word.toUpperCase());
        guessedWordText.setText(word.toString());

        setWordField.setVisible(true);
        wordToGuessLabel.setVisible(true);

        letterLabel.setVisible(false);
        letterField.setVisible(false);
        submitButton.setVisible(false);
    }

    private void handleLoadGame() {
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
        } catch (IOException | ClassNotFoundException e) {
            showMessage("Error :(", "");
            throw new RuntimeException(e);
        }
    }

    public void listenForMessage() {

        new Thread(() -> {
            String messageFromGroup;
            while (socket.isConnected()){
                try{
                    messageFromGroup = bufferedReader.readLine();

                    switch (messageFromGroup) {

                        case "conn":
                            sendConnectedMessage();
                            break;

                        case "connSend":
                            handleConnection();
                            break;

                        case "enteredWord":
                            handleEnteredWord();
                            break;

                        case "enteredLetter":
                            handleEnteredLetter();
                            break;

                        case "newGame":
                            handleNewGame();
                            break;

                        case "loadGame":
                            handleLoadGame();
                            break;

                        default:
                            System.out.println(messageFromGroup);
                            break;
                    }

                }catch (IOException e){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public void enterWord() {
        StringBuilder secretWord = new StringBuilder();
        for (int i = 0; i < setWordField.getText().length(); i++) {
            if (setWordField.getText().charAt(i) == ' ') {
                secretWord.append(" ");
            } else {
                secretWord.append("*");
            }
        }
        wordText.setText(setWordField.getText().toUpperCase());
        guessedWordText.setText(secretWord.toString().toUpperCase());

        try {
            bufferedWriter.write("enteredWord");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.write(setWordField.getText().toUpperCase());
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        setWordField.clear();

        letterLabel.setVisible(false);
        letterField.setVisible(false);
        submitButton.setVisible(false);
        setWordField.setVisible(false);
        wordToGuessLabel.setVisible(false);
    }

    public void enterLetter() {
        if (letterField.getText().isEmpty()) return;

        try {
            bufferedWriter.write("enteredLetter");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.write(letterField.getText().toUpperCase());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

        if (!gameOver) {
            checkLetter(letterField.getText().toString());
            checkWin();
        }

        System.out.println(letterField.getText() + " in enterLetter()");

        if (lives == 0) {
            gameOver = true;
            showConfirmation("You lost!", "Start a new game?");
        }
        letterField.clear();
    }
}