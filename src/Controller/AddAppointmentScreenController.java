package Controller;

import Utils.Appointment;
import Utils.ConnectionToDB;
import Utils.User;
import javafx.application.Application;
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


public class AddAppointmentScreenController implements Initializable {

    /**
     * AddAppointmentScreenController is a class for the add appointment screen oject.
     * Lambda expressions are used to handle button and selection change action events throughout the class because it
     * makes the program easier to read and understand what the application intend to do.
     *
     * NOTE: Lambda expressions are anonymous methods and therefore can not have dedicated javadoc comments, so their justification
     * is mentioned wherever they appear to be in use.
     */


    @FXML public Label newAppointmentLabel;
    @FXML public Label titleLabel;
    @FXML public TextField titleText;
    @FXML public Label descriptionLabel;
    @FXML public TextField descriptionText;
    @FXML public Label locationLabel;
    @FXML public TextField locationText;
    @FXML public Label typeLabel;
    @FXML public TextField typeText;
    @FXML public Label contactLabel;
    @FXML public ComboBox<String> contactComboBox;
    @FXML public Button save;
    @FXML public Button cancel;
    @FXML public Label startLabel;
    @FXML public Label endLabel;
    @FXML public Label customerIDLabel;
    @FXML public ComboBox<String> customerIDComboBox;
    @FXML public TextField appointmentID;

    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> startHourPicker;
    @FXML private ComboBox<String> startMinutePicker;

    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> endHourPicker;
    @FXML private ComboBox<String> endMinutePicker;

    Appointment appointment;


    @FXML public Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /**
         * Initialize AddAppointmentScreen Object.
         * Connect to DB and fetch metadata in resource bundle language file properties
         */
        ConnectionToDB connection = new ConnectionToDB();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("rb", Locale.getDefault());
        newAppointmentLabel.setText(resourceBundle.getString("newappointment"));
        titleLabel.setText(resourceBundle.getString("title"));
        descriptionLabel.setText("Description");
        locationLabel.setText(resourceBundle.getString("locale"));
        typeLabel.setText("Type");
        customerIDLabel.setText(resourceBundle.getString("customerid"));
        startLabel.setText(resourceBundle.getString("start"));
        endLabel.setText(resourceBundle.getString("end"));

        //appointment.setDateFields(appointment.getLocalStart(), startDatePicker, startHourPicker, startMinutePicker);
        //appointment.setDateFields(appointment.getLocalEnd(), endDatePicker, endHourPicker, endMinutePicker);

        contactLabel.setText(resourceBundle.getString("contact"));
        save.setText(resourceBundle.getString("save"));
        cancel.setText(resourceBundle.getString("cancel"));
        /**
         * Fetch AUTO_INCREMENT in DB
         */
        String SQLAppointmentIDQuery = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'WJ07qY8'" +
                " AND TABLE_NAME = 'appointments'";
        try {
            Statement statement = connection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(SQLAppointmentIDQuery);
            resultSet.next();
            appointmentID.setText(Integer.toString(resultSet.getInt("AUTO_INCREMENT")));
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Line 103. initialize - AddAppointmentScreenController");
        }




        /**
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

        ObservableList<String> contactsOL = FXCollections.observableArrayList();
        /**
         * Fetch Contact_Name in DB
         */
        String SQLContactQuery = "SELECT Contact_Name FROM contacts";
        try {
            Statement pStatement = connection.getConnection().createStatement();
            ResultSet result = pStatement.executeQuery(SQLContactQuery);
            while (result.next()) {
                contactsOL.add(result.getString("Contact_Name"));
            }
        } catch (SQLException error) {
            error.printStackTrace();
            System.out.println("Line 116. initialize - AddAppointmentScreenController");
        }
        contactComboBox.setItems(contactsOL);


        ObservableList<String> customersOL = FXCollections.observableArrayList();
        /**
         * Fetch Customer_ID in DB
         */
        String SQLCustomerQuery = "SELECT Customer_ID FROM customers";
        try {
            Statement pStatement = connection.getConnection().createStatement();
            ResultSet result = pStatement.executeQuery(SQLCustomerQuery);
            while (result.next()) {
                customersOL.add(Integer.toString(result.getInt("Customer_ID")));
            }
        } catch (SQLException error) {
            error.printStackTrace();
            System.out.println("Line 131. initialize - AddAppointmentScreenController");
        }
        customerIDComboBox.setItems(customersOL);


