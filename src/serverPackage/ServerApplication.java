package serverPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ServerApplication extends Application {

    private Server server = null;
    private PlayersListForServer playerListServer;

    public  void init() {
        playerListServer = new PlayersListForServer();
    }
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerGui.fxml"));
        try {
            Parent root = loader.load();
            primaryStage.initStyle(StageStyle.DECORATED);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
