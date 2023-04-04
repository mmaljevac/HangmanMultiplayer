module hr.tvz.hangman {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;


    opens hr.tvz.hangman to javafx.fxml;
    exports hr.tvz.hangman;
}