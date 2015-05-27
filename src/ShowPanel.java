import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ShowPanel extends JPanel {
	private BufferedImage image;
	int count = 0;

	public ShowPanel() {
		super();
	}

	public void setFrame(BufferedImage frame) {
		image = frame;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image == null) {
			System.out.println("Pusta klatka!");
			return;
		}
		g.drawImage(this.image, 10, 10, this.image.getWidth(),
				this.image.getHeight(), null);
		g.setFont(new Font("arial", 2, 20));
		g.setColor(Color.BLACK);
		g.drawString("Frame: " + (count++), 50, 50);

	}

}
