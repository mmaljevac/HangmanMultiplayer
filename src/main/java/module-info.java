module hr.tvz.hangman {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires javafx.web;


    opens hr.tvz.hangman to javafx.fxml;
    exports hr.tvz.hangman;
}