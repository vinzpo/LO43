package controller;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Application;

public class ControlleurSlider implements ChangeListener {

	private Application app;
	
	public ControlleurSlider(Application app) {
		this.app = app;
	}

	public void stateChanged(ChangeEvent evt) {
		JSlider slider = (JSlider) evt.getSource();
		float val = (float)slider.getValue()/100;
		float oldVal = app.getZoom();
		app.modifierZoom(val - oldVal);
	}

}
