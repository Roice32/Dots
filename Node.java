import java.awt.*;
import javax.swing.JPanel;

public class Node extends JPanel
{
	public int x, y, parent; // parent - pentru pathfinding.
	double h; // Costul nodului (pentru funcția de pathfinding).
	char status; // 'u' = unchecked, 'o' = open, 'c' = closed.
	
	Node(int a, int b, Graphics c)
	{
		x=a; y=b;
		paint(c);
	}
	
	
	// Desenează nodul.
	public void paint(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
        g2D.setColor(Color.BLACK);
        g2D.fillOval(x-18, y-18, 36, 36);
        g2D.setColor(Color.RED);
        g2D.fillOval(x-16, y-16, 32, 32);
    }
	
	// Șterge nodul (doar vizual).
	public void delete(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
        g2D.setColor(Color.ORANGE);
        g2D.fillOval(x-18, y-18, 36, 36);
	}
}