package com.echinacoop.form;

import java.awt.Dimension;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BasePanel extends JPanel {

	private int width = 800;
	private int height = 600;

	public BasePanel() {
		setPreferredSize(new Dimension(width, height));
	}

	public BasePanel(int width, int height) {
		setPreferredSize(new Dimension(width, height));
	}

	public void showPanel() {
		this.setVisible(true);
	}

}
