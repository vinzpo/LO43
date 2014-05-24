package model;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import static java.lang.Math.*;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;

import view.*;
import controller.*;
d
public class Application {
	// Quelques constantes
	public final static float ZOOM_MAX = (float) 1;
	public final static float ZOOM_MIN = (float)0.1;
	public final static float ZOOM_INITIAL = (float) 0.5;
	public final static float CRAN_ZOOM_PLUS = (float) 0.1;
	public final static float CRAN_ZOOM_MOINS = (float) -0.1;
	public final static float RETOUR_ZOOM_INITIAL = -2;
	private final String DOSSIER_IMAGES = "img/";
	private final double ECHELLE_CARTE = 7.5; // <ECHELLE_CARTE> metres = 1 px
	private final int ECHELLE_TAILLE = 30;
	
	// Constantes des coordonnées 
	private final Point LAMBERT_HAUT_GAUCHE = new Point(897990, 2324046);
	private final Point LAMBERT_BAS_DROITE = new Point(971518, 2272510);
	private final Point PIXELS_BAS_DROITE = new Point(9807, 6867);
	private final String SYSTEME_UNITE = "Lambert II";
	private final int MARGE_REDIMENSIONNEMENT_AUTO = 50;
	
	// Constantes pour le filtre de Rues
	public final static String TOUTES = "Toutes";
	public final static String AUTOROUTE = "Autoroutes";
	public final static String EUROPEENNE = "Europ\u00e9enne";
	public final static String NATIONALE = "Nationale";
	public final static String DEPARTEMENTALE = "D\u00e9partementale";
	public enum rueFlag {TOUTES,AUTOROUTE,EUROPEENNE,NATIONALE,DEPARTEMENTALE,AUTRE};
	
	// Vue
	private Fenetre fenetre;
	private String lienCarte;
	
	// Controlleur
	private ControlleurBoutons controlleur_boutons;
	private ControlleurSlider controlleur_slider;
	private ControlleurCarte controlleur_carte;
	private ControlleurScrollBar controlleur_scroll_bar;
	private ControlleurComboBox controlleur_combo_box;
	private ControlleurMenuContextuel controlleur_menu_contextuel;
	
	// Réseau routier
	private ReseauRoutier reseau_routier;
	private PlusCourtChemin plus_court_chemin;
	private Vector<EtatReseau> chemin;
	
	// Point le plus proche de la souris
	private int point_proche_souris = -1;
	
	//Point au centre de l'ecran (memorise en coord taille reelle)
	private Point ptCentre = new Point(-1, -1);
	
	// Numéros des points de départ et d'arrivée
	private int depart = -1, arrivee = -1;
	private float pourcentage_zoom = ZOOM_INITIAL;
	private float old_zoom = ZOOM_INITIAL;
	
	public Application(String fichierXml) {
		// Construction des différents éléments de l'application
		reseau_routier = new ReseauRoutier();
		reseau_routier.parseXml(fichierXml);
		
		lienCarte = DOSSIER_IMAGES + reseau_routier.getNomFichierImage();
		new ImageIcon(lienCarte);
		
		controlleur_boutons = new ControlleurBoutons(this);
		controlleur_slider = new ControlleurSlider(this);
		controlleur_carte = new ControlleurCarte(this);
		controlleur_scroll_bar = new ControlleurScrollBar(this);
		controlleur_combo_box = new ControlleurComboBox(this);
		controlleur_menu_contextuel = new ControlleurMenuContextuel(this);
		
		fenetre = new Fenetre(lienCarte, SYSTEME_UNITE);
		fenetre.setVisible(true);
		
		plus_court_chemin = new PlusCourtChemin();
		plus_court_chemin.init(reseau_routier, ZOOM_INITIAL);
		chemin = new Vector<EtatReseau>();
		
		// Initialisations des différents éléments
		fenetre.getPanneauVue().getCarte().setTailleEchelle(ECHELLE_TAILLE);
		miseEnPlaceImages();
		remplirListesVilles();
		remplirListesRoutes(rueFlag.TOUTES, PanelControles.jcbFlag.BOTH);
		miseEnPlaceEcouteurs();
		initialiserListesPoints();
		updateCentre();
		afficherCarte();
	}
	
