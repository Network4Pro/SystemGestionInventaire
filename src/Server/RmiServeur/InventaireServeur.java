package Server.RmiServeur;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class InventaireServeur {

    public static void main(String[] args) {

        InventaireService inventaireService;
        try {
            inventaireService = new InventaireServiceImpl();       	
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://localhost/InventaireService", inventaireService);
            System.out.println("Service d'inventaire prêt....");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Server exception: " + e.getMessage());
            System.exit(1);
        }
    }

}
