/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;
import java.util.Random;

public class Schedule
{
/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public int[] schedule;
	public int   slots_per_monitor;
	public double rawFitness;
	public double sclFitness;
	public double proFitness;

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	private static double randnum;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public Schedule(){

		//  Set schedule to a sequence of slot assignments (slot --> monitor)
		schedule = new int[Parameters.slots];
		slots_per_monitor = Parameters.slots/Parameters.monitors;
		for (int i=0; i<Parameters.monitors; i++){
			for (int j=0; j<slots_per_monitor; j++){
				schedule[i+j] = i;
			}
		}
		Collections.shuffle(Arrays.asList(schedule)); 
		
		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized
	}


/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

	//  Mutate the schedule Based on Mutation Type *****************************

	public void doMutation(){

		switch (Parameters.mutationType){

		case 1:     //  random shuffle
			int len_schedule = this.schedule.length;
			int random_index_1 = new Random().nextInt(len_schedule);
			int random_index_2 = new Random().nextInt(len_schedule);
		    while(random_index_1 == random_index_2) {
		    	random_index_1 = new Random().nextInt(len_schedule);
				random_index_2 = new Random().nextInt(len_schedule);
			}
			int temp = this.schedule[random_index_1];
	        this.schedule[random_index_1] = this.schedule[random_index_2];
	        this.schedule[random_index_2] = temp;
			break;

		default:
			System.out.println("ERROR - No mutation method selected");
		}
	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	//  Select a parent for crossover ******************************************

	public static int selectParent(){

		double rWheel = 0;
		int j = 0;
		int k = 0;

		switch (Parameters.selectType){

		case 1:     // Proportional Selection
			randnum = Search.r.nextDouble();
			for (j=0; j<Parameters.popSize; j++){
				rWheel = rWheel + Search.member[j].proFitness;
				if (randnum < rWheel) return(j);
			}
			break;

		case 3:     // Random Selection
			randnum = Search.r.nextDouble();
			j = (int) (randnum * Parameters.popSize);
			return(j);

		case 2:     //  Tournament Selection

		default:
			System.out.println("ERROR - No selection method selected");
		}
	return(-1);
	}

	//  Produce a new child from two parents  **********************************

	public static void mateParents(int pnum1, int pnum2, Schedule parent1, Schedule parent2, Schedule child1, Schedule child2){

		switch (Parameters.xoverType){

		case 1:     //  Single Point Crossover
			//TODO
			break;

		case 2:     //  Two Point Crossover

		case 3:     //  Uniform Crossover

		default:
			System.out.println("ERROR - Bad crossover method selected");
		}

		//  Set fitness values back to zero
		child1.rawFitness = -1;   //  Fitness not yet evaluated
		child1.sclFitness = -1;   //  Fitness not yet scaled
		child1.proFitness = -1;   //  Fitness not yet proportionalized
		child2.rawFitness = -1;   //  Fitness not yet evaluated
		child2.sclFitness = -1;   //  Fitness not yet scaled
		child2.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Produce a new child from a single parent  ******************************

	public static void mateParents(int pnum, Schedule parent, Schedule child){

		//  Create child chromosome from parental material
		child.schedule = parent.schedule;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Copy one chromosome to another  ***************************************

	public static void copyB2A (Schedule targetA, Schedule sourceB){

		targetA.schedule = sourceB.schedule;

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}

}   // End of Chromo.java ******************************************************