	private void miseEnPlaceImages() {
		fenetre.getPanneauControles().setIconZoomMoins(new ImageIcon(DOSSIER_IMAGES + "loupe_moins.gif"));
		fenetre.getPanneauControles().setIconZoomPlus(new ImageIcon(DOSSIER_IMAGES + "loupe_plus.gif"));
	}
	
	private void miseEnPlaceEcouteurs() {
		// Met en place les différents écouteurs pour les interractions avec l'utilisateur
		fenetre.getPanneauControles().ajouterEcouteurAuBoutonOk(controlleur_boutons);
		fenetre.getPanneauControles().ajouterEcouteurAuBoutonZoomMoins(controlleur_boutons);
		fenetre.getPanneauControles().ajouterEcouteurAuBoutonZoomPlus(controlleur_boutons);
		fenetre.getPanneauControles().ajouterEcouteurAuBoutonZoomReel(controlleur_boutons);
		fenetre.getPanneauControles().ajouterEcouteurAuBoutonZoomGlobal(controlleur_boutons);
		fenetre.getPanneauControles().ajouterEcouteurAuBoutonZoomGrosPlan(controlleur_boutons);
		fenetre.getPanneauControles().ajouterEcouteurAuSlider(controlleur_slider);
		fenetre.getPanneauControles().ajouterEcouteurVilleDepart(controlleur_combo_box);
		fenetre.getPanneauControles().ajouterEcouteurVilleArrivee(controlleur_combo_box);
		fenetre.getPanneauControles().ajouterEcouteurRueDepart(controlleur_combo_box);
		fenetre.getPanneauControles().ajouterEcouteurRueArrivee(controlleur_combo_box);
		fenetre.getPanneauVue().getCarte().ajouterEcouteurMenu(controlleur_menu_contextuel);
		fenetre.getPanneauVue().getCarte().ajouterEcouteurCarte(controlleur_carte);
		fenetre.getPanneauVue().ajouterEcouteurScrollBar(controlleur_scroll_bar);
	}
	
	public void remplirListesVilles() {
		// Remplit les liste des villes
		Object[] listeRoutes = reseau_routier.getListeRoutes().toArray();
		Arrays.sort(listeRoutes);
		fenetre.getPanneauControles().ajouterVilleDansCombobox(TOUTES);
		fenetre.getPanneauControles().ajouterVilleDansCombobox(AUTOROUTE);
		fenetre.getPanneauControles().ajouterVilleDansCombobox(EUROPEENNE);
		fenetre.getPanneauControles().ajouterVilleDansCombobox(NATIONALE);
		fenetre.getPanneauControles().ajouterVilleDansCombobox(DEPARTEMENTALE);
		
		String ville;
		NomRue rue;
		for (int l = 0; l < reseau_routier.getNombreRoutes(); l++) {			
			rue = new NomRue(listeRoutes[l]);
			if(rue.estUneVille()){
				ville = rue.extraireNomVille();
				if(!fenetre.getPanneauControles().villeDejaPresente(ville))
					fenetre.getPanneauControles().ajouterVilleDansCombobox(ville);
			}
		}
	}
	
