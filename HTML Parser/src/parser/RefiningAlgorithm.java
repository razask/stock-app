package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;

public class RefiningAlgorithm {

	static HashMap<String, Integer> counter(String body) throws Exception {
		String[] lines = body.split("\n");
		HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
		for (int i = 0; i < lines.length; i++) {
			String[] words = lines[i].split(" ");
			for (String w : Arrays.asList(words)) {
				Integer num = frequencies.get(w);
				if (num != null) {
					frequencies.put(w, num + 1);
				} else {
					frequencies.put(w, 1);
				}
			}
		}
		HashMap<String, Integer> filteredList = filter(frequencies);
		filteredList = positiveFilter(frequencies);
		filteredList = negativeFilter(frequencies);
		System.out.println(filteredList);
		return filteredList;
	}

	private static HashMap<String, Integer> filter(HashMap<String, Integer> input) throws Exception{
		FileReader list = new FileReader("./src/resources/Exclusion List");
		BufferedReader bufRead = new BufferedReader(list);
		String myLine = null;
		
		while ( (myLine = bufRead.readLine()) != null)
		{    
		    String[] parselist = myLine.split("\n");
			for (int i = 0; i < parselist.length; i++) {
				String word = parselist[i];
				if (input.containsKey(word)){
					input.remove(word);
				}
			}
		}
		return input;
	}
	
	private static HashMap<String, Integer> positiveFilter(
			HashMap<String, Integer> input) throws Exception {
		FileReader list = new FileReader("./src/resources/Positive");
		BufferedReader bufRead = new BufferedReader(list);
		String myLine = null;
		int result = 0;
		while ((myLine = bufRead.readLine()) != null) {
			String[] parselist = myLine.split("\n");
			for (int i = 0; i < parselist.length; i++) {
				String word = parselist[i];
				if (input.containsKey(word)){
					input.remove(word);
				}
			}
		}
		return input;
	}
	
	private static HashMap<String, Integer> negativeFilter(
			HashMap<String, Integer> input) throws Exception {
		FileReader list = new FileReader("./src/resources/Negative");
		BufferedReader bufRead = new BufferedReader(list);
		String myLine = null;
		int result = 0;
		while ((myLine = bufRead.readLine()) != null) {
			String[] parselist = myLine.split("\n");
			for (int i = 0; i < parselist.length; i++) {
				String word = parselist[i];
				if (input.containsKey(word)){
					input.remove(word);
				}
			}
		}
		return input;
	}
}
