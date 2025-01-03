package Client.Views.Admin;

import Server.Model.Produit;
import Server.RmiServeur.InventaireService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Label;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Client.Main.RmiServiceManager;
import Client.Utils.UIUtils;

import org.apache.poi.ss.usermodel.*;


public class GestionController {

    private List<Produit> produits;

    private InventaireService inventaireService;

    private String URL_Dossier_Images = "src/resources/Images_Produits/";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM");

     @FXML
    private ImageView close;

    @FXML
    void HandClose(MouseEvent event) {
        System.exit(0);
    }
  
    @FXML
    private TableView<Produit> listProduits;

    @FXML
    private TableColumn<Produit, Integer> idColumn;

    @FXML
    private TableColumn<Produit, String> nameColumn;

    @FXML
    private TableColumn<Produit, String> categorieColumn;

    @FXML
    private TableColumn<Produit, Float> quantiteColumn;

    @FXML
    private TableColumn<Produit, Float> prixColumn;

    

     @FXML
    private Button Ajouter;

    @FXML
    private Button Annuler;

    @FXML
    private Button Export;

    @FXML
    private Button Modifier;

    @FXML
    private Button Supprimer;

    @FXML
    private Label nom;

 
     // Méthode pour définir le nom d'utilisateur
     public void setUsername(String username) {
        nom.setText(username);
    }

    @FXML
    private Label nbTotal;

    @FXML
    private TextField txPrix;

    @FXML
    private TextField txQuantite;

    @FXML
    private TextField txcategrie;

    @FXML
    private TextField txname;

    @FXML
    private Button logout;

