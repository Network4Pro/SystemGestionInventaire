package Server.RmiServeur;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Server.Model.Produit;

public interface InventaireService extends Remote {
    
    boolean authenticate(String Name, String MoDePass) throws RemoteException;

    Produit AddProduit(Produit produit) throws RemoteException;

    Produit UpdateProduit(Produit produit) throws RemoteException;

    boolean DeleteProduit(int id) throws RemoteException;

    List<Produit> GetAllProduits() throws RemoteException;
    
    List<Produit> FindProduits(String keyword) throws RemoteException;
    
}