	public void remplirListesRoutes(rueFlag filtre, PanelControles.jcbFlag flag) {
		Object[] listeRoutes = reseau_routier.getListeRoutes().toArray();
		Arrays.sort(listeRoutes);
		
		//Reinitialisation de la comboBox
		fenetre.getPanneauControles().viderRueComboBox(flag);
		
		//Remplissage en fonction du flag
		for (int l = 0; l < reseau_routier.getNombreRoutes(); l++) {		
			NomRue rue = new NomRue(listeRoutes[l]);
			
			switch (filtre){
			case TOUTES :
				fenetre.getPanneauControles().ajouterRouteDansCombobox(listeRoutes[l].toString(),flag);
				break;
				
			case AUTOROUTE :
				if(rue.estUneAutoroute())
					fenetre.getPanneauControles().ajouterRouteDansCombobox(listeRoutes[l].toString(),flag);
				break;
				
			case EUROPEENNE :
				if(rue.estUneEuropeenne())
					fenetre.getPanneauControles().ajouterRouteDansCombobox(listeRoutes[l].toString(),flag);
				break;
				
			case NATIONALE :	
				if(rue.estUneNationale())
					fenetre.getPanneauControles().ajouterRouteDansCombobox(listeRoutes[l].toString(),flag);
				break;
				
			case DEPARTEMENTALE :
				if(rue.estUneDepartementale())
					fenetre.getPanneauControles().ajouterRouteDansCombobox(listeRoutes[l].toString(),flag);
				break;
				
			default : break;
			}
		}
	}
	
	public void remplirListesRoutes(String filtre, PanelControles.jcbFlag flag) {
		Object[] listeRoutes = reseau_routier.getListeRoutes().toArray();
		Arrays.sort(listeRoutes);
	
		// Reinitialisation de la comboBox
		fenetre.getPanneauControles().viderRueComboBox(flag);
		
		// Insertion uniquement si la ville correspond
		for (int l = 0; l < reseau_routier.getNombreRoutes(); l++) {
			NomRue rue = new NomRue(listeRoutes[l]);
			if( rue.extraireNomVille().equals(filtre)) {
				fenetre.getPanneauControles().ajouterRouteDansCombobox(listeRoutes[l].toString(),flag);
			}
		}
	}
	
	public void remplirListePoints(PanelControles.jcbFlag flag) {
		String nom_route = fenetre.getPanneauControles().getNomRoute(flag);
		if (nom_route != null) {
			Vector<Integer> liste_points = reseau_routier.getRoute(nom_route).getPoints();
			fenetre.getPanneauControles().setPoints(flag, liste_points);
		}
	}
	
	// Selectionne le filtre a envoyer à la comboBox
	public void selectionnerFiltre(PanelControles.jcbFlag flag) {
		String selection = fenetre.getPanneauControles().getNomVille(flag);
		rueFlag rFlag = rueFlag.AUTRE;
		if(selection.equals(TOUTES))
			rFlag = rueFlag.TOUTES;
		else if(selection.equals(AUTOROUTE))
			rFlag = rueFlag.AUTOROUTE;
		else if(selection.equals(EUROPEENNE))
			rFlag = rueFlag.EUROPEENNE;
		else if(selection.equals(NATIONALE))
			rFlag = rueFlag.NATIONALE;
		else if(selection.equals(DEPARTEMENTALE))
			rFlag = rueFlag.DEPARTEMENTALE;
		
		if(rFlag == rueFlag.AUTRE)
			remplirListesRoutes(selection, flag);
		else	
			remplirListesRoutes(rFlag, flag);

	}
	
	private void initialiserListesPoints() {
		remplirListePoints(PanelControles.jcbFlag.DEPART);
		remplirListePoints(PanelControles.jcbFlag.ARRIVEE);
	}
	
	private void afficherCarte() {
		fenetre.getPanneauVue().getCarte().setEchelle(convertirUniteDistance(ECHELLE_TAILLE, pourcentage_zoom));
		fenetre.getPanneauVue().revalidate();
		recentrerVue(ptCentre);
	}
	
	public void lireComboboxPourRechercheItineraire() {
		// Lecture de la demande de l'utilisateur
		setDepart(fenetre.getPanneauControles().getNumPoint(PanelControles.jcbFlag.DEPART));
		setArrivee(fenetre.getPanneauControles().getNumPoint(PanelControles.jcbFlag.ARRIVEE));
		// Mise à jour de l'affichage
		chercherItineraire();
		repositionnerVue();
	}
	
