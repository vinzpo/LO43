/*
 * Class Carte :
 * Dérivée de ImageScrollable (pour l'image)
 * Contient et dessine l'itinï¿½raire
 */

package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import controller.ControlleurCarte;

import model.Application;

@SuppressWarnings("serial")
public class Carte extends ImageScrollable {
	
	//Image représentant la carte
	Image imgCarte;
	int imgLargeur;
	int imgHauteur;
	AffineTransform affineTransform;
	
	private float pourcentage_zoom;
	
	// Constantes de l'itinéraire
    private final int itineraire_epaisseur = 4;
    private final Color itineraire_couleur_depart = Color.BLUE;
    private final Color itineraire_couleur_arrivee = Color.RED;
    private final int itineraire_epaisseur_points = 20;
    private final Color itineraire_echelle_couleur = Color.BLUE;
    private final int itineraire_echelle_depart_x = 10;
    private final int itineraire_echelle_depart_y = 15;
    private final int itineraire_echelle_epaisseur = 2;
    private final String lettre_depart = "D";
    private final String lettre_arrivee = "A";
    private final Color lettres_couleur = Color.WHITE;
    private final int lettres_decalage = 3;
    
    // Données de l'itinéraire
    private Vector<Point> itineraire;
    private String itineraire_echelle = "";
    private int itineraire_echelle_taille_px = 10;
    private Color itineraire_couleur = Color.GREEN;
    private boolean itineraire_point_unique_est_point_depart = true;
    
    // Données du point le plus proche
    private final Color point_proche_couleur = Color.BLACK;
    private final int point_proche_taille = 15;
    private final int donnees_depart_x = 15;
    private final int donnees_depart_y = 5;
    private final int donnees_decalage_y = 15;
    private final Color point_proche_donnees_couleur = Color.BLACK;
    private final Color rectangle_couleur_fond = new Color(252, 255, 124);
    private final int rectangle_largeur = 250;
    private final int rectangle_marge_interne = 10;
    private Point point_proche;
    private Vector<String> point_proche_donnees;
    private boolean point_proche_donnees_afficher = true;
    
    // Menu contextuel
    private final int menu_depart_x = 5;
    private final int menu_depart_y = 30;
	private JPopupMenu menu;
	private JMenuItem choix_depart;
	private JMenuItem choix_arrivee;
	private JCheckBoxMenuItem choix_anti_aliasing;
	private boolean anti_aliasing = true;
	private JMenuItem choix_itineraire_couleur;
    
	public Carte(String imgPath, int m) {
		super(m);
		imgCarte = Toolkit.getDefaultToolkit().getImage(imgPath);
		
		init();
	}
	
	private void init(){
		imgLargeur = imgCarte.getWidth(null);
		imgHauteur = imgCarte.getHeight(null);
		updateZoom(Application.ZOOM_INITIAL);
		
		itineraire = new Vector<Point>();
		point_proche = new Point(-1, -1);
		point_proche_donnees = new Vector<String>();
		
		setBackground(Color.white);
		setOpaque(true);
		
		// Initialisation du menu
		menu = new JPopupMenu();
		
		// Option "chosir comme point de départ"
		BufferedImage dessin_depart = new BufferedImage(15, 15, BufferedImage.TYPE_INT_RGB);
		Graphics2D g21 = dessin_depart.createGraphics();
		g21.setColor(itineraire_couleur_depart);
		g21.fillRect(0, 0, 15, 15);
		ImageIcon icone_depart = new ImageIcon(dessin_depart);
		choix_depart = new JMenuItem("Choisir comme point de d\u00e9part", icone_depart);
		choix_depart.setName("choix_depart");
		menu.add(choix_depart);
		
		// Option "chosir comme point de destination"
		BufferedImage dessin_arrivee = new BufferedImage(15, 15, BufferedImage.TYPE_INT_RGB);
		Graphics2D g22 = dessin_arrivee.createGraphics();
		g22.setColor(itineraire_couleur_arrivee);
		g22.fillRect(0, 0, 15, 15);
		ImageIcon icone_arrivee = new ImageIcon(dessin_arrivee);
		choix_arrivee = new JMenuItem("Choisir comme point de destination", icone_arrivee);
		choix_arrivee.setName("choix_arrivee");
		menu.add(choix_arrivee);
		
		menu.addSeparator();
		
		// Option "Changer la couleur du tracé"
		choix_itineraire_couleur = new JMenuItem("Changer la couleur du trac\u00e9...");
		choix_itineraire_couleur.setName("choix_itineraire_couleur");
		menu.add(choix_itineraire_couleur);
		setItineraireCouleur(itineraire_couleur);
		
		// Option "Filtre anti-aliasing"
		choix_anti_aliasing = new JCheckBoxMenuItem("Filtre anticr\u00e8nelage");
		choix_anti_aliasing.setSelected(true);
		choix_anti_aliasing.setName("choix_anti_aliasing");
		menu.add(choix_anti_aliasing);
		
		// Ajout du menu à la carte
		add(menu);
		
		// Initialisation de l'anti-aliasing
		anti_aliasing = !anti_aliasing;
		changerAntiAliasing();
	}
    
