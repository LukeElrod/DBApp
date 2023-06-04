package luke.qam2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import luke.qam2.HelperFunctions;
import luke.qam2.model.Appointment;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller class for the Login Form
 */
public class LoginFormController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label locationLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label loginLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label userIdLabel;

    /**
     * Logs the user in with the inputted credentials.
     * Lambdas are used here to check the users language before outputting the message
     * @param event
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    void login(ActionEvent event) throws SQLException, IOException {
        //run checks
        if (usernameField.getText().isBlank() || passwordField.getText().isBlank()) {
            HelperFunctions.displayError(() -> {
                if (HelperFunctions.usersLanguage == "fr") {
                    return "veuillez saisir un identifiant et un mot de passe valides.";
                } else {
                    return "Please enter a valid user id and password.";
                }
            });
            //log attempt
            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            fileWriter.write("FAILED LOGIN: Invalid credentials inputted at " + LocalDateTime.now() + "\n");
            fileWriter.close();
            return;
        }

        Statement stmt = HelperFunctions.connection.createStatement();
        String query = "SELECT * FROM client_schedule.users WHERE User_Name = '" + usernameField.getText() + "' AND Password = '" + passwordField.getText() + "'";
        ResultSet resultSet = stmt.executeQuery(query);

        //look for a valid user account
        if (resultSet.next()) {
            //successful login
            stmt.close();
            resultSet.close();
            root = FXMLLoader.load(getClass().getResource("/luke/qam2/MainForm.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            //log attempt
            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            fileWriter.write("SUCCESSFUL LOGIN: Valid credentials inputted at " + LocalDateTime.now() + "\n");
            fileWriter.close();

            //calculate if there is a nearby appt
            HelperFunctions.updateCustomersAndAppointments();
            for (Appointment appt : HelperFunctions.allAppointments){
                int apptStart = (appt.start.getHour() * 60) + appt.start.getMinute();
                int currentTime = (LocalDateTime.now().getHour() * 60) + LocalDateTime.now().getMinute();
                int diff = apptStart - currentTime;
                int time = Math.abs(diff);
                if (time <= 15){
                    HelperFunctions.displayMessage(()->{return "An appointment is scheduled to start within 15 minutes\nAppointment ID: " + appt.appointmentId + " Appointment Time: " + appt.start;});
                    return;
                }
            }

            HelperFunctions.displayMessage(()->{return "No appointment is scheduled to start within 15 minutes";});
            return;
        } else {
            //failed login
            HelperFunctions.displayError(() -> {
                if (HelperFunctions.usersLanguage == "fr") {
                    return "Aucun utilisateur trouvé";
                } else {
                    return "No user found";
                }
            });
            stmt.close();
            resultSet.close();

            //log attempt
            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            fileWriter.write("FAILED LOGIN: Incorrect credentials inputted at " + LocalDateTime.now() + "\n");
            fileWriter.close();
        }

    }

    /**
     * Called on initialization
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (HelperFunctions.usersLanguage == "fr") {
            locationLabel.setText("emplacement: " + ZoneId.systemDefault());
            loginButton.setText("connexion");
            loginLabel.setText("connexion");
            passwordLabel.setText("le mot de passe:");
            userIdLabel.setText("identifiant d'utilisateur:");
        } else {
            locationLabel.setText("Location: " + ZoneId.systemDefault());
        }
    }
}
