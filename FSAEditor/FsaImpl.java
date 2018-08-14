import java.util.*;
import java.io.*;

public class FsaImpl implements Fsa, FsaSim{

	private Set<State> states;
	private Set<Transition> transitions;
    private Set<State> initialStates;
    private Set<State> currentStates;
    private Set<State> finalStates;
    private int resetCounter;
    private FsaListener editor;

	public FsaImpl(){
		states = new LinkedHashSet<State>();
		transitions = new LinkedHashSet<Transition>();
        initialStates = new LinkedHashSet<State>();
        currentStates = new LinkedHashSet<State>();
        finalStates = new LinkedHashSet<State>();
        resetCounter = 0;
	}

	//Create a new State and add it to this FSA
    //Returns the new state
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing state
	public State newState(String name, int x, int y)
      throws IllegalArgumentException{

        //name checking (first char must be non digit/name cannot be null/cant duplicate state)
      	if(name == null || !name.matches(".*[a-zA-Z]+.*") || findState(name) != null || (Character.isDigit(name.charAt(0)))){
      		throw new IllegalArgumentException("Duplicated or Illegal state name");
      	}

    	State s = new StateClass(name, x, y);
      	states.add(s);
        editor.statesChanged();

      	return s;
    }

    //Remove a state from the FSA
    //If the state does not exist, returns without error
    public void removeState(State s){
        StateClass tempState;
        Transition tempTrans;
        Iterator stateItr = states.iterator();
        Iterator transItr = transitions.iterator();

        //iterate through states set to see if state exists
        //if found, iterate transitions and remove all transitions from/to

        while(stateItr.hasNext()){
            tempState = (StateClass)stateItr.next();

            if(tempState == s){
                while(transItr.hasNext()){
                    tempTrans = (Transition)transItr.next();

                    if(tempTrans.fromState() == tempState){
                        tempState.removeTransitionFrom(tempTrans);

                        StateClass tempTo = (StateClass)tempTrans.toState();
                        tempTo.removeTransitionTo(tempTrans);

                        transItr.remove();
                    }
                    else if(tempTrans.toState() == tempState){
                        tempState.removeTransitionTo(tempTrans);

                        StateClass tempFrom = (StateClass)tempTrans.fromState();
                        tempFrom.removeTransitionFrom(tempTrans);

                        transItr.remove();
                    }
                }

                stateItr.remove();
                currentStates.remove(s);
                editor.statesChanged();
                editor.transitionsChanged();
                return;
            }
        }
    }

    //Find and return the State with the given name
    //If no state exists with given name, return NULL
    public State findState(String stateName){
    	State s;
    	Iterator itr = states.iterator();

    	while(itr.hasNext()){
    		s = (State)itr.next();
    		if(stateName.equals(s.getName())){
    			return s;
    		}
    	}
    
    	return null;
    }

    //Return a set containing all the states in this Fsa
    public Set<State> getStates(){
    	return new LinkedHashSet<State>(states);
    }

    //Create a new Transition and add it to this FSA
    //Returns the new transition.
    //eventName==null specifies an epsilon-transition
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist or
    //  The eventName is invalid or
    //  An identical transition already exists
    public Transition newTransition(State fromState, State toState,
      String eventName) 
      throws IllegalArgumentException{

        if(fromState == null || toState == null){
            throw new IllegalArgumentException("null input");
        }

        //if eventName contains digits or is invalid
        if(eventName != null && eventName.matches(".*\\d+.*")){
            throw new IllegalArgumentException("Transition eventName is invalid");
        }

      	if(!states.contains(fromState)){
            throw new IllegalArgumentException("fromState doesn't exist");
        }
        if(!states.contains(toState)){
            throw new IllegalArgumentException("toState doesn't exist");
        }

        Transition t = new TransitionClass(fromState, toState, eventName);

        Set<Transition> foundTrans = findTransition(fromState, toState);
        Iterator<Transition> itr = foundTrans.iterator();
        Transition searchTrans;

        while(itr.hasNext()){
            searchTrans = itr.next();
            if(eventName == searchTrans.eventName() || searchTrans.eventName().equals(eventName)){
                throw new IllegalArgumentException("Duplicate transition");
            }
        }
        
        //setting from/to transitions for each state
        StateClass from = (StateClass)findState(fromState.getName());
        StateClass to = (StateClass)findState(toState.getName());
        to.addTransitionTo(t);
        from.addTransitionFrom(t);
      	
        transitions.add(t);
        editor.transitionsChanged();

      	return t;

    }

