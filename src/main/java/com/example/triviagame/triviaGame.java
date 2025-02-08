package com.example.triviagame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.Base64;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Random;
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
    private VBox buttonsArea;
    private static final String API_URL = "https://opentdb.com/api.php?amount=10&type=multiple";
    private triviaResponse tResponse;
    private int round;
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
        this.buttonsArea = new VBox(20);
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
        this.buttonOne.setOnAction(e -> this.answerClicked(tResponse,round,buttonOne));
        this.buttonTwo.setOnAction(e -> this.answerClicked(tResponse,round,buttonTwo));
        this.buttonThree.setOnAction(e -> this.answerClicked(tResponse,round,buttonThree));
        this.buttonFour.setOnAction(e -> this.answerClicked(tResponse,round,buttonFour));

        Scene scene = new Scene(root, 600, 500);
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
            tResponse = GSON.fromJson(jsonString, triviaResponse.class);
            this.setQuestions(tResponse, 0);
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
    }

    public void setQuestions(triviaResponse response, int pos) {
        this.questionLabel.setText(response.results[pos].question);
        Random random = new Random();
        int num = random.nextInt(4);
        int incNum = 0;
        if (num == 0) {
            this.buttonOne.setText(response.results[pos].correct_answer);
        }
        else {
            this.buttonOne.setText(response.results[pos].incorrect_answers[incNum]);
            incNum++;
        }
        if (num == 1) {
            this.buttonTwo.setText(response.results[pos].correct_answer);
        }
        else {
            this.buttonTwo.setText(response.results[pos].incorrect_answers[incNum]);
            incNum++;
        }
        if (num == 2) {
            this.buttonThree.setText(response.results[pos].correct_answer);
        }
        else {
            this.buttonThree.setText(response.results[pos].incorrect_answers[incNum]);
            incNum++;
        }
        if (num == 3) {
            this.buttonFour.setText(response.results[pos].correct_answer);
        }
        else {
            this.buttonFour.setText(response.results[pos].incorrect_answers[incNum]);
        }
    }

    public void answerClicked(triviaResponse response, int pos, Button choice) {
        if (choice.getText().equals(response.results[pos].correct_answer)) {
            this.points++;
            this.scoreLabel.setText("Points: " + points);
        }
        this.round++;
           if (round < 10) {
               this.setQuestions(tResponse, round);
           }
        else {
            this.questionLabel.setText("You got " +points+ "/10 correct!");
            this.buttonOne.setDisable(true);
            this.buttonTwo.setDisable(true);
            this.buttonThree.setDisable(true);
            this.buttonFour.setDisable(true);
        }
    }
}