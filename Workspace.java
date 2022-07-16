import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;

public class Workspace extends JFrame implements MouseListener, ActionListener
{	
	// Memorează numărul de ordine și nodul, respectiv muchia.
	HashMap<Integer,Node> nodes = new HashMap<Integer,Node>();
	HashMap<Integer,Road> roads = new HashMap<Integer,Road>();
	int linkFirst=-1; // Pentru acțiunile ce necesită două noduri.
	
	// Butoanele de schimbare a acțiunii click-ului.
	JPanel controls = new JPanel();
	JButton createNode = new JButton();
	JButton eraseNode = new JButton();
	JButton createLink = new JButton();
	JButton eraseLink = new JButton();
	JButton clearAll = new JButton();
	JButton path = new JButton();
	public static char act = 's';
	
	// Instrucțiunile
	JPanel title = new JPanel();
	JLabel titleText = new JLabel();
	
	Workspace()
	{
		// Spațiul de lucru
		setTitle("Dots");
		setSize(720,720);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.ORANGE);
		
		// Text
		titleText.setForeground(Color.PINK);
		titleText.setFont(new Font("Times New Roman", Font.BOLD, 40));
		titleText.setText("Selectați acțiunea");
		titleText.setHorizontalAlignment(JLabel.CENTER);
		title.setLayout(new BorderLayout());
		title.setBackground(Color.BLACK);
		title.setPreferredSize(new Dimension(720,50));
		title.add(titleText);
		
		// Butoane
		createNode.setText("Adaugă noduri");
		createNode.setPreferredSize(new Dimension(360,50));
		createNode.setFocusable(false);
		createNode.addActionListener(this);
		eraseNode.setText("Șterge noduri");
		eraseNode.setPreferredSize(new Dimension(360,50));
		eraseNode.setFocusable(false);
		eraseNode.addActionListener(this);
		createLink.setText("Adaugă muchie");
		createLink.setPreferredSize(new Dimension(360,50));
		createLink.setFocusable(false);
		createLink.addActionListener(this);
		eraseLink.setText("Șterge muchie");
		eraseLink.setPreferredSize(new Dimension(360,50));
		eraseLink.setFocusable(false);
		eraseLink.addActionListener(this);
		clearAll.setText("Șterge tot");
		clearAll.setPreferredSize(new Dimension(360,50));
		clearAll.setFocusable(false);
		clearAll.addActionListener(this);
		path.setText("Găsește drum");
		path.setPreferredSize(new Dimension(360,50));
		path.setFocusable(false);
		path.addActionListener(this);

		controls.setLayout(new GridLayout(2,3));
		controls.setVisible(true);
		controls.add(createNode);
		controls.add(createLink);
		controls.add(path);
		controls.add(eraseNode);
		controls.add(eraseLink);
		controls.add(clearAll);
		
