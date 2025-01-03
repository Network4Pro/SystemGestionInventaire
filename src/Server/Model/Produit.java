package Server.Model;

import java.io.Serializable;

public class Produit implements Serializable {
    
	private int id;
	private String name;
	private String categorie;
	private float quantite;
	private float prix;
	private String image;
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return name;	}
	public void setName(String nom) { this.name = nom;	}
	public String getCategorie() {	return categorie; }
	public void setCategorie(String categorie) { this.categorie = categorie; }
	public float getQuantite() {return quantite; }
	public void setQuantite(float quantite) {	this.quantite = quantite; }
	public float getPrix() { return prix;	}
	public void setPrix(float prix) {	this.prix = prix; 	}
	public String getImage() { return image; }
	public void setImage(String image) { this.image = image; }
	
	public Produit() {}
	
	public Produit(String name, String categorie, float quantite, float prix, String image) {
		this.name = name;
		this.categorie = categorie;
		this.quantite = quantite;
		this.prix = prix;
		this.image = image;
	}

	public Produit(int id, String name, String categorie, float quantite, float prix, String image) {
		this.id = id;
		this.name = name;
		this.categorie = categorie;
		this.quantite = quantite;
		this.prix = prix;
		this.image = image;
	}

}
