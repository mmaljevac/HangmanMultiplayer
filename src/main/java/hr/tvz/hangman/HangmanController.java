package hr.tvz.hangman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HangmanController {
    private Integer numTries = 6;
    private boolean gameOver = false;
    private String word = "hangman";

    @FXML
    private ImageView imageView;
    @FXML
    private TextField letter;
    @FXML
    private Text numTriesText;
    @FXML
    private Button myButton;


    @FXML
    public void update() {
        if (gameOver) return;

        wrongLetter();

    }

    public void wrongLetter() {
        numTriesText.setText(numTries.toString());

        numTries--;

        //        File file = new File("./img/" + numTries + ".png");
//        Image image = new Image(file.toURI().toString());
//        System.out.println(image.getUrl());
//        System.out.println(imageView.getImage().getUrl());
//        imageView.setImage(image);
//        System.out.println(imageView.getImage().getUrl());
    }

}