package model;
/********************************************************************/
/* PlusCourtChemin.java                                             */
/* Modification de Dijkstra.java pour fonctionnement                */
/* avec le projet de calculs d'itin�raires                          */
/********************************************************************/

import java.awt.*;
import java.util.*;

class Node {
	int	x;
	int	y;
	int	delta_plus;	/* edge starts from this node */
	int	delta_minus;	/* edge terminates at this node */
	int	dist;		/* distance from the start node */
	int	prev;		/* previous node of the shortest path */
	int	succ,pred;	/* node in Sbar with finite dist. */
	int	w;
	int	h;
	int	pw;
	int	dx;
	int	dy;
	String	name;
        int     id;
}

class Edge {
	int	rndd_plus;	/* initial vertex of this edge */
	int	rndd_minus;	/* terminal vertex of this edge */
	int	delta_plus;	/* edge starts from rndd_plus */
	int	delta_minus;	/* edge terminates at rndd_minus */
	int	len;		/* length */
	String	name;
        int     id;
}

public class PlusCourtChemin {
	float zoom;
	int	n,m;
	int	u,snode;	/* start node */
        int     enode; // end node
	int	pre_s_first, pre_s_last;
	boolean	isdigraph;
	int	iteration, step;
	Node	v[];
	Edge	e[];

    ReseauRoutier reseau;
    int p1;
    int p2;
    Vector<EtatReseau> chemin = new Vector<EtatReseau>();
    boolean findGoal=false;

	int findNode(String name) {
		for (int i=0; i<n; i++)
			if (v[i].name.equals(name))
				return i;
		return -1;
	}

    int findNode(int id) {
            for (int i=0; i<n; i++)
                    if (v[i].id==id)
                            return i;
            return -1;
    }
    
    // Fonction renvoyant le nom de la route d'un arc
    public int findEdge(int numNode1, int numNode2) {
    	int r = -1;
    	int numPt1, numPt2;
    	for (int i = 0; i < m; i++) {
    		numPt1 = v[e[i].rndd_minus].id;
    		numPt2 = v[e[i].rndd_plus].id;
    		if (((numNode1 == numPt1) && (numNode2 == numPt2)) || ((numNode1 == numPt2) && (numNode2 == numPt1))) {
    			r = i;
    			break;
    		}
    	}
    	return r;
    }
    
    public String getEdgeName(int id) {
    	return e[id].name;
    }
    
    public int getEdgeLength(int id) {
    	return e[id].len;
    }
    
    public Point getNodeCoords(int id) {
    	Node node = v[findNode(id)];
    	return new Point(node.x, node.y);
    }
    
    public int getNodeId(int id) {
    	return v[id].id;
    }
    
    public int cherchePointProche(Point pt) {
		// Recherche le point le plus proche de la souris
		Point pointCourant;
		Node node;
		int i, idPointProche = -1;
		double meilleureDistance = 1000, distanceCourante;
        for (i = 0; i < n; i++) {
        	node = v[i];
        	pointCourant = new Point(node.x, node.y);
        	distanceCourante = pointCourant.distance(pt);
        	if( distanceCourante < meilleureDistance){
        		meilleureDistance = distanceCourante;
        		idPointProche = node.id;
        	}
		}
        return idPointProche;
    }

