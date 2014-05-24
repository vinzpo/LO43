package controller;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

import model.Application;

@SuppressWarnings("serial")
//MouseInputListener interface utilise MouseMotionListener + MouseListener
public class ControlleurCarte implements MouseInputListener, MouseWheelListener {
	private Application app;
	
	// Constantes pour la molette
	private final float MOLETTE_TAUX = (float)-0.06; // (de)zoom par unite de molette
	private final int MOLETTE_TICKS = 3; // le zoom n'est effectué que tous les <MOLETTE_TICKS> ticks de la molette, afin d'éviter des rafraichissement trop fréquents
	
	// Nombre actuel de ticks de la molette
	private int nbTicks = 0;
	
	// Coordonnées du point de départ du Drag
	private int prevX = 0;
	private int prevY = 0;
	
	public ControlleurCarte(Application app) {
		this.app = app;
	}
    
    //Methods required by the MouseMotionListener interface
    public void mouseMoved(MouseEvent e) {
    	app.updateCoord(e.getX(),e.getY());
    }
    public void mouseDragged(MouseEvent e) {
    	app.deplacerCarte(prevX-e.getX(),prevY-e.getY());
    }
    
    //Methods required by the MouseListener interface
     public void mousePressed(MouseEvent e) {
    	 // Click gauche enfoncé
    	 if (e.getButton() == 1) {
	    	 app.modifierCurseurVue(Cursor.MOVE_CURSOR);
	    	 prevX = e.getX();
	    	 prevY = e.getY();
    	 }
     }
     public void mouseReleased(MouseEvent e) {
    	 app.modifierCurseurVue(Cursor.HAND_CURSOR);
     }
     public void mouseEntered(MouseEvent e) {
    	 app.modifierCurseurVue(Cursor.HAND_CURSOR);
     }
     public void mouseExited(MouseEvent e) {
    	 app.modifierCurseurVue(Cursor.DEFAULT_CURSOR);
     }
     public void mouseClicked(MouseEvent e) {
    	 // Click droit
    	 if (e.getButton() == 3) {
    		 app.afficherMenuContextuel();
    	 }
     }

     // Gestion de la molette de la souris
     public void mouseWheelMoved(MouseWheelEvent e) {
    	 if (++nbTicks >= MOLETTE_TICKS) {
    		 nbTicks = 0;
    		 app.modifierZoom(e.getWheelRotation() * MOLETTE_TAUX);
    	 }
     }
}
