module com.secret.dictionary.secretdictionarydesktop {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.secret.dictionary to javafx.fxml;
    exports com.secret.dictionary;
    exports com.secret.dictionary.controller;
    opens com.secret.dictionary.controller to javafx.fxml;
}