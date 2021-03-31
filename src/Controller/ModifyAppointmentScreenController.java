package Controller;

import Utils.Appointment;
import Utils.PassAppointment;
import Utils.ConnectionToDB;
import Utils.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;


public class ModifyAppointmentScreenController implements Initializable {
    /**
     * ModifyAppointmentScreenController is a class for the modify appointment screen.
     *
     * Lambda expressions are used to handle button and selection change action events throughout the class because it
     * makes the program easier to read and understand what the application is trying to implement.
     *
     * NOTE: Lambda expressions are anonymous methods and therefore can not have dedicated javadoc comments, so their justification
     * is mentioned wherever they appear in the class.
     */


    @FXML public Label updateAppointmentLabel;
    @FXML public Label titleLabel;
    @FXML public Label descriptionLabel;
    @FXML public Label locationLabel;
    @FXML public Label typeLabel;
    @FXML public Label startLabel;
    @FXML public Label contactLabel;
    @FXML public Label endLabel;
    @FXML public Label customerIDLabel;
    @FXML public Label errorLabel;
    @FXML public ComboBox<String> contactComboBox;
    @FXML public ComboBox<String> customerIDComboBox;
    @FXML public Button save;
    @FXML public Button cancel;
    @FXML public TextField appointmentID;
    @FXML public TextField titleText;
    @FXML public TextField descriptionText;
    @FXML public TextField locationText;
    @FXML public TextField typeText;

    @FXML public TextField startHourText;

    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> startHourPicker;
    @FXML private ComboBox<String> startMinutePicker;

    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> endHourPicker;
    @FXML private ComboBox<String> endMinutePicker;



    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /**
         * Initialize ModifyAppointmentScreen Object.
         * Connect to DB and fetch metadata in resource bundle language file properties
         */
        ConnectionToDB connection = new ConnectionToDB();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("rb", Locale.getDefault());
        updateAppointmentLabel.setText(resourceBundle.getString("modifyappointment"));
        titleLabel.setText(resourceBundle.getString("title"));
        descriptionLabel.setText("Description");
        locationLabel.setText(resourceBundle.getString("locale"));
        typeLabel.setText("Type");
        customerIDLabel.setText(resourceBundle.getString("customerid"));
        startLabel.setText(resourceBundle.getString("start"));
        endLabel.setText(resourceBundle.getString("end"));
        contactLabel.setText(resourceBundle.getString("contact"));
        save.setText(resourceBundle.getString("save"));
        cancel.setText(resourceBundle.getString("cancel"));
        appointmentID.setText(Integer.toString(PassAppointment.getAppointment().getAppointmentID()));

        titleText.setText(PassAppointment.getAppointment().getTitle());
        descriptionText.setText(PassAppointment.getAppointment().getDescription());
        locationText.setText(PassAppointment.getAppointment().getLocation());
        typeText.setText(PassAppointment.getAppointment().getType());
        LocalDateTime appointmentStart = LocalDateTime.of(PassAppointment.getAppointment().getStartYear(), PassAppointment.getAppointment().getStartMonth(),
                PassAppointment.getAppointment().getStartDay(), PassAppointment.getAppointment().getStartHour(), PassAppointment.getAppointment().getStartMinute());
        ZonedDateTime appointmentStartZoned = ZonedDateTime.of(appointmentStart, ZoneOffset.UTC);
        ZonedDateTime appointmentStartToLocal = appointmentStartZoned.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime appointmentEnd = LocalDateTime.of(PassAppointment.getAppointment().getEndYear(), PassAppointment.getAppointment().getEndMonth(),
                PassAppointment.getAppointment().getEndDay(), PassAppointment.getAppointment().getEndHour(), PassAppointment.getAppointment().getEndMinute());
        ZonedDateTime appointmentEndZoned = ZonedDateTime.of(appointmentEnd, ZoneOffset.UTC);
        ZonedDateTime appointmentEndToLocal = appointmentEndZoned.withZoneSameInstant(ZoneId.systemDefault());
        PassAppointment.getAppointment().setStartTime(appointmentStart);
        PassAppointment.getAppointment().setEndTime(appointmentEnd);

        startDatePicker.setValue(PassAppointment.getAppointment().getLocalStart().toLocalDate());

        startHourPicker.setValue(String.valueOf(PassAppointment.getAppointment().getStartHour()));

        startMinutePicker.setValue(String.valueOf(PassAppointment.getAppointment().getStartMinute()));

        endDatePicker.setValue(PassAppointment.getAppointment().getLocalEnd().toLocalDate());

