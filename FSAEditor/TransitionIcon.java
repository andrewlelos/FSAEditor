import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.lang.Math;
import java.awt.geom.QuadCurve2D;


public class TransitionIcon extends JComponent implements TransitionListener{
	private Transition t;
	private State fromState;
	private State toState;
	private JLabel name;

	private double x1, y1, x2, y2, xm, ym, s1, s2, s3, ss1, ss2, ss3, xc, yc;
	private double xb1, yb1, xb2, yb2;

	public TransitionIcon(Transition tra){
		t = tra;
		fromState = t.fromState();
		toState = t.toState();

		String evName = t.eventName();
		if(evName == null){
			evName = "?";
		}

		name = new JLabel(evName, SwingConstants.CENTER);
		this.add(name);
		name.setBounds(0, 0, 100, 100);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
		this.setVisible(true);
		this.repaint();
	}

	public void TransitionHasChanged(){
		this.repaint();	
	}

	public void paintComponent(Graphics g){
 		Graphics2D grfx = (Graphics2D)g;
		super.paintComponent(g);

		Boolean toStateIsLeft = false;
		Boolean toStateIsAbove = false;
 
        x1 = (double)fromState.getXpos() + 40;
        y1 = (double)fromState.getYpos() + 40;
        x2 = (double)toState.getXpos() + 40;
        y2 = (double)toState.getYpos() + 40;

        if(x2 < x1){
        	toStateIsLeft = true;
        }

        if(y2 < y1){
        	toStateIsAbove = true;
        }

        xm = (x1 + x2)/2;
        ym = (y1 + y2)/2;

        s1 = 100; //d
        //distance from xm, ym to x2, y2
        s2 = Math.sqrt((xm-x2)*(xm-x2) + (ym-y2)*(ym-y2));
        //distance from xc, yc to x2, y2
        s3 = Math.sqrt((s1*s1) + (s2*s2));

        ss1 = s1 * (40/s3);
        ss2 = s2 * (40/s3);
        ss3 = 40;

        if(toStateIsLeft){
        	xc = x2 + s2;
        	xb1 = x1 - ss2;
        	xb2 = x2 + ss2;
        }
        else{
        	xc = x2 - s2;
        	xb1 = x1 + ss2;
        	xb2 = x2 - ss2;
        }

        if(toStateIsAbove){
            yb2 = y2 + ss1;
            yb1 = y1 - ss1;
            yc = y2 + s1;
        }
        else{
            yb2 = y2 - ss1;
            yb1 = y1 + ss1;
            yc = y2 - s1;
        }

   //      yc = y2 - s1;
 		// yb1 = y1 - ss1;
   //      yb2 = y2 - ss1;


        name.setBounds((int)xc-80, (int)yc-80, 200, 200);


        QuadCurve2D line = new QuadCurve2D.Double();
		line.setCurve(xb1, yb1, xc, yc, xb2, yb2);

		grfx.setStroke(new BasicStroke(2));
		grfx.draw(line);


		//arrowheads

		double a = 0.3;
		double b = 0.25;


		double OCx = xb2 - x2;
		double OCy = yb2 - y2;

		double Bx = xb2 + (a * OCx);
		double By = yb2 + (a * OCy);

		double DPx = OCy;
		double DPy = OCx * -1;

		double Px = Bx + (b * DPx);
		double Py = By + (b * DPy);

		double Dx = Bx - (b * DPx);
		double Dy = By - (b * DPy);

		grfx.drawLine((int)Px, (int)Py, (int)xb2, (int)yb2);
		grfx.drawLine((int)Dx, (int)Dy, (int)Px, (int)Py);
		grfx.drawLine((int)Dx, (int)Dy, (int)xb2, (int)yb2);

	}

	public Transition getTransition(){
		return t;
	}
}