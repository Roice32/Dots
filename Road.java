import java.awt.*;

public class Road
{
	public Node start, end;
	public double cost;
	
	Road(Node a, Node b, Graphics g, Color c)
	{
		start = a;
		end = b;
		cost = dist(a,b);
		paint(g, c);
	}
	
	// Metodă geometrică de a calcula costul muchiei cu precizie suficientă.
	// Rădăcina pătrată se aplică pentru a nu lucra cu numere prea mari la găsirea de drumuri.
	public double dist(Node a, Node b)
	{
		return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));
	}
	
	// Desenează muchia.
	public void paint(Graphics g, Color c)
	{
		Graphics2D g2D = (Graphics2D)g;
		g2D.setColor(c);
        g2D.setStroke(new BasicStroke(10));
        g2D.drawLine(start.x, start.y, end.x, end.y);
        start.paint(g);
        end.paint(g);
    }
	
	// Șterge muchia (doar vizual).
	public void delete(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		g2D.setColor(Color.ORANGE);
        g2D.setStroke(new BasicStroke(10));
        g2D.drawLine(start.x, start.y, end.x, end.y);
        start.paint(g);
        end.paint(g);
	}
}
