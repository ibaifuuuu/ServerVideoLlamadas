package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface interfazRMI extends Remote{
    String getAPI(String urlParaVisitar) throws RemoteException ;
}
