package Controller;

import Utils.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.Date;

public class MainScreenController implements Initializable {
    /**
     * MainScreenController is a class for the main screen of this application.
     *
     * Lambda expressions are used to handle button and selection change action events throughout the class because it
     * makes the program easier to read and understand what the application is trying to implement.
     *
     * NOTE: Lambda expressions are anonymous methods and therefore can not have dedicated javadoc comments, so their justification
     * is mentioned wherever they are in use in the class.
     *
     * Main screen is also house to the report feature of the scheduler App.
     */

    @FXML public Button exit;
    @FXML public TableView<Customer> allCustomersList;
    @FXML public TableColumn<Customer, String> allCustomersName;
    @FXML public TableColumn<Customer, String> allCustomersAddress;
    @FXML public TableColumn<Customer, String> allCustomersPostalCode;
    @FXML public TableColumn<Customer, String> allCustomersPhone;
    @FXML public TableColumn<Customer, String> allCustomersCreateDate;
    @FXML public TableColumn<Customer, String> allCustomersCreatedBy;
    @FXML public TableColumn<Customer, String> allCustomersLastUpdate;
    @FXML public TableColumn<Customer, String> allCustomersLastUpdatedBy;
    @FXML public TableColumn<Customer, String> allCustomersDivision;
    @FXML public TableColumn<Customer,String> allCustomersCustomerIDColumn;
    @FXML public TableView<Appointment> customerAppointments;
    @FXML public TableColumn<Appointment, Number> appointmentIDColumn;
    @FXML public TableColumn<Appointment, String> titleColumn;
    @FXML public TableColumn<Appointment, String> descriptionColumn;
    @FXML public TableColumn<Appointment, String> locationColumn;
    @FXML public TableColumn<Appointment, String> typeColumn;
    @FXML public TableColumn<Appointment, String> startColumn;
    @FXML public TableColumn<Appointment, String> endColumn;
    @FXML public TableColumn<Appointment, String> createDateColumn;
    @FXML public TableColumn<Appointment, String> createdByColumn;
    @FXML public TableColumn<Appointment, String> lastUpdateColumn;
    @FXML public TableColumn<Appointment, String> lastUpdatedByColumn;
    @FXML public TableColumn<Appointment, String> customerIDColumn;
    @FXML public TableColumn<Appointment, String> userColumn;
    @FXML public TableColumn<Appointment, String> contactColumn;
    @FXML public Button newCustomer;
    @FXML public Button editCustomer;
    @FXML public Button deleteCustomer;
    @FXML public Button newAppointment;
    @FXML public Button editAppointment;
    @FXML public Button deleteAppointment;
    @FXML public TextArea userInterfaceMessages;
    @FXML public RadioButton monthRadio;
    @FXML public RadioButton weekRadio;
    @FXML public ComboBox<String> monthWeekComboBox;
    @FXML public Label yearLabel;
    @FXML public Button previousYear;
    @FXML public Button nextYear;
    @FXML public Button customerReport;
    @FXML public Button contactReport;
    @FXML public Button userReport;
    @FXML public Label customerLabel;
    @FXML public Label appointmentLabel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /**
         * Initialize MainScreen Object.
         * Connect to DB and fetch metadata in resource bundle language file properties
         */
        ResourceBundle resourceBundle = ResourceBundle.getBundle("rb", Locale.getDefault());
        ConnectionToDB connection = new ConnectionToDB();
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        LocalDateTime localDateTime = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
        ObservableList<String> monthsList = FXCollections.observableArrayList();
        ObservableList<String> weeksList = FXCollections.observableArrayList();
        ToggleGroup monthOrWeek = new ToggleGroup();
        monthRadio.setToggleGroup(monthOrWeek);
        weekRadio.setToggleGroup(monthOrWeek);
        yearLabel.setText(Integer.toString(calendar.get(Calendar.YEAR)));
        customerLabel.setText(resourceBundle.getString("allcustomers"));
        allCustomersName.setText(resourceBundle.getString("name"));
        allCustomersAddress.setText(resourceBundle.getString("address"));
        allCustomersPostalCode.setText(resourceBundle.getString("zip"));
        allCustomersPhone.setText(resourceBundle.getString("phone"));
        allCustomersCreateDate.setText(resourceBundle.getString("createdate"));
        allCustomersCreatedBy.setText(resourceBundle.getString("createdby"));
        allCustomersLastUpdate.setText(resourceBundle.getString("lastupdate"));
        allCustomersLastUpdatedBy.setText(resourceBundle.getString("lastupdatedby"));
        allCustomersDivision.setText(resourceBundle.getString("division"));
        newCustomer.setText(resourceBundle.getString("newcustomer"));
        editCustomer.setText(resourceBundle.getString("modifycustomer"));
        deleteCustomer.setText(resourceBundle.getString("deletecustomer"));
        customerReport.setText(resourceBundle.getString("customerreport"));
        contactReport.setText(resourceBundle.getString("contactreport"));
        userReport.setText(resourceBundle.getString("userreportbutton"));
        appointmentLabel.setText(resourceBundle.getString("appointments"));
        monthRadio.setText(resourceBundle.getString("viewbymonth"));
        weekRadio.setText(resourceBundle.getString("viewbyweek"));
        previousYear.setText(resourceBundle.getString("previous"));
        nextYear.setText(resourceBundle.getString("next"));
        appointmentIDColumn.setText(resourceBundle.getString("appointmentid"));
        titleColumn.setText(resourceBundle.getString("title"));
        locationColumn.setText(resourceBundle.getString("locale"));
        startColumn.setText(resourceBundle.getString("start"));
        endColumn.setText(resourceBundle.getString("end"));
        createDateColumn.setText(resourceBundle.getString("createdate"));
        createdByColumn.setText(resourceBundle.getString("createdby"));
        lastUpdateColumn.setText(resourceBundle.getString("lastupdate"));
        lastUpdatedByColumn.setText(resourceBundle.getString("lastupdatedby"));
        customerIDColumn.setText(resourceBundle.getString("customerid"));
        userColumn.setText(resourceBundle.getString("user"));
        newAppointment.setText(resourceBundle.getString("newappointment"));
        editAppointment.setText(resourceBundle.getString("modifyappointment"));
        deleteAppointment.setText(resourceBundle.getString("deleteappointment"));
        exit.setText(resourceBundle.getString("exit"));
        allCustomersCustomerIDColumn.setText(resourceBundle.getString("customerid"));