	void input_graph(ReseauRoutier res) {

        n = res.getNombrePoints();
        m = res.getNombreConnexions();
        isdigraph = false;
        double z = (double)zoom;
        
        // Mise en place des points
        int i = 0;
        Integer numPoint;
        Point pt;
        for (Iterator f = res.getListePoints().iterator(); f.hasNext();) {
          	numPoint = (Integer) f.next();
         	pt = res.getPoint(numPoint);
	        Node node = new Node();
	        node.name = new String(numPoint.toString());
	        node.id = numPoint;
	        node.x = (int) (pt.getX()*z);
	        node.y = (int) (pt.getY()*z);
	        v[i++] = node;
        }
        
        // Mise en place des connexions entre les points gr�ce aux routes
        i = 0;
        String nomRoute;
        Route route = null;
        int ipt;
        Integer ptCourant, ptPrecedent;
        Edge edge;
        for (Iterator f = res.getListeRoutes().iterator(); f.hasNext();) {
         	nomRoute = (String) f.next();
            route = res.getRoute(nomRoute);
            ptPrecedent = null;
            for (ipt = 0; ipt < route.getNombrePoints(); ipt++)
            {
            	ptCourant = route.getNumPoint(ipt);
            	if (ptPrecedent != null)
            	{
			        edge = new Edge();
			        edge.name = new String(nomRoute);
			        edge.id = i;
			        //edge.rndd_plus = findNode(new String(""+C.getEnd().getId()));
			        //edge.rndd_minus = findNode(new String(""+C.getStart().getId()));
			        edge.rndd_plus = findNode(ptCourant);
			        edge.rndd_minus = findNode(ptPrecedent);
			        edge.len = (int) res.getPoint(ptCourant).distance(res.getPoint(ptPrecedent));
			        e[i++] = edge;
            	}
            	ptPrecedent = ptCourant;
            }
        }

		for (i=0; i<n; i++) {
			v[i].succ = v[i].pred = -2;
			v[i].prev = v[i].dist = -1;
			v[i].pw = 0;
		}
		iteration = step = 0;
	}

	void rdb() {
		int	i,k;

		for (i=0; i<n; i++)
			v[i].delta_plus = v[i].delta_minus = -1;
		for (i=0; i<m; i++)
			e[i].delta_plus = e[i].delta_minus = -1;
		for (i=0; i<m; i++) {
			k = e[i].rndd_plus;
			if (v[k].delta_plus == -1)
				v[k].delta_plus = i;
			else {
				k = v[k].delta_plus;
				while(e[k].delta_plus >= 0)
					k = e[k].delta_plus;
				e[k].delta_plus = i;
			}
			k = e[i].rndd_minus;
			if (v[k].delta_minus == -1)
				v[k].delta_minus = i;
			else {
				k = v[k].delta_minus;
				while(e[k].delta_minus >= 0)
					k = e[k].delta_minus;
				e[k].delta_minus = i;
			}
		}
	}

	void append_pre_s(int i) {
		v[i].succ = v[i].pred = -1;
		if (pre_s_first<0)
			pre_s_first = i;
		else
			v[pre_s_last].succ = i;
		v[i].pred = pre_s_last;
		pre_s_last = i;
	}

	void remove_pre_s(int i) {
		int	succ = v[i].succ;
		int	pred = v[i].pred;

		if (succ>=0)
			v[succ].pred = pred;
		else
			pre_s_last = pred;
		if (pred>=0)
			v[pred].succ = succ;
		else
			pre_s_first = succ;
	}

	void step1() {		/* initialize */
		u = snode;
		for (int i=0; i<n; i++) {
			v[i].succ = v[i].pred = -2;
			v[i].prev = v[i].dist = -1;
		}
		v[u].succ = -3;
		v[u].dist = 0;
		pre_s_first = pre_s_last = -1;
	}

	void step2() {		/* replace labels */
		int i,j;

		j = v[u].delta_plus;
		while (j>=0) {
			i = e[j].rndd_minus;
			if ((v[i].succ>=-2)&&((v[i].dist<0)||
				(v[i].dist>v[u].dist+e[j].len))) {
				if (v[i].dist<0)
					append_pre_s(i);
				v[i].dist = v[u].dist + e[j].len;
				v[i].prev = u;	  /* label */
			}
			j = e[j].delta_plus;
		}
		if (!isdigraph) {
		j = v[u].delta_minus;
		while (j>=0) {
			i = e[j].rndd_plus;
			if ((v[i].succ>=-2)&&((v[i].dist<0)||
				(v[i].dist>v[u].dist+e[j].len))) {
				if (v[i].dist<0)
					append_pre_s(i);
				v[i].dist = v[u].dist + e[j].len;
				v[i].prev = u;	  /* label */
			}
			j = e[j].delta_minus;
		}
		}
		v[u].succ = -4;
	}

