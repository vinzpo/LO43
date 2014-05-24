package view;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class PanelVue extends JScrollPane {
	
	private Carte carte;

	public PanelVue(Carte isCarte) {
		super(isCarte);
		carte = isCarte;
		setWheelScrollingEnabled(false);
		getViewport().setBackground(Color.white);
	}

	public Carte getCarte() {
		return carte;
	}
	
	public void deplacerCarte(int x, int y){
		Point positionCourante = getViewport().getViewPosition();
		int newX = (int)positionCourante.getX() + x;
		int newY = (int)positionCourante.getY() + y;
	
		newX = resituerX(newX);
		newY = resituerY(newY);
		
		getViewport().setViewPosition(new Point(newX, newY));
	}
	
	// Ces deux classes repositionnent X et Y pour ne pas afficher en dehors de la carte
	public int resituerX(int X){
		int ancreMaxX = (int) (carte.getWidth() - getViewport().getSize().getWidth());
		if(X > ancreMaxX) X = ancreMaxX;
		if(X < 0) X = 0;
		return X;
	}
	
	public int resituerY(int Y){
		int ancreMaxY = (int) (carte.getHeight() - getViewport().getSize().getHeight());
		if(Y > ancreMaxY) Y = ancreMaxY;
		if(Y < 0) Y = 0;
		return Y;
	}
	
	public void ajouterEcouteurScrollBar(MouseListener ecouteur) {
		this.getHorizontalScrollBar().addMouseListener(ecouteur);
		this.getVerticalScrollBar().addMouseListener(ecouteur);
	}
}
