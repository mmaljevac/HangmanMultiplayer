package hr.tvz.hangman.model;

import java.io.Serializable;

public class ClientData implements Serializable {
    private String playerName;
    private Integer port;

    public ClientData(String playerName, Integer port) {
        this.playerName = playerName;
        this.port = port;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