	void step3() {		/* find the shortest node in Sbar */
		int i,rho;

		rho = -1;
		for (i = pre_s_first; i>=0; i = v[i].succ) {
			if ((rho < 0)||(rho>v[i].dist)) {
				rho = v[i].dist;
				u = i;
			}
		}
		remove_pre_s(u);
		v[u].succ = -3;
                if (v[u].id == p2) {
                  //remonter le chemin en creant le vecteur de sortie
                  int k=0;
                  while (true) {
                    findGoal = true;
                    chemin.insertElementAt(new EtatReseau(v[u].id, v[u].prev), 0);
                    if (v[u].id == p1)
                      break;
                    if (v[u].prev >= 0)
                      u = v[u].prev;
                    else {
                      chemin.clear();
                      break;
                    }
                    k++;
                    if (k > n) {
                      chemin.clear();
                      break;
                    }
                  }
                }
	}

	void step4() {
		v[u].succ = -4;
	}

	double weight(Node n, double x, double y) {
		double	w,z,xx,yy;

		w = 0;
		for (int j = n.delta_plus; j>=0; j=e[j].delta_plus) {
			xx = (double)(v[e[j].rndd_minus].x - n.x);
			yy = (double)(v[e[j].rndd_minus].y - n.y);
			z = (x*xx+y*yy)/Math.sqrt((x*x+y*y)*(xx*xx+yy*yy))+1.0;
			w += z*z*z*z;
		}
		for (int j = n.delta_minus; j>=0; j=e[j].delta_minus) {
			xx = (double)(v[e[j].rndd_plus].x - n.x);
			yy = (double)(v[e[j].rndd_plus].y - n.y);
			z = (x*xx+y*yy)/Math.sqrt((x*x+y*y)*(xx*xx+yy*yy))+1.0;
			w += z*z*z*z;
		}
		return w;
	}

	void init_sub() {
		int x[] = {1, 0, -1, 1, 0, -1};
		int y[] = {1, 1, 1, -1, -1, -1};
		int 	i,j,k;
		double	w,z;

		for (i=0; i<n; i++) {
			k=0;
			w=weight(v[i],(double)x[0],(double)y[0]);
			for (j=1; j<6; j++) {
				z=weight(v[i],(double)x[j],(double)y[j]);
				if (z<w) {
					w = z;
					k = j;
				}
			}
			v[i].dx = x[k];
			v[i].dy = y[k];
		}
	}

    public void init(ReseauRoutier res, float z) {
      reseau = res;
      zoom = z;
      n = reseau.getNombrePoints();
      m = reseau.getNombreConnexions();
      v = new Node[n];
      e = new Edge[m];
      input_graph(reseau);
      findGoal = false;
      chemin.clear();
      iteration = step = 0;
      snode = 0;
      rdb();
      init_sub();
    }

	public Vector<EtatReseau> solve(int ps, int pe) {
          iteration = step = 0;
          p1 = ps;
          p2 = pe;
          findGoal = false;
          chemin.clear();
          if (p1 == p2) {
            chemin.insertElementAt(new EtatReseau(p1, p2), 0);
            return chemin;
          }
          snode = findNode(p1);
          enode = findNode(p2);
          rdb();
          init_sub();
          Exec();
          return chemin;
	}

	int [] xy(int a, int b, int w, int h) {
		int	x[] = new int[2];

		if (Math.abs(w*b)>=Math.abs(h*a)) {
			x[0] = ((b>=0)?1:-1)*a*h/b/2;
			x[1] = ((b>=0)?1:-1)*h/2;
		} else {
			x[0] = ((a>=0)?1:-1)*w/2;
			x[1] = ((a>=0)?1:-1)*b*w/a/2;
		}
		return x;
	}

	public void Exec() {
          while (iteration<n) {
            if (iteration == 0) {
              step1();
              iteration++;
              step = 2;
            }
            else {
              if (step == 2) {
                step2();
                step = 3;
              }
              else {
                step3();
                iteration++;
                step = 2;
              }
            }
            if (findGoal)
                break;
          }
          step4();
	}
}

