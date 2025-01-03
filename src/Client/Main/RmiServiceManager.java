package Client.Main;

import java.rmi.Naming;

import Server.RmiServeur.InventaireService;

public class RmiServiceManager {

    private static InventaireService inventaireService;

    public static InventaireService getInventaireService() {
        
        if (inventaireService == null) {
            try {
                inventaireService = (InventaireService) Naming.lookup("rmi://localhost/InventaireService");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inventaireService;
    }

    
}