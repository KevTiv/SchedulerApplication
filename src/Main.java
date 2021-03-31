import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main function that runs the application.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle rb = ResourceBundle.getBundle("rb", Locale.getDefault());
        Parent root = FXMLLoader.load(getClass().getResource("/ScreenView/LogScreen.fxml"));
        primaryStage.setTitle(rb.getString("window"));
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    /**
     * @param args launches arguments.
     */
    public static void main(String[] args) {

        System.out.println(Locale.getDefault().getDisplayCountry());
        launch(args);
        System.out.println("Scheduler App is now closed.");
    }
}
