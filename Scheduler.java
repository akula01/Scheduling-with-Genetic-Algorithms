/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Scheduler extends FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/


/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/


/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public Scheduler(){
		name = "Scheduling Problem";
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){
        InputReader reader = new InputReader(Parameters.dataInputFileName);
        ArrayList<ArrayList<Integer>> lists = reader.getLists();
        String rep = Parameters.problemRep;
        
        double fitness = 0;
        int index = 0;
        
		switch(rep){
			case "timeslots":
				for(ArrayList<Integer> list : lists) {
		        	fitness += calculateFitness(index, list, ((ChromoTimeslots)X).chromo);
		        	index += 1;
		        }
		        X.rawFitness = fitness;
				break;
			case "gen":
				for(ArrayList<Integer> list : lists) {
		        	fitness += calculateFitness(index, list, ((ChromoGenerative)X).chromo);
		        	index += 1;
		        }
		        X.rawFitness = fitness;
				break;
			case "character":
				for(ArrayList<Integer> list : lists) {
		        	fitness += calculateFitness(index, list, ((ChromoCharacter)X).chromo);
		        	index += 1;
		        }
		        X.rawFitness = fitness;
				break;
			default:
				break;
		}
	}
    
	// Fitness calculation method for representations Generative and Character
    private int calculateFitness(int monitorID, ArrayList<Integer> input, char[] chromo) {
		int monitor_fitness = 0;
    	char monitorCharID = (char)(monitorID + 'A');
    	int num_slots_assigned = 0;
		for(int i=0; i<chromo.length; i++) 
		{
			if(chromo[i] == monitorCharID)
			{
				int slot_number = i;
				num_slots_assigned += 1;
				int monitor_priority = input.get(slot_number);
				switch(monitor_priority) {
	    		case 0:
	    			monitor_fitness -= 1000;
	    			break;
	    		case 1:
	    			monitor_fitness += 500;
	    			break;
	    		case 2:
	    			monitor_fitness += 200;
	    			break;
	    		case 3:
	    			monitor_fitness += 100;
	    			break;
	    		case 4:
	    			monitor_fitness += 50;
	    			break;
	    		default:
	    			break;
				}
			}
		}
		if(num_slots_assigned > 7) {
			int excess = num_slots_assigned - 7;
			monitor_fitness -= excess * 100;
		}
		int count = 0;
		for(int i=0; i<chromo.length; i++) {
			if(chromo[i] == '*') {
				count += 1;
			}
		}
		monitor_fitness -= count * 20;
		return monitor_fitness;
	}
	
 // Fitness calculation method for representation TimeSlots
	private int calculateFitness(int monitorID, ArrayList<Integer> input, int[] chromo) {
		int monitor_fitness = 0;
		int num_slots_per_monitor = 5; 
		int startIndex = monitorID * num_slots_per_monitor;
		int endIndex = (monitorID + 1) * num_slots_per_monitor;
	    for(int i=startIndex; i<endIndex; i++) {
	    	int slot_number = chromo[i];
	    	int monitor_priority = input.get(slot_number);
	    	switch(monitor_priority) {
	    		case 0:
	    			monitor_fitness -= 1000;
	    			break;
	    		case 1:
	    			monitor_fitness += 500;
	    			break;
	    		case 2:
	    			monitor_fitness += 200;
	    			break;
	    		case 3:
	    			monitor_fitness += 100;
	    			break;
	    		case 4:
	    			monitor_fitness += 50;
	    			break;
	    		default:
	    			break;
	    	}
	    }
		return monitor_fitness;
	}
	
    private int calculateFitness(int monitorIndex, ArrayList<Integer> input, String chromo) {
		return 0;
	}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{

		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getGeneAlpha(i),11,output);
		}
		output.write("   RawFitness");
		output.write("\n        ");
		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getPosIntGeneValue(i),11,output);
		}
		Hwrite.right((int) X.rawFitness,13,output);
		output.write("\n\n");
		return;
	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

}   // End of OneMax.java ******************************************************