        /**
         * Fetch appointments that are within 15 minutes
         */
        /**String userAppointmentsQuery = "SELECT Appointment_ID, Start FROM appointments JOIN users USING (User_ID) WHERE User_Name = '" + User.getUsername() +
                "' AND TIMEDIFF(TIME(Start), TIME(NOW())) BETWEEN '00:00' AND '00:15' AND DATE(Start) = DATE(NOW())"; */

        String userAppointmentsQuery = "SELECT Appointment_ID, Start FROM appointments JOIN users USING (User_ID) WHERE User_Name = '" + User.getUsername() +
                "' AND (Start BETWEEN ? AND ?)";
        /**
         * Get time and date from user system in order to fetch appointments in DB that may be
         * within 15 minutes of user current time.
         */
        ZoneId zoneID = ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now(zoneID);
        //System.out.println(now);
        ZonedDateTime nowOfZDT = now.withZoneSameInstant(ZoneId.of("UTC"));
        //System.out.println(nowOfZDT);
        ZonedDateTime nowOfZDT15 = nowOfZDT.plusMinutes(15);

        Timestamp currentTimestamp = Timestamp.valueOf(nowOfZDT.toLocalDateTime());
        Timestamp currentPlus15Timestamp = Timestamp.valueOf(nowOfZDT15.toLocalDateTime());

