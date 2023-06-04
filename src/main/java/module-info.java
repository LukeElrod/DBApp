module luke.qam2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens luke.qam2 to javafx.fxml;
    opens luke.qam2.controllers to javafx.fxml;
    exports luke.qam2;
    exports luke.qam2.controllers;
}