        endHourPicker.setValue(String.valueOf(PassAppointment.getAppointment().getEndHour()));

        endMinutePicker.setValue(String.valueOf(PassAppointment.getAppointment().getEndMinute()));

        /**
         * Populate contact combo box with contact name in DB
         */
        ObservableList<String> contacts = FXCollections.observableArrayList();
        String contactQuery = "SELECT Contact_Name FROM contacts";
        /**
         * Fetch contact name of customer with appointment in DB
         */
        String SQLSelectedContactQuery = "SELECT Contact_Name FROM appointments JOIN contacts USING (Contact_ID) WHERE appointments.Contact_ID = " + PassAppointment.getAppointment().getContactID();
        try {
            Statement statement = connection.getConnection().createStatement();
            ResultSet result = statement.executeQuery(contactQuery);
            while (result.next()) {
                contacts.add(result.getString("Contact_Name"));
            }
            contactComboBox.setItems(contacts);
            ResultSet nextResult = statement.executeQuery(SQLSelectedContactQuery);
            nextResult.next();
            contactComboBox.getSelectionModel().select(nextResult.getString("Contact_Name"));
        }catch (SQLException error) {
            error.printStackTrace();
            System.out.println("Line 138. Initialize - ModifyAppointmentScreenController");
        }

        /**
         * Populate Date & time combo box
         * fills the date fields with all necessary options to choose a start/end date and time
         */
        ComboBox[] hourPickers = {startHourPicker, endHourPicker};
        for (ComboBox hourPicker : hourPickers) {
            final ObservableList<String> options = hourPicker.getItems();
            for (int h = 1; h <= 24; h++) {
                options.add(String.format("%02d", h));
            }
        }
        ComboBox[] minutePickers = {startMinutePicker, endMinutePicker};
        for (ComboBox minutePicker : minutePickers) {
            final ObservableList<String> options = minutePicker.getItems();
            for (int m = 0; m <= 60; m+=10) {
                options.add(String.format("%02d", m));
            }
            minutePicker.getSelectionModel().select("00");
        }
        DatePicker[] datePickers = {startDatePicker, endDatePicker};
        for (DatePicker datePicker : datePickers) {
            datePicker.setValue(LocalDate.now());
        }

        /**
         * Populate customer ID combo box with contact name in DB
         */
        ObservableList<String> customers = FXCollections.observableArrayList();
        /**
         * Fetch customer ID in DB
         */
        String SQLCustomerIDQuery = "SELECT Customer_ID FROM customers";
        /**
         * Fetch customer ID with appt in DB
         */
        String selectedCustomerIDWithAptQuery = "SELECT Customer_ID FROM appointments JOIN customers USING (Customer_ID) WHERE appointments.Customer_ID = " + PassAppointment.getAppointment().getCustomerID();
        try {
            Statement statement = connection.getConnection().createStatement();
            ResultSet result = statement.executeQuery(SQLCustomerIDQuery);
            while (result.next()) {
                customers.add(Integer.toString(result.getInt("Customer_ID")));
            }
            customerIDComboBox.setItems(customers);
            ResultSet nextResult = statement.executeQuery(selectedCustomerIDWithAptQuery);
            nextResult.next();
            customerIDComboBox.getSelectionModel().select(Integer.toString(nextResult.getInt("Customer_ID")));
        }catch (SQLException error) {
            error.printStackTrace();
            System.out.println("Line 165. Initialize - ModifyAppointmentScreenController");
        }

