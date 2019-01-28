import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class InputReader {
	
	private String fileName;
	
	InputReader(String fileName){
		this.fileName = fileName;
	}
	
	// reads input file line by line and creates as list of lists of integers
	public ArrayList<ArrayList<Integer>> getLists() {
		
		ArrayList<ArrayList<Integer>> listOLists = new ArrayList<ArrayList<Integer>>();
	    
		try {
	    	// FileReader reads text files
	        FileReader fileReader = new FileReader(this.fileName);

	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        
	        String line = null;
	        ArrayList<Integer> list = null;
	        while((line = bufferedReader.readLine()) != null) {
	            String[] splits  = line.split("\t");
	            if(splits.length == 1) {
	            	if(list != null) {
	            		listOLists.add(list);
	            	}
	            	list = new ArrayList<Integer>();
	            }
	            else {
	            	for(int i=0; i<splits.length; i++) {
	            		list.add(Integer.parseInt(splits[i]));
	            	}
	            }
	        }
	        listOLists.add(list);
	        
	        // Close the file after reading
	        bufferedReader.close();         
	    }
	    catch(FileNotFoundException ex) {
	        System.out.println("Unable to open file '" + Parameters.dataInputFileName + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println("Error reading file '" + Parameters.dataInputFileName + "'");                  
	    }
		
		return listOLists;
	}
}
