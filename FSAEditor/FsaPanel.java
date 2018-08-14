import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class FsaPanel extends JPanel implements FsaListener{
	private FsaImpl fsa;
	private Set<State> states;
    private Set<String> selectedStates;
	private Set<StateIcon> stateIcons;
	private Set<Transition> transitions;
    private Set<TransitionIcon> transitionIcons;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;

    private State newState;
    private String newTransitionName;
    private State transitionFrom, transitionTo;
    private Boolean drawLine = false;

    private JPanel thisPanel;

    private String selectionState = "idle";
    private int x0, y0;
    private Point clickPoint, dragPoint;
    private Rectangle selectedArea;

	public FsaPanel(FsaImpl f){
        this.setLayout(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setVisible(true);
        this.setBackground(Color.WHITE);
        this.repaint();

        thisPanel = this;

        setMouseListeners();
        selectedArea = null;

		stateIcons = new LinkedHashSet<StateIcon>();
        transitionIcons = new LinkedHashSet<TransitionIcon>();
        selectedStates = new LinkedHashSet<String>();

        clickPoint = new Point();
        dragPoint = new Point();

		fsa = f;
		states = fsa.getStates();
        transitions = fsa.getTransitions();
	}

    public void resetFsa(FsaImpl f){
        fsa = f;
        states = fsa.getStates();
        transitions = fsa.getTransitions();
        fsa.addListener(this);

        for(StateIcon si : stateIcons){
            this.remove(si);
        }

        for(TransitionIcon ti : transitionIcons){
            this.remove(ti);
        }

        stateIcons = new LinkedHashSet<StateIcon>();
        transitionIcons = new LinkedHashSet<TransitionIcon>();
        selectedStates = new LinkedHashSet<String>();
        this.repaint();
    }

	//Called whenever the number of states in the FSA has changed
    public void statesChanged(){
    	Set<State> newStates = fsa.getStates();
    	newStates.removeAll(states);

    	if(newStates.size() > 0){
    		for(State s : newStates){
    			StateIcon stateIcon = new StateIcon(s);
                s.addListener(stateIcon);
    			this.add(stateIcon);
    			stateIcons.add(stateIcon);
    		}
    	}

    	else{
    		newStates = fsa.getStates();
    		Set<State> oldStates = states;
    		oldStates.removeAll(newStates);

            Iterator stateItr = oldStates.iterator();

            while(stateItr.hasNext()){
                State s = (State)stateItr.next();

                Iterator iconsItr = stateIcons.iterator();

                while(iconsItr.hasNext()){
                    StateIcon si = (StateIcon)iconsItr.next();
    				if(si.getState() == s){
                        iconsItr.remove();
                        this.remove(si);
    				}
    			}
    		}
    	}

    	states = fsa.getStates();
        this.repaint();
        this.revalidate();
    }

    //Called whenever the number of transitions in the FSA has changed
    public void transitionsChanged(){
    	Set<Transition> newTransitions = fsa.getTransitions();
        newTransitions.removeAll(transitions);

        if(newTransitions.size() > 0){
            for(Transition t : newTransitions){
                TransitionIcon transitionIcon = new TransitionIcon(t);
                t.addListener(transitionIcon);
                this.add(transitionIcon);
                transitionIcons.add(transitionIcon);
            }
        }

        else{
            newTransitions = fsa.getTransitions();
            Set<Transition> oldTransitions = transitions;
            oldTransitions.removeAll(newTransitions);

            Iterator transItr = oldTransitions.iterator();

            while(transItr.hasNext()){
                Transition t = (Transition)transItr.next();

                Iterator iconsItr = transitionIcons.iterator();
                while(iconsItr.hasNext()){
                    TransitionIcon ti = (TransitionIcon)iconsItr.next();
                    if(ti.getTransition() == t){
                        iconsItr.remove();
                        this.remove(ti);
                    }
                }
            }
        }

        transitions = fsa.getTransitions();
        this.repaint();
        this.revalidate();
    }

    //Called whenever something about the FSA has changed
    //(other than states or transitions)
    public void otherChanged(){
    	//System.out.println("otherChanged");
    }

    public void setMouseListeners(){
        mouseListener = new MouseListener(){
            public void mouseEntered(MouseEvent e){
            }

            public void mouseExited(MouseEvent e){
            }

            public void mousePressed(MouseEvent e){
                Boolean clickedCircle = false;
                StateIcon selected = null;
                selectedArea = null;

                for(StateIcon si : stateIcons){
                    if(si.onCircle(e.getX(), e.getY())){
                        clickedCircle = true;
                        selected = si;
                    }
                }

                if(!clickedCircle && selectionState.equals("idle")){
                    clickPoint = e.getPoint();
                    unselectAll();
                    selectionState = "selecting";
                }
                else if(clickedCircle && selectionState.equals("idle")){
                    clickPoint = e.getPoint();

                    if(!selected.isSelected()){
                        unselectAll();
                        selected.setSelected(true);
                        selectionState = "dragging";
                    }
                    //UNSURE IF SPEC WANTS THIS??
                    else if(selected.isSelected()){
                        selectionState = "dragging";
                    }
                }

                if(clickedCircle && selectionState.equals("new transition")){
                    if(transitionFrom == null){
                        transitionFrom = selected.getState();
                        clickPoint = new Point(transitionFrom.getXpos() + 40, transitionFrom.getYpos() + 40);
                        dragPoint = new Point(transitionFrom.getXpos() + 40, transitionFrom.getYpos() + 40);
                        drawLine = true;
                        repaint();
                    }
                    else{
                        transitionTo = selected.getState();

                        try{
                            fsa.newTransition(transitionFrom, transitionTo, newTransitionName);
                        }
                        catch(Exception ex){
                            JOptionPane.showMessageDialog(thisPanel, "ERROR " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        drawLine = false;
                        selectionState = "idle";
                        setCursor(Cursor.getDefaultCursor());
                        repaint();
                        transitionFrom = null;
                        transitionTo = null;
                    }

                }
            }

            public void mouseClicked(MouseEvent e){
                if(selectionState.equals("new state")){
                    selectionState = "idle";
                }
            }

            public void mouseReleased(MouseEvent e){
                if(selectionState.equals("selecting") || selectionState.equals("dragging")){
                    selectionState = "idle";
                    clickPoint = null;
                    dragPoint = null;
                    selectedArea = null;
                    repaint();
                }
            }
        };

        mouseMotionListener = new MouseMotionListener(){
            public void mouseMoved(MouseEvent e){
                if(selectionState.equals("new state")){
                    dragPoint = e.getPoint();

                    newState.moveBy(dragPoint.x - clickPoint.x, dragPoint.y - clickPoint.y);

                    clickPoint = dragPoint;
                }

                if(selectionState.equals("new transition")){
                    if(transitionFrom != null){
                        dragPoint = e.getPoint();
                        repaint();
                    }
                }
            }

            public void mouseDragged(MouseEvent e){
                if(selectionState.equals("selecting")){
                    dragPoint = e.getPoint();
                    int x = Math.min(clickPoint.x, dragPoint.x);
                    int y = Math.min(clickPoint.y, dragPoint.y);
                    int width = Math.max(clickPoint.x - dragPoint.x, dragPoint.x - clickPoint.x);
                    int height = Math.max(clickPoint.y - dragPoint.y, dragPoint.y - clickPoint.y);
                    selectedArea = new Rectangle(x, y, width, height);

                    for(StateIcon si : stateIcons){
                        State s = si.getState();
                        if(selectedArea.intersects(new Rectangle(s.getXpos(), s.getYpos(), 80, 80))){
                            si.setSelected(true);
                        }
                        else{
                            si.setSelected(false);
                        }
                    }
                    repaint();
                }

                if(selectionState.equals("dragging")){
                    for(StateIcon si: stateIcons){
                        dragPoint = e.getPoint();
                        if(si.isSelected()){
                            State s = si.getState();

                            s.moveBy(dragPoint.x - clickPoint.x, dragPoint.y - clickPoint.y);
                            repaint();
                        }
                    }
                    clickPoint = dragPoint;
                }
            }
        };

        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseMotionListener);
    }

    protected void paintComponent(Graphics grfx){
        Graphics2D g = (Graphics2D)grfx.create();
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        if(drawLine){
            g.drawLine(clickPoint.x, clickPoint.y, dragPoint.x, dragPoint.y);
        }

        g.setColor(new Color(255, 255, 255, 128));
        g.setStroke(new BasicStroke(1));

        Area fill = new Area(new Rectangle(new Point(0, 0), getSize()));
        if(selectedArea != null){
            fill.subtract(new Area(selectedArea));
        }
        g.fill(fill);
        if(selectedArea != null){
            g.setColor(Color.BLUE);
            g.draw(selectedArea);
        }

        g.dispose();
    }

    public void unselectAll(){
        for(StateIcon si : stateIcons){
            si.setSelected(false);
            this.repaint();
        }
    }

    public Set<StateIcon> getStateIcons(){
        return stateIcons;
    }

    public void printStates(){
        states = fsa.getStates();
        for(State s : states){
            System.out.println(s.getName());
        }
    }

    public void addState(String name){
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;
        
        try{
            newState = fsa.newState(name, x-40, y-100);
            clickPoint = new Point(x, y-60);
            selectionState = "new state";
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "ERROR " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addTransition(String name){
        selectionState = "new transition";
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.repaint(); //get this to actually repaint
        newTransitionName = name;
    }

    public void deleteSelected(){
        Iterator iconsItr = stateIcons.iterator();
        while(iconsItr.hasNext()){
            StateIcon si = (StateIcon)iconsItr.next();
            if(si.isSelected()){
                selectedStates.add(si.getState().getName());
            }
        }

        Iterator selStates = selectedStates.iterator();
        while(selStates.hasNext()){
            State s = (State)fsa.findState((String)selStates.next());
            fsa.removeState(s);
        }

        this.repaint();
    }
}