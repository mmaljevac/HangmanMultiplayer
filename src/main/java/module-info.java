module hr.tvz.hangman {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires javafx.web;
    requires java.rmi;
    requires java.naming;


    opens hr.tvz.hangman to javafx.fxml;
    exports hr.tvz.hangman.rmi to java.rmi;
    exports hr.tvz.hangman;
}