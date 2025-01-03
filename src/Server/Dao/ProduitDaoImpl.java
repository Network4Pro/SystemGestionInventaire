package Server.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Server.Config.DBConnection;
import Server.Model.Produit;

public class ProduitDaoImpl implements ProduitDao {
 @Override
    public Produit save(Produit produit) {
    	
        String sql = "INSERT INTO Produits (name, categorie, quantite, prix,image) VALUES (?, ?, ?, ?,?)";

        try ( Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            c.setAutoCommit(false);
            ps.setString(1, produit.getName());
            ps.setString(2, produit.getCategorie());
            ps.setFloat(3, produit.getQuantite());
            ps.setFloat(4, produit.getPrix());
            ps.setString(5, produit.getImage());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        produit.setId(rs.getInt(1)); 
                        c.commit();
                        return produit;
                    }
                }
            } 
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    
    @Override
    public boolean remove(int id) {
        String sql = "DELETE FROM Produits WHERE id = ?;";
        try (
        		Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } 

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    

    @Override
    public Produit update(Produit produit) {
    	
        String sql = "UPDATE Produits SET name = ?, categorie = ?, quantite = ?, prix = ?, image = ? WHERE id = ?;";
        
        try (
        	Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)
        ) {
            ps.setString(1, produit.getName());
            ps.setString(2, produit.getCategorie());
            ps.setFloat(3, produit.getQuantite());
            ps.setFloat(4, produit.getPrix());
            ps.setString(5, produit.getImage());
            ps.setInt(6, produit.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return produit;
            } 
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    
    @Override
    public List<Produit> getAll() {
    	
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM Produits;";

        try (
        	Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("categorie"),
                    rs.getFloat("quantite"),
                    rs.getFloat("prix"),
                    rs.getString("image")
                );
                produits.add(produit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return produits;
    }

    
    @Override
    public List<Produit> findProducts(String keyword) {
    	
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM Produits WHERE name LIKE ? OR categorie LIKE ?;";

        try (
        	Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)
        ) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("categorie"),
                        rs.getFloat("quantite"),
                        rs.getFloat("prix"),
                        rs.getString("image")
                    );
                    produits.add(produit);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return produits;
    }
    
}