		add(title, BorderLayout.NORTH);
		add(controls, BorderLayout.SOUTH);
		addMouseListener(this);
		setVisible(true);
	}
	
	// Schimbă acțiunea click-ului.
	public void actionPerformed(ActionEvent e)
	{	
		if(e.getSource()==createNode)
		{
			act='a';
			titleText.setText("Adăugare noduri");
		}
		if(e.getSource()==eraseNode)
		{
			act='e';
			titleText.setText("Ștergere noduri");
		}
		if(e.getSource()==createLink)
		{
			act='l';
			titleText.setText("[Adăugare] Alege primul nod");
		}
		if(e.getSource()==eraseLink)
		{
			act='d';
			titleText.setText("[Ștergere] Alege primul nod");
		}
		if(e.getSource()==clearAll)
		{
			act='s';
			eraseAll();
		}
		if(e.getSource()==path)
		{
			act='p';
			titleText.setText("[Drum] Alege primul nod");
		}
	}
	
	
	// Determină acțiunea ce trebuie efectuată la click.
	public void mouseClicked(MouseEvent e)
	{
		int x=e.getX(), y=e.getY();
		switch(act)
		{
		case 'a':
			addNode(x,y);
			break;
		case 'e':
			delNode(x,y);
			break;
		case 'l':
			addLink(x,y);
			break;
		case 'd':
			delLink(x,y);
			break;
		case 'p':
			pathFindTrigger(x,y);
			break;
		case 'r':
			redrawAll();
		}
	}
	
	// Adaugă nodul.
	public void addNode(int x, int y)
	{
		if(y<100 || y>590) return; // Nodurile să nu intre în text/butoane.
		if(x<20 || x>700) return;  // Nodurile să nu intre în margini.
		int over=overlapping(x,y); // Nodurile să nu se suprapună.
		if(over==-1)
		{
			for(int i=0; i<=nodes.size(); i++) // Noul nod se adaugă în prima
				if(!nodes.containsKey(i))      // cheie liberă (în urma unei ștergeri)
				{							   // din HashMap, sau la finalul ei.
					nodes.put(i,new Node(x,y,getGraphics()));
					break;
				}
		}
		else
			JOptionPane.showMessageDialog(null, "Alege altă locație.", "Suprapunere!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	// Șterge nodul.
	public void delNode(int x, int y)
	{
		int over=mouseOver(x,y);
		if(over!=-1)
		{
			for(int i = 0; i<nodes.size(); i++) // Șterge toate muchiile adiacente nodului.
			{
				linkFirst=i;
				delLink(x,y);
			}
			linkFirst=-1;
			nodes.get(over).delete(getGraphics()); // Șterge nodul vizual.
			nodes.remove(over);					   // Șterge nodul în memorie.
			redrawAll();
			titleText.setText("Ștergere noduri");
			act='e';
		}
	}
	
	// Adaugă muchia.
	public void addLink(int x, int y)
	{
		int over=mouseOver(x,y);
		if(over!=-1)
			if(linkFirst==-1) // Memorează primul capăt al muchiei.
			{
				linkFirst=over;
				titleText.setText("[Adăugare] Alege al doilea nod");
			}
			else if(over!=linkFirst) // Creează muchia când are 2 noduri valide.
			{
				boolean duplicate = false; // Verifică să nu se repete o muchie deja existentă.
				for(Map.Entry<Integer,Road> i : roads.entrySet())
						if(i.getValue().start.equals(nodes.get(linkFirst)) && i.getValue().end.equals(nodes.get(over)) ||
						   i.getValue().end.equals(nodes.get(linkFirst)) && i.getValue().start.equals(nodes.get(over)))
							duplicate = true;
				if(!duplicate)
				{
					for(int i=0; i<=roads.size(); i++) // Memorează muchia în prima cheie liberă din HashMap
						if(!roads.containsKey(i))	   // (în urma unei ștergeri), sau la sfârșitul ei.
						{
							roads.put(i,new Road(nodes.get(linkFirst),nodes.get(over),getGraphics(), Color.BLUE));
							break;
						}
				}
				else
					JOptionPane.showMessageDialog(null, "Alege alte noduri", "Muchie deja existentă!", JOptionPane.INFORMATION_MESSAGE);
				titleText.setText("Selectați acțiunea");
				linkFirst=-1;
				act='s';
			}
	}
	
	// Șterge muchia.
	public void delLink(int x, int y)
	{
		int over=mouseOver(x,y);
		if(over!=-1)
			if(linkFirst==-1) // Memorează primul capăt al muchiei.
			{
				linkFirst=over;
				titleText.setText("[Ștergere] Alege al doilea nod");
			}
			else if(over!=linkFirst) // Șterge muchia când are 2 noduri distincte.
			{
				for(Map.Entry<Integer,Road> i : roads.entrySet()) // Caută muchia, dacă există.
					if(i.getValue().start.equals(nodes.get(linkFirst)) && i.getValue().end.equals(nodes.get(over)) ||
					   i.getValue().end.equals(nodes.get(linkFirst)) && i.getValue().start.equals(nodes.get(over)))
					{
						i.getValue().delete(getGraphics());
						roads.remove(i.getKey());
						redrawAll();
						break;
					}
				titleText.setText("Selectați acțiunea");
				linkFirst = -1;
				act = 's';
			}
	}
	
	// Redesenează toate nodurile & muchiile
	// Pentru cazul în care un nod (o muchie) șters (ștearsă)
	// se suprapunea cu noduri sau muchii.
	public void redrawAll()
	{
		for(Road i : roads.values())
			i.paint(getGraphics(), Color.BLUE);
		for(Node i: nodes.values())
			i.paint(getGraphics());
		titleText.setText("Selectați acțiunea");
		act='s';
	}
		
	// Șterge toate nodurile & muchiile.
	public void eraseAll()
	{
		for(Road i : roads.values())
			i.delete(getGraphics());
		roads.clear();
		for(Node i : nodes.values())
			i.delete(getGraphics());
		nodes.clear();
		act='s';
	}
	
	// Memorează cele două noduri între care se caută drumul
	// apoi apelează funcția de căutare propriu-zisă.
	public void pathFindTrigger(int a, int b)
	{
		int over=mouseOver(a,b);
		if(over!=-1)
			if(linkFirst==-1)
			{
				linkFirst=over;
				titleText.setText("[Drum] Alege al doilea nod");
			}
			else if(over!=linkFirst)
				 {
					pathFind(linkFirst, over);
					linkFirst = -1;
				 }
	}

	// A* Pathfinding Algorithm pe graf neorientat.
	// Din cauza căutării muchiilor ce conțin nodurilor, 
	// complexitate O(n^3)...
	public void pathFind(int s, int e)
	{
		int curr = 1; // Nodul curent. Inițializat cu 1 pentru a intra în buclă
		double minCost; // Pentru găsirea nodului deschis cu costul cel mai mic.
		boolean found = false;
		
		for(Node i : nodes.values()) // Inițializează nodurile pentru algoritmul de căutare.
		{
			i.h = Double.MAX_VALUE;
			i.status = 'u';
		}
		
		nodes.get(s).parent = -1;  // Setează s ca nod de start.
		nodes.get(s).status = 'o';
		nodes.get(s).h = 0;
		
		while(!found && curr!=-1) // !!!
		{
			curr = -1;
			minCost = Double.MAX_VALUE;
			for(Map.Entry<Integer,Node> i : nodes.entrySet()) // Caută nodul deschis cu cel mai mic cost.
				if(i.getValue().status=='o' && i.getValue().h<minCost)
				{
					minCost = i.getValue().h;
					curr = i.getKey();
				}
			if(curr==-1) break; // Pentru cazul în care nu se poate găsi drum de la s la e.
			nodes.get(curr).status='c'; // Marchează nodul curent drept vizitat.
			if(curr==e) // Verifică dacă am ajuns la final.
			{
				found = true;
				break;
			}
			for(Map.Entry<Integer,Node> i : nodes.entrySet()) 
			{
				if(i.getValue().status!='c')
					for(Road r : roads.values())
						if(r.start.equals(nodes.get(curr)) && r.end.equals(i.getValue()) ||
						   r.end.equals(nodes.get(curr)) && r.start.equals(i.getValue()))
							if(nodes.get(curr).h+r.cost<i.getValue().h) // Actualizează noii vecini disponibili ai nodului curent, și pe
							{											// cei deja vizitați, dacă există drum mai scurt până la ei prin curr.
								i.getValue().h = nodes.get(curr).h+r.cost;
								i.getValue().parent = curr;
								i.getValue().status = 'o';
							}
			}
		}
			if(found)
			{
				while(nodes.get(curr).parent!=-1) // Afișează cu verde muchiile, în ordinea inversă parcurgerii lor.
					for(Road r : roads.values())
						if(r.start.equals(nodes.get(curr)) && r.end.equals(nodes.get(nodes.get(curr).parent)) ||
						   r.end.equals(nodes.get(curr)) && r.start.equals(nodes.get(nodes.get(curr).parent)))
						{
							r.paint(getGraphics(), Color.GREEN);
							curr = nodes.get(curr).parent;
						}
				JOptionPane.showMessageDialog(null, "Cel mai scurt drum între nodurile selectate!", "Drum găsit!", JOptionPane.INFORMATION_MESSAGE);
				titleText.setText("Click oriunde pentru a reseta muchiile.");
				act='r';
			}
			else
				JOptionPane.showMessageDialog(null, "Nu există drum între nodurile selectate!", "Drum inexistent!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	// Verifică dacă noul nod adăugat nu s-ar suprapune cu un nod deja existent,
	// adică dacă distanța dintre centrele celor două cercuri < diametrul.
	// Returnează numărul cercului cu care s-ar suprapune, sau -1 în caz contrar.
	public int overlapping(int a, int b)
	{
		for(Map.Entry<Integer,Node> i : nodes.entrySet())
			if(Math.pow(a-i.getValue().x,2)+Math.pow(b-i.getValue().y,2)<36*36)
				return i.getKey();
		return -1;
	}
	
	// Verifică dacă mouse-ul se află plasat pe un nod, adică 
	// dacă distanța dintre mouse și centrul cercului <= raza.
	// Returnează numărul nodului, sau -1 în caz contrar.
	public int mouseOver(int a, int b)
	{
		for(Map.Entry<Integer,Node> i : nodes.entrySet())
			if(Math.pow(a-i.getValue().x,2)+Math.pow(b-i.getValue().y,2)<18*18)
				return i.getKey();
		return -1;
	}
	
	// Funcții reziduale de la MouseListener
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

}
