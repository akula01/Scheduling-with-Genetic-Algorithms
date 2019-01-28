/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class ChromoCharacter extends Chromo
{
/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public char[] chromo;

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	private static double randnum;
	private static int randint;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/
	
	// Creates a chromo consisting entirely of '******...*' initially
	public ChromoCharacter(){
		// Set gene values to a randum permutation of {1-n} where n is the 
		// total number of timeslots available
		int numPeople = 7;
		int numShifts = 5;
		chromo = new char[numPeople * numShifts];
		for (int i=0; i<numPeople; i++){
			for (int j=0; j<numShifts; j++){
				chromo[i+j] = (char)(i + 'A');
			}
		}
		this.shuffle();

		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized
	}


/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

	//  Get Alpha Represenation of a Gene **************************************
	private void shuffle(){
	    int index;
	    for (int i = this.chromo.length - 1; i > 0; i--)
	    {
	        index = Search.r.nextInt(i + 1);
	        if (index != i)
	        {
	            char tmp = this.chromo[i];
	            this.chromo[i] = this.chromo[index];
	            this.chromo[index] = tmp;
	        }
	    }
	}

	public String getGeneAlpha(int geneID){
		int start = geneID * Parameters.geneSize;
		int end = (geneID+1) * Parameters.geneSize;
		char[] geneAlpha = new char[end-start];
		for (int i = start; i < end; i++){
			geneAlpha[i-start] = this.chromo[i];
		}
		return new String(geneAlpha);
	}

	//  Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****
	//	value of an empty slot * is -1, A=0 B=1 C=2...

	public int getIntGeneValue(int geneID){
		return ( (int) this.chromo[geneID] - (int) 'A');
	}

	//  Get Integer Value of a Gene (Positive only) ****************************

	public int getPosIntGeneValue(int geneID){
		return this.getIntGeneValue(geneID);
	}

	//  Mutate a Chromosome Based on Mutation Type *****************************

	public void doMutation(){

		int x;

		switch (Parameters.mutationType){

		case 1:     //  changes to random letter A-G
			for (int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
				x = (int) this.chromo[j] - (int) 'A';
				randnum = Search.r.nextDouble();
				if (randnum < Parameters.mutationRate){
					randint = Search.r.nextInt(7);			//is it ok to hard-code this number or do we need another Parameter?
					char c = (char)(randint + (int) 'A');
					this.chromo[j] = c;
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

	public static void mateParents(int pnum1, int pnum2, ChromoCharacter parent1, ChromoCharacter parent2, ChromoCharacter child1, ChromoCharacter child2){

		int xoverPoint1;
		int xoverPoint2;
		char temp;

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
			//System.out.println("ERROR - Unimplemented crossover");

			//  Select crossover points
			xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));
			xoverPoint2 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));

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
			boolean swapped;
			for(int i=xoverPoint1; i<xoverPoint2; i++){
				swapped = false;
				//randnum = Search.r.nextDouble();
				//if(randnum < Parameters.xoverRate){//put the following for loop in this block}
				for(int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
					if( (j<xoverPoint1 || j>=xoverPoint2) && (child1.chromo[i] == child1.chromo[j]) && swapped==false){
						for(int k=0; k<(Parameters.geneSize * Parameters.numGenes); k++){
							if( (k<xoverPoint1 || k>=xoverPoint2) && (child2.chromo[i] == child2.chromo[k]) ){
								temp = child2.chromo[k];
								child2.chromo[k] = child1.chromo[j];
								child1.chromo[j] = temp;
								swapped = true;
							}
						}
					}
				}
			}
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

	public static void mateParents(int pnum, ChromoCharacter parent, ChromoCharacter child){
		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}


}   // End of Chromo.java ******************************************************
