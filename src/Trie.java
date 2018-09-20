import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class TrieNode {

	TrieNode[] arr;
	int rating;
	String errorMessage;

	public TrieNode() {

		arr = new TrieNode[27]; // index 26 is used for space
		rating = -1;
		errorMessage = "";
	}
}

public class Trie {

	private TrieNode root = new TrieNode();
	private Map<Integer, List<String>> mapEntryRatings = new TreeMap<>();

	public Map<Integer, List<String>> getEntryRatings() {
		return mapEntryRatings;
	}

	// Inserts word into the trie
	public void insert(String word, int rating) {

		TrieNode pCurrent = root;

		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);

			// Check for space so as to use the last index in the array
			int index = ((c == ' ') ? 26 : Character.toLowerCase(c) - 'a');

			if (index < 0 || index > 26) {
				System.out.println("Special character " + c + " is not supported.");
				return;
			}

			if (pCurrent.arr[index] == null) {
				TrieNode temp = new TrieNode();
				pCurrent.arr[index] = temp;
				pCurrent = temp;
			} else {
				pCurrent = pCurrent.arr[index];
			}
		}

		pCurrent.rating = rating;
	}

	// Get top 5 entries
	public List<String> getTop5Entries(String prefix) {
		return (getTopNEntries(prefix, 5));
	}

	// Get top n entries
	public List<String> getTopNEntries(String prefix, int numberofMatches) {

		// Clear the entries looked up for the previous prefix
		mapEntryRatings = new TreeMap<>();

		// Lookup prefix
		TrieNode node = searchNode(prefix);

		if (node.errorMessage != "") {
			System.out.println(node.errorMessage);
			return null;
		}

		// Iterate through the map of matching entries and return the top N
		List<String> topEntries = new ArrayList<>();
		ArrayList<Integer> keys = new ArrayList<Integer>(mapEntryRatings.keySet());

		for (int i = keys.size() - 1, count = 0; i >= 0; i--) {
			if (count == numberofMatches)
				return topEntries;

			List<String> entries = mapEntryRatings.get(keys.get(i));
			for (String entry : entries) {
				topEntries.add(entry);
				count++;

				if (count == numberofMatches)
					break;
			}
		}

		return topEntries;
	}

	// This method is also used in JUnit tests and hence is public
	public TrieNode searchNode(String prefix) {

		TrieNode pCurrent = root;

		// Check if there is a match
		for (int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);

			// Check for space so as to use the last index in the array
			int index = ((c == ' ') ? 26 : Character.toLowerCase(c) - 'a');

			if (index < 0 || index > 26) {
				pCurrent.errorMessage = String.format("Special character %c is not supported.", c);
				return pCurrent;
			}

			if (pCurrent.arr[index] != null) {
				pCurrent = pCurrent.arr[index];
			} else {
				pCurrent.errorMessage = String.format("Prefix '%s' does not exist in the collection of words.", prefix);
				return pCurrent;
			}
		}

		if (pCurrent == root) {
			pCurrent.errorMessage = "No prefix was entered.";
			return pCurrent;
		}
		
		// Get all words that would auto-complete the prefix
		getAllWords(pCurrent, prefix);

		return pCurrent;
	}

	private void getAllWords(TrieNode pCurrent, String prefix) {

		for (int index = 0; index < 27; index++) {
			if (pCurrent.arr[index] != null) {
				char buildPrefix = ((index == 26) ? ' ' : (char) ('a' + index));
				getAllWords(pCurrent.arr[index], prefix + buildPrefix);
			}
		}

		// When you reach the leaf node with rating assigned to it, add the path
		// traversed
		// i.e. the word to a map with the corresponding weight
		if (pCurrent.rating != -1) {

			List<String> entries;
			if (mapEntryRatings.get(pCurrent.rating) == null) {
				entries = new ArrayList<>();
			} else {
				entries = mapEntryRatings.get(pCurrent.rating);
			}

			entries.add(prefix);
			mapEntryRatings.put(pCurrent.rating, entries);
		}
	}
}