    @FXML
    void getIteam(MouseEvent event) {
        Produit selectedProduct = listProduits.getSelectionModel().getSelectedItem();
        if(listProduits.getItems().size() > 0 && selectedProduct != null ){
            txname.setText(selectedProduct.getName());
            txcategrie.setText(selectedProduct.getCategorie());
            txQuantite.setText(String.valueOf(selectedProduct.getQuantite()));
            txPrix.setText(String.valueOf(selectedProduct.getPrix()));
            String imagePath = URL_Dossier_Images + selectedProduct.getImage();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageProduit.setImage(image);
            } else {
                UIUtils.showAlert("Erreur", "L'image du produit n'a pas été trouvée.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void AnnulerHand(ActionEvent event) {
        ClearFields();
    }

    @FXML
    void SupprimerHand(ActionEvent event) {

        Produit selectedProduct = listProduits.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            UIUtils.showAlert("Sélection requise", "Veuillez sélectionner un produit à supprimer.", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmation = UIUtils.showConfirmation("Confirmation de suppression", "Êtes-vous sûr de vouloir supprimer le produit : " + selectedProduct.getName() + " ?");
        if (confirmation) {
            try {
                boolean success = inventaireService.DeleteProduit(selectedProduct.getId());
                if (success) {
                    File ImageFile = new File(URL_Dossier_Images + selectedProduct.getImage());
                    if (ImageFile.exists()) {   ImageFile.delete();  }
                    ChargerTableView();
                    UIUtils.showAlert("Suppression réussie", "Le produit a été supprimé avec succès.", Alert.AlertType.INFORMATION);
                } else {
                    UIUtils.showAlert("Erreur", "Échec de la suppression du produit. Veuillez réessayer.", Alert.AlertType.ERROR);
                }
            } catch (RemoteException e) {
                handleRemoteException(e, "Erreur lors de la suppression du produit.");
            }
        }
    }

    @FXML
    void AjouterHand(ActionEvent event) {

            if (!areFieldsValid()) {
                UIUtils.showAlert("Erreur", "Veuillez remplir tous les champs avant de Ajouter..", Alert.AlertType.ERROR);
                return;
            }

            float quantite = 0, prix = 0;
            try {
                quantite = Float.parseFloat(txQuantite.getText());
                prix = Float.parseFloat(txPrix.getText());
            } catch (NumberFormatException e) {
                UIUtils.showAlert("Erreur de format", "Veuillez entrer des valeurs numériques valides pour la quantité et le prix.", Alert.AlertType.ERROR);
                return;  
            }

           
            Image image = imageProduit.getImage();


            if (image == null) {
                UIUtils.showAlert("Erreur", "Veuillez sélectionner une image.", Alert.AlertType.ERROR);
                return;
            }

            UUID uuid = UUID.randomUUID();
          
            String extention =image.getUrl().substring(image.getUrl().lastIndexOf(".") + 1);
            String imageName = dateFormat.format(new Date()) + "_" + nom.getText() + "_" + uuid.toString() + "."+extention;
            File imageFile = new File(URL_Dossier_Images + imageName);
            try{
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, extention, imageFile);
            } catch (IOException e) {
                UIUtils.showAlert("Erreur", "Erreur lors de l'enregistrement de l'image.", Alert.AlertType.ERROR);
                return;
            }

            Produit produitAdd = new Produit(txname.getText(), txcategrie.getText(),quantite, prix,imageName);
            
            try{
                Produit produit = inventaireService.AddProduit(produitAdd);
                if (produit != null) {
                    UIUtils.showAlert("Ajout réussi", "Produit ajouté avec succès !", Alert.AlertType.INFORMATION);
                    ChargerTableView();
                } else {
                    UIUtils.showAlert("Erreur", "Échec de l'ajout du produit. Veuillez réessayer.", Alert.AlertType.ERROR);
                }
            }
            catch (RemoteException e) {
                handleRemoteException(e, "Erreur lors de l'ajout du produit.");
            }
    }

    @FXML
    void ModifierHand(ActionEvent event) {
        Produit selectedProduct = listProduits.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            UIUtils.showAlert("Erreur", "Veuillez sélectionner un produit à modifier.", Alert.AlertType.ERROR);
            return;
        }

        if (!areFieldsValid()) {
            UIUtils.showAlert("Erreur", "Veuillez remplir tous les champs avant la modification.", Alert.AlertType.ERROR);
            return;
        }

        float quantite = 0, prix = 0;
        try {
            quantite = Float.parseFloat(txQuantite.getText());
            prix = Float.parseFloat(txPrix.getText());
        } catch (NumberFormatException e) {
            UIUtils.showAlert("Erreur de format", "Veuillez entrer des valeurs numériques valides pour la quantité et le prix.", Alert.AlertType.ERROR);
            return;
        }

        
        Image image = imageProduit.getImage();
            if (image == null) {
                UIUtils.showAlert("Erreur", "Veuillez sélectionner une image.", Alert.AlertType.ERROR);
                return;
            }

        UUID uuid = UUID.randomUUID();
        String imageName = dateFormat.format(new Date()) + "_" + nom + "_" + uuid.toString() + ".jpg";
        File imageFile = new File(URL_Dossier_Images + imageName);
        try{
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufferedImage, "jpg", imageFile);
        } catch (IOException e) {
            UIUtils.showAlert("Erreur", "Erreur lors de l'enregistrement de l'image.", Alert.AlertType.ERROR);
            return;
        }

        File AncienImageFile = new File(URL_Dossier_Images + selectedProduct.getImage());
        if (AncienImageFile.exists()) {   AncienImageFile.delete();  }

        Produit produitUpdate = new Produit(
            selectedProduct.getId(),
            txname.getText(),
            txcategrie.getText(),
            quantite,
            prix,
            imageName
        );
    
            try {
                Produit produitModifie = inventaireService.UpdateProduit(produitUpdate);
                if (produitModifie != null) {
                    UIUtils.showAlert("Modification réussie", "Produit modifié avec succès !", Alert.AlertType.INFORMATION);
                    ChargerTableView();  // Recharger la table après la modification
                } else {
                    UIUtils.showAlert("Erreur", "Échec de la modification du produit. Veuillez réessayer.", Alert.AlertType.ERROR);
                }
            } catch (RemoteException e) {
                handleRemoteException(e, "Erreur lors de la modification du produit.");
            }
    }

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        ChargerTableView();

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            searchHand(newValue); 
        });
    }

    // Méthode pour vérifier si tous les champs sont remplis.
    private boolean areFieldsValid() {
        return !txname.getText().isEmpty() && !txcategrie.getText().isEmpty() 
                && !txQuantite.getText().isEmpty() && !txPrix.getText().isEmpty();
    }

    // Charger et afficher une liste de produits dans un TableView
    private void ChargerTableView(){
        try {
            inventaireService = RmiServiceManager.getInventaireService();
            produits = inventaireService.GetAllProduits();
            nbTotal.setText( String.valueOf(produits.size()));
            if (idColumn != null) {
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
                quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
                prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
            }
            ObservableList<Produit> observableList = FXCollections.observableArrayList(produits);
            listProduits.setItems(observableList);
            ClearFields();

        } catch (RemoteException e) {
            UIUtils.showAlert("Erreur lors du chargement des produits", 
            "Une erreur est survenue lors du chargement des produits...", 
            Alert.AlertType.ERROR);
        }
    }
   
    // Gestion des exceptions RemoteException
    private void handleRemoteException(RemoteException e, String message) {
        UIUtils.showAlert("Erreur de communication", message, Alert.AlertType.ERROR);
        e.printStackTrace();  // Optionnel : pour le debug en développement
    }
   
    // CLear les fileds
    private void ClearFields(){
        TextField[] fields = {txname, txcategrie, txQuantite, txPrix};
        for (TextField field : fields) {   field.clear();  }
        listProduits.getSelectionModel().clearSelection();
        imageProduit.setImage(null);
    }


    
    private double x=0;
    private double y=0;

    @FXML
    void logoutHand(ActionEvent event) {
        if (UIUtils.showConfirmation("Confirmation de Déconnexion", "Êtes-vous sûr de vouloir vous déconnecter ?")) {
            try {
                ((Stage) logout.getScene().getWindow()).close();
                Parent root = FXMLLoader.load(getClass().getResource("/Client/Views/Login/LoginScene.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root, 700, 500);
                setupWindowDragging(root, stage);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    private void setupWindowDragging(Parent root, Stage stage) {
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
            stage.setOpacity(0.8);
        });

        root.setOnMouseReleased(event -> stage.setOpacity(1));
    }



    @FXML
    void exportExcel(ActionEvent event) {
        if (produits == null || produits.isEmpty()) {
            UIUtils.showAlert("Erreur", "La liste des produits est vide ou nulle.", Alert.AlertType.WARNING);
            return;
        }
    
        // Create a file chooser to select the location and filename for saving
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setTitle("Enregistrer le fichier Excel");
    
        // Show the file chooser and get the selected file
        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Produits");
    
            String[] columnHeaders = {"ID", "Nom", "Catégorie", "Quantité", "Prix"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
            }
    
            int rowNum = 1;
            for (Produit produit : produits) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(produit.getId());
                row.createCell(1).setCellValue(produit.getName());
                row.createCell(2).setCellValue(produit.getCategorie());
                row.createCell(3).setCellValue(produit.getQuantite());
                row.createCell(4).setCellValue(produit.getPrix());
            }
    
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }
    
            // Save the workbook to the selected file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                UIUtils.showAlert("Succès", "Fichier Excel créé avec succès à l'emplacement : " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                UIUtils.showAlert("Erreur", "Erreur lors de la création du fichier Excel : " + e.getMessage(), Alert.AlertType.ERROR);
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    UIUtils.showAlert("Erreur", "Erreur lors de la fermeture du fichier Excel : " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            UIUtils.showAlert("Annulation", "Aucun emplacement sélectionné pour enregistrer le fichier.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private Button import_image;


    @FXML
    private ImageView imageProduit;


    @FXML
    void importHand(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg","*.png","*.jpeg"));
            fileChooser.setTitle("Sélectionner une image");
            File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
                Image image = new Image(file.toURI().toString());
                imageProduit.setImage(image);
            } else {
                UIUtils.showAlert("Annulation", "Aucun fichier sélectionné.", Alert.AlertType.INFORMATION);
            }
    }

    @FXML
    private TextField search;

        @FXML
    void searchHand(String keyword) {
        try {
            if (keyword.isEmpty()) {
                // If search text is empty, reload all products
                ChargerTableView();
            } else {
                // Perform search with the entered keyword
                inventaireService = RmiServiceManager.getInventaireService();
                List<Produit> produits = inventaireService.FindProduits(keyword);
                nbTotal.setText(String.valueOf(produits.size())); // Update total count
                ObservableList<Produit> observableList = FXCollections.observableArrayList(produits);
                listProduits.setItems(observableList); // Update table with search results
            }
        } catch (RemoteException e) {
            UIUtils.showAlert("Erreur lors du chargement des produits", 
                "Une erreur est survenue lors du chargement des produits...", 
                Alert.AlertType.ERROR);
        }
    }

}
