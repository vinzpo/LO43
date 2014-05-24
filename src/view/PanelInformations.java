package view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import model.Application;

@SuppressWarnings("serial")
public class PanelInformations extends JPanel{

	private JLabel lblInfos;
	private DefaultListModel dlmInfos; 
	private JList jlInfos;
	private JScrollPane jspInfos;
	
	private JLabel lblFeuilleRoute;
	private DefaultListModel dlmFeuilleRoute; 
	private JList jlFeuilleRoute;
	private JScrollPane jspFeuilleRoute;
	
	private Border jspBorder, outsideBorder, insideBorder;
	
	private final int INFOS_HAUTEUR = 175;
	
	// Donnees des infos
	private String message1, message2, su, longueur_trajet;
	private int x, y;
	private int depart = -1, arrivee = -1;
	float zoom = Application.ZOOM_INITIAL;
	
	public PanelInformations(int l, int h, String su) {
		super();
		this.su = su;
		
		// Creation du Layout (de type BoxLayout)
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		
		// Ajout du label informations
		lblInfos = new JLabel("Informations :");
		lblInfos.setFont(lblInfos.getFont().deriveFont(Font.BOLD));
		lblInfos.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		lblInfos.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblInfos);
		
		// Creation de la bordure des listes
		outsideBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		// A ameliorer
		insideBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		jspBorder = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
		
		// Ajout de la liste scrollable d'informations
		dlmInfos = new DefaultListModel();
		jlInfos = new JList(dlmInfos);
		jlInfos.setLayoutOrientation(JList.VERTICAL);
		jlInfos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlInfos.setVisibleRowCount(-1);
		jspInfos = new JScrollPane(jlInfos);
		jspInfos.setPreferredSize(new Dimension((int)l, INFOS_HAUTEUR));
		jspInfos.setMinimumSize(new Dimension((int)l, INFOS_HAUTEUR));
		jspInfos.setBorder(jspBorder);
		add(jspInfos);
		setMessage("Choisissez un itin\u00e9raire :");
		
		// Ajout du label Feuille de route
		lblFeuilleRoute = new JLabel("Feuille de route :");
		lblFeuilleRoute.setFont(lblFeuilleRoute.getFont().deriveFont(Font.BOLD));
		lblFeuilleRoute.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblFeuilleRoute);
		
		// Ajout de la liste scrollable feuille de route
		dlmFeuilleRoute = new DefaultListModel();
		jlFeuilleRoute = new JList(dlmFeuilleRoute);
		jlFeuilleRoute.setLayoutOrientation(JList.VERTICAL);
		jlFeuilleRoute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlFeuilleRoute.setVisibleRowCount(-1);
		jlFeuilleRoute.setCellRenderer(new AfficheurElementListe());
		jspFeuilleRoute = new JScrollPane(jlFeuilleRoute);
		jspFeuilleRoute.setPreferredSize(new Dimension((int)l, (int)(h * (float)4/5)));
		jspFeuilleRoute.setBorder(jspBorder);
		add(jspFeuilleRoute);
	}
	
	public void ajouterRoute(String route, String chemin_image) {
		dlmFeuilleRoute.addElement(new ElementListe(route, chemin_image));
	}
	
	public void ajouterRoute(String route) {
		dlmFeuilleRoute.addElement(new ElementListe(route));
	}
	
	public void reinitialiserInfos() {
		dlmInfos.removeAllElements();
	}
	
	public void reinitialiserRoutes() {
		dlmFeuilleRoute.removeAllElements();
	}
	
	public void setMessage(String mess1) {
		message1 = mess1;
		refaireInfos();
	}
	
	public void setLongueurTrajet(String longueur) {
		longueur_trajet = longueur;
		refaireInfos();
	}
	
	public void updateCoord(int x, int y) {
		this.x = x;
		this.y = y;
		refaireInfos();
	}
	
	public void updateZoom(float zoom) {
		this.zoom = zoom;
		refaireInfos();
	}
	
	public void updateDepart(int d) {
		depart = d;
		refaireInfos();
	}	
	
	public void updateArrivee(int a) {
		arrivee = a;
		refaireInfos();
	}
	
	private void refaireInfos() {
		dlmInfos.removeAllElements();
		if(message1 != null)
			dlmInfos.addElement(message1);
		if(message2 != null)
			dlmInfos.addElement(message2);
		//dlmInfos.addElement(new String("Systeme d'unit\u00e9s : " + su));
		dlmInfos.addElement(new String("Longueur du trajet : " + ((longueur_trajet == null) ? "-" : longueur_trajet)));
		dlmInfos.addElement(new String("D\u00e9part : " + ((depart == -1) ? "-" : new Integer(depart).toString())));
		dlmInfos.addElement(new String("Arriv\u00e9e : " + ((arrivee == -1) ? "-" : new Integer(arrivee).toString())));
		dlmInfos.addElement(new String("Coordonn\u00e9e X : " + x));
		dlmInfos.addElement(new String("Coordonn\u00e9e Y : " + y));
	}
}

class ElementListe {
	  private final String texte;
	  private final String chemin_image;
	  private ImageIcon image;
	  
	  public ElementListe(String texte) {
		  this.texte = texte;
		  this.chemin_image = "";
	  }

	  public ElementListe(String texte, String chemin_image) {
		  this.texte = texte;
		  this.chemin_image = chemin_image;
	  }

	  public String getTitle() {
		  return texte;
	  }
	  
	  public String getCheminImage() {
		  return chemin_image;
	  }

	  public ImageIcon getImage() {
		  if (image == null) {
			  image = new ImageIcon(chemin_image);
		  }
		  return image;
	  }
	  
	  // Override standard toString method to give a useful result
	  public String toString() {
		  return texte;
	  }
}

@SuppressWarnings("serial")
class AfficheurElementListe extends JLabel implements ListCellRenderer {
	
	private final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);
	
	public AfficheurElementListe() {
		setOpaque(true);
	    setIconTextGap(5);
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	    ElementListe element = (ElementListe) value;
		setText(element.getTitle());
		if (element.getCheminImage() != "") {
			setIcon(element.getImage());
		}
		else {
			setIcon(new ImageIcon());
		}
	    if (isSelected) {
	      setBackground(HIGHLIGHT_COLOR);
	      setForeground(Color.white);
	    }
	    else {
	      setBackground(Color.white);
	      setForeground(Color.black);
	    }
		return this;
	}

}