        try {
            PreparedStatement pStatement = connection.getConnection().prepareStatement(userAppointmentsQuery);

            pStatement.setString(1,String.valueOf(currentTimestamp));
            pStatement.setString(2,String.valueOf(currentPlus15Timestamp));

            ResultSet result = pStatement.executeQuery();

            /**
             * find appointments and display data found DB
             */
            if(result.next()) {
                userInterfaceMessages.setText(resourceBundle.getString("appointmentcoming"));
                do {
                    //get info about appointments
                    int appointmentID = result.getInt("Appointment_ID");
                    LocalDateTime utc = LocalDateTime.of(result.getDate("Start").toLocalDate(), result.getTime("Start").toLocalTime());
                    ZonedDateTime zdt = ZonedDateTime.of(utc, ZoneOffset.UTC);
                    ZonedDateTime local = zdt.withZoneSameInstant(ZoneId.systemDefault());
                    String startTime = local.format(DateTimeFormatter.ofPattern("HH:mm"));
                    String startDate = local.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    //Display info
                    String newUIAppointentText = "\n" + resourceBundle.getString("thisappointmentid") +
                            appointmentID + "\n" + resourceBundle.getString("start") + ": " +
                            startTime + " " + resourceBundle.getString("on") + startDate;
                    userInterfaceMessages.appendText(newUIAppointentText);

                    }while (result.next());
            }
            /**
             * if nothing is found display userInterfaceMessages = noappointmentcoming
             */
            else {
                userInterfaceMessages.setText(resourceBundle.getString("noappointmentcoming"));
            }
        } catch (SQLException error) {
            error.printStackTrace();
            System.out.println("Line 178. Initialize - MainScreenController");
        }

