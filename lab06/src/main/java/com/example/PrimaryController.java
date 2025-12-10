package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class PrimaryController {

    @FXML
    private TextField primeLowerBoundField;
    @FXML
    private TextField primeUpperBoundField;
    @FXML
    private Button primeGenerateButton;
    @FXML
    private Button primePauseButton;
    @FXML
    private Button primeResumeButton;
    @FXML
    private Button primeStopButton;
    @FXML
    private Button primeResetButton;
    @FXML
    private TextArea primeTextArea;
    @FXML
    private ProgressBar primeProgressBar;

    @FXML
    private TextField fibLowerBoundField;
    @FXML
    private TextField fibUpperBoundField;
    @FXML
    private Button fibGenerateButton;
    @FXML
    private Button fibPauseButton;
    @FXML
    private Button fibResumeButton;
    @FXML
    private Button fibStopButton;
    @FXML
    private Button fibResetButton;
    @FXML
    private TextArea fibTextArea;
    @FXML
    private ProgressBar fibProgressBar;

    private PrimeThread primeThread;
    private FibThread fibThread;

    @FXML
    public void initialize() {
        primePauseButton.setDisable(true);
        primeResumeButton.setDisable(true);
        primeStopButton.setDisable(true);
        primeResetButton.setDisable(true);

        fibPauseButton.setDisable(true);
        fibResumeButton.setDisable(true);
        fibStopButton.setDisable(true);
        fibResetButton.setDisable(true);
    }

    @FXML
    private void handlePrimeGenerate(ActionEvent event) {

        if (primeThread != null && primeThread.isAlive()) {
            primeThread.stopThread();
            try {
                primeThread.join(100);
            } catch (InterruptedException ignored) {
            }
        }

        primeTextArea.clear();
        primeProgressBar.setProgress(0);

        int lowerBound, upperBound;
        try {
            lowerBound = getPrimeLowerBoundProperty();
            upperBound = getPrimeUpperBoundProperty();
            if (lowerBound > upperBound) {
                new Alert(Alert.AlertType.ERROR, "Lower bound must be smaller than Upper bound").showAndWait();
                return;
            }
            else if (lowerBound < 0) {
                new Alert(Alert.AlertType.ERROR, "Lower bound must be positive").showAndWait();
                return;
            }
            else if (upperBound < 2) {
                new Alert(Alert.AlertType.ERROR, "Upper bound must be greater than 1").showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format for bounds").showAndWait();
            return;
        }

        primeThread = new PrimeThread(lowerBound, upperBound, primeTextArea, primeProgressBar, this);
        primeThread.start();

        primeGenerateButton.setDisable(true);
        primePauseButton.setDisable(false);
        primeResumeButton.setDisable(true);
        primeStopButton.setDisable(false);
        primeResetButton.setDisable(false);
    }

    @FXML
    private void handlePrimePause(ActionEvent event) {
        if (primeThread != null && primeThread.isAlive()) {
            primeThread.pauseThread();
            primePauseButton.setDisable(true);
            primeResumeButton.setDisable(false);
        }
    }

    @FXML
    private void handlePrimeResume(ActionEvent event) {
        if (primeThread != null) {
            primeThread.resumeThread();
            primePauseButton.setDisable(false);
            primeResumeButton.setDisable(true);
        }
    }

    @FXML
    private void handlePrimeStop(ActionEvent event) {
        if (primeThread != null && primeThread.isAlive()) {
            primeThread.stopThread();
        }
        primeGenerateButton.setDisable(false);
        primePauseButton.setDisable(true);
        primeResumeButton.setDisable(true);
        primeStopButton.setDisable(true);
        primeResetButton.setDisable(false);
        primeProgressBar.setProgress(0);
    }

    @FXML
    private void handlePrimeReset(ActionEvent event) {
        handlePrimeStop(event);

        primeLowerBoundField.clear();
        primeUpperBoundField.clear();
        primeTextArea.clear();

        primeResetButton.setDisable(true);
        primeProgressBar.setProgress(0);
    }

    @FXML
    private void handleFibGenerate(ActionEvent event) {
        if (fibThread != null && fibThread.isAlive()) {
            fibThread.stopThread();
            try {
                fibThread.join(100);
            } catch (InterruptedException ignored) {
            }
        }

        fibTextArea.clear();
        fibProgressBar.setProgress(0);

        int lowerBound, upperBound;
        try {
            lowerBound = getFibLowerBoundProperty();
            upperBound = getFibUpperBoundProperty();
            if (lowerBound > upperBound) {
                new Alert(Alert.AlertType.ERROR, "Lower bound must be smaller than Upper bound").showAndWait();
                return;
            }
            else if (lowerBound < 0) {
                new Alert(Alert.AlertType.ERROR, "Lower bound must be positive").showAndWait();
                return;
            }
            else if (upperBound < 1) {
                new Alert(Alert.AlertType.ERROR, "Upper bound must be greater than 0").showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format for bounds").showAndWait();
            return;
        }

        fibThread = new FibThread(lowerBound, upperBound, fibTextArea, fibProgressBar, this);
        fibThread.start();

        fibGenerateButton.setDisable(true);
        fibPauseButton.setDisable(false);
        fibResumeButton.setDisable(true);
        fibStopButton.setDisable(false);
        fibResetButton.setDisable(false);
    }

    @FXML
    private void handleFibPause(ActionEvent event) {
        if (fibThread != null && fibThread.isAlive()) {
            fibThread.pauseThread();
            fibPauseButton.setDisable(true);
            fibResumeButton.setDisable(false);
        }
    }

    @FXML
    private void handleFibResume(ActionEvent event) {
        if (fibThread != null) {
            fibThread.resumeThread();
            fibPauseButton.setDisable(false);
            fibResumeButton.setDisable(true);
        }
    }

    @FXML
    private void handleFibStop(ActionEvent event) {
        if (fibThread != null && fibThread.isAlive()) {
            fibThread.stopThread();
        }
        fibGenerateButton.setDisable(false);
        fibPauseButton.setDisable(true);
        fibResumeButton.setDisable(true);
        fibStopButton.setDisable(true);
        fibResetButton.setDisable(false);
        fibProgressBar.setProgress(0);
    }

    @FXML
    private void handleFibReset(ActionEvent event) {
        handleFibStop(event);
        fibLowerBoundField.clear();
        fibUpperBoundField.clear();
        fibTextArea.clear();
        fibResetButton.setDisable(true);
        fibProgressBar.setProgress(0);
    }

    private int getPrimeLowerBoundProperty() {
        return primeLowerBoundField.getText().isEmpty() ? 2 : Integer.parseInt(primeLowerBoundField.getText());
    }

    private int getPrimeUpperBoundProperty() {
        return primeUpperBoundField.getText().isEmpty() ? Integer.MAX_VALUE
                : Integer.parseInt(primeUpperBoundField.getText());
    }

    private int getFibLowerBoundProperty() {
        return fibLowerBoundField.getText().isEmpty() ? 2 : Integer.parseInt(fibLowerBoundField.getText());
    }

    private int getFibUpperBoundProperty() {
        return fibUpperBoundField.getText().isEmpty() ? Integer.MAX_VALUE
                : Integer.parseInt(fibUpperBoundField.getText());
    }

    public void setPrimeButtonsFinishedState() {
        primeGenerateButton.setDisable(false);
        primePauseButton.setDisable(true);
        primeResumeButton.setDisable(true);
        primeStopButton.setDisable(true);
        primeResetButton.setDisable(false);
    }

    public void setFibButtonsFinishedState() {
        fibGenerateButton.setDisable(false);
        fibPauseButton.setDisable(true);
        fibResumeButton.setDisable(true);
        fibStopButton.setDisable(true);
        fibResetButton.setDisable(false);
    }

}
