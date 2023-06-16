package hr.tvz.hangman.rmi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Properties;

public class RMIChatClientServer {

    private static final int RANDOM_PORT_HINT = 0;
    private static final int RMI_PORT = 1746;

    public static void main(String[] args) {

        Integer rmiPort = -1;

        try{
            rmiPort = Integer.parseInt(getValueFromJndiResource("rmi.port"));
        }catch (IOException | NamingException e){
            e.printStackTrace();
        }

        try {
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            ChatRemoteServiceImpl remoteService = new ChatRemoteServiceImpl();
            RemoteService skeleton = (RemoteService) UnicastRemoteObject.exportObject(remoteService, RANDOM_PORT_HINT);
            registry.rebind(RemoteService.REMOTE_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException  e) {
            e.printStackTrace();
        }

    }

    public static String getValueFromJndiResource(String key) throws IOException, NamingException {
        Hashtable<String, String> environment = new Hashtable<String, String>();

        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL, "file:/C:/Users/filip/Downloads/KnightFight/src/main/resources/hr/tvz/KF/");

        Context context = new InitialContext(environment);
        //context.rebind("John", new Person("John", "Smith", 35, 'm'));
        Object object = context.lookup("conf.properties");
        Properties props = new Properties();
        props.load(new FileReader(object.toString()));

        return props.getProperty(key);
        //rmiPort = Integer.parseInt(props.getProperty(key));
    }

}
