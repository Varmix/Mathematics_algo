package tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

/* ---------------------------------------------------------------- */

/*
 * Constructor
 */
public class LexicographicTreeTest {
	private static final String FILE_PATH = "mots/dictionnaire_FR_sans_accents.txt";
	private static final String[] WORDS = new String[] { "aide", "as", "au", "aux", "bu", "bus", "but", "et", "ete" };
	private static final LexicographicTree DICT = new LexicographicTree();

	@BeforeAll
	private static void initTestDictionary() {
		for (int i = 0; i < WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}

	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i = 0; i < WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}

	@Test
	void containsWord_General() {
		for (String word : WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] { "", "aid", "ai", "aides", "mot", "e" }) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}

	@Test
	void getWords_General() {
		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());

		assertEquals(0, DICT.getWords("x").size());

		assertEquals(3, DICT.getWords("bu").size());
		assertArrayEquals(new String[] { "bu", "bus", "but" }, DICT.getWords("bu").toArray());
	}

	@Test
	void getWords_withHippen() {
		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());
		String[] words = new String[] { "aujourd'hui", "tire-bouchon" };
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord(words[0]);
		tree.insertWord(words[1]);

		assertEquals(1, tree.getWords("a").size());

		assertEquals(1, tree.getWords("tire").size());
		assertArrayEquals(new String[] { "aujourd'hui" }, tree.getWords("a").toArray());
		assertArrayEquals(new String[] { "tire-bouchon" }, tree.getWords("t").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] { "aux", "bus", "but", "ete" }, DICT.getWordsOfLength(3).toArray());
	}

	@Test
	void getWordsOfLength_with_hippen_and_apostophe() {
		String[] words = new String[] { "aujourd'hui", "tire-bouchon" };
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord(words[0]);
		tree.insertWord(words[1]);
		assertEquals(0, tree.getWordsOfLength(10).size());
		// Mot : aujourd'hui
		assertEquals(1, tree.getWordsOfLength(11).size());
		// Mot : tire-bouchon
		assertEquals(1, tree.getWordsOfLength(12).size());
		assertArrayEquals(new String[] {}, tree.getWordsOfLength(10).toArray());
		assertArrayEquals(new String[] { "aujourd'hui" }, tree.getWordsOfLength(11).toArray());
		assertArrayEquals(new String[] { "tire-bouchon" }, tree.getWordsOfLength(12).toArray());
	}

	@Test
	void getWordsOfLength_WithNoOder_length_of_3() {
		String[] words = new String[] { "bus", "aide", "as", "but", "au", "aux", "ete", "bu", "et" };
		LexicographicTree tree = new LexicographicTree();
		for (int i = 0; i < words.length; i++) {
			tree.insertWord(words[i]);
		}
		// Mot à trouver (en conservant l'ordre alphabétique : aux, bus, but, ete
		assertEquals(4, tree.getWordsOfLength(3).size());
		// Expected
		assertArrayEquals(new String[] { "aux", "bus", "but", "ete" }, tree.getWordsOfLength(3).toArray());
		// Not expected
		assertFalse(Arrays.equals(new String[] { "bus", "aux", "but", "ete" }, tree.getWordsOfLength(3).toArray()));
	}
	
	@Test
	void insert_empty_string() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("");
		assertTrue(tree.containsWord(""));
		assertEquals(1, tree.size());
		assertEquals(1, tree.getWords("").size());
	}

	@Test
	void contains_prefix_is_the_length_of_the_word_in_the_three() {
		String[] words = new String[] { "aujourd'hui", "tire-bouchon" };
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord(words[0]);
		tree.insertWord(words[1]);
		// Mot : aujourd'hui
		assertTrue(tree.containsPrefix("aujourd'hui"));
		assertFalse(tree.containsPrefix("aujourd'huz"));
		assertTrue(tree.containsPrefix("a"));
		assertFalse(tree.containsPrefix(""));
		// Mot : tire-bouchon
		assertTrue(tree.containsPrefix("tire-"));
		assertFalse(tree.containsPrefix("bouchon"));
		assertTrue(tree.containsPrefix("tire-bouchon"));

	}

	@Test
	void getWords_with_empty_string() {
		LexicographicTree tree = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
		assertEquals(327956, tree.getWords("").size());
	}

	@Test
	void getWords_length__with_a_length_of_zero_or_lower() {
		assertEquals(0, DICT.getWordsOfLength(0).size());
		assertEquals(0, DICT.getWordsOfLength(-1).size());
	}
	
	@Test
	void insert_with_no_valid_character() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("a+b+c");
		assertTrue(tree.containsWord("abc"));
		assertEquals("abc", tree.getWordsOfLength(3).get(0));
	}
	
	@Test 
	void no_insert_of_the_empty_string() {
		LexicographicTree tree = new LexicographicTree();
		assertFalse(tree.containsWord(""));
	}
	
	@Test
	void insert_all_words_with_same_legth() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("terre");
		tree.insertWord("pomme");
		tree.insertWord("carre");
		tree.insertWord("abcde");
		tree.insertWord("motos");
	}


	
	@Test
	void constructorBadFile(){
		// Given
		LexicographicTree dicoBad;
		
		// When
		dicoBad = new LexicographicTree("nope");
		
		// Then
		assertEquals(0, dicoBad.size());
	}
	
	@Test
	void constructorEmptyFile() {
		// Given
		LexicographicTree dico;
		
		// When
		dico = new LexicographicTree("mots/empty.txt");
		
		// Then
		assertEquals(0, dico.size());
		
	}
	
	// InsertWord
	@Test
	void insertWordNormal() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word = "hello";
		
		// When
		dict.insertWord(word);
		words.add(word);
				
		// Then
		assertEquals(1, dict.size());
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void insertWordNormalWithNumbers() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word = "hello";
		String badWord = "hel15lo";
		
		// When
		dict.insertWord(badWord);
		words.add(word);
		
		// Then
		assertEquals(1, dict.size());
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void insertWordNormalWithSpecialCharacters() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word = "hello";
		String badWord = "he^^ll$o";
		
		// When
		dict.insertWord(badWord);
		words.add(word);
		
		// Then
		assertEquals(1, dict.size());
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void insertEmptyWord() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		String word = "";
		
		// When
		dict.insertWord(word);
		
		// Then
		assertEquals(1, dict.size());
	}

	
	
	// GetWords
	@Test
	void getWordsOfNulLength() {
		assertEquals(0, DICT.getWordsOfLength(0).size());	
	}
	
	@Test
	void getWordsOfNegativeLength() {
		assertEquals(0, DICT.getWordsOfLength(-5).size());	
	}
	
	@Test
	void getWordsOfTooHighLength() {
		assertEquals(0, DICT.getWordsOfLength(35).size());
	}
	
	
	
	@Test
	void getWordsInAlphabeticalOrdrerByPrefix() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word2 = "hello";
		String word3 = "nope";
		String word4 = "oukilest";
		String word1 = "azerbaijan";
		
		// When
		words.add(word1);
		words.add(word2);
		words.add(word3);
		words.add(word4);
		
		dict.insertWord(word4);
		dict.insertWord(word3);
		dict.insertWord(word1);
		dict.insertWord(word2);
		
		// Then
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void getWordsInAlphabeticalOrdrerByLength() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word2 = "hello";
		String word3 = "nopel";
		String word4 = "oukil";
		String word1 = "azerb";
		
		// When
		words.add(word1);
		words.add(word2);
		words.add(word3);
		words.add(word4);
		
		dict.insertWord(word4);
		dict.insertWord(word3);
		dict.insertWord(word1);
		dict.insertWord(word2);
		
		// Then
		assertEquals(words, dict.getWordsOfLength(5));
	}
	
	
	@Test
	void getWordWithUppercaseAndAccents(){
		// Given
		LexicographicTree dict = new LexicographicTree(FILE_PATH);
		List<String> words = new ArrayList<>();
		
		// When
		words = dict.getWords("artIste");
		words = dict.getWords("téléphone");
		words = dict.getWords("héberGEMent");
		
		// Then
		assertEquals(0, words.size());
	}
	
	@Test
	void containsWord_in_another_word() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("photo");
		dict.insertWord("photographe");
		
		assertTrue(dict.containsWord("photo"), "Mot photo non trouvé");	
		assertTrue(dict.containsWord("photographe"), "Mot photographe non trouvé");		
	}
	
	@Test
	void containsWord_update_isWord() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("photographe");
		assertFalse(dict.containsWord("photo"), "Mot photo non trouvé");	
		
		
		dict.insertWord("photo");
		assertTrue(dict.containsWord("photo"), "Mot photo non trouvé");		
	}
	
	@Test
	void containsWord_similar_words() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("photo");
		dict.insertWord("photi");
		dict.insertWord("phatp");
		dict.insertWord("photm");
		dict.insertWord("phots");
		dict.insertWord("phtte");
		
		assertTrue(dict.containsWord("photo"), "Mot photo non trouvé");	
		assertTrue(dict.containsWord("photi"), "Mot photi non trouvé");
		assertTrue(dict.containsWord("phatp"), "Mot phatp non trouvé");
		assertTrue(dict.containsWord("photm"), "Mot photm non trouvé");
		assertTrue(dict.containsWord("phots"), "Mot phots non trouvé");
		assertTrue(dict.containsWord("phtte"), "Mot phtte non trouvé");
		
		assertFalse(dict.containsWord("photx"), "Mot photx inexistant trouvé");
		assertFalse(dict.containsWord("phytz"), "Mot phytz inexistant trouvé");
		assertFalse(dict.containsWord("fhotx"), "Mot fhotx inexistant trouvé");
		assertFalse(dict.containsWord("pmotx"), "Mot pmotx inexistant trouvé");
		assertFalse(dict.containsWord("photx"), "Mot photx inexistant trouvé");
	}
	
	@Test
	void containsWord_in_empty_tree() {
		LexicographicTree dict = new LexicographicTree();
		
		assertFalse(dict.containsWord("photo"), "Mot photo inexistant trouvé");	
		assertFalse(dict.containsWord(""), "Mot inexistant trouvé");	
		
		dict.insertWord("");
		
		assertTrue(dict.containsWord(""));
		assertEquals(1, dict.getWords("").size());
	}
	
	@Test
	void insert_same_word_severals_times() {
		LexicographicTree dict = new LexicographicTree();
		
		for (int i = 0; i < 10; i++) {
			dict.insertWord("francis");
		}
		
		assertEquals(1, dict.size());		
	}
	
	
	@Test
	void getWords_sorted() {
		LexicographicTree dict = new LexicographicTree();		
		dict.insertWord("zo");
		dict.insertWord("za");
		dict.insertWord("zu");
		dict.insertWord("zi");
		
		assertArrayEquals(new String[] {"za", "zi", "zo", "zu"}, dict.getWords("z").toArray());
	}
	
	@Test
	void getWords_empty() {		
		assertArrayEquals(new String[] {"aide", "as", "au", "aux","bu", "bus", "but", "et", "ete"}, DICT.getWords("").toArray());
	}
	
	@Test
	void getWordsOfLength_too_many() {
		assertEquals(0, DICT.getWordsOfLength(100).size());
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(100).toArray());	
	}
	
	@Test
	void getWordsOfLength_null() {
		assertEquals(0, DICT.getWordsOfLength(0).size());
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(0).toArray());	
	}
	
	@Test
	void getWordsOfLength_negative() {
		assertEquals(0, DICT.getWordsOfLength(-5).size());
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(0).toArray());	
	}
	
	@Test
	void getWordsOfLength_sorted() {
		assertArrayEquals(new String[] {"aux", "bus", "but", "ete"}, DICT.getWordsOfLength(3).toArray());	
	}
	
	@Test
	void getWordsOfLength_sorted_with_unsorted_insertion() {
		LexicographicTree dict = new LexicographicTree();		
		dict.insertWord("zo");
		dict.insertWord("za");
		dict.insertWord("zu");
		dict.insertWord("zi");
		assertArrayEquals(new String[] {"za", "zi", "zo", "zu"}, dict.getWordsOfLength(2).toArray());	
	}
	
	@Test
	void getWordsOfLength_one_found() {
		assertEquals(1, DICT.getWordsOfLength(4).size());
		assertArrayEquals(new String[] {"aide"}, DICT.getWordsOfLength(4).toArray());	
	}
	
	@Test
	void getWordsOfLength_normal_case() {
		assertEquals(4, DICT.getWordsOfLength(2).size());
		assertArrayEquals(new String[] {"as", "au", "bu", "et"}, DICT.getWordsOfLength(2).toArray());	
	}
	
	@Test
    void hasRepeatedLetters() {
        assertTrue(DICT.hasRepeatedLetters("pomme"));
        assertTrue(DICT.hasRepeatedLetters("abaissasse"));
        assertTrue(DICT.hasRepeatedLetters("abat"));
    }
	
	@Test
	void hasNoRepeatedLetters() {
        assertFalse(DICT.hasRepeatedLetters("monde"));
        assertFalse(DICT.hasRepeatedLetters("salut"));
	}
	
	@Test
	void getLetterPattern_general() {
		assertEquals("abccd", DICT.getLetterPattern("pomme"));
        assertEquals("abac", DICT.getLetterPattern("abat"));
        assertEquals("abcde", DICT.getLetterPattern("monde"));
        assertEquals("ab", DICT.getLetterPattern("ha"));
        assertEquals("abacdefgh", DICT.getLetterPattern("abat-jour"));
        assertEquals("abcdefgchegiigjgch", DICT.getLetterPattern("SJBZFUMBDFMHHMOMBD"));
        assertEquals("abcdefgchegiigjgch", DICT.getLetterPattern("CONFIDENTIELLEMENT"));
	}
	

}
