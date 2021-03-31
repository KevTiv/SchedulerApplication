package Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Appointment model class.
 */
public class Appointment {

    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private int customerID;
    private int userID;
    private int contactID;

    public Appointment(int appointmentID, String title, String description, String location, String type,
                       LocalDateTime start, LocalDateTime end, String createDate, String createdBy, String lastUpdate,
                       String lastUpdatedBy, int customerID, int userID, int contactID){
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    public Appointment() { }

    /**
     * @return The appointment ID.
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * @return The create date.
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * @return The username of the user that created the appointment.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @return The time of the last update.
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return The username of the user that last updated the appointment.
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @return The customer ID.
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @return The user ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @return The contact ID.
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @return the start time in the local user's time zone
     */
    public ZonedDateTime getLocalStart() {
        return start.atZone(ZoneId.systemDefault());
    }

    /**
     * @return the end time in the local user's time zone
     */
    public ZonedDateTime getLocalEnd() {
        return end.atZone(ZoneId.systemDefault());
    }

    /**
     * Method sets the hour and minute values for the start time.
     *
     */
    public void setStartTime(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Method sets the hour and minute values for the end time.
     *
     */
    public void setEndTime(LocalDateTime end) {
        this.end = end;
    }

    /**
     * Method accepts a month and year and returns all appointments in the database that are scheduled to start in that month and year.
     * @param month The month to get appointments from.
     * @param year The year of the month to get appointments from.
     * @return A list of appointments that start in the month and year provided.
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAllAppointmentsForMonth(int month, int year) throws SQLException {
        ConnectionToDB connection = new ConnectionToDB();
        ObservableList<Appointment> allAppointmentsOL = FXCollections.observableArrayList();
        ArrayList<Appointment> allAppointments = new ArrayList<>();
        String allAppointmentsQuery = String.format("SELECT * FROM appointments WHERE MONTH(Start) = %d AND YEAR(Start) = %d", month, year);
        PreparedStatement statement = connection.getConnection().prepareStatement(allAppointmentsQuery);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            Appointment newAppointment = new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), rs.getString("Type"),
                    rs.getTimestamp("Start").toLocalDateTime(), rs.getTimestamp("End").toLocalDateTime(), rs.getString("Create_Date"), rs.getString("Created_By"), rs.getString("Last_Update"), rs.getString("Last_Updated_By"),
                    rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getInt("Contact_ID"));
            allAppointments.add(newAppointment);
        }
        allAppointmentsOL.setAll(allAppointments);
        return allAppointmentsOL;
    }

    /**
     * Method accepts a week and year and returns all appointments in the database that are scheduled to start in that week and year.
     * @param week The week to get appointments from.
     * @param year The year of the week to get appointments from.
     * @return A list of appointments that start in the week and year provided.
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAllAppointmentsForWeek(int week, int year) throws SQLException {
        ConnectionToDB connection = new ConnectionToDB();
        ObservableList<Appointment> allAppointmentsOL = FXCollections.observableArrayList();
        ArrayList<Appointment> allAppointments = new ArrayList<>();
        String allAppointmentsQuery = String.format("SELECT * FROM appointments WHERE WEEK(Start) = %d AND YEAR(Start) = %d", week, year);
        PreparedStatement statement = connection.getConnection().prepareStatement(allAppointmentsQuery);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            Appointment newAppointment = new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), rs.getString("Type"),
                    LocalDateTime.parse("Start"), LocalDateTime.parse("End"), rs.getString("Create_Date"), rs.getString("Created_By"), rs.getString("Last_Update"), rs.getString("Last_Updated_By"),
                    rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getInt("Contact_ID"));
            allAppointments.add(newAppointment);
        }
        allAppointmentsOL.setAll(allAppointments);
        return allAppointmentsOL;
    }

    /**
     * @return The start year.
    */
    public int getStartYear(){
        return start.getYear();
    }

    /**
     * @return The start month
     */
    public int getStartMonth (){
        return start.getMonthValue();
    }
    /**
     * @return The start day.
     */
    public int getStartDay(){
        return start.getDayOfMonth();
    }

    /**
     * @return The start hour.
     */
    public int getStartHour(){
        return start.getHour();
    }

    /**
     * @return The start minute.
     */
    public int getStartMinute() {

        return start.getMinute();
    }

    /**
     * @return The end year.
     */
    public int getEndYear(){
        return end.getYear();
    }

    /**
     * @return The end month
     */
    public int getEndMonth(){
        return end.getMonthValue();
    }

    /**
     * @return The end day.
     */
    public int getEndDay(){
        return end.getDayOfMonth();
    }

    /**
     * @return The end hour.
     */
    public int getEndHour(){
        return end.getHour();
    }

    /**
     * @return The end minute.
     */
    public int getEndMinute(){
        return end.getMinute();
    }
}
