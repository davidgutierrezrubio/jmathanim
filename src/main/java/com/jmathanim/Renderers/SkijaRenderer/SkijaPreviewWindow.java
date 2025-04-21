package com.jmathanim.Renderers.SkijaRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class SkijaPreviewWindow {
    private final AtomicReference<BufferedImage> currentImage = new AtomicReference<>();
    private final int width, height;
    private final AtomicBoolean keepRunning;
    private JFrame frame;
    private JPanel panel;
    AtomicReference<JFrame> frameRef;
    public SkijaPreviewWindow(int width, int height, AtomicBoolean keepRunning, AtomicReference<JFrame> frameRef) {
        this.width = width;
        this.height = height;
        this.keepRunning = keepRunning;
        this.frameRef=frameRef;
    }

    public void show() {
        Thread swingThread = new Thread(() -> SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Previsualización");
            frameRef.set(frame);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    BufferedImage img = currentImage.get();
                    if (img != null) {
                        g.drawImage(img, 0, 0, null);
                    }
                }
            };


            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    keepRunning.set(false);
                }
            });

            frame.add(panel);
            panel.setSize(width, height);
            frame.setSize(width, height);
            frame.setVisible(true);
        }));
        swingThread.setDaemon(true);
        swingThread.start();
    }

    public void updateImage(BufferedImage image) {
        currentImage.set(image);
        if (panel != null) {
            panel.repaint();
        }
    }

    public boolean isVisible() {
        return frame != null && frame.isVisible();
    }
}

