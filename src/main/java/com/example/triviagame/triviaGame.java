package com.example.triviagame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;

public class triviaGame extends Application {
    private Label questionLabel;
    private VBox optionsBox;
    private Label scoreLabel;
    private int points;
    private Button buttonOne;
    private Button buttonTwo;
    private Button buttonThree;
    private Button buttonFour;
    private Button nextButton;
    private HBox buttonsArea;
    private static final String API_URL = "https://opentdb.com/api.php?amount=10&type=multiple";
    /**
     * HTTP client.
     */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    /**
     * Google {@code Gson} object for parsing JSON-formatted strings.
     */
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public triviaGame() {
        this.points = 0;
        this.questionLabel = new Label("Loading question...");
        this.optionsBox = new VBox(10);
        this.scoreLabel = new Label("Points: 0");
        this.buttonsArea = new HBox(20);
        this.buttonOne = new Button("");
        this.buttonTwo = new Button("");
        this.buttonThree = new Button("");
        this.buttonFour = new Button("");
        VBox root = new VBox(20, questionLabel, optionsBox, scoreLabel);
        root.setStyle("-fx-padding: 20px; -fx-alignment: center;");
    }

    @Override
    public void start(Stage stage) {
        questionLabel = new Label("Loading question...");
        optionsBox = new VBox(10);
        scoreLabel = new Label("Score: 0");
        this.buttonsArea.getChildren().addAll(this.buttonOne, this.buttonTwo, this.buttonThree, this.buttonFour);
        VBox root = new VBox(20, questionLabel, optionsBox, scoreLabel, buttonsArea);
        root.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        buttonsArea.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Trivia Game");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
        this.parseQuestion();
    }

    public void parseQuestion() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
            // ensure the request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            }
            String jsonString = response.body();
            System.out.println(jsonString);
            triviaResponse tResponse = GSON.fromJson(jsonString, triviaResponse.class);
            this.buttonOne.setText(tResponse.results[0].correct_answer);
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
    }
}