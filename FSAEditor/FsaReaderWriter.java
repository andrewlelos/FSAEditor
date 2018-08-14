import java.io.Reader;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedReader;
import java.util.*;

public class FsaReaderWriter implements FsaIo{

	public FsaReaderWriter(){
	}

	//This class handles reading and writing FSA representations as 
    //described in the practical specification

    //Read the description of a finite-state automaton from the 
    //Reader , r, and transfer it to Fsa, f.
    //If an error is detected, throw an exception that indicates the line
    //where the error was detected, and has a suitable text message
    public void read(Reader r, Fsa f)
        throws IOException, FsaFormatException{

            if(r == null || f == null){
                return;
            }

            BufferedReader input = new BufferedReader(r);
            String line = input.readLine();

            if(line == null){
                throw new IOException("Empty or corrupted input file");
            }

            //while not end of file
            while(line != null){
                line = line.trim(); //remove leading and trailing whitespace from line

                //loop over comment/whitespace lines
                while(line.equals("") || line.charAt(0) == '#'){
                    line = input.readLine();
                    line = line.trim();
                }

                //split string with whitespace as delim
                String[] splitLine = line.split("\\s+");

                if(splitLine[0].equals("state")){
                    if(splitLine.length < 4 || splitLine.length > 4){
                        throw new FsaFormatException(43, "Too many or not enough state arguments provided");
                    }

                    if(!splitLine[2].matches("^[0-9]+$") || !splitLine[3].matches("^[0-9]+$")){
                        throw new FsaFormatException(48, "xPos or yPos contains non digit characters");
                    }

                    f.newState(splitLine[1], Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));
                }

                else if(splitLine[0].equals("transition")){
                    if(splitLine.length < 4 || splitLine.length > 4){
                        throw new FsaFormatException(56, "Too many or not enough transition arguments provided");
                    }

                    State fromState = f.findState(splitLine[1]);
                    State toState = f.findState(splitLine[3]);

                    if(splitLine[2].equals("?")){
                        splitLine[2] = null;
                    }

                    f.newTransition(fromState, toState, splitLine[2]);
                }

                else if(splitLine[0].equals("initial")){
                    if(splitLine.length < 2 || splitLine.length > 2){
                        throw new FsaFormatException(56, "Too many or not enough initial state names provided");
                    }

                    State initial = f.findState(splitLine[1]);

                    if(initial == null){
                        throw new FsaFormatException(75, "No such state exists to set as initial");
                    }

                    initial.setInitial(true);
                    f.getInitialStates();
                }

                else if(splitLine[0].equals("final")){
                    if(splitLine.length < 2 || splitLine.length > 2){
                        throw new FsaFormatException(56, "Too many or not enough final state names provided");
                    }

                    State fin = f.findState(splitLine[1]);

                    if(fin == null){
                        throw new FsaFormatException(75, "No such state exists to set as final");
                    }

                    fin.setFinal(true);
                    f.getFinalStates();
                }

                else{
                    throw new FsaFormatException(102, "Invalid line");
                }

                line = input.readLine();
            }
    }
    
    
    //Write a representation of the Fsa, f, to the Writer, w.
    public void write(Writer w, Fsa f)
      throws IOException{

      	if(w == null || f == null){
            return;
        }

      	String result = "";

    	State s;
    	Transition t;

    	Set<State> states = f.getStates();
		Set<Transition> transitions = new LinkedHashSet<Transition>();
    	Set<State> initialStates = f.getInitialStates();
    	Set<State> finalStates = f.getFinalStates();

    	Iterator itr = states.iterator();

    	while(itr.hasNext()){
    		s = (State)itr.next();
            Set<Transition> transFrom = s.transitionsFrom();

            for(Transition from : transFrom){
                transitions.add(from);
            }

    		result += "state " + s.getName() + " " + s.getXpos() + " " + s.getYpos() + "\n";
    	}

    	itr = transitions.iterator();

    	while(itr.hasNext()){
    		t = (Transition)itr.next();
    		String ev = "";
        	if(t.eventName() == null){
            	ev = "?";
        	}
        	else{
        		ev = t.eventName();
        	}
    		result += "transition " + t.fromState().getName() + " " + ev + " " + t.toState().getName() + "\n";
    	}

        itr = initialStates.iterator();

    	while(itr.hasNext()){
            s = (State)itr.next();
    		result += "initial " + s.getName() + "\n";
    	}

    	itr = finalStates.iterator();

    	while(itr.hasNext()){
            s = (State)itr.next();
    		result += "final " + s.getName() + "\n";
    	}


      	w.write(result);
      	w.flush();
    }
}