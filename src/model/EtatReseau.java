package model;

// Classe stockant un element d'un plus court chemin
public class EtatReseau
{
	// Numero de la Node (numéro du point du reseau routier)
	public int n;
	// Autres donnees
	public int g;

	// Constructeur
	public EtatReseau(int nde, int goal)
	{
		n = nde;
		g = goal;
	}
}
