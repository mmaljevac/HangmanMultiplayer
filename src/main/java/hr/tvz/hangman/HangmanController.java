package hr.tvz.hangman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HangmanController {

    @FXML
    private ImageView imageView;

    @FXML
    private Button myButton;

    @FXML
    public void changeImage() {

    }

}