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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import luke.qam2.HelperFunctions;

import javax.xml.transform.Result;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller class for the Update Customer form
 */
public class UpdateCustomerFormController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField customerAddressTextField;

    @FXML
    private ComboBox<String> customerCountryComboBox;

    @FXML
    private ComboBox<String> customerDivisionComboBox;

    @FXML
    private TextField customerIdTextField;

    @FXML
    private TextField customerNameTextField;

    @FXML
    private TextField customerPhoneTextField;

    @FXML
    private TextField customerPostalCodeTextField;

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
     * Updates the given customer
     * @param event
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    void saveCustomer(ActionEvent event) throws SQLException, IOException {

        if (customerNameTextField.getText().isBlank() || customerAddressTextField.getText().isBlank() || customerPostalCodeTextField.getText().isBlank() || customerPhoneTextField.getText().isBlank() || customerCountryComboBox.getSelectionModel().isEmpty() || customerDivisionComboBox.getSelectionModel().isEmpty()){
            HelperFunctions.displayError(()-> {return "Please enter a valid value for each entry.";});
            return;
        }

        PreparedStatement pstmt = HelperFunctions.connection.prepareStatement("UPDATE client_schedule.customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Division_ID=? WHERE Customer_ID = " + HelperFunctions.currentlySelectedCustomer.customerId, Statement.RETURN_GENERATED_KEYS);
        Statement stmt = HelperFunctions.connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division = '" + customerDivisionComboBox.getSelectionModel().getSelectedItem() + "'");
        resultSet.next();

        pstmt.setString(1, customerNameTextField.getText());
        pstmt.setString(2, customerAddressTextField.getText());
        pstmt.setString(3, customerPostalCodeTextField.getText());
        pstmt.setString(4, customerPhoneTextField.getText());
        pstmt.setInt(5, resultSet.getInt("Division_ID"));
        stmt.close();
        resultSet.close();

        pstmt.executeUpdate();

        pstmt.close();

        returnToMainForm(event);
    }

    /**
     * Populated the first-level-divitions of the selected country
     * @param event
     * @throws SQLException
     */
    @FXML
    void populateFirstLevelDivisions(ActionEvent event) throws SQLException {
        int id;
        ObservableList<String> divisionList = FXCollections.observableList(new ArrayList<String>());
        customerDivisionComboBox.setItems(divisionList);
        switch (customerCountryComboBox.getSelectionModel().getSelectedItem()){
            case "U.S":
                id = 1;
                break;
            case "UK":
                id = 2;
                break;
            case "Canada":
                id = 3;
                break;
            default:
                return;
        }
        Statement stmt = HelperFunctions.connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT Division FROM client_schedule.first_level_divisions WHERE Country_ID = " + id);
        while (resultSet.next()){
            divisionList.add(resultSet.getString(1));
        }
        stmt.close();
        resultSet.close();
    }

    /**
     * Called on initialization
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> countryList = FXCollections.observableList(new ArrayList<String>());
        countryList.addAll("U.S", "UK", "Canada");
        customerCountryComboBox.setItems(countryList);

        //auto-populate entries
        customerIdTextField.setText(String.valueOf(HelperFunctions.currentlySelectedCustomer.customerId));
        customerNameTextField.setText(HelperFunctions.currentlySelectedCustomer.customerName);
        customerAddressTextField.setText(HelperFunctions.currentlySelectedCustomer.address);
        customerPostalCodeTextField.setText(HelperFunctions.currentlySelectedCustomer.postalCode);
        customerPhoneTextField.setText(HelperFunctions.currentlySelectedCustomer.phone);

        try {
            Statement stmt = HelperFunctions.connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM client_schedule.countries INNER JOIN client_schedule.first_level_divisions USING(Country_ID) WHERE Division_ID = " + HelperFunctions.currentlySelectedCustomer.divisionId);
            resultSet.next();
            customerCountryComboBox.getSelectionModel().select(resultSet.getString("Country"));
            populateFirstLevelDivisions(new ActionEvent());
            stmt.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            Statement stmt = HelperFunctions.connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT Division FROM client_schedule.first_level_divisions WHERE Division_ID = " + HelperFunctions.currentlySelectedCustomer.divisionId);
            resultSet.next();
            customerDivisionComboBox.getSelectionModel().select(resultSet.getString("Division"));
            stmt.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
