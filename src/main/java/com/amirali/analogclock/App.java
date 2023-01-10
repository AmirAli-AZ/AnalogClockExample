package com.amirali.analogclock;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

public class App extends Application {

    private Pane secondHand, minuteHand, hourHand;

    @Override
    public void start(Stage stage) throws Exception {
        var clock = new Polygon();
        clock.setFill(Color.TRANSPARENT);
        clock.setStrokeWidth(5);
        clock.setStroke(Color.web("#6F588D"));

        var clockHandRotation = getCurrentClockHandRotation();

        secondHand = new Pane();
        secondHand.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        secondHand.setRotate(clockHandRotation[2]);
        secondHand.setStyle("-fx-background-color: #de3131; -fx-background-radius: 5px;");

        minuteHand = new Pane();
        minuteHand.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        minuteHand.setRotate(clockHandRotation[1]);
        minuteHand.setStyle("-fx-background-color: #506198; -fx-background-radius: 5px;");

        hourHand = new Pane();
        hourHand.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        hourHand.setRotate(clockHandRotation[0]);
        hourHand.setStyle("-fx-background-color: #6F3B8D; -fx-background-radius: 5px;");

        var clockContainer = new StackPane(clock, secondHand, minuteHand, hourHand);
        clockContainer.prefWidthProperty().addListener((observableValue, number, newValue) -> {
            if (newValue.doubleValue() > clockContainer.getPrefHeight())
                return;
            createPoints(clock, newValue.doubleValue() / 2, 12);
        });
        clockContainer.prefHeightProperty().addListener((observableValue, number, newValue) -> {
            if (newValue.doubleValue() > clockContainer.getPrefWidth())
                return;
            createPoints(clock, newValue.doubleValue() / 2, 12);
        });

        var tickingSound = new CheckBox("Ticking Sound");

        var audioClip = new AudioClip(Objects.requireNonNull(getClass().getResource("Ticking sound.m4a")).toExternalForm());
        var timer = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            if (tickingSound.isSelected())
                audioClip.play();

            var currentClockHandRotation = getCurrentClockHandRotation();
            secondHand.setRotate(currentClockHandRotation[2]);
            minuteHand.setRotate(currentClockHandRotation[1]);
            hourHand.setRotate(currentClockHandRotation[0]);
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();

        var top = new HBox(tickingSound);
        top.setPadding(new Insets(5));

        var root = new VBox(top, clockContainer);
        root.setStyle("-fx-background-color: white;");

        var scene = new Scene(root, 900, 600);
        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();

        clockContainer.prefWidthProperty().bind(root.widthProperty());
        clockContainer.prefHeightProperty().bind(root.heightProperty().subtract(top.heightProperty()));
    }

    public static void main(String[] args) {
        launch(args);
    }

    private int[] getCurrentClockHandRotation() {
        var time = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
        return new int[] {time.get(Calendar.HOUR) * 30, time.get(Calendar.MINUTE) * 6, time.get(Calendar.SECOND) * 6};
    }

    private void createPoints(Polygon polygon, double radius, int sides) {
        polygon.getPoints().clear();
        var angleStep = Math.PI * 2 / sides;
        var angle = 0.0;
        for (int i = 0; i < sides; i++) {
            angle += angleStep;
            polygon.getPoints().addAll(
                    Math.sin(angle) * radius,
                    Math.cos(angle) * radius
            );
        }

        resizeHands(secondHand, 2.5/3.0, 10, radius);
        resizeHands(minuteHand, 2.0/3.0, 10, radius);
        resizeHands(hourHand, 1.0/3.0, 10, radius);
    }

    private void resizeHands(Pane pane, double ratio, double width, double height) {
        pane.setPrefSize(width, height * ratio);
        if (pane.getTransforms().isEmpty())
            pane.getTransforms().add(new Translate(0, -pane.getPrefHeight()/2));
        else
            pane.getTransforms().set(0, new Translate(0, -pane.getPrefHeight()/2));
    }
}
