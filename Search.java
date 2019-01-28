/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/
//comment
import java.io.*;
import java.util.*;
import java.text.*;

public class Search {

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

	public static FitnessFunction problem;

	public static Chromo[] member;
	public static Chromo[] child;

	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;
	public static Chromo bestOverAllChromo;
	public static int bestOverAllR;
	public static int bestOverAllG;

	public static double sumRawFitness;
	public static double sumRawFitness2;	// sum of squares of fitness
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double stdevRawFitness;

	public static int G;
	public static int R;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

	private static double fitnessStats[][];  // 0=Avg, 1=Best

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/


/*******************************************************************************
*                             MEMBER METHODS                                   *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	public static void main(String[] args) throws java.io.IOException{

		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

	//  Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters parmValues = new Parameters(args[0]);

	//  Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		parmValues.outputParameters(summaryOutput);

	//  create data-only file
		String dataFileName = Parameters.expID + "_data.txt";
		FileWriter dataOutput = new FileWriter(dataFileName);

	//	Set up Fitness Statistics matrix
		fitnessStats = new double[2][Parameters.generations];
		for (int i=0; i<Parameters.generations; i++){
			fitnessStats[0][i] = 0;
			fitnessStats[1][i] = 0;
		}

	//	Problem Specific Setup - For new new fitness function problems, create
	//	the appropriate class file (extending FitnessFunction.java) and add
	//	an else_if block below to instantiate the problem.
 
		if (Parameters.problemType.equals("NM")){
			problem = new NumberMatch();
		}
		else if (Parameters.problemType.equals("OM")){
			problem = new OneMax();
		}
		else if (Parameters.problemType.equals("SCHED")){
			problem = new Scheduler();
		}
		else System.out.println("Invalid Problem Type");

		System.out.println(problem.name);

	//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		if (Parameters.problemType.equals("SCHED")){
			switch (Parameters.problemRep){
				case "timeslots":
					member = new ChromoTimeslots[Parameters.popSize];
					child = new ChromoTimeslots[Parameters.popSize];
					bestOfGenChromo = new ChromoTimeslots();
					bestOfRunChromo = new ChromoTimeslots();
					bestOverAllChromo = new ChromoTimeslots();
					break;
				case "gen":
					member = new ChromoGenerative[Parameters.popSize];
					child = new ChromoGenerative[Parameters.popSize];
					bestOfGenChromo = new ChromoGenerative();
					bestOfRunChromo = new ChromoGenerative();
					bestOverAllChromo = new ChromoGenerative();
					break;
				case "character":
					member = new ChromoCharacter[Parameters.popSize];
					child = new ChromoCharacter[Parameters.popSize];
					bestOfGenChromo = new ChromoCharacter();
					bestOfRunChromo = new ChromoCharacter();
					bestOverAllChromo = new ChromoCharacter();
					break;
				default:
					break;
			}
		}
		else{
			member = new ChromoBinary[Parameters.popSize];
			child = new ChromoBinary[Parameters.popSize];
			bestOfGenChromo = new ChromoBinary();
			bestOfRunChromo = new ChromoBinary();
			bestOverAllChromo = new ChromoBinary();
		}
		

		if (Parameters.minORmax.equals("max")){
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;

		//  Start program for multiple runs
		for (R = 1; R <= Parameters.numRuns; R++){

			bestOfRunChromo.rawFitness = defaultBest;
			System.out.println();

			//	Initialize First Generation
			for (int i=0; i<Parameters.popSize; i++){
				if ( Parameters.problemType.equals("SCHED") ){
					switch(Parameters.problemRep){
						case "timeslots":
							member[i] = new ChromoTimeslots();
							child[i] = new ChromoTimeslots();
							break;
						case "gen":
							member[i] = new ChromoGenerative();
							child[i] = new ChromoGenerative();
							break;
						case "character":
							member[i] = new ChromoCharacter();
							child[i] = new ChromoCharacter();
							break;
						default:
							System.out.println("Invalid problem representation " + Parameters.problemRep);
							break;
					}
					
				}
				else{
					member[i] = new ChromoBinary();
					child[i] = new ChromoBinary();
				}
			}

			//	Begin Each Run
			for (G=0; G<Parameters.generations; G++){

				sumProFitness = 0;
				sumSclFitness = 0;
				sumRawFitness = 0;
				sumRawFitness2 = 0;
				bestOfGenChromo.rawFitness = defaultBest;

				//	Test Fitness of Each Member
				for (int i=0; i<Parameters.popSize; i++){

					member[i].rawFitness = 0;
					member[i].sclFitness = 0;
					member[i].proFitness = 0;

					problem.doRawFitness(member[i]);

					sumRawFitness = sumRawFitness + member[i].rawFitness;
					sumRawFitness2 = sumRawFitness2 +
						member[i].rawFitness * member[i].rawFitness;

					if (Parameters.minORmax.equals("max")){
						if (member[i].rawFitness > bestOfGenChromo.rawFitness){
							switch(Parameters.problemRep){
								case "timeslots":
									Chromo.copyB2A( (ChromoTimeslots)bestOfGenChromo, (ChromoTimeslots)member[i] );
									break;
								case "gen":
									Chromo.copyB2A( (ChromoGenerative)bestOfGenChromo, (ChromoGenerative)member[i] );
									break;
								case "character":
									Chromo.copyB2A( (ChromoCharacter)bestOfGenChromo, (ChromoCharacter)member[i] );
									break;
								default:
									Chromo.copyB2A(bestOfGenChromo, member[i]);
							}
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness > bestOfRunChromo.rawFitness){
							switch(Parameters.problemRep){
								case "timeslots":
									Chromo.copyB2A( (ChromoTimeslots)bestOfRunChromo, (ChromoTimeslots)member[i] );
									break;
								case "gen":
									Chromo.copyB2A( (ChromoGenerative)bestOfRunChromo, (ChromoGenerative)member[i] );
									break;
								case "character":
									Chromo.copyB2A( (ChromoCharacter)bestOfRunChromo,(ChromoCharacter) member[i] );
									break;
								default:
									Chromo.copyB2A(bestOfRunChromo, member[i]);
							}
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness > bestOverAllChromo.rawFitness){
							switch(Parameters.problemRep){
								case "timeslots":
									Chromo.copyB2A( (ChromoTimeslots)bestOverAllChromo, (ChromoTimeslots)member[i] );
									break;
								case "gen":
									Chromo.copyB2A( (ChromoGenerative)bestOverAllChromo, (ChromoGenerative)member[i] );
									break;
								case "character":
									Chromo.copyB2A( (ChromoCharacter)bestOverAllChromo, (ChromoCharacter)member[i] );
									break;
								default:
									Chromo.copyB2A(bestOverAllChromo, member[i]);
							}
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					else {
						if (member[i].rawFitness < bestOfGenChromo.rawFitness){
							switch(Parameters.problemRep){
								case "timeslots":
									Chromo.copyB2A( (ChromoTimeslots)bestOfGenChromo, (ChromoTimeslots)member[i] );
									break;
								case "gen":
									Chromo.copyB2A( (ChromoGenerative)bestOfGenChromo, (ChromoGenerative)member[i] );
									break;
								case "character":
									Chromo.copyB2A( (ChromoCharacter)bestOfGenChromo, (ChromoCharacter)member[i] );
									break;
								default:
									Chromo.copyB2A(bestOfGenChromo, member[i]);
							}
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness < bestOfRunChromo.rawFitness){
							switch(Parameters.problemRep){
								case "timeslots":
									Chromo.copyB2A( (ChromoTimeslots)bestOfRunChromo, (ChromoTimeslots)member[i] );
									break;
								case "gen":
									Chromo.copyB2A( (ChromoGenerative)bestOfRunChromo, (ChromoGenerative)member[i] );
									break;
								case "character":
									Chromo.copyB2A( (ChromoCharacter)bestOfRunChromo,(ChromoCharacter) member[i] );
									break;
								default:
									Chromo.copyB2A(bestOfRunChromo, member[i]);
							}
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness < bestOverAllChromo.rawFitness){
							switch(Parameters.problemRep){
								case "timeslots":
									Chromo.copyB2A( (ChromoTimeslots)bestOverAllChromo, (ChromoTimeslots)member[i] );
									break;
								case "gen":
									Chromo.copyB2A( (ChromoGenerative)bestOverAllChromo, (ChromoGenerative)member[i] );
									break;
								case "character":
									Chromo.copyB2A( (ChromoCharacter)bestOverAllChromo, (ChromoCharacter)member[i] );
									break;
								default:
									Chromo.copyB2A(bestOverAllChromo, member[i]);
							}
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
				}

				// Accumulate fitness statistics
				fitnessStats[0][G] += sumRawFitness / Parameters.popSize;
				fitnessStats[1][G] += bestOfGenChromo.rawFitness;

				averageRawFitness = sumRawFitness / Parameters.popSize;
				stdevRawFitness = Math.sqrt(
							Math.abs(sumRawFitness2 - 
							sumRawFitness*sumRawFitness/Parameters.popSize)
							/
							(Parameters.popSize-1)
							);

				// Output generation statistics to screen
				System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromo.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);

				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				dataOutput.write(R + ", ");
				summaryOutput.write(" G ");
				Hwrite.right(G, 3, summaryOutput);
				dataOutput.write(G + ", ");
				Hwrite.right((int)bestOfGenChromo.rawFitness, 7, summaryOutput);
				dataOutput.write( (int)bestOfGenChromo.rawFitness + ",");
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				dataOutput.write(stdevRawFitness + " ");
				summaryOutput.write("\n");
				dataOutput.write("\r\n");


		// *********************************************************************
		// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
		// *********************************************************************

				switch(Parameters.scaleType){

				case 0:     // No change to raw fitness
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = member[i].rawFitness + .000001;
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 1:     // Fitness not scaled.  Only inverted.
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = 1/(member[i].rawFitness + .000001);
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 2:     // Fitness scaled by Rank (Maximizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=Parameters.popSize-1; i>0; i--){
						for (int j=0; j<i; j++){
							if (memberFitness[j] > memberFitness[j+1]){
								TmemberIndex = memberIndex[j];
								TmemberFitness = memberFitness[j];
								memberIndex[j] = memberIndex[j+1];
								memberFitness[j] = memberFitness[j+1];
								memberIndex[j+1] = TmemberIndex;
								memberFitness[j+1] = TmemberFitness;
							}
						}
					}
					//  Copy ordered array to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				case 3:     // Fitness scaled by Rank (minimizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=1; i<Parameters.popSize; i++){
						for (int j=(Parameters.popSize - 1); j>=i; j--){
							if (memberFitness[j-i] < memberFitness[j]){
								TmemberIndex = memberIndex[j-1];
								TmemberFitness = memberFitness[j-1];
								memberIndex[j-1] = memberIndex[j];
								memberFitness[j-1] = memberFitness[j];
								memberIndex[j] = TmemberIndex;
								memberFitness[j] = TmemberFitness;
							}
						}
					}
					//  Copy array order to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				default:
					System.out.println("ERROR - No scaling method selected");
				}


		// *********************************************************************
		// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
		// *********************************************************************

				for (int i=0; i<Parameters.popSize; i++){
					member[i].proFitness = member[i].sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + member[i].proFitness;
				}

		// *********************************************************************
		// ************ CROSSOVER AND CREATE NEXT GENERATION *******************
		// *********************************************************************

				int parent1 = -1;
				int parent2 = -1;

				//  Assumes always two offspring per mating
				for (int i=0; i<Parameters.popSize; i=i+2){

					//	Select Two Parents
					if (Parameters.problemType.equals("SCHED")){
						switch(Parameters.problemRep){
							case "timeslots":
								parent1 = ChromoTimeslots.selectParent();
								parent2 = parent1;
								while (parent2 == parent1){
									parent2 = ChromoTimeslots.selectParent();
								}
								break;
							case "gen":
								parent1 = ChromoGenerative.selectParent();
								parent2 = parent1;
								while (parent2 == parent1){
									parent2 = ChromoGenerative.selectParent();
								}
								break;
							case "character":
								parent1 = ChromoCharacter.selectParent();
								parent2 = parent1;
								while (parent2 == parent1){
									parent2 = ChromoCharacter.selectParent();
								}
								break;
							default:
								System.out.println("ERROR: invalid problem type " + Parameters.problemType);
						}
						
					}
					else{
						parent1 = ChromoBinary.selectParent();
						parent2 = parent1;
						while (parent2 == parent1){
							parent2 = ChromoBinary.selectParent();
						}
					}
					

					//	Crossover Two Parents to Create Two Children
					randnum = r.nextDouble();
					if (Parameters.problemType.equals("SCHED")){
						switch (Parameters.problemRep){
							case "timeslots":
								if (randnum < Parameters.xoverRate){
									ChromoTimeslots.mateParents(parent1, parent2, (ChromoTimeslots)member[parent1], (ChromoTimeslots)member[parent2], (ChromoTimeslots)child[i], (ChromoTimeslots)child[i+1]);
								}
								else {
									ChromoTimeslots.mateParents(parent1, (ChromoTimeslots)member[parent1], (ChromoTimeslots)child[i]);
									ChromoTimeslots.mateParents(parent2, (ChromoTimeslots)member[parent2], (ChromoTimeslots)child[i+1]);
								}
								break;
							case "gen":
								if (randnum < Parameters.xoverRate){
									ChromoGenerative.mateParents(parent1, parent2, (ChromoGenerative)member[parent1], (ChromoGenerative)member[parent2], (ChromoGenerative)child[i], (ChromoGenerative)child[i+1]);
								}
								else {
									ChromoGenerative.mateParents(parent1, (ChromoGenerative)member[parent1], (ChromoGenerative)child[i]);
									ChromoGenerative.mateParents(parent2, (ChromoGenerative)member[parent2], (ChromoGenerative)child[i+1]);
								}
								break;
							case "character":
								if (randnum < Parameters.xoverRate){
									ChromoCharacter.mateParents(parent1, parent2, (ChromoCharacter)member[parent1], (ChromoCharacter)member[parent2], (ChromoCharacter)child[i], (ChromoCharacter)child[i+1]);
								}
								else {
									ChromoCharacter.mateParents(parent1, (ChromoCharacter)member[parent1], (ChromoCharacter)child[i]);
									ChromoCharacter.mateParents(parent2, (ChromoCharacter)member[parent2], (ChromoCharacter)child[i+1]);
								}
								break;
							default:
								break;
						}
					}
					else{
						if (randnum < Parameters.xoverRate){
							ChromoBinary.mateParents(parent1, parent2, (ChromoBinary)member[parent1], (ChromoBinary)member[parent2], (ChromoBinary)child[i], (ChromoBinary)(ChromoBinary)child[i+1]);
						}
						else {
							ChromoBinary.mateParents(parent1, (ChromoBinary)member[parent1], (ChromoBinary)child[i]);
							ChromoBinary.mateParents(parent2, (ChromoBinary)member[parent2], (ChromoBinary)child[i+1]);
						}
					}
					
				} // End Crossover

				//	Mutate Children
				for (int i=0; i<Parameters.popSize; i++){
					child[i].doMutation();
				}

				//	Swap Children with Last Generation
				for (int i=0; i<Parameters.popSize; i++){
					Chromo.copyB2A(member[i], child[i]);
				}

			} //  Repeat the above loop for each generation

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			problem.doPrintGenes(bestOfRunChromo, summaryOutput);

			System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromo.rawFitness);

		} //End of a Run

		Hwrite.left("B", 8, summaryOutput);

		problem.doPrintGenes(bestOverAllChromo, summaryOutput);

		//	Output Fitness Statistics matrix
		summaryOutput.write("Gen                 AvgFit              BestFit \n");
		for (int i=0; i<Parameters.generations; i++){
			Hwrite.left(i, 15, summaryOutput);
			Hwrite.left(fitnessStats[0][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[1][i]/Parameters.numRuns, 20, 2, summaryOutput);
			summaryOutput.write("\n");
		}

		summaryOutput.write("\n");
		summaryOutput.close();
		dataOutput.close();

		System.out.println();
		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance(); 
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);
		
	} // End of Main Class

}   // End of Search.Java ******************************************************

