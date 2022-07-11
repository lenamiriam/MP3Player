package com.example.mp3musicplayer;

import java.io.File;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Controller implements Initializable {

    @FXML
    private Pane pane;

    @FXML
    private Label songLabel;

    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;

    @FXML
    private ComboBox<String> speedComboBox;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ProgressBar songProgressBar;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs; // playlist

    private int trackNumber;
    private final int[] speedsPercentages = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

    private Timer timer;
    private TimerTask task;

    private boolean running;

    @Override
    public void initialize(URL arg0, ResourceBundle resourceBundle) {
        songs = new ArrayList<>();
        directory = new File("tracks");
        files = directory.listFiles();

        if (files != null) {
            Collections.addAll(songs, files);
        }

        media = new Media(songs.get(trackNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(trackNumber).getName());

        for (int i = 0; i < speedsPercentages.length; i++) {
            speedComboBox.getItems().add(speedsPercentages[i] +"%");
        }

        speedComboBox.setOnAction(this::changeSpeed);
        volumeSlider.valueProperty().addListener((arg01, arg1, t1) -> mediaPlayer.setVolume(volumeSlider.getValue() * 0.01));
        songProgressBar.setStyle("-fx-accent: #99CCFF;");
    }

    public void playTrack() {
        beginTimer();
        changeSpeed(null);
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void pauseTrack() {
        cancelTimer();
        mediaPlayer.pause();
    }

    public void resetTrack() {
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void previousTrack() {
        if (trackNumber > 0) {
            trackNumber--;
        }
        else {
            trackNumber = songs.size() - 1;
        }
        mediaPlayer.stop();

        if (running) {
            cancelTimer();
        }
        media = new Media(songs.get(trackNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(trackNumber).getName());
        playTrack();
    }

    public void nextTrack() {
        if (trackNumber < songs.size() - 1) {
            trackNumber++;
        }
        else {
            trackNumber = 0;
        }
        mediaPlayer.stop();
        
        if (running) {
            cancelTimer();
        }
        media = new Media(songs.get(trackNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(trackNumber).getName());
        playTrack();
    }

    public void changeSpeed(ActionEvent event) {
        if (speedComboBox.getValue() == null) {
            mediaPlayer.setRate(1);
        }
        else {
            mediaPlayer.setRate(Integer.parseInt(speedComboBox.getValue().substring(0, speedComboBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current/end);

                if(current/end == 1) {
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
    }
}