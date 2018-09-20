import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Thoughts on what could be done in order to scale to a billion entries ----------

// Cache previous results by: 
// a) the user OR 
// b) location because users in a particular location will likely search for similar things OR
// c) world news.

// Shortcomings in my current approach:
// 1. I am creating a node with an array of 27 pointers (last one being for space). 
//    Most of the indexes have empty values thus wasting space.
// 2. I have increased the array length by 1 to support spaces. This is not an ideal solution if we had 
//    to extend it to all special characters. 

// These changes could somewhat help with the shortcoming mentioned above:
// Remove special characters from  the entries before storing them in memory. Do the same from 
// the prefix being searched for. 
// This could be extended to words i.e. remove words like a, an, the, of etc from entries as well as prefix. 
// Remove repetitive letters like 'z' from dizzy or 'e' from sleepy. This could get tricky if in fact there 
// was a word 'dizy' that meant something. 
// There would off course have to be a mapping between the abbreviated entry and the actual entry.

public class App {

	private static Trie trie = new Trie();

	// Functions like this should ideally go in a utils library
	private static List<String> safe(List<String> other) {
		return other == null ? Collections.emptyList() : other;
	}

	public static void main(String[] args) {

		// Read entries from file and populate the data structure
		long startTime = System.currentTimeMillis();
		String filename = "top1000.txt";

		try {
			readEntriesFromFileAndStore(filename);
		} catch (FileNotFoundException e1) {
			System.out.println("Can't find file " + filename);
			return;
		} catch (NullPointerException | IOException e1) {
			System.out.println("Unable to read file " + filename);
			return;
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;

		System.out.println("Time taken to insert: " + elapsedTime + " milliseconds.");
		System.out.println("\n---------------- ");

		// Lookup prefix and suggest entries for auto-complete based on weight
		String lookup = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			// Get prefix to auto-complete
			System.out.print("Enter prefix to lookup (enter 'Exit' to exit.):");
			try {
				lookup = br.readLine();
				if ((lookup.toLowerCase()).equals("exit"))
					return;

				if (lookup.equals("")) {
					System.out.println("No prefix was entered.");
					continue;
				}
			} catch (IOException e) {
				System.out.println("Error occurred while reading prefix to be auto-completed.");
				return;
			}

			lookupPrefix(lookup);
		}
	}

	public static void readEntriesFromFileAndStore(String filename)
			throws FileNotFoundException, IOException, NullPointerException {

		Random randomGenerator = new Random();

		InputStream stream = App.class.getClassLoader().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		
		String line;
		while ((line = br.readLine()) != null) {
			int rating = randomGenerator.nextInt(50);
			System.out.println("Entry: " + line + ", rating: " + rating);

			trie.insert(line, rating);
		}
	}

	public static void lookupPrefix(String lookup) {
		
		System.out.println("---------------- Top 5 entries for auto-completing '" + lookup + "'");

		long startTime = System.currentTimeMillis();

		// Lookup prefix in existing entries
		List<String> entries = trie.getTop5Entries(lookup);

		for (String entry : safe(entries)) {
			System.out.println(entry);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;

		System.out.println("----------------\n");
		System.out.println("Time taken to lookup: " + elapsedTime + " milliseconds.");
	}
}
