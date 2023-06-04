package luke.qam2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import luke.qam2.model.Appointment;
import luke.qam2.model.Customer;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Class containing miscellaneous static functions to ease implementation
 */
public abstract class HelperFunctions {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface
    public static String usersLanguage;
    public static ObservableList<Customer> allCustomers = FXCollections.observableList(new ArrayList<Customer>());
    public static ObservableList<Appointment> allAppointments = FXCollections.observableList(new ArrayList<Appointment>());
    public static Customer currentlySelectedCustomer;
    public static Appointment currentlySelectedAppointment;

    /**
     * Connects to the database
     */
    public static void openConnection() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * Disconnects from the database
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * Displays an error.
     * Input is a lambda expression.
     * A lambda is used here because we can input logical checks for language easily in the lambda
     * and return the corresponding string
     * @param inFunctionalInterface
     */
    public static void displayError(Supplier<String> inFunctionalInterface) {
        Alert alert = new Alert(Alert.AlertType.ERROR, inFunctionalInterface.get(), ButtonType.CLOSE);
        alert.showAndWait();
    }
    /**
     * Displays a message.
     * Input is a lambda expression.
     * A lambda is used here because we can input logical checks for language easily in the lambda
     * and return the corresponding string
     * @param inFunctionalInterface
     */
    public static void displayMessage(Supplier<String> inFunctionalInterface){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, inFunctionalInterface.get(), ButtonType.CLOSE);
        alert.showAndWait();
    }

    /**
     * Updates the 2 static lists for customers and appointments.
     * The information is pulled from the database and preps it for entry into the front-end tables
     * @throws SQLException
     */
    public static void updateCustomersAndAppointments() throws SQLException {
           allCustomers.clear();
           allAppointments.clear();
           Statement stmt = connection.createStatement();
           ResultSet resultSet = stmt.executeQuery("SELECT * FROM client_schedule.customers");
           while (resultSet.next()) {
               allCustomers.add(new Customer(resultSet.getInt("Customer_ID"), resultSet.getString("Customer_Name"), resultSet.getString("Address"), resultSet.getString("Postal_Code"), resultSet.getString("Phone"), resultSet.getInt("Division_ID")));
           }
           resultSet = stmt.executeQuery("SELECT * FROM client_schedule.appointments");
           while (resultSet.next()){
               LocalDateTime localStartTime = convertUTCToLocal((LocalDateTime) resultSet.getObject("Start"));
               LocalDateTime localEndTime = convertUTCToLocal((LocalDateTime) resultSet.getObject("End"));
               allAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"), resultSet.getString("Description"), resultSet.getString("Location"), resultSet.getString("Type"), localStartTime, localEndTime, resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), resultSet.getInt("Contact_ID")));
           }
           stmt.close();
           resultSet.close();
    }

    /**
     * Converts the correctly formatted string into UTC time
     * @param dateTime
     * @return
     */
    public static LocalDateTime convertDateTimeUTC(LocalDateTime dateTime) {
        ZonedDateTime zoneDT = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcDT = zoneDT.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime localOUT = utcDT.toLocalDateTime();
        return localOUT;
    }

    /**
     * Converts the correctly formatted string into EST time
     * @param dateTime
     * @return
     */
    public static LocalDateTime convertDateTimeEST(LocalDateTime dateTime) {
        ZonedDateTime zoneDT = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcDT = zoneDT.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDateTime localOUT = utcDT.toLocalDateTime();
        return localOUT;
    }

    /**
     * Converts the correctly formatted UTC string into local user time
     * @param dateTime
     * @return
     */
    public static LocalDateTime convertUTCToLocal(LocalDateTime dateTime) {
        ZonedDateTime zoneDT = dateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime utcDT = zoneDT.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime localOUT = utcDT.toLocalDateTime();
        return localOUT;
    }



}
