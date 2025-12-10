package com.example;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

class PrimeThread extends Thread {

    private int lowerBound;
    private int upperBound;
    private TextArea outputArea;
    private ProgressBar progressBar;

    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    private final PrimaryController controller;

    public PrimeThread(int lower, int upper, TextArea output, ProgressBar progress, PrimaryController controller) {
        this.lowerBound = lower;
        this.upperBound = upper;
        this.outputArea = output;
        this.progressBar = progress;
        this.controller = controller;
        setDaemon(true);
    }

    @Override
    public void run() {
        int currentNum = lowerBound;
        while (currentNum <= upperBound) {
            synchronized (this) {
                while (isPaused && !isStopped) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        if (isStopped) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
            if (isStopped) {
                break;
            }

            if (upperBound == Integer.MAX_VALUE && currentNum == Math.max(2, lowerBound)) {
                Platform.runLater(() -> progressBar.setProgress(-1));
            }

            if (isPrime(currentNum)) {
                int prime = currentNum;
                Platform.runLater(() -> {
                    outputArea.appendText(prime + "\n");
                });
            }

            if (upperBound != Integer.MAX_VALUE) {
                final double progress = (double) (currentNum - lowerBound) / (upperBound - lowerBound);
                Platform.runLater(() -> progressBar.setProgress(progress));
            } else {
                Platform.runLater(() -> progressBar.setProgress(-1));
            }

            currentNum++;
            
            try {
                Thread.sleep(1); 
            } catch (InterruptedException e) {
                isStopped = true;
                break;
            }
        }
        Platform.runLater(() -> {
            progressBar.setProgress(isStopped ? 0 : 1);
            
            if (!isStopped) {
                controller.setPrimeButtonsFinishedState();
            }
        });
    }

    private boolean isPrime(int n) {
        if (n < 2)
            return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public void pauseThread() {
        synchronized (this) {
            this.isPaused = true;
        }
    }

    public void resumeThread() {
        synchronized (this) {
            this.isPaused = false;
            notifyAll();
        }
    }

    public void stopThread() {
        this.isStopped = true;
        interrupt(); 
        synchronized (this) {
            notifyAll(); 
        }
    }
}