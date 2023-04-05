package hr.tvz.hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HangmanController implements Initializable {
    private final Integer MAX_LIVES = 6;
    private Integer lives;
    private boolean gameOver;
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

    @FXML
    public void enterLetter(ActionEvent event) {
        if (!gameOver) {
            checkLetter();
            checkWin();
            letterField.clear();
        }
        else {
            System.out.println(lives);
            //TODO start a new game
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game over");
            alert.setHeaderText("Game over!");
            alert.setContentText("Start a new game?");
            alert.showAndWait();
            return;
        }
    }

    public void checkLetter() {

        if (letterField.getText().length() != 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("You can enter 1 letter only!");
            alert.showAndWait();
            letterField.clear();
            return;
        }

        Character letter = letterField.getText().toUpperCase().charAt(0);
        String word = wordText.getText();

        if (guessedWordText.getText().indexOf(letter) != -1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("You already guessed this letter!");
            alert.showAndWait();
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
            lives--;
            livesText.setText(lives.toString());
            if (lives <= 0) {
                gameOver = true;
            }
        }

    }

    public void checkWin() {
        if (wordText.getText().equals(guessedWordText.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game over");
            alert.setHeaderText("You guessed the word!");
            alert.setContentText("Well played!");
            alert.showAndWait();
            gameOver = true;
        }
    }

    public void newGame() {
        gameOver = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameOver = false;
        lives = MAX_LIVES;
        livesText.setText(lives.toString());

//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Input Dialog");
//        dialog.setHeaderText("Enter a word:");
//        dialog.setContentText("Word:");
//
//        Optional<String> wordDialog = dialog.showAndWait();
//        String word = wordDialog.orElse("");
        String word = "tes t";

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
}