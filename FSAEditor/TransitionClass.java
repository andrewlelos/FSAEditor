public class TransitionClass implements Transition, TransitionListener
{

    private State fromState;
    private State toState;
    private String eventName;
    private TransitionListener listener;

    public TransitionClass(State from, State to, String event){
        fromState = from;
        toState = to;
        eventName = event;
    }

    //Add a listener to this Transition
    public void addListener(TransitionListener tl){
        listener = tl;
    }


    //Remove a listener tfrom this Transition
    public void removeListener(TransitionListener tl){
        listener = null;
    }


    //Return the from-state of this transition
    public State fromState(){
        return fromState;
    }
    

    //Return the to-state of this transition
    public State toState(){
        return toState;
    }
    

    //Return the name of the event that causes this transition
    public String eventName(){
        return eventName;
    }
    

    //Return a string containing information about this transition 
    //in the form (without quotes, of course!):
    //"fromStateName(eventName)toStateName"
    public String toString(){
        String ev = "";
        if(eventName == null){
            ev = "";
        }
        else{
                ev = eventName;
        }
        
        return fromState.getName() + "(" + ev + ")" + toState.getName();
    }

    public void TransitionHasChanged(){

    }
}
