package luke.qam2.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import luke.qam2.HelperFunctions;
import luke.qam2.model.Appointment;
import luke.qam2.model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller class for the main form
 */
public class MainFormController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private RadioButton allAppointmentsRadioButton;
    @FXML
    private RadioButton monthRadioButton;
    @FXML
    private RadioButton weekRadioButton;

    @FXML
    private TableColumn<Appointment, String> appointmentContactIdColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentCustomerIdColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentIdColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentLocationColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartColumn;

    @FXML
    private TableView<Appointment> appointmentTableView;

    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentTypeColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentUserIdColumn;

    @FXML
    private TableColumn<Customer, String> customerAddressColumn;

    @FXML
    private TableColumn<Customer, String> customerFirstLevelDataColumn;

    @FXML
    private TableColumn<Customer, String> customerIdColumn;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    @FXML
    private TableColumn<Customer, String> customerPhoneColumn;

    @FXML
    private TableColumn<Customer, String> customerPostalCodeColumn;

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TextField customerSearchField;

    @FXML
    private TextField appointmentSearchField;

    /**
     * Opens the report form
     * @param event
     * @throws IOException
     */
    @FXML
    void openReportForm(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/luke/qam2/ReportForm.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Filters the appointment table to show all appointments
     * @param event
     * @throws SQLException
     */
    @FXML
    void filterToAllAppointments(ActionEvent event) throws SQLException {
        allAppointmentsRadioButton.setDisable(true);
        allAppointmentsRadioButton.setSelected(true);
        monthRadioButton.setSelected(false);
        monthRadioButton.setDisable(false);
        weekRadioButton.setSelected(false);
        weekRadioButton.setDisable(false);

        HelperFunctions.updateCustomersAndAppointments();
    }

    /**
     * Filters the appointment table to show appointments
     * only in this month
     * @param event
     * @throws SQLException
     */
    @FXML
    void filterToMonth(ActionEvent event) throws SQLException {
        monthRadioButton.setDisable(true);
        monthRadioButton.setSelected(true);
        allAppointmentsRadioButton.setSelected(false);
        allAppointmentsRadioButton.setDisable(false);
        weekRadioButton.setSelected(false);
        weekRadioButton.setDisable(false);
        LocalDateTime firstDate = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime secondDate = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth());

        Statement stmt = HelperFunctions.connection.createStatement();
        String query = "SELECT * FROM client_schedule.appointments WHERE Start BETWEEN '" + firstDate.getYear() + "-" + firstDate.getMonthValue() + "-" + firstDate.getDayOfMonth() + "' AND '" + secondDate.getYear() + "-" + secondDate.getMonthValue() + "-" + secondDate.getDayOfMonth() + "'";
        ResultSet resultSet = stmt.executeQuery(query);

        HelperFunctions.allAppointments.clear();

        while (resultSet.next()){
            LocalDateTime localStartTime = HelperFunctions.convertUTCToLocal((LocalDateTime) resultSet.getObject("Start"));
            LocalDateTime localEndTime = HelperFunctions.convertUTCToLocal((LocalDateTime) resultSet.getObject("End"));
            HelperFunctions.allAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"), resultSet.getString("Description"), resultSet.getString("Location"), resultSet.getString("Type"), localStartTime, localEndTime, resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), resultSet.getInt("Contact_ID")));
        }

        resultSet.close();
        stmt.close();
    }

    /**
     * Filters the appointment table to show appointments
     * only in this week
     * @param event
     * @throws SQLException
     */
    @FXML
    void filterToWeek(ActionEvent event) throws SQLException {
        weekRadioButton.setDisable(true);
        weekRadioButton.setSelected(true);
        allAppointmentsRadioButton.setSelected(false);
        allAppointmentsRadioButton.setDisable(false);
        monthRadioButton.setSelected(false);
        monthRadioButton.setDisable(false);
        LocalDateTime firstDate = LocalDateTime.now();
        firstDate = firstDate.minusDays(7);
        LocalDateTime secondDate = LocalDateTime.now();
        secondDate = secondDate.plusDays(7);

        Statement stmt = HelperFunctions.connection.createStatement();
        String query = "SELECT * FROM client_schedule.appointments WHERE Start BETWEEN '" + firstDate.getYear() + "-" + firstDate.getMonthValue() + "-" + firstDate.getDayOfMonth() + "' AND '" + secondDate.getYear() + "-" + secondDate.getMonthValue() + "-" + secondDate.getDayOfMonth() + "'";
        ResultSet resultSet = stmt.executeQuery(query);

        HelperFunctions.allAppointments.clear();

        while (resultSet.next()){
            LocalDateTime localStartTime = HelperFunctions.convertUTCToLocal((LocalDateTime) resultSet.getObject("Start"));
            LocalDateTime localEndTime = HelperFunctions.convertUTCToLocal((LocalDateTime) resultSet.getObject("End"));
            HelperFunctions.allAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"), resultSet.getString("Description"), resultSet.getString("Location"), resultSet.getString("Type"), localStartTime, localEndTime, resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), resultSet.getInt("Contact_ID")));
        }

        resultSet.close();
        stmt.close();
    }

    /**
     * Opens the add appointment form
     * @param event
     * @throws IOException
     */
    @FXML
    void addAppointment(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/luke/qam2/AddAppointmentForm.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the add customer form
     * @param event
     * @throws IOException
     */
    @FXML
    void addCustomer(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/luke/qam2/AddCustomerForm.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes the users currently selected appointment
     * @param event
     * @throws SQLException
     */
    @FXML
    void deleteAppointment(ActionEvent event) throws SQLException {
        HelperFunctions.displayMessage(()->{return "Appointment canceled. ID: " + appointmentTableView.getSelectionModel().getSelectedItem().appointmentId + " Type: " + appointmentTableView.getSelectionModel().getSelectedItem().type;});
        Statement stmt = HelperFunctions.connection.createStatement();
        stmt.executeUpdate("DELETE FROM client_schedule.appointments WHERE Appointment_ID = " + appointmentTableView.getSelectionModel().getSelectedItem().appointmentId);
        stmt.close();
        HelperFunctions.updateCustomersAndAppointments();
    }

    /**
     * Deletes the users currently selected customer
     * @param event
     * @throws SQLException
     */
    @FXML
    void deleteCustomer(ActionEvent event) throws SQLException {
        Statement stmt = HelperFunctions.connection.createStatement();
        stmt.executeUpdate("DELETE FROM client_schedule.customers WHERE Customer_ID = " + customerTableView.getSelectionModel().getSelectedItem().customerId);
        stmt.close();
        HelperFunctions.updateCustomersAndAppointments();
        HelperFunctions.displayMessage(()->{return "Customer and all attached appointments deleted";});
    }

    /**
     * Exits the program
     * @param event
     */
    @FXML
    void exitProgram(ActionEvent event) {
        HelperFunctions.closeConnection();
        Platform.exit();
    }

    /**
     * Opens the update customer form if the user has a customer selected
     * @param event
     * @throws IOException
     */
    @FXML
    void updateCustomer(ActionEvent event) throws IOException {
        if (customerTableView.getSelectionModel().isEmpty()){
            return;
        }
        HelperFunctions.currentlySelectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        root = FXMLLoader.load(getClass().getResource("/luke/qam2/UpdateCustomerForm.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the update appointment form if the user has a appointment selected
     * @param event
     * @throws IOException
     */
    @FXML
    void updateAppointment(ActionEvent event) throws IOException {
        if (appointmentTableView.getSelectionModel().isEmpty()){
            return;
        }
        HelperFunctions.currentlySelectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();
        root = FXMLLoader.load(getClass().getResource("/luke/qam2/UpdateAppointmentForm.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes all appointments from the database
     * @param event
     * @throws SQLException
     */
    @FXML
    void deleteAllAppointments(ActionEvent event) throws SQLException {
        HelperFunctions.displayMessage(()->{return "All Appointments Deleted";});
        Statement stmt = HelperFunctions.connection.createStatement();
        stmt.executeUpdate("DELETE FROM client_schedule.appointments");
        stmt.close();
        HelperFunctions.updateCustomersAndAppointments();
    }

    /**
     * Deletes all customers from the database
     * @param event
     * @throws SQLException
     */
    @FXML
    void deleteAllCustomers(ActionEvent event) throws SQLException {
        HelperFunctions.displayMessage(()->{return "All Customers Deleted";});
        Statement stmt = HelperFunctions.connection.createStatement();
        stmt.executeUpdate("DELETE FROM client_schedule.customers");
        stmt.close();
        HelperFunctions.updateCustomersAndAppointments();
    }

    /**
     * Called when user hits enter on the appointment search field.
     * Filters appointments.
     * @param event
     * @throws SQLException
     */
    @FXML
    void filterAppointments(ActionEvent event) throws SQLException {
        HelperFunctions.updateCustomersAndAppointments();
        ObservableList<Appointment> foundAppointments = FXCollections.observableList(new ArrayList<Appointment>());
        for (Appointment apt : HelperFunctions.allAppointments){
            if (apt.title.matches("(?i).*" + appointmentSearchField.getText() + ".*")){
                foundAppointments.add(apt);
            }
        }
        HelperFunctions.allAppointments.clear();
        for (Appointment apt : foundAppointments){
            HelperFunctions.allAppointments.add(apt);
        }
    }

    /**
     * Called when user hits enter on the customer search field.
     * Filters customers.
     * @param event
     * @throws SQLException
     */
    @FXML
    void filterCustomers(ActionEvent event) throws SQLException {
        HelperFunctions.updateCustomersAndAppointments();
        ObservableList<Customer> foundCustomers = FXCollections.observableList(new ArrayList<Customer>());
        for (Customer customer : HelperFunctions.allCustomers){
            if (customer.customerName.matches("(?i).*" + customerSearchField.getText() + ".*")){
                foundCustomers.add(customer);
            }
        }
        HelperFunctions.allCustomers.clear();
        for (Customer customer : foundCustomers){
            HelperFunctions.allCustomers.add(customer);
        }
    }

    /**
     * Called on initialization.
     * Lambdas are used here because setCallValueFactory takes in a functional interface as an argument.
     * Using an anonymous object here would make the code less readable and less performant, therefore, a lambda is used
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerTableView.setItems(HelperFunctions.allCustomers);
        appointmentTableView.setItems(HelperFunctions.allAppointments);
        try {
            HelperFunctions.updateCustomersAndAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        customerIdColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(Integer.toString(a.getValue().customerId));});
        customerNameColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().customerName);});
        customerAddressColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().address);});
        customerPostalCodeColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().postalCode);});
        customerPhoneColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().phone);});
        customerFirstLevelDataColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(Integer.toString(a.getValue().divisionId));});

        appointmentIdColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(Integer.toString(a.getValue().appointmentId));});
        appointmentTitleColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().title);});
        appointmentDescriptionColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().description);});
        appointmentLocationColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().location);});
        appointmentTypeColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().type);});
        appointmentStartColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().start);});
        appointmentEndColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().end);});
        appointmentCustomerIdColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(Integer.toString(a.getValue().customerId));});
        appointmentUserIdColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(Integer.toString(a.getValue().userId));});
        appointmentContactIdColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(Integer.toString(a.getValue().contactId));});
    }
}
