package Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Customer model class.
 */
public class Customer {
    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private int divisionID;

    public Customer(int customerID, String customerName, String address, String postalCode, String phone, String createDate, String createdBy, String lastUpdate, String lastUpdatedBy, int divisionID){
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionID = divisionID;
    }


    /**
     * @return The customer ID.
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @return The customer name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return The customer address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return The customer postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return The customer phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @return The customer create date
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * @return The username of the user who created the customer.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @return The last time the customer was updated.
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return The username of the last user to update the customer.
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @return The division ID of the customer.
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @return An observable list of all customers in the database
     * @throws SQLException
     */
    public static ObservableList<Customer> getAllCustomers() throws SQLException {
        ConnectionToDB connection = new ConnectionToDB();
        ObservableList<Customer> allCustomersOL = FXCollections.observableArrayList();
        ArrayList<Customer> allCustomers = new ArrayList<>();
        String allCustomersQuery = "SELECT * FROM customers;";
        PreparedStatement statement = connection.getConnection().prepareStatement(allCustomersQuery);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            Customer newCustomer = new Customer(rs.getInt("Customer_ID"), rs.getString("Customer_Name"), rs.getString("Address"), rs.getString("Postal_Code"), rs.getString("Phone"),
                    rs.getString("Create_Date"), rs.getString("Created_By"), rs.getString("Last_Update"), rs.getString("Last_Updated_By"), rs.getInt("Division_ID"));
            allCustomers.add(newCustomer);
        }
        allCustomersOL.setAll(allCustomers);
        return allCustomersOL;
    }
}
