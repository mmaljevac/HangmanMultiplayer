package hr.tvz.hangman.rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteService extends Remote {
    public static final String REMOTE_OBJECT_NAME = "hr.tvz.rmi.service";

    void sendMessage(ChatMessage message) throws RemoteException;

    List<ChatMessage> getAllChatMessages() throws RemoteException;
}