        save.setOnAction(actionEvent -> {
            /**
             * save.setOnAction lambda expression saves the entered appointment information after checking if the time entered isn't out of business hours.
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            try {

                LocalDateTime startDate = LocalDateTime.of(startDatePicker.getValue().getYear(),startDatePicker.getValue().getMonthValue(),
                        startDatePicker.getValue().getDayOfMonth(),Integer.parseInt(startHourPicker.getValue()),Integer.parseInt(startMinutePicker.getValue()));

                ZonedDateTime startDateLocal = ZonedDateTime.of(startDate, ZoneId.systemDefault());
                ZonedDateTime startToUTC = startDateLocal.withZoneSameInstant(ZoneOffset.UTC);
                String start = startToUTC.getYear() + "-" + startToUTC.getMonthValue() + "-" + startToUTC.getDayOfMonth() + " " + startToUTC.getHour() + ":" + startToUTC.getMinute();

                LocalDateTime endDate = LocalDateTime.of(endDatePicker.getValue().getYear(),endDatePicker.getValue().getMonthValue(),
                        endDatePicker.getValue().getDayOfMonth(),Integer.parseInt(endHourPicker.getValue()),Integer.parseInt(endMinutePicker.getValue()));
                ZonedDateTime endDateLocal = ZonedDateTime.of(endDate, ZoneId.systemDefault());
                ZonedDateTime endToUTC = endDateLocal.withZoneSameInstant(ZoneOffset.UTC);
                String end = endToUTC.getYear() + "-" + endToUTC.getMonthValue() + "-" + endToUTC.getDayOfMonth() + " " + endToUTC.getHour() + ":" + endToUTC.getMinute();

                /**
                 * Fetch appointment ID with correct hours in DB
                 */
                String SQLCustomerScheduleQuery = "SELECT Appointment_ID FROM appointments JOIN customers USING (Customer_ID) WHERE Customer_ID = (SELECT Customer_ID FROM customers WHERE Customer_Name" +
                        " = '" + customerIDComboBox.getSelectionModel().getSelectedItem() +
                        "') AND Start BETWEEN '" + start + "' AND '" + end + "' OR End BETWEEN '" + start + "' AND '" + end + "' AND Appointment_ID != " + PassAppointment.getAppointment().getAppointmentID();
                Statement statement = connection.getConnection().createStatement();
                ResultSet result = statement.executeQuery(SQLCustomerScheduleQuery);

                if((startToUTC.getHour() < 13 && endToUTC.getHour() > 3) || (endToUTC.getDayOfMonth() > endDateLocal.getDayOfMonth() || endToUTC.getMonthValue() > endDateLocal.getMonthValue())) {
                    /**
                     * If hours are incorrect display errorLabel.
                     */

                    errorLabel.setText(resourceBundle.getString("invalidtime"));
                    System.out.println("("+(startToUTC.getHour())+") && ("+(endToUTC.getHour() > 3)+") || ("+(endToUTC.getDayOfMonth() > endDateLocal.getDayOfMonth())+") || ("+(endToUTC.getMonthValue() > endDateLocal.getMonthValue())+")");
                }
                else if (result.next()){
                    /**
                     * If hours conflict display errorLabel
                     */
                    errorLabel.setText(resourceBundle.getString("conflictingappointment"));
                    System.out.println("("+(startToUTC.getHour())+") && ("+(endToUTC.getHour() > 3)+") || ("+(endToUTC.getDayOfMonth() > endDateLocal.getDayOfMonth())+") || ("+(endToUTC.getMonthValue() > endDateLocal.getMonthValue())+")");
                }
                else {

                    /**
                     * Insert appointment in DB
                     */
                    errorLabel.setText(resourceBundle.getString("appointmentsaved"));
                    String SQLInsertAppointmentQuery = String.format("UPDATE appointments SET Title = '%s', Description = '%s', Location = '%s', Type = '%s', Start = '%s', End = '%s', Last_Update = CURRENT_TIMESTAMP," +
                                    "Customer_ID = '%s', User_ID = (SELECT User_ID FROM users WHERE User_Name = '%s'), " +
                                    "Contact_ID = (SELECT Contact_ID FROM contacts WHERE Contact_Name = '%s') WHERE Appointment_ID = %d", titleText.getText(), descriptionText.getText(), locationText.getText(), typeText.getText(), start, end,
                            customerIDComboBox.getSelectionModel().getSelectedItem(), User.getUsername(), contactComboBox.getSelectionModel().getSelectedItem(), PassAppointment.getAppointment().getAppointmentID());
                    statement.executeUpdate(SQLInsertAppointmentQuery);

                    Stage close = (Stage) updateAppointmentLabel.getScene().getWindow();
                    close.close();
                    Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
                    Scene scene = new Scene(root);
                    Stage mainMenu = new Stage();
                    mainMenu.setScene(scene);
                    mainMenu.show();
                }
            } catch (SQLException | IOException error) {
                error.printStackTrace();
                System.out.println("Line 236. Initialize - ModifyAppointmentScreenController");
            }
        });

        cancel.setOnAction(actionEvent -> {
            /**
             * cancel.setOnAction lambda expression closes the current window and returns to the main screen.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            try {
                Stage close = (Stage) updateAppointmentLabel.getScene().getWindow();
                close.close();
                Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
                Scene scene = new Scene(root);
                Stage mainMenu = new Stage();
                mainMenu.setScene(scene);
                mainMenu.show();
            } catch (IOException error) {
                error.printStackTrace();
                System.out.println("Line 257. Initialize - ModifyAppointmentScreenController");
            }
        });
    }

}
