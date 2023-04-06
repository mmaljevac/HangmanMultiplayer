package hr.tvz.hangman.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameState implements Serializable {

    private Boolean gameOver;
    private Integer lives;
    private String guessedWord;

}
