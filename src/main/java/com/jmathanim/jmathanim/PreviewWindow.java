/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Renderers.Renderer;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PreviewWindow extends Frame {

    Renderer renderer;
    public JPanel drawPanel;
    public JPanel buttonsPanel;
    public JLabel statusLabel;
    public JToggleButton pauseToggleButton;

    public PreviewWindow(Renderer r) throws HeadlessException {
        super();
        this.renderer = r;

    }

    public void buildGUI() {
        this.setSize(renderer.getWidth() + 20, renderer.getHeight() + 60);//TODO: Scale window to fixed size
//            frame.setLayout(new BasicSplitPaneUI.BasicVerticalLayoutManager());
        this.setLayout(new BorderLayout(0, 10));
        drawPanel = new JPanel();
        buttonsPanel = new JPanel();
        drawPanel.setBounds(0, 0, renderer.getWidth(), renderer.getHeight());//x axis, y axis, width, height  
        this.add(drawPanel, BorderLayout.CENTER);
        statusLabel = new JLabel("");

        pauseToggleButton = new JToggleButton("Pause");
        buttonsPanel.add(pauseToggleButton);

        buttonsPanel.add(statusLabel);
//            buttonsPanel.add(stepButton);
//            stepButton.setEnabled(false);

        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.add(statusLabel, BorderLayout.NORTH);
    }

}