	private void chercherItineraire() {
		// Résoue l'itinéraire et ajoute les points à la carte
		fenetre.getPanneauVue().getCarte().viderPoints();
		if (depart == arrivee) {
			fenetre.getPanneauInfos().setMessage("Veuillez choisir 2 points diff\u00e9rents !");
			setDepart(arrivee);
			setArrivee(-1);
			fenetre.getPanneauVue().getCarte().setTypePointUnique(true);
		}
		if ((depart >= 0) && (arrivee >= 0)) {
	    	chemin = plus_court_chemin.solve(depart, arrivee);
	    	EtatReseau pos;
	    	for (Iterator it = chemin.iterator(); it.hasNext();)
	    	{
	    		pos = (EtatReseau) it.next();
	    		fenetre.getPanneauVue().getCarte().ajouterPoint(plus_court_chemin.getNodeCoords(pos.n));
	    	}
	    	
	    	afficherListeRoutes();
		}
		else {
			if (depart >= 0) {
				fenetre.getPanneauVue().getCarte().ajouterPoint(plus_court_chemin.getNodeCoords(depart));
			}
			else if (arrivee >= 0) {
				fenetre.getPanneauVue().getCarte().ajouterPoint(plus_court_chemin.getNodeCoords(arrivee));
			}
		}
	}
	
	private void afficherListeRoutes() {
		fenetre.getPanneauInfos().reinitialiserInfos();
		
		// Erreur si chemin vide (non trouvé)
		if (chemin.isEmpty()) {
			fenetre.getPanneauInfos().setMessage("Aucun chemin n'a pu etre trouv\u00e9 !");
		}
		else {
			// Remplissage de la feuille de route
			fenetre.getPanneauInfos().setMessage(null);
			String nomRoute = "", nomRoutePrec = "";
			EtatReseau pos = null;
			int lenRoute = 0;
			int lenTotale = 0;
			int idEdge;
			int numPt, numPtPrec = -1, numPtPrec2 = -1;
			String gaucheDroite;
			fenetre.getPanneauInfos().reinitialiserRoutes();
			for (Iterator it = chemin.iterator(); it.hasNext();) {
				pos = (EtatReseau) it.next();
				numPt = pos.n;
				if (numPtPrec >= 0) {
					idEdge = plus_court_chemin.findEdge(numPt, numPtPrec);
					if (idEdge >= 0) {
						nomRoute = plus_court_chemin.getEdgeName(idEdge);
						lenRoute += plus_court_chemin.getEdgeLength(idEdge);
						if ((!nomRoutePrec.equals(nomRoute)) || (!it.hasNext())) {
							if (numPtPrec2 != -1) {
								gaucheDroite = determinerGaucheDroite(numPtPrec2, numPtPrec, numPt);
							}
							else {
								gaucheDroite = "tout_droit";
							}
							fenetre.getPanneauInfos().ajouterRoute(nomRoute + " (" + convertirUniteDistance(lenRoute, 1) + ")", DOSSIER_IMAGES + "tourner_" + gaucheDroite + ".gif");
							lenTotale += lenRoute;
							lenRoute = 0;
						}
						nomRoutePrec = nomRoute;
					}
					else
						fenetre.getPanneauInfos().ajouterRoute("Erreur : Route non trouv\u00e9e ! (" + numPtPrec + "|" + numPt + ")");
				}
				numPtPrec2 = numPtPrec;
				numPtPrec = numPt;
			}
			
			// Mise en place des informations
			fenetre.getPanneauInfos().setLongueurTrajet(convertirUniteDistance(lenTotale, 1));
		}
	}
	
