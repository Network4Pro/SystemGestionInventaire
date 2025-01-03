package Server.RmiServeur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;

import Server.Config.DBConnection;
import Server.Dao.ProduitDaoImpl;
import Server.Model.Produit;
import Server.Utils.LoggerUtil;

public class InventaireServiceImpl extends UnicastRemoteObject implements InventaireService  {
    
	private static final long serialVersionUID = 1L;
	private final ProduitDaoImpl productDAO;
	private static final Logger logger = LoggerUtil.getLogger();
	
    public InventaireServiceImpl() throws RemoteException {
        super();
        productDAO = new ProduitDaoImpl();
    }
	
	@Override
	public boolean authenticate(String name, String modePass) throws RemoteException {
		logger.info("Tentative d'authentification pour l'utilisateur : " + name);
		String sql = "SELECT COUNT(*) AS user_count FROM Users WHERE name = ? AND MoDePass = ?";
		try(
				Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
		)
		{
			System.out.println("Requête SQL : " + sql);
			System.out.println("Paramètres : name=" + name + ", modePass=" + modePass);
			statement.setString(1, name);
	        statement.setString(2, modePass);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                int userCount = resultSet.getInt("user_count");
	                if (userCount > 0) {
	                    logger.info("Authentification réussie pour l'utilisateur : " + name);
	                    return true; 
	                } else {
	                    logger.warning("Authentification échouée pour l'utilisateur : " + name);
	                }
	            }
	        }
		}
		catch(Exception e) {
			logger.severe("Erreur lors de l'authentification de l'utilisateur " + name + ": " + e.getMessage());
			throw new RemoteException("Échec de l'authentification de l'utilisateur...", e);
		}
		return false;
	}

	@Override
	public Produit AddProduit(Produit product) throws RemoteException {
		logger.info("Tentative d'ajout du produit : " + product);
		Produit produit;
		try {
			produit = productDAO.save(product);
			logger.info("Produit ajouté avec succès : " + produit);
		} catch (Exception e) {
			logger.severe("Échec de l'ajout du produit : " + product + ", Erreur : " + e.getMessage());
			throw new RemoteException("Échec de l'ajout du produit...", e);
		}
		return produit;
	}

	@Override
	public Produit UpdateProduit(Produit product) throws RemoteException {
		logger.info("Tentative de mise à jour du produit : " + product);
		Produit produit;
		try {
            produit = productDAO.update(product);
            logger.info("Produit mis à jour avec succès : " + produit);
        } catch (Exception e) {
            logger.severe("Échec de la mise à jour du produit : " + product + ", Erreur : " + e.getMessage());
            throw new RemoteException("Échec de la mise à jour du produit", e);
        }
		return produit;
	}


	@Override
	public boolean DeleteProduit(int id) throws RemoteException {
		logger.info("Tentative de suppression du produit avec ID : " + id);
		boolean result = false;
		try {
			result = productDAO.remove(id);
			if (result) {
				logger.info("Produit supprimé avec succès avec ID : " + id);
			} else {
				logger.warning("Aucun produit trouvé à supprimer avec ID : " + id);
			}
		} catch (Exception e) {
			logger.severe("Échec de la suppression du produit avec ID : " + id + ", Erreur : " + e.getMessage());
			throw new RemoteException("Échec de la suppression du produit", e);
		}
		return result;
	}


	@Override
	public List<Produit> GetAllProduits() throws RemoteException {
		logger.info("Tentative de récupération de tous les produits");
        try {
            List<Produit> products = productDAO.getAll();
            logger.info("Nombre de produits récupérés : " + products.size());
            return products;
        } catch (Exception e) {
            logger.severe("Échec de la récupération des produits : " + e.getMessage());
            throw new RemoteException("Échec de la récupération des produits", e);
        }
	}


	@Override
	public List<Produit> FindProduits(String keyword) throws RemoteException {
		logger.info("Tentative de recherche des produits avec le mot-clé : " + keyword);
        try {
        	List<Produit> produits = productDAO.findProducts(keyword);
        	logger.info("Nombre de produits trouvés pour le mot-clé \"" + keyword + "\": " + produits.size());
            return produits;
        } catch (Exception e) {
            logger.severe("Échec de la recherche des produits : " + e.getMessage());
            throw new RemoteException("Échec de la recherche des produits", e);
        }
	}
	
}
