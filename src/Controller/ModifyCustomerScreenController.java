package Controller;

import Utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

public class ModifyCustomerScreenController implements Initializable {
    /**
     * ModifyCustomerScreenController is a class for the modify customer screen.
     *
     * Lambda expressions are used to handle button and selection change action events throughout the class because it
     * makes the program easier to read and understand what the application is trying to implement.
     * NOTE: Lambda expressions are anonymous methods and therefore can not have dedicated javadoc comments, so their justification
     * is mentioned wherever they appear in the class.
     */

    @FXML public Label modifyCustomerLabel;
    @FXML public Label nameLabel;
    @FXML public Label phoneLabel;
    @FXML public Label addressLabel;
    @FXML public Label zipLabel;
    @FXML public Label countryLabel;
    @FXML public Label stateProvinceLabel;
    @FXML public Label customerID;

    @FXML public TextField nameText;
    @FXML public TextField phoneText;
    @FXML public TextField addressText;
    @FXML public TextField customerIDTextField;
    @FXML public TextField zipText;

    @FXML public ComboBox<String> countryComboBox;
    @FXML public ComboBox<String> stateProvinceComboBox;

    @FXML public Button saveCustomer;
    @FXML public Button cancelChanges;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /**
         * Initialize ModifyAppointmentScreen Object.
         * Connect to DB and fetch metadata in resource bundle language file properties
         */

        ConnectionToDB connection = new ConnectionToDB();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("rb", Locale.getDefault());
        modifyCustomerLabel.setText(resourceBundle.getString("modifycustomer"));
        nameLabel.setText(resourceBundle.getString("name"));
        phoneLabel.setText(resourceBundle.getString("phone"));
        addressLabel.setText(resourceBundle.getString("address"));
        zipLabel.setText(resourceBundle.getString("zip"));
        countryLabel.setText(resourceBundle.getString("country"));
        stateProvinceLabel.setText(resourceBundle.getString("state"));
        saveCustomer.setText(resourceBundle.getString("save"));
        cancelChanges.setText(resourceBundle.getString("cancel"));
        customerID.setText(resourceBundle.getString("customerid"));
        customerIDTextField.setText(Integer.toString(PassCustomer.getCustomer().getCustomerID()));

        /**
         * Fetch customer to modify in DB
         */
        String SQLCustomerToModifyQuery = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Create_Date, customers.Created_By, " +
                "customers.Last_Update, customers.Last_Updated_By, first_level_divisions.Division, countries.Country FROM customers JOIN first_level_divisions USING (Division_ID) JOIN countries USING (Country_ID) " +
                "WHERE Customer_ID = " + PassCustomer.getCustomer().getCustomerID();
        try {
            Statement getCustomerToModify = connection.getConnection().createStatement();
            ResultSet result = getCustomerToModify.executeQuery(SQLCustomerToModifyQuery);
            result.next();
            nameText.setText(result.getString("Customer_Name"));
            addressText.setText(result.getString("Address"));
            zipText.setText(result.getString("Postal_Code"));
            phoneText.setText(result.getString("Phone"));
            countryComboBox.getSelectionModel().select(result.getString("Country"));
            stateProvinceComboBox.getSelectionModel().select(result.getString("Division"));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Line 89. Initialize - ModifyCustomerScreenController");
        }

        /**
         * Fetch country in DB for country combo box
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
            System.out.println("Line 110. Initialize - ModifyCustomerScreenController");
        }

        /**
         * Populate Division combo box
         */
        EventHandler<ActionEvent> populateDivision = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                /**
                 * Fetch division data
                 */
                String SQLStateProvinceQuery = "SELECT first_level_divisions.division_id, first_level_divisions.division, first_level_divisions.create_date, first_level_divisions.created_by, first_level_divisions.last_update, first_level_divisions.last_updated_by, first_level_divisions.COUNTRY_ID FROM first_level_divisions INNER JOIN countries ON first_level_divisions.COUNTRY_ID = countries.Country_ID\n" +
                        "WHERE countries.Country = '" + countryComboBox.getSelectionModel().getSelectedItem() + "'";
                try {
                    Statement getDivision = connection.getConnection().createStatement();
                    ResultSet result = getDivision.executeQuery(SQLStateProvinceQuery);
                    ObservableList<String> divisonOL = FXCollections.observableArrayList();
                    while (result.next()) {
                        FirstLevelDivision division = new FirstLevelDivision(result.getString("Division"));
                        divisonOL.add(division.getDivision());
                    }
                    stateProvinceComboBox.setItems(divisonOL);
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 138. Initialize - ModifyCustomerScreenController");
                }
            }
        };
        countryComboBox.setOnAction(populateDivision);
        countryComboBox.fireEvent(new ActionEvent());

        saveCustomer.setOnAction(actionEvent ->{
            /**
             * saveCustomer.setOnAction lambda expression is used to save the customer information that was entered in modifyCustomerScreen.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            if(nameText.textProperty().isEmpty().get() || addressText.textProperty().isEmpty().get() || zipText.textProperty().isEmpty().get() || phoneText.textProperty().isEmpty().get() || stateProvinceComboBox.getSelectionModel().isEmpty()){}
            else{
                /**
                 * update customer depending on which division is selected.
                 */
                String SQLInsertQuery = String.format("UPDATE customers SET Customer_Name = '%s', Address = '%s', Postal_Code = '%s', Phone = '%s', Last_Update = CURRENT_TIMESTAMP, Last_Updated_By = '%s', Division_ID = " +
                        "(SELECT Division_ID FROM first_level_divisions WHERE Division = '%s') WHERE Customer_ID = %d", nameText.getText(), addressText.getText(), zipText.getText(), phoneText.getText(), User.getUsername(), stateProvinceComboBox.getSelectionModel().getSelectedItem(), PassCustomer.getCustomer().getCustomerID());
                try{
                    Statement updateFoundCustomer = connection.getConnection().createStatement();
                    updateFoundCustomer.executeUpdate(SQLInsertQuery);
                }catch (SQLException exception) {
                    exception.printStackTrace();
                    System.out.println("Line 168. Initialize - ModifyCustomerScreenController");
                }
                try {
                    Stage close = (Stage) modifyCustomerLabel.getScene().getWindow();
                    close.close();
                    Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
                    Scene scene = new Scene(root);
                    Stage mainMenu = new Stage();
                    mainMenu.setScene(scene);
                    mainMenu.show();
                } catch (IOException error) {
                    error.printStackTrace();
                    System.out.println("Line 180. Initialize - ModifyCustomerScreenController");
                }
            }});

        cancelChanges.setOnAction(actionEvent -> {
            /**
             * cancelChanges.setOnAction lambda expression will close the current screen and return to the main screen.
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
                System.out.println("Line 195. Initialize - ModifyCustomerScreenController");
            }
            Scene scene = new Scene(root);
            Stage mainMenu = new Stage();
            mainMenu.setScene(scene);
            mainMenu.show();
        });
    }
}
