package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import model.Application;
import view.PanelControles;

public class ControlleurComboBox implements ActionListener {
	private Application app;
	
	public ControlleurComboBox(Application app) {
		this.app = app;
	}
	
	public void actionPerformed(ActionEvent evt) {
		JComboBox box_clique = (JComboBox) evt.getSource();
		String nom_box = box_clique.getName();
		if (nom_box.equals("jcbVilleDepart")) {
			app.selectionnerFiltre(PanelControles.jcbFlag.DEPART);
		}
		else if (nom_box.equals("jcbVilleArrivee")) {
			app.selectionnerFiltre(PanelControles.jcbFlag.ARRIVEE);
		}
		else if (nom_box.equals("jcbRueDepart")) {
			app.remplirListePoints(PanelControles.jcbFlag.DEPART);
		}
		else if (nom_box.equals("jcbRueArrivee")) {
			app.remplirListePoints(PanelControles.jcbFlag.ARRIVEE);
		}
	}
}