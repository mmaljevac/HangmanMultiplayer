package hr.tvz.hangman;

import hr.tvz.hangman.model.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.*;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HangmanController implements Initializable {
    private final Integer MAX_LIVES = 10;
    private final String SAVE_GAME_FILE_NAME = "hangmanSave.bin";
    private Boolean gameOver;
    private Integer lives;
    @FXML
    private Text wordText;
    @FXML
    private Text guessedWordText;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField letterField;
    @FXML
    //TODO to be replaced with images
    private Text livesText;
    @FXML
    private Button submitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        newGame();
    }

    public void newGame() {
        gameOver = false;
        lives = MAX_LIVES;
        livesText.setText(lives.toString());

        Image image = new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + MAX_LIVES + ".png"));
        imageView.setImage(image);


//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Input Dialog");
//        dialog.setHeaderText("Enter a word:");
//        dialog.setContentText("Word:");
//
//        Optional<String> wordDialog = dialog.showAndWait();
//        String word = wordDialog.orElse("");
        // TODO temporary word
        String word = "te st";

        wordText.setText(word.toUpperCase());

        String secretWord = "";
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                secretWord += " ";
            }
            else {
                secretWord += "*";
            }
        }
        guessedWordText.setText(secretWord);
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
        }
        else {
            if (lives > 0) {
                lives--;
                livesText.setText(lives.toString());
                Image image = new Image(getClass().getResourceAsStream("/hr/tvz/hangman/img/" + lives + ".png"));
                imageView.setImage(image);
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
}