    //Remove a transition from the FSA
    //If the transition does not exist, returns without error
    public void removeTransition(Transition t){
        StateClass fromState = (StateClass)t.fromState();
        StateClass toState = (StateClass)t.toState();
        fromState.removeTransitionFrom(t);
        toState.removeTransitionTo(t);

        editor.transitionsChanged();
        transitions.remove(t);
    }

    //Find all the transitions between two states
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist
    public Set<Transition> findTransition(State fromState, State toState)
     throws IllegalArgumentException{

        if(!states.contains(fromState)){
            throw new IllegalArgumentException("fromState doesn't exist");
        }
        if(!states.contains(toState)){
            throw new IllegalArgumentException("toState doesn't exist");
        }

    	Set<Transition> foundTransitions = new LinkedHashSet<Transition>();
        Transition t;
        Iterator itr = transitions.iterator();

        while(itr.hasNext()){
            t = (Transition)itr.next();
            if(t.fromState() == fromState && t.toState() == toState){
                foundTransitions.add(t);
            }
        }

    	return foundTransitions;
    }

    public Set<Transition> getTransitions(){
        return new LinkedHashSet<Transition>(transitions);
    }

    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates(){
        initialStates.clear();

        State s;

        Iterator itr = states.iterator();

        while(itr.hasNext()){
            s = (State)itr.next();
            if(s.isInitial()){
                initialStates.add(s);
            }
        }

        return new LinkedHashSet<State>(initialStates);

    }

    //Return the set of final states of this Fsa
    public Set<State> getFinalStates(){
        finalStates.clear();

        State s;

        Iterator itr = states.iterator();

        while(itr.hasNext()){
            s = (State)itr.next();
            if(s.isFinal()){
                finalStates.add(s);
            }
        }
    	return new LinkedHashSet<State>(finalStates);
    }

    //Returns a set containing all the current states of this FSA
    public Set<State> getCurrentStates(){
    	currentStates.clear();

    	State s;

    	Iterator itr = states.iterator();

    	while(itr.hasNext()){
    		s = (State)itr.next();
    		if(s.isCurrent()){
    			currentStates.add(s);
    		}
    	}

    	return new LinkedHashSet<State>(currentStates);
    }

    public void printCurrentStates(){
        getCurrentStates();
        Iterator itr = currentStates.iterator();
        StateClass s;

        while(itr.hasNext()){
            s = (StateClass)itr.next();
            System.out.println(s.getName());
        }
        System.out.println("");
    }

    //Return a string describing this Fsa
    //Returns a string that contains (in this order):
    //for each state in the FSA, a line (terminated by \n) containing
    //  STATE followed the toString result for that state
    //for each transition in the FSA, a line (terminated by \n) containing
    //  TRANSITION followed the toString result for that transition
    //for each initial state in the FSA, a line (terminated by \n) containing
    //  INITIAL followed the name of the state
    //for each final state in the FSA, a line (terminated by \n) containing
    //  FINAL followed the name of the state
    public String toString(){
    	String result = "";

    	State s;
    	Transition t;

    	Iterator itr = states.iterator();

    	while(itr.hasNext()){
    		s = (State)itr.next();
    		result += "STATE " + s.toString() + "\n";
    	}

    	itr = transitions.iterator();

    	while(itr.hasNext()){
    		t = (Transition)itr.next();
    		result += "TRANSITION " + t.toString() + "\n";
    	}

    	getInitialStates();
        itr = initialStates.iterator();

    	while(itr.hasNext()){
            s = (State)itr.next();
    		result += "INITIAL " + s.getName() + "\n";
    	}

        getFinalStates();
    	itr = finalStates.iterator();

    	while(itr.hasNext()){
            s = (State)itr.next();
    		result += "FINAL " + s.getName() + "\n";
    	}

    	return result;
    }

