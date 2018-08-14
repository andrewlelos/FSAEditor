import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;

public class StateIcon extends JComponent implements StateListener{
	private State s;
	private int xPos, yPos;
	private Boolean isSelected;

	private JLabel name;

	public StateIcon(State st){
		s = st;
		isSelected = false;
		xPos = s.getXpos();
		yPos = s.getYpos();

		name = new JLabel(st.getName(), SwingConstants.CENTER);
		name.setVisible(true);
		this.add(name);
		setBounds(xPos, yPos, 200, 200);
		this.setVisible(true);
		this.repaint();
	}

	//Called whenever the observable properties of a state have changed
	public void StateHasChanged(){
		xPos = s.getXpos();
		yPos = s.getYpos();	
		this.setBounds(xPos, yPos, 200, 200);
		this.repaint();	
	}

	public State getState(){
		return s;
	}

	public void paintComponent(Graphics grfx){
		Graphics2D g = (Graphics2D)grfx;
		super.paintComponent(g);

		name.setBounds(-60, -60, 200, 200);

		g.setStroke(new BasicStroke(1));

		if(isSelected){
			g.setColor(Color.GRAY);
			g.fillOval(0, 0, 80, 80);
		}
		else{
			g.setColor(Color.BLACK);
			g.drawOval(0, 0, 80, 80);
		}

		g.setColor(Color.BLACK);
		if(s.isCurrent()){
			g.fillOval(30, 50, 22, 22);
		}

		if(s.isFinal()){
			g.setStroke(new BasicStroke(1));
			g.drawOval(4, 4, 72, 72);
		}

		//FIX LIGHTNING BOLT
		if(s.isInitial()){
			g.setStroke(new BasicStroke(2));
			g.drawLine(42, 10, 38, 20);
	 		g.drawLine(46, 16, 42, 26);
	 		g.drawLine(38, 20, 46, 16);
		}
	}

	public Boolean onCircle(int x, int y){
		int dx = Math.abs(x-(s.getXpos() + 40));
		int dy = Math.abs(y-(s.getYpos() + 40));

		return ((dx*dx) + (dy*dy) <= 1600);
	}

	public void flipSelected(){
		if(isSelected){
			isSelected = false;
		}
		else{
			isSelected = true;
		}

		this.repaint();
	}

	public void setSelected(Boolean state){
		isSelected = state;
	}

	public Boolean isSelected(){
		return isSelected;
	}
}