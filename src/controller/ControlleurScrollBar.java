package controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import model.Application;

public class ControlleurScrollBar implements MouseListener{

	private Application app;
	
	public ControlleurScrollBar(Application app){
		this.app = app;
	}
	
	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	//update le centre apres un deplacement a partir de la Scroll Bar
	public void mouseReleased(MouseEvent e) {
		app.updateCentre();
	}
	
}