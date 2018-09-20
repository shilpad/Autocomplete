
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class AutoCompleteTest {
	Trie trie = new Trie();

	@Before
	public void setup() {
		trie.insert("Yoyo", 20);
		trie.insert("yogurt land", 32);
		trie.insert("Ballet", 40);
		trie.insert("Violin lessons", 35);
		trie.insert("Yosemite park", 50);
		trie.insert("barnes and nobles", 15);
		trie.insert("Baymax", 45);
		trie.insert("vasona park", 10);
		trie.insert("barnyard", 40);
		trie.insert("backyardigans", 40);
		trie.insert("Business Insider", 40);
		trie.insert("booster seats", 40);
	}

	@Test
	public void testAutoComplete_yo() {
		TrieNode node = trie.searchNode("yo");
		assertNotNull("AutoComplete for yo returned no matches!", node);

		Map<Integer, List<String>> entryRatings = trie.getEntryRatings();
		assertEquals(3, entryRatings.size());

		assertEquals(Arrays.asList("yoyo"), entryRatings.get(20));
		assertEquals(Arrays.asList("yogurt land"), entryRatings.get(32));
		assertEquals(Arrays.asList("yosemite park"), entryRatings.get(50));
	}

	@Test
	public void testAutoComplete_empty() {
		TrieNode node = trie.searchNode("");
		assertEquals("No prefix was entered.", node.errorMessage);
	}
	
	@Test
	public void testAutoComplete_prefixNotFound() {
		TrieNode node = trie.searchNode("Z");
		assertEquals("Prefix 'Z' does not exist in the collection of words.", node.errorMessage);
	}

	@Test
	public void testAutoComplete_b() {
		TrieNode node = trie.searchNode("b");
		assertNotNull("AutoComplete for b returned no matches!", node);

		Map<Integer, List<String>> entryRatings = trie.getEntryRatings();
		assertEquals(3, entryRatings.size());

		assertEquals(Arrays.asList("backyardigans", "ballet", "barnyard", "booster seats", "business insider"),
				entryRatings.get(40));
		assertEquals(Arrays.asList("barnes and nobles"), entryRatings.get(15));
		assertEquals(Arrays.asList("baymax"), entryRatings.get(45));
	}

	@Test
	public void testAutoComplete_b_top5() {
		List<String> entries = trie.getTop5Entries("b");

		assertEquals(5, entries.size());
		assertEquals(Arrays.asList("baymax", "backyardigans", "ballet", "barnyard", "booster seats"), entries);
	}

	@Test
	public void testAutoComplete_b_topN() {
		List<String> entries = trie.getTopNEntries("b", 2);

		assertEquals(2, entries.size());
		assertEquals(Arrays.asList("baymax", "backyardigans"), entries);
	}
	
	@Test
	public void testAutoComplete_vasona() {
		TrieNode node = trie.searchNode("VASONA");
		assertNotNull("AutoComplete for VASONA returned no matches!", node);

		Map<Integer, List<String>> entryRatings = trie.getEntryRatings();
		assertEquals(1, entryRatings.size());

		assertEquals(Arrays.asList("VASONA park"), entryRatings.get(10));
	}

	@Test
	public void testAutoComplete_prefixWithSpace() {
		TrieNode node = trie.searchNode("vasona p");
		assertNotNull("AutoComplete for VASONA returned no matches!", node);

		Map<Integer, List<String>> entryRatings = trie.getEntryRatings();
		assertEquals(1, entryRatings.size());

		assertEquals(Arrays.asList("vasona park"), entryRatings.get(10));
	}
	
	@Test
	public void testAutoComplete_insertingspecialcharacters() {
		// Error logged in console when special character other than space is
		// used
		trie.insert("google.com", 100);
	}

	@Test
	public void testAutoComplete_searchingspecialcharacters() {
		// Error logged in console when special character other than space is
		// used
		TrieNode node = trie.searchNode("bay.max");
		assertEquals("Special character . is not supported.", node.errorMessage);
	}
}