    public void updateZoom(float zoom) {
    	pourcentage_zoom = zoom;
    	super.setMaxUnitIncrement(zoom);
    	affineTransform = new AffineTransform();
    	affineTransform.scale(pourcentage_zoom, pourcentage_zoom);
    	int larg = (int)(imgLargeur * pourcentage_zoom);
    	int haut = (int)(imgHauteur * pourcentage_zoom);
    	setSize(new Dimension(larg, haut));
		setBorder(BorderFactory.createEmptyBorder(haut, larg, 0, 0));
    }
    
    public void ajouterEcouteurCarte(ControlleurCarte ecouteur) {
	    addMouseMotionListener(ecouteur); //gere le drag de souris
		addMouseListener(ecouteur); //gere les changements de forme du pointeur
		addMouseWheelListener(ecouteur); //gere la molette
    }
    
    public void ajouterEcouteurMenu(ActionListener a) {
    	choix_depart.addActionListener(a);
    	choix_arrivee.addActionListener(a);
    	choix_anti_aliasing.addActionListener(a);
    	choix_itineraire_couleur.addActionListener(a);
    }
    
    public void ajouterPoint(Point pt) {
    	itineraire.add(pt);
    }
    
    public void viderPoints() {
    	itineraire.removeAllElements();
    }
    
    public void afficherMenu() {
    	if ((point_proche.getX() != -1) && (point_proche.getY() != -1)) {
	    	Point pt = new Point(point_proche);
	    	// Annulation du déplacement de la carte
	    	pt.translate((int) -getVisibleRect().getX() + menu_depart_x, (int) -getVisibleRect().getY() + menu_depart_y);
	    	menu.setLocation(pt);
	    	menu.setVisible(true);
	    	point_proche_donnees_afficher = false;
	    	repaint();
    	}
    }
    
    public void cacherMenu() {
    	menu.setVisible(false);
    	point_proche_donnees_afficher = true;
    	repaint();
    }
    
    public void setTailleEchelle(int taille) {
    	itineraire_echelle_taille_px = taille;
    }
    
    public void setEchelle(String echelle) {
    	itineraire_echelle = echelle;
    }
    
    synchronized public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2 = (Graphics2D) g;
    	g2.setColor(getBackground());

    	g2.drawImage(imgCarte, affineTransform, null);
    	
    	// Traçage de l'échelle
    	g2.setColor(itineraire_echelle_couleur);
    	g2.setStroke(new BasicStroke(itineraire_echelle_epaisseur));
    	int depart_x = ((int) getVisibleRect().getMinX()) + itineraire_echelle_depart_x;
    	int depart_y = ((int) getVisibleRect().getMinY()) + itineraire_echelle_depart_y;
    	g2.drawString(itineraire_echelle, depart_x, depart_y - 5);
    	g2.drawLine(depart_x, depart_y, depart_x + itineraire_echelle_taille_px, depart_y);
    	g2.drawLine(depart_x, depart_y - 2, depart_x, depart_y + 2);
    	g2.drawLine(depart_x + itineraire_echelle_taille_px, depart_y - 2, depart_x + itineraire_echelle_taille_px, depart_y + 2);
    	
    	// Mise en place d'un anti-aliasing
    	if (anti_aliasing) {
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	}
    	
    	// Traçage de l'itinéraire
    	if (itineraire.size() > 1) {
    		Point pt_depart = new Point();
	    	Point pt = new Point(0, 0);
	    	Point prec = new Point(-1, -1);
	    	g2.setColor(itineraire_couleur);
	    	g2.setStroke(new BasicStroke(itineraire_epaisseur));
	    	
	    	for (Iterator it = itineraire.iterator(); it.hasNext();) {
	    		pt = (Point) it.next();
	    		if (prec.getX() == -1) {
	    			// Premier point
	    			pt_depart = new Point(pt);
	    		}
	    		else {
	    			// Traçage de la droite reliant les 2 points
	    			g2.drawLine((int)prec.getX(), (int)prec.getY(), (int)pt.getX(), (int)pt.getY());
	    		}
	    		prec.setLocation(pt);
	    	}
	    	
	    	// Premier point : traçage du point de départ
	    	tracePointDepart(g2, pt_depart);
	    	
	    	// Dernier point : traçage du point d'arrivée
			tracePointArrivee(g2, pt);
    	}
    	else if (itineraire.size() == 1) {
    		if (itineraire_point_unique_est_point_depart) {
    			tracePointDepart(g2, itineraire.get(0));
    		}
    		else {
    			tracePointArrivee(g2, itineraire.get(0));
    		}
    	}
    
