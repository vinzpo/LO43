package model;
import java.util.Vector;


public class Route {
	// Sens de la route
	private int sens;
	// Liste des points
	private Vector<Integer> points;
	
	// Constructeur
	public Route(int s) {
		points = new Vector<Integer>();
		sens = s;
	}
	
	// Ajoute un numéro de point à la route
	public void ajouterNumPoint(Integer num) {
		points.add(num);
	}

	// Renvoie le point de position pos
	public Integer getNumPoint(int pos) {
		return points.get(pos);
	}
	
	// Renvoie le nombre de points constituants la route
	public int getNombrePoints() {
		return points.size();
	}
	
	// Renvoie la liste des points
	public Vector<Integer> getPoints() {
		return points;
	}

	/**
	 * @return the sens
	 */
	public int getSens() {
		return sens;
	}
}
