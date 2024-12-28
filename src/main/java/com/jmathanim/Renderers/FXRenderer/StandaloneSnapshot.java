/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers.FXRenderer;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

/**
 * A class to ensure a StandAlone JavaFX thread that allows to take snapshots to
 * generate the movie
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class StandaloneSnapshot {

    public int w, h;

    public static class FXStarter extends Application {

        public static Stage stage;
        private static final CountDownLatch latch = new CountDownLatch(1);

        public static void waitForInit() throws InterruptedException {
            latch.await();
        }

        @Override
        public void init() {
            latch.countDown();
        }

        @Override
        public void start(Stage primaryStage) {
            stage = primaryStage;
            stage.setTitle("JMathAnim preview window");
        }
    }
}