        save.setOnAction(actionEvent -> {
            /**
             * save.setOnAction lambda expression saves a new appointment in the database in UTC using the values entered by the user.
             * Lambda expressions are used for the handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implements.
             */

            try {

                /**
                 * Fetch startDate in DB
                 */
                LocalDateTime startDate = LocalDateTime.of(startDatePicker.getValue().getYear(),startDatePicker.getValue().getMonthValue(),
                        startDatePicker.getValue().getDayOfMonth(),Integer.parseInt(startHourPicker.getValue()),Integer.parseInt(startMinutePicker.getValue()));
                System.out.println("startDate: "+startDate);
                ZonedDateTime startDateLocal = ZonedDateTime.of(startDate, ZoneId.systemDefault());
                System.out.println("\nstartDateLocal: "+startDate);
                ZonedDateTime startToUTC = startDateLocal.withZoneSameInstant(ZoneOffset.UTC);
                System.out.println("\nstartToUTC: "+startToUTC);
                String start = startToUTC.getYear() + "-" + startToUTC.getMonthValue() + "-" + startToUTC.getDayOfMonth() + " " +
                        startToUTC.getHour() + ":" + startToUTC.getMinute();

                LocalDateTime endDate = LocalDateTime.of(endDatePicker.getValue().getYear(),endDatePicker.getValue().getMonthValue(),
                        endDatePicker.getValue().getDayOfMonth(),Integer.parseInt(endHourPicker.getValue()),Integer.parseInt(endMinutePicker.getValue()));
                ZonedDateTime endDateLocal = ZonedDateTime.of(endDate, ZoneId.systemDefault());
                ZonedDateTime endToUTC = endDateLocal.withZoneSameInstant(ZoneOffset.UTC);
                String end = endToUTC.getYear() + "-" + endToUTC.getMonthValue() + "-" + endToUTC.getDayOfMonth() + " " + endToUTC.getHour() + ":" + endToUTC.getMinute();

                /**
                 * Fetch customerSchedule in DB
                 */
                String customerScheduleQuery = "SELECT Appointment_ID FROM appointments JOIN customers USING (Customer_ID) WHERE Customer_ID = " + customerIDComboBox.getSelectionModel().getSelectedItem() +
                        " AND Start BETWEEN '" + start + "' AND '" + end + "' OR End BETWEEN '" + start + "' AND '" + end + "'";
                System.out.println(customerScheduleQuery);
                Statement statement = connection.getConnection().createStatement();
                ResultSet rs = statement.executeQuery(customerScheduleQuery);

                if ((startToUTC.getHour() < 13 && endToUTC.getHour() > 3) || (endToUTC.getDayOfMonth() > endDateLocal.getDayOfMonth() || endToUTC.getMonthValue() > endDateLocal.getMonthValue())) {
                    System.out.println((startToUTC.getHour()) + " " + (endToUTC.getHour()));
                    errorLabel.setText(resourceBundle.getString("invalidtime"));
                } else if (rs.next()) {
                    errorLabel.setText(resourceBundle.getString("conflictingappointment"));
                } else {
                    /**
                     * Insert Appointment in DB
                     */
                    errorLabel.setText(resourceBundle.getString("appointmentsaved"));
                    String insertAppointmentQuery = String.format("INSERT INTO appointments (Title, Description, Location, Type, Start, End, Created_By, Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', (SELECT User_ID FROM users WHERE User_Name = '%s'), " +
                                    "(SELECT Contact_ID FROM contacts WHERE Contact_Name = '%s'))", titleText.getText(), descriptionText.getText(), locationText.getText(), typeText.getText(), start, end,
                            User.getUsername(), User.getUsername(), customerIDComboBox.getSelectionModel().getSelectedItem(), User.getUsername(), contactComboBox.getSelectionModel().getSelectedItem());
                    statement.executeUpdate(insertAppointmentQuery);
                    Stage close = (Stage) newAppointmentLabel.getScene().getWindow();
                    close.close();
                    Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
                    Scene scene = new Scene(root);
                    Stage mainMenu = new Stage();
                    mainMenu.setScene(scene);
                    mainMenu.show();
                }
            } catch (SQLException | IOException exception) {
                exception.printStackTrace();
                System.out.println("Line 186. initialize - AddAppointmentScreenController");
            }
        });


        cancel.setOnAction(actionEvent -> {
            /**
             * cancel.setOnAction lambda expression closes the form and opens the main form.
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implements.
             */
            Stage close = (Stage) newAppointmentLabel.getScene().getWindow();
            close.close();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/MainScreen.fxml"));
                Scene scene = new Scene(root);
                Stage mainMenu = new Stage();
                mainMenu.setScene(scene);
                mainMenu.show();
            } catch (IOException exception) {
                exception.printStackTrace();
                System.out.println("Line 208. initialize - AddAppointmentScreenController");
            }
        });
    }






}