        /**
         * Fetch for customer info into a list by binding each Customer object value
         * to its respective column's cell value factory.
         */
        try {
            allCustomersList.getItems().setAll(Customer.getAllCustomers());
        } catch (SQLException error) {
            error.printStackTrace();
            System.out.println("Line 189. Initialize - MainScreenController");
        }
        /**
         * Fetch for customer name info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custName) {
                return new SimpleStringProperty(custName.getValue().getCustomerName());
            }
        });
        /**
         * Fetch for customer ID info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersCustomerIDColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custID) {
                return new SimpleStringProperty(Integer.toString(custID.getValue().getCustomerID()));
            }
        });
        /**
         * Fetch for customer address info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersAddress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custAddress) {
                return new SimpleStringProperty(custAddress.getValue().getAddress());
            }
        });
        /**
         * Fetch for customer Postal Code (ZIP) info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersPostalCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custPostalCode) {
                return new SimpleStringProperty(custPostalCode.getValue().getPostalCode());
            }
        });
        /**
         * Fetch for customer phone info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersPhone.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custPhone) {
                return new SimpleStringProperty(custPhone.getValue().getPhone());
            }
        });
        /**
         * Fetch for customer create date info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersCreateDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custCreateDate) {
                return new SimpleStringProperty(custCreateDate.getValue().getCreateDate());
            }
        });
        /**
         * Fetch for customer create by (user that created the cust info) info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersCreatedBy.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custCreatedBy) {
                return new SimpleStringProperty(custCreatedBy.getValue().getCreatedBy());
            }
        });
        /**
         * Fetch for customer last update (date of last changed customer info) info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersLastUpdate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custLastUpdate) {
                return new SimpleStringProperty(custLastUpdate.getValue().getLastUpdate());
            }
        });
        /**
         * Fetch for customer last updated by (user who was last to change customer info) info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersLastUpdatedBy.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custLastUpdatedBy) {
                return new SimpleStringProperty(custLastUpdatedBy.getValue().getLastUpdatedBy());
            }
        });
        /**
         * Fetch for customer division info into a list by binding each Customer name value
         * to its respective column's cell value factory.
         */
        allCustomersDivision.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> custDivision) {
                /**
                 * Fetch for division info
                 */
                String getDivisionQuery = String.format("SELECT Division FROM first_level_divisions WHERE Division_ID = '%s'", custDivision.getValue().getDivisionID());
                try {
                    PreparedStatement getDivision = connection.getConnection().prepareStatement(getDivisionQuery);
                    ResultSet result = getDivision.executeQuery();
                    result.next();
                    return new SimpleStringProperty(result.getString("Division"));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 289. Initialize - MainScreenController");
                }
                return null;
            }
        });
        /**
         * Sets up the appointment list by month by binding each Appointment object value to its respective column's cell value factory.
         */
        try {
            customerAppointments.getItems().setAll(Appointment.getAllAppointmentsForMonth(localDateTime.getMonthValue(), localDateTime.getYear()));
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Line 301. Initialize - MainScreenController");
        }

        //Set up month array list
        ArrayList<String> months = new ArrayList<>();
        int monthCount;
        for (monthCount = 0; monthCount < 12; monthCount++){
            months.add(resourceBundle.getString(Integer.toString(monthCount + 1)));
        }
        monthsList.setAll(months);
        monthWeekComboBox.setItems(monthsList);

        monthRadio.setOnAction(actionEvent ->{
            /**
             * monthRadio.setOnAction lambda expression will change the appointment table view to be filtered by month.
             * It also populates the monthWeekComboBox (function just above this lambda function) with the names of the months for further filtering.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            try {
                customerAppointments.getItems().setAll(Appointment.getAllAppointmentsForMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
                months.clear();
                int thisMonthCount;
                for (thisMonthCount = 0; thisMonthCount < 12; thisMonthCount++){
                    months.add(resourceBundle.getString(Integer.toString(thisMonthCount + 1)));
                }
                monthsList.setAll(months);
                monthWeekComboBox.setItems(monthsList);
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("Line 332. Initialize - MainScreenController");
            }
        });

        weekRadio.setOnAction(actionEvent ->{
            /**
             * weekRadio.setOnAction lambda expression will change the appointment table view to be filtered by week.
             * It also populates the monthWeekComboBox with the week numbers for filtering.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            try {
                customerAppointments.getItems().setAll(Appointment.getAllAppointmentsForWeek(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.YEAR)));
                ArrayList<String> weeks = new ArrayList<>();
                int weekCount;
                for (weekCount = 0; weekCount < 52; weekCount++){
                    weeks.add(resourceBundle.getString("week") + " " + Integer.toString(weekCount + 1));
                }
                weeksList.setAll(weeks);
                monthWeekComboBox.setItems(weeksList);
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("Line 355. Initialize - MainScreenController");
            }
        });

        monthWeekComboBox.setOnAction(actionEvent ->{
            /**
             * monthWeekComboBox.setOnAction lambda expression will format the appointment table view
             * to filter by month or week depending what's selected.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implements.
             */
            if(monthRadio.isSelected()){
                try {
                    customerAppointments.setItems(Appointment.getAllAppointmentsForMonth(monthWeekComboBox.getSelectionModel().getSelectedIndex() + 1, Integer.parseInt(yearLabel.getText())));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 372. Initialize - MainScreenController");
                }
            }
            else {
                try {
                    customerAppointments.setItems(Appointment.getAllAppointmentsForWeek(monthWeekComboBox.getSelectionModel().getSelectedIndex(), Integer.parseInt(yearLabel.getText())));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 380. Initialize - MainScreenController");
                }
            }
        });

        previousYear.setOnAction(actionEvent -> {
            /**
             * previousYear.setOnAction lambda expression decrements the year label and filter.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            yearLabel.setText(Integer.toString(Integer.parseInt(yearLabel.getText()) - 1));
            if(monthRadio.isSelected()){
                try {
                    customerAppointments.setItems(Appointment.getAllAppointmentsForMonth(monthWeekComboBox.getSelectionModel().getSelectedIndex() + 1, Integer.parseInt(yearLabel.getText())));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 398. Initialize - MainScreenController");
                }
            }
            else {
                try {
                    customerAppointments.setItems(Appointment.getAllAppointmentsForWeek(monthWeekComboBox.getSelectionModel().getSelectedIndex(), Integer.parseInt(yearLabel.getText())));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 406. Initialize - MainScreenController");
                }
            }
        });

        nextYear.setOnAction(actionEvent -> {
            /**
             * nextYear.setOnAction lambda expression increments the year label and filter.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            yearLabel.setText(Integer.toString(Integer.parseInt(yearLabel.getText()) + 1));
            if(monthRadio.isSelected()){
                try {
                    customerAppointments.setItems(Appointment.getAllAppointmentsForMonth(monthWeekComboBox.getSelectionModel().getSelectedIndex() + 1, Integer.parseInt(yearLabel.getText())));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 424. Initialize - MainScreenController");
                }
            }
            else {
                try {
                    customerAppointments.setItems(Appointment.getAllAppointmentsForWeek(monthWeekComboBox.getSelectionModel().getSelectedIndex(), Integer.parseInt(yearLabel.getText())));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 432. Initialize - MainScreenController");
                }
            }
        });

        /**
         * Fetch for Appointment ID info into a list by binding each appointment value
         * to its respective column's cell value factory.
         */
        appointmentIDColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, Number>, ObservableValue<Number>>() {
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<Appointment, Number> apptID) {
                return new SimpleIntegerProperty(apptID.getValue().getAppointmentID());
            }
        });
        /**
         * Fetch for title info into a list by binding each title value
         * to its respective column's cell value factory.
         */
        titleColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> titleInfo) {
                return new SimpleStringProperty(titleInfo.getValue().getTitle());
            }
        });
        /**
         * Fetch for description info into a list by binding each description value
         * to its respective column's cell value factory.
         */
        descriptionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> descrpInfo) {
                return new SimpleStringProperty(descrpInfo.getValue().getDescription());
            }
        });
        /**
         * Fetch for location info into a list by binding each location value
         * to its respective column's cell value factory.
         */
        locationColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> locInfo) {
                return new SimpleStringProperty(locInfo.getValue().getLocation());
            }
        });
        /**
         * Fetch for type info into a list by binding each type value
         * to its respective column's cell value factory.
         */
        typeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> typeInfo) {
                return new SimpleStringProperty(typeInfo.getValue().getType());
            }
        });
        /**
         * Fetch for start time info into a list by binding each start time value
         * to its respective column's cell value factory.
         */
        startColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> startTimeInfo) {
                LocalDateTime utc = LocalDateTime.of(startTimeInfo.getValue().getStartYear(), startTimeInfo.getValue().getStartMonth(), startTimeInfo.getValue().getStartDay(),
                        startTimeInfo.getValue().getStartHour(), startTimeInfo.getValue().getStartMinute());
                ZonedDateTime zdt = ZonedDateTime.of(utc, ZoneOffset.UTC);
                ZonedDateTime local = zdt.withZoneSameInstant(ZoneId.systemDefault());
                local.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a"));
                return new SimpleStringProperty(local.getYear() + "-" + local.getMonthValue() + "-" + local.getDayOfMonth() + " " + local.getHour() + ":" + startTimeInfo.getValue().getStartMinute());
            }
        });
        /**
         * Fetch for end time info into a list by binding each end time value
         * to its respective column's cell value factory.
         */
        endColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> endTimeInfo) {
                LocalDateTime utc = LocalDateTime.of(endTimeInfo.getValue().getEndYear(), endTimeInfo.getValue().getEndMonth(), endTimeInfo.getValue().getEndDay(),
                        endTimeInfo.getValue().getEndHour(), endTimeInfo.getValue().getEndMinute());
                ZonedDateTime zdt = ZonedDateTime.of(utc, ZoneOffset.UTC);
                ZonedDateTime local = zdt.withZoneSameInstant(ZoneId.systemDefault());
                local.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a"));
                return new SimpleStringProperty(local.getYear() + "-" + local.getMonthValue() + "-" + local.getDayOfMonth() + " " + local.getHour() + ":" + endTimeInfo.getValue().getEndMinute());
            }
        });
        /**
         * Fetch for create date info into a list by binding each create date value
         * to its respective column's cell value factory.
         */
        createDateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> createDateInfo) {
                return new SimpleStringProperty(createDateInfo.getValue().getCreateDate());
            }
        });
        /**
         * Fetch for create by info into a list by binding each create by value
         * to its respective column's cell value factory.
         */
        createdByColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> createByInfo) {
                return new SimpleStringProperty(createByInfo.getValue().getCreatedBy());
            }
        });
        /**
         * Fetch for last update info into a list by binding each last update value
         * to its respective column's cell value factory.
         */
        lastUpdateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> lastUpdateInfo) {
                return new SimpleStringProperty(lastUpdateInfo.getValue().getLastUpdate());
            }
        });
        /**
         * Fetch for last updated by info into a list by binding each last updated by value
         * to its respective column's cell value factory.
         */
        lastUpdatedByColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> lastUpdatedByInfo) {
                return new SimpleStringProperty(lastUpdatedByInfo.getValue().getLastUpdatedBy());
            }
        });
        /**
         * Fetch for customer ID info into a list by binding each customer ID value
         * to its respective column's cell value factory.
         */
        customerIDColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> custIDInfo) {
                return new SimpleStringProperty(Integer.toString(custIDInfo.getValue().getCustomerID()));
            }
        });
        /**
         * Fetch for user info into a list by binding each user value
         * to its respective column's cell value factory.
         */
        userColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> userInfo) {
                String getUserQuery = String.format("SELECT User_Name FROM users WHERE User_ID = %d", userInfo.getValue().getUserID());
                try {
                    PreparedStatement getUser = connection.getConnection().prepareStatement(getUserQuery);
                    ResultSet result = getUser.executeQuery();
                    result.next();
                    return new SimpleStringProperty(result.getString("User_Name"));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 569. Initialize - MainScreenController");
                }
                return null;
            }
        });
        /**
         * Fetch for contact info into a list by binding each contact value
         * to its respective column's cell value factory.
         */
        contactColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> contactInfo) {
                String getContactQuery = String.format("SELECT Contact_Name FROM contacts WHERE Contact_ID = %d", contactInfo.getValue().getContactID());
                try {
                    PreparedStatement getContact = connection.getConnection().prepareStatement(getContactQuery);
                    ResultSet result = getContact.executeQuery();
                    result.next();
                    return new SimpleStringProperty(result.getString("Contact_Name"));
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 588. Initialize - MainScreenController");
                }
                return null;
            }
        });

        newCustomer.setOnAction(actionEvent ->{
            /**
             * newCustomer.setOnAction lambda expression opens the add customer screen.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to do.
             */
            try {
                Stage close = (Stage) exit.getScene().getWindow();
                close.close();
                Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/AddCustomerScreen.fxml"));
                Stage newCustomerForm = new Stage();
                Scene scene = new Scene(root);
                newCustomerForm.setScene(scene);
                newCustomerForm.show();
            } catch (IOException error) {
                error.printStackTrace();
                System.out.println("Line 611. Initialize - MainScreenController");
            }
        });

        editCustomer.setOnAction(actionEvent ->{
            /**
             * editCustomer.setOnAction lambda expression opens the modify customer screen and passes an instance of the selected customer object
             * using the PassCustomer class.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            boolean selection = true;
            Customer customerToModify = (Customer) allCustomersList.getSelectionModel().getSelectedItem();
            PassCustomer passCustomer = new PassCustomer(customerToModify);
            try {
                PassCustomer.getCustomer().getCustomerID();
            } catch (NullPointerException error) {
                userInterfaceMessages.setText(resourceBundle.getString("noselection"))
                ; selection = false;
            }
            if (selection) {
                try {
                    Stage close = (Stage) exit.getScene().getWindow();
                    close.close();
                    Stage modifyCustomerForm = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/ModifyCustomerScreen.fxml"));
                    Scene scene = new Scene(root);
                    modifyCustomerForm.setScene(scene);
                    modifyCustomerForm.show();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    System.out.println("Line 643. Initialize - MainScreenController");
                }
            }
        });

        deleteCustomer.setOnAction(actionEvent ->{
            /**
             * deleteCustomer.setOnAction lambda expression checks that the selected customer has no appointments associated with it,
             * and if not, deletes the chosen customer object.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            Customer customerToDelete = (Customer) allCustomersList.getSelectionModel().getSelectedItem();
            /**
             * Fetch for all customer info associated with a customer ID
             */
            String SQLCustomerQuery = "SELECT * FROM appointments WHERE Customer_ID = " + customerToDelete.getCustomerID();
            try {
                Statement getCustomerInfo = connection.getConnection().createStatement();
                ResultSet result = getCustomerInfo.executeQuery(SQLCustomerQuery);
                if (result.next()){
                    userInterfaceMessages.setText(resourceBundle.getString("cannotdelete"));
                }
                else {
                    /**
                     * delete customer associated with a customer ID
                     */
                    String deleteQuery = "DELETE FROM customers WHERE Customer_ID = " + customerToDelete.getCustomerID();
                    getCustomerInfo.executeUpdate(deleteQuery);
                    userInterfaceMessages.setText(resourceBundle.getString("thiscustomerid") + customerToDelete.getCustomerID() +"-> "+ resourceBundle.getString("customerdeleted"));
                }
                allCustomersList.getItems().setAll(Customer.getAllCustomers());
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("Line 678. Initialize - MainScreenController");
            }
        });

        newAppointment.setOnAction(actionEvent ->{
            /**
             * newAppointment.setOnAction lambda expression closes the main screen  and opens the add appointment screen.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/AddAppointmentScreen.fxml"));
                Stage close = (Stage) exit.getScene().getWindow();
                close.close();
                Scene scene = new Scene(root);
                Stage mainMenu = new Stage();
                mainMenu.setScene(scene);
                mainMenu.show();
            } catch (IOException error) {
                error.printStackTrace();
                System.out.println("Line 699. Initialize - MainScreenController");
            }
        });

        editAppointment.setOnAction(actionEvent ->{
            /**
             *editAppointment.setOnAction lambda expression opens the modify appointment screen and passes an instance of an appointment object
             * using the PassAppointment class.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            boolean selection = true;
            try {
                Appointment appointmentToModify = (Appointment) customerAppointments.getSelectionModel().getSelectedItem();
                PassAppointment passAppointment = new PassAppointment(appointmentToModify);
                try {
                    PassAppointment.getAppointment().getAppointmentID();
                } catch (NullPointerException error) {
                    userInterfaceMessages.setText(resourceBundle.getString("noselection"));
                    selection = false;
                    System.out.println("Line 720. Initialize - MainScreenController");
                }
                if(selection) {
                    Stage close = (Stage) exit.getScene().getWindow();
                    close.close();
                    Stage modifyCustomerForm = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("../ScreenView/ModifyAppointmentScreen.fxml"));
                    Scene scene = new Scene(root);
                    modifyCustomerForm.setScene(scene);
                    modifyCustomerForm.show();
                }
            } catch (IOException error) {
                error.printStackTrace();
                System.out.println("Line 733. Initialize - MainScreenController");
            }
        });

        deleteAppointment.setOnAction(actionEvent -> {
            /**
             *deleteAppointment.setOnAction lambda expression deletes a selected appointment.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            Appointment appointmentToDelete = (Appointment) customerAppointments.getSelectionModel().getSelectedItem();
            /**
             * delete appointment from DB
             */
            String SQLDeleteQuery = "DELETE FROM appointments WHERE Appointment_ID = " + appointmentToDelete.getAppointmentID();
            try {
                Statement statement = connection.getConnection().createStatement();
                statement.executeUpdate(SQLDeleteQuery);
                customerAppointments.getItems().setAll(Appointment.getAllAppointmentsForMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
                userInterfaceMessages.setText(resourceBundle.getString("thisappointmentid")+ appointmentToDelete.getAppointmentID() +resourceBundle.getString("type")+": "+ appointmentToDelete.getType()+" -> "+ resourceBundle.getString("appointmentdeleted"));
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("Line 756. Initialize - MainScreenController");
            }
        });

        customerReport.setOnAction(actionEvent ->{
            /**
             * customerReport.setOnAction lambda expression produces a report of the total number of customer appointments by type
             * and month and displays it in the user interface message dialogue box at the bottom of the main screen.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to do.
             */
            String SQLCustomerReportQuery = "SELECT MONTH(Start), COUNT(*) FROM appointments GROUP BY MONTH(Start)";
            String SQLCustomerReportTypeQuery = "SELECT Type, COUNT(*) FROM appointments GROUP BY Type";
            ArrayList<String> monthsInReport = new ArrayList<>();
            try {
                Statement statement = connection.getConnection().createStatement();
                ResultSet rs = statement.executeQuery(SQLCustomerReportQuery);
                while(rs.next()){
                    monthsInReport.add(resourceBundle.getString(rs.getString("MONTH(Start)")) + ": " + rs.getString("COUNT(*)") + "\n");
                }
                int j;
                userInterfaceMessages.setText(resourceBundle.getString("appointmentreport") + "\n");
                for (j = 0; j < 12; j++){
                    try {
                        userInterfaceMessages.setText(userInterfaceMessages.getText() + monthsInReport.get(j));
                    } catch (IndexOutOfBoundsException exception) {break;}
                }
                ResultSet resultSet = statement.executeQuery(SQLCustomerReportTypeQuery);
                ArrayList<String> typesInReport = new ArrayList<>();
                while (resultSet.next()){
                    typesInReport.add(resourceBundle.getString("type")+ " " +resultSet.getString("Type") + ": " + resultSet.getString("COUNT(*)") + "\n");
                }
                j = 0;
                do{
                    try {
                        userInterfaceMessages.setText(userInterfaceMessages.getText() + typesInReport.get(j));
                    } catch (IndexOutOfBoundsException error) {
                        System.out.println("Line 795. Initialize - MainScreenController");
                        break;
                    }
                    j++;
                } while (true);
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("Line 802. Initialize - MainScreenController");
            }
        });

        contactReport.setOnAction(actionEvent ->{
            /**
             *contactReport.setOnAction lambda expression produces a schedule for each contact in the organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID
             * and puts the results in the user interface messages dialogue box at the bottom of main screen.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            int g = 1;
            userInterfaceMessages.setText("");
            boolean appending = true;
            do {
                try {
                    String SQLContactScheduleQuery = "SELECT Appointment_ID, Title, Type, Description, Start, End, Customer_ID, Contact_Name FROM appointments JOIN contacts USING (Contact_ID) WHERE Contact_ID = " + g;
                    Statement statement = connection.getConnection().createStatement();
                    ResultSet result = statement.executeQuery(SQLContactScheduleQuery);
                    ArrayList<String> contactReportList = new ArrayList<>();
                    if (!result.next()) {
                        appending = false;
                        break;
                    }
                    userInterfaceMessages.setText(userInterfaceMessages.getText() + result.getString("Contact_Name") + ":");
                    do {
                        contactReportList.add("\n" + resourceBundle.getString("appointmentid") + ": " + result.getString("Appointment_ID") + "\n" + resourceBundle.getString("title") + ": " + result.getString("Title") +
                                "\n" + "Description: " + result.getString("Description") + "\n" + resourceBundle.getString("start") + ": " + result.getTimestamp("Start") + "\n" + resourceBundle.getString("end") +
                                ": " + result.getTimestamp("End") + "\n" + resourceBundle.getString("customerid") + ": " + result.getString("Customer_ID") + "\n");
                    } while (result.next());
                    int j = 0;
                    do {
                        try {
                            userInterfaceMessages.setText(userInterfaceMessages.getText() + contactReportList.get(j));
                        } catch (IndexOutOfBoundsException exception) {break;}
                        j++;
                    } while (appending);
                    userInterfaceMessages.setText(userInterfaceMessages.getText() + "\n");
                } catch (SQLException error) {
                    error.printStackTrace();
                    System.out.println("Line 843. Initialize - MainScreenController");
                }
                g++;
            } while (appending);
        });

        userReport.setOnAction(actionEvent ->{
            /**
             *userReport.setOnAction lambda expression produces a report of every appointment that was created by the user and puts the results
             * in the user interface messages dialogue box at the bottom of main screen.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            userInterfaceMessages.setText(resourceBundle.getString("userreport") + "\n");
            String userReportQuery = "SELECT Appointment_ID, Title FROM appointments JOIN users USING (User_ID) WHERE User_Name = '" + User.getUsername() + "'";
            try {
                Statement statement = connection.getConnection().createStatement();
                ResultSet rs = statement.executeQuery(userReportQuery);
                ArrayList<String> userAppointments = new ArrayList<>();
                while (rs.next()) {
                    userAppointments.add(resourceBundle.getString("appointmentid") + ": " + rs.getString("Appointment_ID") + "\n" + resourceBundle.getString("title") + ": " + rs.getString("Title") +
                            "\n");
                }
                int j;
                for (j = 0; ; j++) {
                    try {
                        userInterfaceMessages.setText(userInterfaceMessages.getText() + userAppointments.get(j));
                    } catch (IndexOutOfBoundsException exception) {
                        break;
                    }
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Line 877. Initialize - MainScreenController");
            }
        });

        exit.setOnAction(actionEvent -> {
            /**
             *exit.setOnAction lambda expression exits the application.
             *
             * Lambda expressions are used to handle button and selection change action events throughout the software because it
             * makes the program easier to read and understand what the application is trying to implement.
             */
            Stage stage = (Stage) exit.getScene().getWindow();
            stage.close(); });
    }
}
