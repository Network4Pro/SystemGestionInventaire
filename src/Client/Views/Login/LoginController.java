package Client.Views.Login;

import java.io.IOException;
import java.rmi.RemoteException;

import Client.Main.RmiServiceManager;
import Client.Utils.UIUtils;
import Client.Views.Admin.GestionController;
import Server.RmiServeur.InventaireService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController {


    @FXML
    private Button Login;

    @FXML
    private PasswordField Password;

    @FXML
    private TextField UserName;

    
    private double x=0;
    private double y=0;

    @FXML
    void OnBtnClick(ActionEvent event) {
        try {
            InventaireService inventaireService = RmiServiceManager.getInventaireService();

            if (!inventaireService.authenticate(UserName.getText(), Password.getText())) {
                UIUtils.showAlert("Échec de l'authentification", "Authentification échouée !!!...", Alert.AlertType.ERROR);
            }
            else{
                Parent root;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/Views/Admin/Gestion.fxml"));
                    root = loader.load();
                    GestionController gController = loader.getController();
                    gController.setUsername(UserName.getText());
                    Stage stage = new Stage();

                    root.setOnMousePressed((MouseEvent mouseEvent) -> {
                        x = mouseEvent.getSceneX();
                        y = mouseEvent.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent mouseEvent) -> {
                        stage.setX(mouseEvent.getScreenX() - x);
                        stage.setY(mouseEvent.getScreenY() - y);
                        stage.setOpacity(.8);
                    });

                     root.setOnMouseReleased((@SuppressWarnings("unused") MouseEvent mouseEvent) -> {
                        stage.setOpacity(1);
                    });

                    stage.initStyle(StageStyle.UNDECORATED); 
                    Scene scene = new Scene(root);
                    String css = this.getClass().getResource("/Client/Views/Application.css").toExternalForm();
                    scene.getStylesheets().add(css);
                    stage.setScene(scene);
                    stage.show();
                    Stage currentStage = (Stage) Login.getScene().getWindow();
                    currentStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }

    @FXML
    public void initialize() {
        try {
            // Connexion au service RMI
            InventaireService inventaireService = RmiServiceManager.getInventaireService();
            if (inventaireService != null) {
                Login.setDisable(false); 
            } else {
                UIUtils.showAlert("Erreur de Connexion", "Échec de la connexion au serveur RMI !!!", Alert.AlertType.ERROR); 
                Login.setDisable(true);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Erreur de Connexion", "Échec de la connexion au serveur RMI !!!", Alert.AlertType.ERROR);
            Login.setDisable(true);
            e.printStackTrace();
        }
    }

}
