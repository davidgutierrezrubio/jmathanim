/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class StandaloneSnapshot {
    public int w,h;

    public static class FXStarter extends Application {

//        public static Scene scene;
public static Stage stage;
        private static final CountDownLatch latch = new CountDownLatch(1);

        public static void awaitFXToolkit() throws InterruptedException {
            latch.await();
        }
        

        @Override
        public void init() {
            
//            Group group=new Group();
//            scene = new Scene(group, 800, 600);
            
            latch.countDown();
        }

        @Override
        public void start(Stage primaryStage) {
            stage=primaryStage;
            stage.setTitle("JMathAnim preview window");
//            stage.setScene(scene);
//            stage.show();
        }
    }

    protected void manipulatePdf() throws Exception {

        WritableImage img1 = image1();
        // do something with the image:
        BufferedImage bImg1 = SwingFXUtils.fromFXImage(img1, new BufferedImage((int) img1.getWidth(), (int) img1.getHeight(), BufferedImage.TYPE_INT_ARGB));
        ImageIO.write(bImg1, "png", new File("rect.png"));

        WritableImage img2 = image2();
        // do something with the image:
        BufferedImage bImg2 = SwingFXUtils.fromFXImage(img2, new BufferedImage((int) img2.getWidth(), (int) img2.getHeight(), BufferedImage.TYPE_INT_ARGB));
        ImageIO.write(bImg2, "png", new File("chart.png"));
    }

    private WritableImage image1() throws Exception {

        Rectangle rectangle = new Rectangle();
        rectangle.setX(50);
        rectangle.setY(50);
        rectangle.setWidth(300);
        rectangle.setHeight(20);

        rectangle.setStroke(Color.WHITE);

        LinearGradient linearGrad = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0f, Color.rgb(255, 0, 0, 1)), new Stop(0.25f,
                        Color.rgb(255, 255, 0, 1)), new Stop(0.5f, Color.rgb(
                        255, 255, 255, 1)), new Stop(0.75f, Color.rgb(124, 252,
                        0, 1)), new Stop(1.0f, Color.rgb(0, 255, 0, 1)));
        rectangle.setFill(linearGrad);

        FutureTask<WritableImage> task = new FutureTask<>(() -> {
            new Scene(new Pane(rectangle));
            WritableImage img = new WritableImage((int) rectangle.getWidth(),
                    (int) rectangle.getHeight());
            rectangle.snapshot(null, img);
            return img;
        });

        Platform.runLater(task);

        // wait for FX Application Thread to return image, and return the result:
        return task.get();

    }

    private WritableImage image2() throws Exception {

        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        Random rng = new Random();
        Series<String, Number> series = new Series<>();
        series.setName("Data");
        for (int i = 1; i <= 10; i++) {
            series.getData().add(new Data<>("Group " + i, rng.nextDouble()));
        }
        chart.getData().add(series);

        FutureTask<WritableImage> task = new FutureTask<>(() -> {
            new Scene(chart);
            WritableImage img = chart.snapshot(null, null);
            return img;
        });

        Platform.runLater(task);
        return task.get();
    }
}
