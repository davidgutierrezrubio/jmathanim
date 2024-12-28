/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Renderers.Renderer;

import javax.swing.*;
import java.awt.*;

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
		this.setSize(renderer.getMediaWidth() + 20, renderer.getMediaHeight() + 60);// TODO: Scale window to fixed size
//            frame.setLayout(new BasicSplitPaneUI.BasicVerticalLayoutManager());
		this.setLayout(new BorderLayout(0, 10));
		drawPanel = new JPanel();
		buttonsPanel = new JPanel();
		drawPanel.setBounds(0, 0, renderer.getMediaWidth(), renderer.getMediaHeight());// x axis, y axis, width, height
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
