package view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class Fenetre extends JFrame{

	//couleur du trac� ( commun a Carte et Contr�le )
	public final static Color DefaultItineraireColor = Color.GREEN;
	private final int CONTROLES_HAUTEUR = 150;
	private final int INFOS_LARGEUR = 250;
	
	// Dimensions de l'�cran
	private Dimension dimEcran;
    
    // Dimensions utilisables du bureau
    private int largeurUtil;
    private int hauteurUtil;
	
	// Pannels
	private PanelVue pnlVue;
	private PanelControles pnlCtrl;
	private PanelInformations pnlInfo;
	
	// Bordure des Panels
	Border raisedbevel = BorderFactory.createRaisedBevelBorder();
	Border loweredbevel = BorderFactory.createLoweredBevelBorder();
	Border coumpoundBorder = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
	
	public Fenetre(String lienCarte, String su) {	
		super("Calcul d'itin\u00e9raires");
		
		// Recuperer l'apparence par defaut du systeme
		String nativeLF = UIManager.getSystemLookAndFeelClassName();
    
		// Installation de l'apparence
		try {
			UIManager.setLookAndFeel(nativeLF);
		} 
		catch (InstantiationException e) {}
		catch (ClassNotFoundException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		catch (IllegalAccessException e) {}
		
		// Recuperation de la taille de l'ecran
        Toolkit tk = Toolkit.getDefaultToolkit();
        dimEcran = tk.getScreenSize();
        
        // Recuperation des rebords
        Insets insets = tk.getScreenInsets(getGraphicsConfiguration()); 
        
        // Calcul de la taille utilisable sur le bureau
        largeurUtil = (int)(dimEcran.getWidth()-insets.left-insets.right); 
        hauteurUtil = (int)(dimEcran.getHeight()-insets.top-insets.bottom); 
        setPreferredSize(new Dimension(largeurUtil,hauteurUtil));
        
        int h,l;
        
		// Cr�ation du PanelControles
        h = CONTROLES_HAUTEUR;
        l = largeurUtil;
		pnlCtrl = new PanelControles();
		pnlCtrl.setPreferredSize(new Dimension(l, h));
		getContentPane().add(pnlCtrl ,BorderLayout.NORTH);
		
		// Cr�ation de la carte
		Carte carte = new Carte(lienCarte, 40);
		
		// Cr�ation du PanelVue avec la carte
		h = hauteurUtil - CONTROLES_HAUTEUR;
		l = largeurUtil - INFOS_LARGEUR;
		pnlVue = new PanelVue(carte);
		pnlVue.setPreferredSize(new Dimension(l, h));
		pnlVue.setBorder(coumpoundBorder);
		getContentPane().add(pnlVue, BorderLayout.CENTER);
		
		// Cr�ation du PanelInformations
		l = INFOS_LARGEUR;
		h = hauteurUtil;
		pnlInfo = new PanelInformations(l, h, su);
		pnlInfo.setPreferredSize(new Dimension(l,h));
		getContentPane().add(pnlInfo ,BorderLayout.WEST);
		
		// Mise � la taille du bureau pour le mode fen�tre normal
		pack();
		setLocation(insets.left, insets.top);
		
		// Mise en mode fenetre aggrandie
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Op�ration de fermeture
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public PanelControles getPanneauControles() {
		return pnlCtrl;
	}

	public PanelInformations getPanneauInfos() {
		return pnlInfo;
	}

	public PanelVue getPanneauVue() {
		return pnlVue;
	}
}