    //Add a listener to this FSA
    public void addListener(FsaListener fl){
        editor = fl;
    }

    //Remove a listener from this FSA
    public void removeListener(FsaListener fl){
        editor = null;
    }

    //Reset the simulation to its initial state(s)
    public void reset(){
        resetCounter++;
        getInitialStates();
        currentStates = initialStates;

        Iterator itr = states.iterator();
        StateClass s;

        //set only initial states as current
        while(itr.hasNext()){
            s = (StateClass)itr.next();
            if(s.isInitial()){
                s.setCurrent(true);
            }
            if(s.isCurrent() && !s.isInitial()){
                s.setCurrent(false);
            }
        }

        editor.statesChanged();
        editor.transitionsChanged();

    }

    public void step(String event){
        if(event == null){
            return;
        }

        if(resetCounter == 0){
            reset();
        }

        getCurrentStates();

        currentStates = eClose();

        for( State s: currentStates ){
            for( Transition t : s.transitionsFrom() ){
                if(t.eventName() != null && t.eventName().equals(event)){
                    ((StateClass)t.toState()).setCurrent(true);
                    ((StateClass)t.fromState()).setCurrent(false);
                }
            }
        }

        editor.otherChanged();
    }

    //function that finds all states reachable by epsilon transition from currentStates
    public Set<State> eClose(){
        Set<State> result = new LinkedHashSet<State>();

        Queue<State> queue = new LinkedList<>();
        Set<State> checked = new LinkedHashSet<>();

        getCurrentStates();

        Iterator it = currentStates.iterator();
        State curr;

        while(it.hasNext()){
            curr = (State)it.next();
            queue.add(curr);
        }

        //DFS to add all states reachable by epsilon transition
        while(!queue.isEmpty()){
            State s = queue.remove();
            if(!checked.contains(s)){
                checked.add(s);
                result.add(s);
                
                it = s.transitionsFrom().iterator();
                Transition t;


                while(it.hasNext()){
                    t = (Transition)it.next();
                    //System.out.println(t.eventName());
                    if(t.eventName() == null){
                        //System.out.println("EPSILON from " + t.fromState() + ", to " + t.toState());
                        ((StateClass)t.toState()).setCurrent(true);
                        ((StateClass)t.fromState()).setCurrent(false);
                        result.add(t.toState());
                        queue.add(t.toState());
                    }
                }
            }
        }

        return result;
    }


    //Returns true if the simulation has recognised
    //the sequence of events it has been given
    public boolean isRecognised(){
    	currentStates = eClose();
        getFinalStates();

        StateClass curr;
        Iterator currItr = currentStates.iterator();


        currItr = currentStates.iterator();

        while(currItr.hasNext()){
            curr = (StateClass)currItr.next();
            if(curr.isFinal()){
                curr.setCurrent(true);
                return true;
            }
        }

    	return false;
    }

    public boolean initAndFinalSet(){
        getInitialStates();
        getFinalStates();

        if(initialStates.size() > 0 && finalStates.size() > 0){
            return true;
        }

        return false;
    }

    public void checkStateTransitions(State s){
        Set<Transition> trans = s.transitionsFrom();
        Iterator itr = trans.iterator();
        Transition t;
        System.out.println("Transitions from " + s.getName());

        while(itr.hasNext()){
            t = (Transition)itr.next();
            System.out.println(t.eventName());
        }
        System.out.println("");
    }
}