	private void repositionnerVue() {
		if (chemin.size() > 1) {
			// Cherche le rectangle occupé
			int minx = 100000, miny = 100000, maxx = 0, maxy = 0;
			EtatReseau pos;
			Point pt;
			int x, y;
			for (Iterator it = chemin.iterator(); it.hasNext();) {
				pos = (EtatReseau) it.next();
				pt = reseau_routier.getPoint(pos.n);
				x = (int)pt.getX();
				y = (int)pt.getY();
				if (x < minx) minx = x;
				if (x > maxx) maxx = x;
				if (y < miny) miny = y;
				if (y > maxy) maxy = y;
			}
		
			// Calcul du nouveau zoom
			old_zoom = pourcentage_zoom;
			int largeur = maxx - minx;
			int hauteur = maxy - miny;
			Dimension taille_ecran = fenetre.getPanneauVue().getSize();
			double ratio_ecran = taille_ecran.getWidth() / taille_ecran.getHeight();
			int marge_dynamique;
			if (largeur >= ratio_ecran*hauteur) { // Largeur
				pourcentage_zoom = (float) (taille_ecran.getWidth() / largeur);
				marge_dynamique = 2*(int) (MARGE_REDIMENSIONNEMENT_AUTO/pourcentage_zoom);
				pourcentage_zoom = (float) (taille_ecran.getWidth() / (largeur + marge_dynamique));
			}
			else { // Hauteur
				pourcentage_zoom = (float) (taille_ecran.getHeight() / hauteur);
				marge_dynamique = 2*(int) (MARGE_REDIMENSIONNEMENT_AUTO/pourcentage_zoom);
				pourcentage_zoom = (float) (taille_ecran.getHeight() / (hauteur + marge_dynamique));
			}
			// Arrondissement et mise en place du zoom
			pourcentage_zoom = ((float) Math.round(pourcentage_zoom * 100)) / 100;
			setZoom();
			
			// Recentrage de la vue
			minx = (int)((minx * pourcentage_zoom) - (taille_ecran.getWidth() - largeur*pourcentage_zoom) / 2);
			miny = (int)((miny * pourcentage_zoom) - (taille_ecran.getHeight() - hauteur*pourcentage_zoom) / 2);
			minx = fenetre.getPanneauVue().resituerX(minx);
			miny = fenetre.getPanneauVue().resituerY(miny);
			
			fenetre.getPanneauVue().getViewport().setViewPosition(new Point(minx, miny));
		}
	}
	
	public void deplacerCarte(int x, int y) {
		fenetre.getPanneauVue().deplacerCarte(x, y);
		updateCentre();
	}
	
	//memorise le point centre de l'ecran.
	public void updateCentre() {
		//On conserve le point au centre de l'écran
		Dimension dim = fenetre.getPanneauVue().getViewport().getSize();
		Point coin = new Point(fenetre.getPanneauVue().getViewport().getViewPosition());
		ptCentre = new Point((int)((coin.getX()/pourcentage_zoom) + dim.getWidth()/(2*pourcentage_zoom)), (int)((coin.getY()/pourcentage_zoom) + dim.getHeight()/(2*pourcentage_zoom)));
	}

	//Positionne la vue sur le pt passé en param (generalement ptCentre)
	public void recentrerVue(Point pt) {
		//Mise en mémoire du point Centre
		ptCentre = pt;
		
		//centrage de la vue sur le point
		Dimension dim = fenetre.getPanneauVue().getViewport().getSize();
		
		int newX = (int)((float)pt.getX() * pourcentage_zoom) - (int)((float)dim.getWidth()/(float)2);
		int newY = (int) ((float)pt.getY() * pourcentage_zoom) - (int)((float)dim.getHeight()/(float)2);
		
		newX = fenetre.getPanneauVue().resituerX(newX);
		newY = fenetre.getPanneauVue().resituerY(newY);
		
		fenetre.getPanneauVue().getViewport().setViewPosition(new java.awt.Point(newX,newY));
	}
	
	public float getZoom() {
		return pourcentage_zoom;
	}
	
	public void modifierZoom(float mod) {
		// Modifie le zoom
		old_zoom = pourcentage_zoom;
		if (mod == RETOUR_ZOOM_INITIAL) {
			pourcentage_zoom = ZOOM_INITIAL;
		}
		else {
			pourcentage_zoom += mod;
		}
		setZoom();
	}
	
