package controller;
//
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import model.Application;

public class ControlleurBoutons implements ActionListener {
	
	private Application app;
	
	public ControlleurBoutons(Application app) {
		this.app = app;
	}

	public void actionPerformed(ActionEvent evt) {
		JButton bouton_clique = (JButton) evt.getSource();
		String nom_bouton = bouton_clique.getName();
		if (nom_bouton.equals("jbOk")) {
			app.lireComboboxPourRechercheItineraire();
		}
		else if (nom_bouton.equals("jbZoomPlus")) {
			app.modifierZoom((float)Application.CRAN_ZOOM_PLUS);
		}
		else if (nom_bouton.equals("jbZoomMoins")) {
			app.modifierZoom((float)Application.CRAN_ZOOM_MOINS);
		}
		else if (nom_bouton.equals("jbZoomReel")) {
			app.modifierZoom(Application.RETOUR_ZOOM_INITIAL);
		}
		else if (nom_bouton.equals("jbZoomGlobal")) {
			app.determinerZoomGlobal();
		}
		else if (nom_bouton.equals("jbZoomGrosPlan")) {
			app.modifierZoom((float)Application.ZOOM_MAX);
		}
	}
	
}
