module com.example.triviagame {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;


    opens com.example.triviagame to javafx.fxml;
    exports com.example.triviagame;
}