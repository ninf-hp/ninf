package ninf.calc.plus;
import java.awt.*;

public class Blinker extends Thread {
    Panel panel;
    public Blinker(Panel panel) {
	this.panel = panel;
    }
    public void run() {
	try {
	    while (true) {
		panel.setBackground(new Color(80, 80, 255));
		panel.repaint();
		sleep(500);
		panel.setBackground(new Color(220, 220, 220));
		panel.repaint();
		sleep(500);
	    }
	} catch (InterruptedException e) {
	    panel.setBackground(new Color(220, 220, 220));
	} catch (ThreadDeath e) {
	    panel.setBackground(new Color(220, 220, 220));
	}
    }
}
