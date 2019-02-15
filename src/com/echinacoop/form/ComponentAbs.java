package com.echinacoop.form;

import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;

import javax.swing.JPanel;
public abstract class ComponentAbs extends JPanel {

	public ComponentAbs() {
		this.addMouseListener(new ComponentMouseListenr(this));
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}
	
	public boolean isChat() {
		return false;
	}

	public void setChat(boolean isChat) {
	}
	
	public void resetBack(){
		
	}

}

class ComponentMouseListenr implements MouseListener {
	private ComponentAbs pb;

	public ComponentMouseListenr(ComponentAbs pb) {
		this.pb = pb;
	}

	public void mouseClicked(MouseEvent e) {
		pb.mouseClicked(e);
		pb.updateUI();
	}

	public void mouseEntered(MouseEvent e) {
		pb.mouseEntered(e);
		pb.updateUI();
	}

	public void mouseExited(MouseEvent e) {
		pb.mouseExited(e);
		pb.updateUI();
	}

	public void mousePressed(MouseEvent e) {
		pb.mousePressed(e);
		pb.updateUI();
	}

	public void mouseReleased(MouseEvent e) {
		pb.mouseReleased(e);
		pb.updateUI();
	}

}