	private void setZoom() {
		if (pourcentage_zoom > ZOOM_MAX) pourcentage_zoom = ZOOM_MAX;
		if (pourcentage_zoom < ZOOM_MIN) pourcentage_zoom = ZOOM_MIN;
		if (old_zoom != pourcentage_zoom) {
			fenetre.getPanneauInfos().updateZoom(pourcentage_zoom);
			fenetre.getPanneauVue().getCarte().updateZoom(pourcentage_zoom);
			fenetre.getPanneauVue().getCarte().setMaxUnitIncrement(pourcentage_zoom);
			fenetre.getPanneauControles().setSliderValue((int) (pourcentage_zoom*100));
			plus_court_chemin.init(reseau_routier, pourcentage_zoom);
			afficherCarte();
			chercherItineraire();
			point_proche_souris = -1;
			fenetre.getPanneauVue().getCarte().setPointProche(new Point(-1, -1));
		}
	}
	
	private String convertirUniteDistance(double px, float zoom) {
		// Conversion dans l'unité de mesure
		String unite = "m";
		double m = (double)(px * (double)ECHELLE_CARTE * (double)((double)1 / (double)zoom));
		if (m > 1000) {
			m /= 1000;
			unite = "km";
		}
		// Arrondissement à 2 chiffres après la virgule
		m = ((double) Math.round(m * 100)) / 100;
		return new String(m + " " + unite);
	}
	
	public void modifierCurseurVue(int curseur) {
		fenetre.getPanneauVue().getCarte().setCursor(new Cursor(curseur));
	}
	
	public void choixCouleur() {
		Color newColor = JColorChooser.showDialog(
                fenetre, "Choisissez la nouvelle couleur du trac\u00e9",
                fenetre.getPanneauVue().getCarte().getItineraireCouleur());
		if (newColor != null){
			fenetre.getPanneauVue().getCarte().setItineraireCouleur(newColor);
		}
		chercherItineraire();
		
	}

	public void updateCoord(int X, int Y) {
		Point pointSouris = new Point(X,Y);

        // Affichage du point le plus proche sur la carte
		Point pointProchePrecedent = fenetre.getPanneauVue().getCarte().getPointProche();
		int idPointProche = plus_court_chemin.cherchePointProche(pointSouris);
		Point pointProche = plus_court_chemin.getNodeCoords(idPointProche);
		if (!pointProchePrecedent.equals(pointProche)) {
			// On ne redessine que si le point change
			point_proche_souris = idPointProche;
			fenetre.getPanneauVue().getCarte().cacherMenu();
			fenetre.getPanneauVue().getCarte().setPointProche(pointProche);
			// Affichage des coordonnees du point
			Point nouvelles_coords = getLambertCoords(reseau_routier.getPoint(idPointProche));
			fenetre.getPanneauVue().getCarte().ajouterDonneeAuPointProche(new Integer(idPointProche).toString());
			fenetre.getPanneauVue().getCarte().ajouterDonneeAuPointProche("Coordonn\u00e9es : (" + (int)nouvelles_coords.getX() + "," + (int)nouvelles_coords.getY() + ")");
			// Recherche des routes du point
			Set liste_routes = reseau_routier.getListeRoutes();
			String nom_route;
			Integer num_point;
			Vector<Integer> points_route;
			Iterator it_routes;
			Iterator it_points;
			for (it_routes = liste_routes.iterator(); it_routes.hasNext();) {
				nom_route = (String) it_routes.next();
				points_route = reseau_routier.getRoute(nom_route).getPoints();
				for (it_points = points_route.iterator(); it_points.hasNext();) {
					num_point = (Integer) it_points.next();
					if (num_point == idPointProche) {
						fenetre.getPanneauVue().getCarte().ajouterDonneeAuPointProche("Route : " + nom_route);
					}
				}
			}
			fenetre.getPanneauVue().revalidate();
			fenetre.repaint();
		}
		
		// Rafraichissement des informations
		Point pt_lambert = getLambertCoords(new Point((int) (X / pourcentage_zoom), (int) (Y / pourcentage_zoom)));
		fenetre.getPanneauInfos().updateCoord((int)pt_lambert.getX(), (int)pt_lambert.getY());
	}
	
