package Server.Dao;

import java.util.List;

import Server.Model.Produit;

public interface ProduitDao {

    Produit  save(Produit produit);
	
	boolean remove(int id);
	
	Produit update(Produit produit);
	
	List<Produit> findProducts(String Keyword);
	
	List<Produit> getAll();
	
}
