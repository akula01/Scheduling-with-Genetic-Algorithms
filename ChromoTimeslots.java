/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class ChromoTimeslots extends Chromo
{
/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public int[] chromo;

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	private static double randnum;
	private static int randint;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public ChromoTimeslots(){

		// Set gene values to a randum permutation of {1-n} where n is the 
		// total number of timeslots available
		int genomeLength = Parameters.numGenes * Parameters.geneSize;
		chromo = new int[genomeLength];
		for (int i=0; i<genomeLength; i++){
			chromo[i] = i; 
		}
		this.shuffle();

		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized
	}


/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

	// Shuffle a chromosome using Fisher-Yates, used in generation of initial population
	private void shuffle()
	{
	    int index;
	    for (int i = this.chromo.length - 1; i > 0; i--)
	    {
	        index = Search.r.nextInt(i + 1);
	        if (index != i)
	        {
	            int tmp = this.chromo[i];
	            this.chromo[i] = this.chromo[index];
	            this.chromo[index] = tmp;
	        }
	    }
	}

	@Override
	public String toString(){
		String str = new String();
		System.out.print(" " + this.chromo.length + " ");
		for (int i = 0; i>this.chromo.length; i++){
			str = str + this.getGeneAlpha(i);
		}
		return str;
	}

	//  Get Alpha Represenation of a Gene **************************************
	@Override
	public String getGeneAlpha(int geneID){
		int start = geneID * Parameters.geneSize;
		int end = (geneID+1) * Parameters.geneSize;
		String[] gene = new String[end-start];
		for (int i = start; i < end; i++){
			gene[i-start] = Integer.toString(this.chromo[i]);
		}
		String geneAlpha = String.join(",",gene);
		return (geneAlpha);
	}

	//  Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****
	@Override
	public int getIntGeneValue(int geneID){
		/*int start = geneID * Parameters.geneSize;
		int end = (geneID+1) * Parameters.geneSize;
		int[] gene = new int[end-start];
		for (int i = start; i < end; i++){
			gene[i-start] = this.chromo[i];
		}
		return (gene);*/
		return this.chromo[geneID];
	}

	//  Get Integer Value of a Gene (Positive only) ****************************
	@Override
	public int getPosIntGeneValue(int geneID){
		return this.chromo[geneID];
	}

	//  Mutate a Chromosome Based on Mutation Type *****************************
	@Override
	public void doMutation(){

		int x;

		switch (Parameters.mutationType){

		case 1:     //  Swap
			for (int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
				x = this.chromo[j];
				randnum = Search.r.nextDouble();
				if (randnum < Parameters.mutationRate){
					randint = Search.r.nextInt(Parameters.geneSize * Parameters.numGenes);
					this.chromo[j] = this.chromo[randint];
					this.chromo[randint] = x;
				}
			}
			break;

		case 2:
			//Put alternative mutation operators here, add documentation to Parameters.java file
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
		double l = 0.8;	//likelihood of return better fit indv in tournament selection

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
			randnum = Search.r.nextDouble();
			j = (int) (randnum * Parameters.popSize);
			randnum = Search.r.nextDouble();
			k = (int) (randnum * Parameters.popSize);
			randnum = Search.r.nextDouble();
			if (randnum < l){	//return the better fit indv
				return (Search.member[j].proFitness > Search.member[k].proFitness)? j : k;
			}
			else{				//return the lesser fit indv
				return (Search.member[j].proFitness < Search.member[k].proFitness)? j : k;
			}

		default:
			System.out.println("ERROR - No selection method selected");
		}
	return(-1);
	}

	//  Produce a new child from two parents  **********************************
	public static void mateParents(int pnum1, int pnum2, ChromoTimeslots parent1, ChromoTimeslots parent2, ChromoTimeslots child1, ChromoTimeslots child2){

		int xoverPoint1;
		int xoverPoint2;
		//int[] chromoTemp = Arrays.copyOf(parent1, parent1.length);
		int temp;

		switch (Parameters.xoverType){

		case 1:     //  Single Point Crossover

			//  Select crossover point
			//xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));

			//  Create child chromosome from parental material
			//child1.chromo = parent1.chromo.substring(0,xoverPoint1) + parent2.chromo.substring(xoverPoint1);
			//child2.chromo = parent2.chromo.substring(0,xoverPoint1) + parent1.chromo.substring(xoverPoint1);
			break;

		case 2:     //  Two Point Crossover
			//System.out.println("ERROR - Unimplemented crossover");
			break;

		case 3:     //  Uniform Crossover
			//System.out.println("ERROR - Unimplemented crossover");
			break;

		case 4:     //  Permutation xover ops

			//  Select crossover points
			xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));
			xoverPoint2 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));

			//System.out.println("parent1: " + Arrays.toString(parent1.chromo));
			//System.out.println("parent2: " + Arrays.toString(parent2.chromo));

			//  Two-point crossover
			for(int i=0; i<(Parameters.numGenes * Parameters.geneSize); i++){
				if(i>=xoverPoint1 && i<xoverPoint2){
					child1.chromo[i] = parent2.chromo[i];
					child2.chromo[i] = parent1.chromo[i];
				}
				else{
					child1.chromo[i] = parent1.chromo[i];
					child2.chromo[i] = parent2.chromo[i];
				}
			}

			//  Fix the doubles
			for(int i=xoverPoint1; i<xoverPoint2; i++){
				//randnum = Search.r.nextDouble();
				//if(randnum < Parameters.xoverRate){//put the following for loop in this block}
				for(int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
					if( (j<xoverPoint1 || j>=xoverPoint2) && (child1.chromo[i] == child1.chromo[j]) ){
						for(int k=0; k<(Parameters.geneSize * Parameters.numGenes); k++){
							if( (k<xoverPoint1 || k>=xoverPoint2) && (child2.chromo[i] == child2.chromo[k]) ){
								temp = child2.chromo[k];
								child2.chromo[k] = child1.chromo[j];
								child1.chromo[j] = temp;
							}
						}
					}
				}
			}
			//System.out.println("child1: " + Arrays.toString(child1.chromo));
			//System.out.println("child2: " + Arrays.toString(child2.chromo));
			break;

		default:
			System.out.println("ERROR - Bad crossover method selected: " + Parameters.xoverType);
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
	public static void mateParents(int pnum, ChromoTimeslots parent, ChromoTimeslots child){
		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

}   // End of Chromo.java ******************************************************
