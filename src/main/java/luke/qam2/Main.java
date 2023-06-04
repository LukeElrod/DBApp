package luke.qam2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    /**
     * Entry method for the rest of the applications functionality
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        HelperFunctions.usersLanguage = Locale.getDefault().getLanguage();
        Parent root = FXMLLoader.load(getClass().getResource("LoginForm.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("QAM2 Task 1");

        stage.setScene(scene);
        stage.show();

        HelperFunctions.openConnection();

    }

    public static void main(String[] args) {
        launch();
    }
}