    	// Traçage du point le plus proche de la souris
    	if ((point_proche.getX() != -1) && (point_proche.getY() != -1)) {
    		tracePoint(g2, point_proche, point_proche_couleur, point_proche_taille);
        	// Tracage des donnees du point le plus proche de la souris
        	if (point_proche_donnees_afficher) {
        		int x = (int)point_proche.getX();
        		int y = (int)point_proche.getY();
    	    	// Rectangle
        		g2.setColor(Color.BLACK);
        		g2.drawRect(x + rectangle_marge_interne, y - rectangle_marge_interne, rectangle_largeur, donnees_decalage_y * point_proche_donnees.size() + rectangle_marge_interne);
        		g2.setColor(rectangle_couleur_fond);
        		g2.fillRect(x + rectangle_marge_interne, y - rectangle_marge_interne, rectangle_largeur, donnees_decalage_y * point_proche_donnees.size() + rectangle_marge_interne);
        		// Contenu
        		String donnee;
    	    	int donnees_x = x + donnees_depart_x;
    	    	int donnees_y = y + donnees_depart_y;
    	    	g2.setColor(point_proche_donnees_couleur);
    	    	g2.setFont(g2.getFont().deriveFont(Font.BOLD));
    	    	for (Iterator it_donnees = point_proche_donnees.iterator(); it_donnees.hasNext();) {
    	    		donnee = (String) it_donnees.next();
    	    		g2.drawString(donnee, donnees_x, donnees_y);
    	    		donnees_y += donnees_decalage_y;
    	    	}
        	}
	    }
    }
    
    private void tracePointDepart(Graphics2D g2, Point pt) {
    	tracePointDepartOuArrivee(g2, pt, itineraire_couleur_depart, lettre_depart);
    }
    
    private void tracePointArrivee(Graphics2D g2, Point pt) {
    	tracePointDepartOuArrivee(g2, pt, itineraire_couleur_arrivee, lettre_arrivee);
    }
    
    private void tracePointDepartOuArrivee(Graphics2D g2, Point pt, Color couleur, String lettre) {
    	Color couleur_precedente = g2.getColor();
    	tracePoint(g2, pt, couleur, itineraire_epaisseur_points);
    	g2.setColor(lettres_couleur);
    	g2.drawString(lettre, (int)pt.getX() - lettres_decalage, (int)pt.getY() + lettres_decalage);
    	g2.setColor(couleur_precedente);
    }
    
	public void tracePoint(Graphics2D g2, Point pt, Color c, int taille) {
		Color couleur_precedente = g2.getColor();
		g2.setColor(c);
		g2.fillOval((int)pt.getX() - taille/2, (int)pt.getY() - taille/2, taille, taille);
		g2.setColor(couleur_precedente);
	}

	public Color getItineraireCouleur() {
		return itineraire_couleur;
	}

	public void setItineraireCouleur(Color couleur) {
		itineraire_couleur = couleur;
		BufferedImage dessin_couleur = new BufferedImage(15, 15, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = dessin_couleur.createGraphics();
		g2.setColor(itineraire_couleur);
		g2.fillRect(0, 0, 15, 15);
		ImageIcon icone_couleur = new ImageIcon(dessin_couleur);
		choix_itineraire_couleur.setIcon(icone_couleur);
	}
	
	public void setPointProche(Point pt) {
		point_proche = pt;
		point_proche_donnees.removeAllElements();
	}
	
	public Point getPointProche() {
		return point_proche;
	}
	
	public void ajouterDonneeAuPointProche(String route) {
		point_proche_donnees.add(route);
	}

	public void changerAntiAliasing() {
		anti_aliasing = !anti_aliasing;
		choix_anti_aliasing.setSelected(anti_aliasing);
		repaint();
	}
	
	public void setTypePointUnique(boolean est_depart) {
		itineraire_point_unique_est_point_depart = est_depart;
	}

	/**
	 * @return the imgHauteur
	 */
	public int getHauteur() {
		return imgHauteur;
	}

	/**
	 * @return the imgLargeur
	 */
	public int getLargeur() {
		return imgLargeur;
	}
}