	public void afficherMenuContextuel() {
		fenetre.getPanneauVue().getCarte().afficherMenu();
	}
	
	public void setPointProcheDepart() {
		setDepart(point_proche_souris);
		fenetre.getPanneauVue().getCarte().setTypePointUnique(true);
		chercherItineraire();
	}	
	
	public void setPointProcheArrivee() {
		setArrivee(point_proche_souris);
		fenetre.getPanneauVue().getCarte().setTypePointUnique(false);
		chercherItineraire();
	}
	
	public void cacherMenuCarte() {
		fenetre.getPanneauVue().getCarte().cacherMenu();
	}
	
	private Point getLambertCoords(Point pt_pixels) {
		int etendue_x = (int) (LAMBERT_BAS_DROITE.getX() - LAMBERT_HAUT_GAUCHE.getX());
		int etendue_y = (int) (LAMBERT_BAS_DROITE.getY() - LAMBERT_HAUT_GAUCHE.getY());
		double lambert_zero_x = pt_pixels.getX() * etendue_x / PIXELS_BAS_DROITE.getX();
		double lambert_zero_y = pt_pixels.getY() * etendue_y / PIXELS_BAS_DROITE.getY();
		int x = (int) (LAMBERT_HAUT_GAUCHE.getX() + lambert_zero_x);
		int y = (int) (LAMBERT_HAUT_GAUCHE.getY() + lambert_zero_y);
		return new Point(x, y);
	}
	
	private String determinerGaucheDroite(int id1, int id2, int id3) {
		Point p1 = reseau_routier.getPoint(id1);
		Point p2 = reseau_routier.getPoint(id2);
		Point p3 = reseau_routier.getPoint(id3);

		//déterminer l'angle entre les deux droites
		
		//clacul de l'angle du précédent arc par rapport à l'origine
		double angle1 = (atan2((p2.getY()-p1.getY()),(p2.getX()-p1.getX())));
		
		//calcul de l'angle de l'arc deux fois précédent par rapport a l'origine
		double angle2 = (atan2((p3.getY()-p1.getY()),(p3.getX()-p1.getX())));
		
		//soustraction de l'un par rapport à l'autre pour avoir leur angle relatif
		double angle = angle2-angle1;
		
		if(sin(angle)<-0.1)
			return "gauche";
		else if (sin(angle)>0.1)
			return "droite";
		else
			return "tout_droit";
	}
	
	public void changerAntiAliasing() {
		fenetre.getPanneauVue().getCarte().changerAntiAliasing();
	}
	
	private void setDepart(int d) {
		depart = d;
		fenetre.getPanneauInfos().updateDepart(depart);
	}
	
	private void setArrivee(int a) {
		arrivee = a;
		fenetre.getPanneauInfos().updateArrivee(arrivee);
	}

	public void determinerZoomGlobal() {
		float i=ZOOM_MAX;
		int largeurMin =(int) fenetre.getPanneauVue().getViewport().getViewSize().getWidth();
		int hauteurMin =(int) fenetre.getPanneauVue().getViewport().getViewSize().getHeight();
		int largeurCarte = fenetre.getPanneauVue().getCarte().getLargeur();
		int hauteurCarte = fenetre.getPanneauVue().getCarte().getHauteur();
		//tant que la carte ne rentre pas en entier dans le ViewPort, on diminue le zoom
		while(i>ZOOM_MIN || ( (float)(largeurCarte * i) > (float)largeurMin && (float)(hauteurCarte * i) > (float)hauteurMin) )
			i-=0.01;
		pourcentage_zoom = i;
		
		setZoom();
	}
}
