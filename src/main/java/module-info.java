module com.secret.dictionary.secretdictionarydesktop {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.secret.dictionary.secretdictionarydesktop to javafx.fxml;
    exports com.secret.dictionary.secretdictionarydesktop;
}