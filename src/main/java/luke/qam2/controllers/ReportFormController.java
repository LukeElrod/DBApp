package luke.qam2.controllers;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import luke.qam2.HelperFunctions;
import luke.qam2.model.Appointment;
import luke.qam2.model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Controller class for the Report Form
 */
public class ReportFormController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TableColumn<Appointment, String> appointmentContactTableTypeColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIdColumn;
    @FXML
    private ListView<String> appointmentTypeList;
    @FXML
    private ListView<String> appointmentMonthList;
    @FXML
    private TableView<Appointment> contactTable;
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private ListView<String> customerCountryList;

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
     * Sorts the contact table to the user's selected contact
     * @param event
     * @throws SQLException
     */
    @FXML
    void sortContactTable(ActionEvent event) throws SQLException {
        ObservableList<Appointment> displayAppointmentList = FXCollections.observableList(new ArrayList<Appointment>());
        int selectedContactId = 0;
        switch (contactComboBox.getSelectionModel().getSelectedItem()) {
            case "Anika Costa":
                selectedContactId = 1;
                break;
            case "Daniel Garcia":
                selectedContactId = 2;
                break;
            case "Li Lee":
                selectedContactId = 3;
                break;
        }
        if (selectedContactId > 0){
            Statement stmt = HelperFunctions.connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM client_schedule.appointments WHERE Contact_ID = " + selectedContactId);
            while (resultSet.next()){
                LocalDateTime localStartTime = HelperFunctions.convertUTCToLocal((LocalDateTime) resultSet.getObject("Start"));
                LocalDateTime localEndTime = HelperFunctions.convertUTCToLocal((LocalDateTime) resultSet.getObject("End"));
                displayAppointmentList.add(new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"), resultSet.getString("Description"), resultSet.getString("Location"), resultSet.getString("Type"), localStartTime, localEndTime, resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), resultSet.getInt("Contact_ID")));
            }
            stmt.close();
            resultSet.close();
        }
        contactTable.setItems(displayAppointmentList);
    }

    /**
     * Initializes members
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //appointments sorted by type
        ObservableList<String> allStringTypes = FXCollections.observableArrayList(new ArrayList<String>());
        ObservableList<String> exclusiveStringTypes = FXCollections.observableArrayList(new ArrayList<String>());
        ObservableList<String> displayedTypeList = FXCollections.observableList(new ArrayList<String>());
        for (Appointment apt : HelperFunctions.allAppointments) {
            allStringTypes.add(apt.type);
        }
        for (String str : allStringTypes) {
            if (Collections.frequency(exclusiveStringTypes, str) < 1) {
                exclusiveStringTypes.add(str);
            }
        }

        for (String str : exclusiveStringTypes) {
            int occurences = Collections.frequency(allStringTypes, str);
            String stringToAdd = str + ": " + occurences;
            displayedTypeList.add(stringToAdd);
        }
        appointmentTypeList.setItems(displayedTypeList);

        //appointments sorted by month
        ObservableList<String> displayedMonthList = FXCollections.observableList(new ArrayList<String>());
        for (int i = 1; i <= 12; i++) {
            try {
                String stringToAdd = "";
                int occurences = 0;
                Statement stmt = HelperFunctions.connection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT MONTH(Start) AS Month FROM client_schedule.appointments WHERE MONTH(Start) = " + i);
                while (resultSet.next()) {
                    occurences++;
                    switch (resultSet.getInt("Month")) {
                        case 1:
                            stringToAdd = "January";
                            break;
                        case 2:
                            stringToAdd = "February";
                            break;
                        case 3:
                            stringToAdd = "March";
                            break;
                        case 4:
                            stringToAdd = "April";
                            break;
                        case 5:
                            stringToAdd = "May";
                            break;
                        case 6:
                            stringToAdd = "June";
                            break;
                        case 7:
                            stringToAdd = "July";
                            break;
                        case 8:
                            stringToAdd = "August";
                            break;
                        case 9:
                            stringToAdd = "September";
                            break;
                        case 10:
                            stringToAdd = "October";
                            break;
                        case 11:
                            stringToAdd = "November";
                            break;
                        case 12:
                            stringToAdd = "December";
                            break;
                        default:
                            stringToAdd = "INVALID";
                            break;
                    }
                }
                stringToAdd = stringToAdd + ": " + occurences;
                if (occurences > 0) {
                    displayedMonthList.add(stringToAdd);
                }
                stmt.close();
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        appointmentMonthList.setItems(displayedMonthList);

        //initialize contact combo box and list
        ObservableList<String> contactComboBoxList = FXCollections.observableList(new ArrayList<String>());
        contactComboBoxList.addAll("Anika Costa", "Daniel Garcia", "Li Lee");
        contactComboBox.setItems(contactComboBoxList);

        appointmentIdColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().appointmentId);});
        appointmentTitleColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().title);});
        appointmentDescriptionColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().description);});
        appointmentContactTableTypeColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().type);});
        appointmentStartColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().start);});
        appointmentEndColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().end);});
        customerIdColumn.setCellValueFactory((a)-> {return new ReadOnlyObjectWrapper<>(a.getValue().customerId);});

        //country table
        ObservableList<String> allCountryStringTypes = FXCollections.observableList(new ArrayList<String>());
        ObservableList<String> exclusiveCountryStringTypes = FXCollections.observableList(new ArrayList<String>());
        ObservableList<String> displayedCountryTypeList = FXCollections.observableList(new ArrayList<String>());

        try {
            Statement stmt = HelperFunctions.connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT Country_ID FROM client_schedule.first_level_divisions INNER JOIN client_schedule.customers USING(Division_ID)");
            while (resultSet.next()){
                switch (resultSet.getInt("Country_ID")){
                    case 1:
                        allCountryStringTypes.add("U.S");
                        break;
                    case 2:
                        allCountryStringTypes.add("UK");
                        break;
                    case 3:
                        allCountryStringTypes.add("Canada");
                        break;
                    default:
                        allCountryStringTypes.add("UNKNOWN");
                        break;
                }
            }
            stmt.close();
            resultSet.close();


            for (String str : allCountryStringTypes){
                if (Collections.frequency(exclusiveCountryStringTypes, str) < 1) {
                    exclusiveCountryStringTypes.add(str);
                }
            }

            for (String str : exclusiveCountryStringTypes){
                displayedCountryTypeList.add(str + ": " + Collections.frequency(allCountryStringTypes, str));
            }
            customerCountryList.setItems(displayedCountryTypeList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}