import java.util.*;

public class StateClass implements State, StateListener
{

    private String name;
    private int xPos;
    private int yPos;
    private boolean initialState;
    private boolean finalState;
    private boolean currentState;
    private Set<Transition> transitionsFrom;
    private Set<Transition> transitionsTo;
    private StateIcon si;
    private StateListener listener;

    public StateClass(String n, int x, int y){
        name = n;
        xPos = x;
        yPos = y;
        initialState = false;
        finalState = false;
        currentState = false;
        transitionsFrom = new LinkedHashSet<Transition>();
        transitionsTo = new LinkedHashSet<Transition>();
    }


    //Add a listener to this state
    public void addListener(StateListener sl){
        listener = sl;
    }


    //Remove a listener to this state
    public void removeListener(StateListener sl){
        listener = null;
    }


    //Return a set containing all transitions FROM this state
    public Set<Transition> transitionsFrom(){
        return new LinkedHashSet<Transition>(transitionsFrom);
    }


    //Return a set containing all transitions TO this state
    public Set<Transition> transitionsTo(){
        return new LinkedHashSet<Transition>(transitionsTo);
    }

    public void addTransitionFrom(Transition from){
        //System.out.println("From: " + name + " To: " + from.toState().getName() + " " + from.eventName());
        transitionsFrom.add(from);
        listener.StateHasChanged();
        //si.StateHasChanged();
    }

    public void removeTransitionFrom(Transition from){
        transitionsFrom.remove(from);
        listener.StateHasChanged();
        //si.StateHasChanged();
    }

    public void addTransitionTo(Transition to){
        transitionsTo.add(to);
        listener.StateHasChanged();
        //si.StateHasChanged();
    }

    public void removeTransitionTo(Transition to){
        transitionsTo.remove(to);
        listener.StateHasChanged();
        //si.StateHasChanged();
    }
    

    //Move the position of this state 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy){
        xPos += dx;
        yPos += dy;
        listener.StateHasChanged();
        //si.StateHasChanged();
    }
    

    //Return a string containing information about this state 
    //in the form (without the quotes, of course!) :
    //"stateName(xPos,yPos)jk"
    //where j is 1/0 if this state is/is-not an initial state  
    //where k is 1/0 if this state is/is-not a final state  
    public String toString(){
        String states = "";

        if(initialState == false){
            states += "0";
        }
        else{
           states += "1"; 
        }

        if(finalState == false){
            states += "0";
        }
        else{
           states += "1"; 
        }


        return name + "(" + xPos + "," + yPos + ")" + states;
    }
    

    //Return the name of this state 
    public String getName(){
        return name;
    }
    

    //Return the X position of this state
    public int getXpos(){
        return xPos;
    }
    

    //Return the Y position of this state
    public int getYpos(){
        return yPos;
    }

    //Set/clear this state as an initial state
    public void setInitial(boolean b){
        initialState = b;
        listener.StateHasChanged();
        //si.StateHasChanged();
    }

    //Indicate if this is an initial state
    public boolean isInitial(){
        return initialState;
    }

    //Set/clear this state as a final state
    public void setFinal(boolean b){
        finalState = b;
        listener.StateHasChanged();
        //si.StateHasChanged();
    }

    //Indicate if this is a final state
    public boolean isFinal(){
        return finalState;
    }

    //Indicate if this is a current state
    public boolean isCurrent(){
        return currentState;
    }

    public void setCurrent(boolean b){
        currentState = b;
        listener.StateHasChanged();
        //si.StateHasChanged();
    }

    //Called whenever the observable properties of a state have changed
    public void StateHasChanged(){
    }
}