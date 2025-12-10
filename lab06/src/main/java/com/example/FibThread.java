package com.example;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

class FibThread extends Thread {

    private final int lowerBound;
    private final int upperBound;
    private final TextArea outputArea;
    private final ProgressBar progressBar;

    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    private PrimaryController controller;

    public FibThread(int lower, int upper, TextArea output, ProgressBar progress, PrimaryController controller) {
        this.lowerBound = lower;
        this.upperBound = upper;
        this.outputArea = output;
        this.progressBar = progress;
        this.controller = controller;
        setDaemon(true);
    }

    @Override
    public void run() {
        int a = 0;
        int b = 1;
        int count = 0;

        while (a <= upperBound && a >= 0) {
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

            if (a >= lowerBound) {
                long f = a;
                Platform.runLater(() -> outputArea.appendText(f + "\n"));
                count++;
            }

            final double progress = (double) count / upperBound;
            Platform.runLater(() -> progressBar.setProgress(progress));

            if (a > Integer.MAX_VALUE - b) { 
                break;
            }

            int next = a + b;
            a = b;
            b = next;

            if (next < 0) {
                break;
            }

            try {
                Thread.sleep(50);
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
