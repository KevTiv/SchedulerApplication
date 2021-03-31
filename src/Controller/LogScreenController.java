package Controller;

import Utils.ConnectionToDB;
import Utils.User;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LogScreenController implements Initializable {

    /**
     *LogScreenController a class for the log in screen.
     *
     * Lambda expressions are used to handle button and selection change action events throughout the class because it
     * makes the program easier to read and understand what the application is trying to implement.
     *
     * NOTE: Lambda expressions are anonymous methods and therefore can not have dedicated javadoc comments, so their justification
     * is mentioned wherever they appear in the class.
     */

    public AnchorPane anchorPane;
    public TextField username;
    public PasswordField password;
    public Button login;
    public Label errorMessage;
    public Label usernameLabel;
    public Label passwordLabel;
    public Label location;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /**
         * Initialize LogScreenController Object.
         * Connect to DB and fetch metadata in resource bundle language file properties
         */
        ResourceBundle resourceBundle = ResourceBundle.getBundle("rb", Locale.getDefault());
        ConnectionToDB connection = new ConnectionToDB();
        usernameLabel.setText(resourceBundle.getString("username"));
        passwordLabel.setText(resourceBundle.getString("password"));
        location.setText(Locale.getDefault().getDisplayCountry());
        login.setText(resourceBundle.getString("signin"));

        Logger log = Logger.getLogger("login_activity.txt");
        FileHandler file = null;
        try {
            file = new FileHandler("login_activity.txt", true);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        SimpleFormatter sf = new SimpleFormatter();
        file.setFormatter(sf);
        log.addHandler(file);
        log.setLevel(Level.INFO);

        login.setOnAction(actionEvent ->{
            /**
             *login.setOnAction lambda expression checks the entered username and password against the database to find if there is a valid match,
             * and opens the main screen only if there is a match.
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             *
             * UserName = Admin_Test
             * Password = PassWord
             */
            Timestamp time = new Timestamp(System.currentTimeMillis());
            String usernameText = username.getText();
            String passwordText = password.getText();
            /**
             * Fetch username in DB
             */
            String SQLUsernameQuery = "SELECT User_Name, Password FROM users";
            Boolean isCorrect = false;
            try (Statement pStatement = connection.getConnection().createStatement()){
                ResultSet result = pStatement.executeQuery(SQLUsernameQuery);
                while (result.next()){
                    String testUserName = result.getString("User_Name");
                    String testPassword = result.getString("Password");
                    /**
                     * Check if username and password are correct
                     * If correct open main screen
                     */
                    if (testUserName.equals(usernameText)){
                        if (testPassword.equals(passwordText)){
                            User user = new User(testUserName);
                            isCorrect = true;
                            Stage close = (Stage) login.getScene().getWindow();
                            close.close();
                            Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
                            Scene scene = new Scene(root);
                            Stage mainMenu = new Stage();
                            mainMenu.setScene(scene);
                            mainMenu.show();
                        }
                    }
                }
            }catch(SQLException | IOException error){
                errorMessage.setText(resourceBundle.getString("connectionerror"));
                System.out.println("Line 119. Initialize - logScreenController");
            }
            /**
             * Check if username and password are correct
             * write a log in status log in terminal
             */
            if (isCorrect) log.info(String.format("\nLogin attempted on %s: \nUsername entered: %s\nResult: Success", time, username.getText()));
            else log.info(String.format("\nLogin attempted on %s: \nUsername entered: %s\nResult: Failure", time, username.getText()));
            errorMessage.setText(resourceBundle.getString("error"));

        });
    }
}
