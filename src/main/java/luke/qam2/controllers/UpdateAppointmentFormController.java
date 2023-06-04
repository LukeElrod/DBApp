package luke.qam2.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import luke.qam2.HelperFunctions;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller class for the Update Appointment form
 */
public class UpdateAppointmentFormController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private final ObservableList<Time> timeList = FXCollections.observableList(new ArrayList<Time>());
    private final ObservableList<String> contactList = FXCollections.observableList(new ArrayList<String>());

    @FXML
    private TextField appointmentDescriptionTextField;

    @FXML
    private DatePicker appointmentEndDatePicker;

    @FXML
    private ComboBox<Time> appointmentEndTimeBox;

    @FXML
    private TextField appointmentIdTextField;

    @FXML
    private TextField appointmentLocationTextField;

    @FXML
    private DatePicker appointmentStartDatePicker;

    @FXML
    private ComboBox<Time> appointmentStartTimeBox;

    @FXML
    private TextField appointmentTitleTextField;

    @FXML
    private TextField appointmentTypeTextField;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TextField customerIdTextField;

    @FXML
    private TextField userIdTextField;

    /**
     * Returns the user to the main form
     * @param event
     * @throws IOException
     */
    @FXML
    void returnToMainForm(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/luke/qam2/MainForm.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the selected appointment
     * @param event
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    void saveAppointment(ActionEvent event) throws SQLException, IOException {
        //run checks
        if (appointmentTitleTextField.getText().isBlank() || appointmentDescriptionTextField.getText().isBlank() || appointmentLocationTextField.getText().isBlank() || appointmentTypeTextField.getText().isBlank() || customerIdTextField.getText().isBlank() || userIdTextField.getText().isBlank() || contactComboBox.getSelectionModel().isEmpty() || !customerIdTextField.getText().matches("^[\\d]+$") || !userIdTextField.getText().matches("^[\\d]+$") || appointmentStartDatePicker.getValue() == null || appointmentEndDatePicker.getValue() == null || appointmentStartTimeBox.getValue() == null || appointmentEndTimeBox.getValue() == null){
            HelperFunctions.displayError(()->{return "Please enter a valid value for all entries";});
            return;
        }
        LocalDateTime utcConvertedStartDateTime = HelperFunctions.convertDateTimeUTC(LocalDateTime.of(LocalDate.of(appointmentStartDatePicker.getValue().getYear(), appointmentStartDatePicker.getValue().getMonthValue(), appointmentStartDatePicker.getValue().getDayOfMonth()), LocalTime.of(appointmentStartTimeBox.getValue().getHours(), appointmentStartTimeBox.getValue().getMinutes(), 0)));
        LocalDateTime utcConvertedEndDateTime = HelperFunctions.convertDateTimeUTC(LocalDateTime.of(LocalDate.of(appointmentEndDatePicker.getValue().getYear(), appointmentEndDatePicker.getValue().getMonthValue(), appointmentEndDatePicker.getValue().getDayOfMonth()), LocalTime.of(appointmentEndTimeBox.getValue().getHours(), appointmentEndTimeBox.getValue().getMinutes(), 0)));
        LocalDateTime etcConvertedDateTime = HelperFunctions.convertDateTimeEST(LocalDateTime.of(LocalDate.of(appointmentStartDatePicker.getValue().getYear(), appointmentStartDatePicker.getValue().getMonthValue(), appointmentStartDatePicker.getValue().getDayOfMonth()), LocalTime.of(appointmentStartTimeBox.getValue().getHours(), appointmentStartTimeBox.getValue().getMinutes(), 0)));
        int estHour = etcConvertedDateTime.toLocalTime().getHour();

        if (estHour < 8 || estHour > 22) {
            HelperFunctions.displayError(()->{return "Appointment is outside of business hours";});
            return;
        }
        Statement stmt = HelperFunctions.connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM client_schedule.appointments");
        while (resultSet.next()){
            if (utcConvertedStartDateTime.getYear() == ((LocalDateTime)resultSet.getObject("Start")).getYear() && utcConvertedStartDateTime.getMonth() == ((LocalDateTime)resultSet.getObject("Start")).getMonth() && utcConvertedStartDateTime.getDayOfMonth() == ((LocalDateTime)resultSet.getObject("Start")).getDayOfMonth()){
                if ((utcConvertedStartDateTime.toLocalTime().getHour() <= ((LocalDateTime)resultSet.getObject("Start")).toLocalTime().getHour()) && (utcConvertedEndDateTime.toLocalTime().getHour() >= ((LocalDateTime)resultSet.getObject("Start")).toLocalTime().getHour())){
                    if (resultSet.getInt("Appointment_ID") != Integer.valueOf(appointmentIdTextField.getText())){
                        HelperFunctions.displayError(()->{return "Appointment is overlapping with another";});
                        stmt.close();
                        resultSet.close();
                        return;
                    }
                }
            }
        }
        stmt.close();
        resultSet.close();

        PreparedStatement pstmt = HelperFunctions.connection.prepareStatement("UPDATE client_schedule.appointments SET Title=?,Description=?,Location=?,Type=?,Start=?,End=?,Customer_ID=?,User_ID=?,Contact_ID=? WHERE Appointment_ID = " + HelperFunctions.currentlySelectedAppointment.appointmentId);
        pstmt.setString(1, appointmentTitleTextField.getText());
        pstmt.setString(2, appointmentDescriptionTextField.getText());
        pstmt.setString(3, appointmentLocationTextField.getText());
        pstmt.setString(4, appointmentTypeTextField.getText());
        pstmt.setObject(5, utcConvertedStartDateTime);
        pstmt.setObject(6, utcConvertedEndDateTime);
        pstmt.setInt(7, Integer.parseInt(customerIdTextField.getText()));
        pstmt.setInt(8, Integer.parseInt(userIdTextField.getText()));

        switch (contactComboBox.getSelectionModel().getSelectedItem()) {
            case "Anika Costa":
                pstmt.setInt(9, 1);
                break;
            case "Daniel Garcia":
                pstmt.setInt(9, 2);
                break;
            case "Li Lee":
                pstmt.setInt(9, 3);
                break;
        }

        pstmt.executeUpdate();
        pstmt.close();

        returnToMainForm(event);
    }

    /**
     * Called on initialization
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j++){
                timeList.add(new Time(i, j, 0));
            }
        }
        contactList.addAll("Anika Costa", "Daniel Garcia", "Li Lee");

        appointmentStartTimeBox.setItems(timeList);
        appointmentEndTimeBox.setItems(timeList);
        contactComboBox.setItems(contactList);

        //auto populate entries
        appointmentIdTextField.setText(String.valueOf(HelperFunctions.currentlySelectedAppointment.appointmentId));
        appointmentTitleTextField.setText(HelperFunctions.currentlySelectedAppointment.title);
        appointmentDescriptionTextField.setText(HelperFunctions.currentlySelectedAppointment.description);
        appointmentLocationTextField.setText(HelperFunctions.currentlySelectedAppointment.location);
        appointmentTypeTextField.setText(HelperFunctions.currentlySelectedAppointment.type);
        customerIdTextField.setText(String.valueOf(HelperFunctions.currentlySelectedAppointment.customerId));
        userIdTextField.setText(String.valueOf(HelperFunctions.currentlySelectedAppointment.userId));
        switch (HelperFunctions.currentlySelectedAppointment.contactId){
            case 1:
                contactComboBox.getSelectionModel().select("Anika Costa");
                break;
            case 2:
                contactComboBox.getSelectionModel().select("Daniel Garcia");
                break;
            case 3:
                contactComboBox.getSelectionModel().select("Li Lee");
                break;
        }
        appointmentStartDatePicker.setValue(HelperFunctions.currentlySelectedAppointment.start.toLocalDate());
        appointmentEndDatePicker.setValue(HelperFunctions.currentlySelectedAppointment.end.toLocalDate());
        appointmentStartTimeBox.getSelectionModel().select(Time.valueOf(HelperFunctions.currentlySelectedAppointment.start.toLocalTime()));
        appointmentEndTimeBox.getSelectionModel().select(Time.valueOf(HelperFunctions.currentlySelectedAppointment.end.toLocalTime()));

    }
}
