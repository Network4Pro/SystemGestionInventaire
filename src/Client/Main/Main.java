package Client.Main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private double x=0;
    private double y=0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Client/Views/Login/LoginScene.fxml"));
        Scene scene = new Scene(root, 700, 500); 

            root.setOnMousePressed ((MouseEvent event) ->{
                x = event.getSceneX();
                y = event.getSceneY();
            });

            root.setOnMouseDragged ((MouseEvent event) ->{
                primaryStage.setX(event.getScreenX () - x);
                primaryStage.setY(event.getScreenY () - y);
                primaryStage.setOpacity(.8);
            });

            root.setOnMouseReleased ((@SuppressWarnings("unused") MouseEvent event) ->{
                primaryStage.setOpacity(1);
            });

        primaryStage.initStyle(StageStyle.UNDECORATED); 
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}