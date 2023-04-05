package hr.tvz.hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
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
    private Text secretWordText;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField letter;
    @FXML
    //TODO to be replaced with images
    private Text livesText;
    @FXML
    private Button submitButton;

    @FXML
    public void enterLetter(ActionEvent event) {
        if (lives <= 0) {
            gameOver = true;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game over");
            alert.setHeaderText("Game over!");
            alert.setContentText("Better luck next time!");
            alert.showAndWait();
            return;
        }

        lives--;
        livesText.setText(lives.toString());

        //        File file = new File("./img/" + numTries + ".png");
//        Image image = new Image(file.toURI().toString());
//        System.out.println(image.getUrl());
//        System.out.println(imageView.getImage().getUrl());
//        imageView.setImage(image);
//        System.out.println(imageView.getImage().getUrl());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameOver = false;
        lives = MAX_LIVES;
        livesText.setText(lives.toString());

        //TODO temporary word
        wordText.setText("tes t");
        String word = wordText.getText();

        String secretWord = "";
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                secretWord += " ";
            }
            else {
                secretWord += "*";
            }
        }
        secretWordText.setText(secretWord);
    }
}