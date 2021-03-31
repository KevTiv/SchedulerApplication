package Controller;

import Utils.ConnectionToDB;
import Utils.Country;
import Utils.FirstLevelDivision;
import Utils.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;


public class AddCustomerScreenController implements Initializable {
    /**
     * AddCustomerScreenController is a class for the add customer screen object.
     *      * Lambda expressions are used to handle button and selection change action events throughout the class because it
     *      * makes the program easier to read and understand what the application intend to do.
     *      *
     *      * NOTE: Lambda expressions are anonymous methods and therefore can not have dedicated javadoc comments, so their justification
     *      * is mentioned wherever they appear to be in use.
     */

    @FXML public Label nameLabel;
    @FXML public TextField nameText;
    @FXML public Label phoneLabel;
    @FXML public TextField phoneText;
    @FXML public Label addressLabel;
    @FXML public TextField addressText;
    @FXML public Label zipLabel;
    @FXML public TextField zipText;
    @FXML public Label countryLabel;
    @FXML public ComboBox<String> countryComboBox;
    @FXML public Label stateProvinceLabel;
    @FXML public ComboBox<String> stateProvinceComboBox;
    @FXML public Button saveCustomer;
    @FXML public Button cancelChanges;
    @FXML public Label addCustomerLabel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /**
         * Initialize AddCustomerScreen Object.
         * Connect to DB and fetch metadata in resource bundle language file properties
         */
        ConnectionToDB connection = new ConnectionToDB();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("rb", Locale.getDefault());
        addCustomerLabel.setText(resourceBundle.getString("addcustomer"));
        nameLabel.setText(resourceBundle.getString("name"));
        phoneLabel.setText(resourceBundle.getString("phone"));
        addressLabel.setText(resourceBundle.getString("address"));
        zipLabel.setText(resourceBundle.getString("zip"));
        countryLabel.setText(resourceBundle.getString("country"));
        stateProvinceLabel.setText(resourceBundle.getString("state"));
        saveCustomer.setText(resourceBundle.getString("save"));
        cancelChanges.setText(resourceBundle.getString("cancel"));

        /**
         * Fetch Countries in DB
         */
        String SQLCountryQuery = "SELECT * FROM countries";
        try {
            Statement getCountries = connection.getConnection().createStatement();
            ResultSet result = getCountries.executeQuery(SQLCountryQuery);
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            while (result.next()) {
                Country country = new Country(result.getString("Country"));
                countryNames.add(country.getCountry());
            }
            countryComboBox.setItems(countryNames);
        }catch (SQLException error) {
            error.printStackTrace();
            System.out.println("Line 90. Initialize - AddCustomerScreenController");
        }

        countryComboBox.setOnAction(actionEvent -> {
            /**
             * countryComboBox.setOnAction lambda expression populates the province (Division) combo box when a country is selected.
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */

            /**
             * Fetch Division in DB
             */
            String SQLDivisionQuery = "SELECT first_level_divisions.division_id, first_level_divisions.division, first_level_divisions.create_date, first_level_divisions.created_by, first_level_divisions.last_update, first_level_divisions.last_updated_by, first_level_divisions.COUNTRY_ID FROM first_level_divisions INNER JOIN countries ON first_level_divisions.COUNTRY_ID = countries.Country_ID\n" +
                    "WHERE countries.Country = '" + countryComboBox.getSelectionModel().getSelectedItem() + "'";
            try {
                Statement getDivision = connection.getConnection().createStatement();
                ResultSet result = getDivision.executeQuery(SQLDivisionQuery);
                ObservableList<String> statesProvinces = FXCollections.observableArrayList();
                while (result.next()) {
                    FirstLevelDivision firstLevelDivision = new FirstLevelDivision(result.getString("Division"));
                    statesProvinces.add(firstLevelDivision.getDivision());
                }
                stateProvinceComboBox.setItems(statesProvinces);
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("Line 116. Initialize - AddCustomerScreenController");
            }
        });

        saveCustomer.setOnAction(actionEvent ->{
            /**
             * saveCustomer.setOnAction lambda expression will save customer to the DB using the values entered into the form by the user.
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implements.
             */
            if(nameText.textProperty().isEmpty().get() || addressText.textProperty().isEmpty().get() || zipText.textProperty().isEmpty().get() || phoneText.textProperty().isEmpty().get() || stateProvinceComboBox.getSelectionModel().isEmpty()){}
            else{
                /**
                 * Insert new customer info in DB
                 */
                String SQLCustomerInsertQuery = String.format("INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Created_By, Last_Updated_By, Division_ID) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', " +
                        "(SELECT Division_ID FROM first_level_divisions WHERE Division = '%s'))", nameText.getText(), addressText.getText(), zipText.getText(), phoneText.getText(), User.getUsername(), User.getUsername(), stateProvinceComboBox.getSelectionModel().getSelectedItem());
                try{
                    Statement addNewCustomer = connection.getConnection().createStatement();
                    addNewCustomer.executeUpdate(SQLCustomerInsertQuery);
                }catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 138. Initialize - AddCustomerScreenController");
                }
                Stage close = (Stage) saveCustomer.getScene().getWindow();
                close.close();
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
                } catch (IOException error) {
                    error.printStackTrace();
                    System.out.println("Line 147. Initialize - AddCustomerScreenController");
                }
                Scene scene = new Scene(root);
                Stage mainMenu = new Stage();
                mainMenu.setScene(scene);
                mainMenu.show();
            }});

        cancelChanges.setOnAction(actionEvent -> {
            /**
             * cancelChanges.setOnAction lambda expression closes the form and returns to the main screen.
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            Stage close = (Stage) cancelChanges.getScene().getWindow();
            close.close();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
            } catch (IOException error) {
                error.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage mainMenu = new Stage();
            mainMenu.setScene(scene);
            mainMenu.show();
        });
    }




}
