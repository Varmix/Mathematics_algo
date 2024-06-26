package cryptanalysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

import tree.LexicographicTree;


public class DictionaryBasedAnalysisTest {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String CLEAR_FILE = "txt/Plus fort que Sherlock Holmes MAJ.txt";
	private static final String ENCODING_ALPHABET = "YESUMZRWFNVHOBJTGPCDLAIXQK"; // Sherlock
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static LexicographicTree dictionary = null;

	@BeforeAll
	private static void initTestDictionary() {
		dictionary = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
	}
	
	@Test
	void applySubstitutionTest() {
		String message = "DEMANDE RENFORTS IMMEDIATEMENT";
		String encoded = "UMOYBUM PMBZJPDC FOOMUFYDMOMBD";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}

	@Test
	void guessApproximatedAlphabetTest() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 24, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlpabetTest_All_GOOD_LETTERS() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlpabetTest_with_no_cipher_text() {
		String cryptogram = readFile(CLEAR_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (LETTERS.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlpabetTest_with_one_word_cryptogram() {
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis("SYOTYRBM", dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlpabetTest_sherlock_cryptogram_with_anoter_random_alphabet() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String randomAlphabet = "JGXFZUMTVABWLDKPQYCNESIOHR";
		String alphabet = dba.guessApproximatedAlphabet(randomAlphabet);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 24, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlpabetTest_sherlock_cryptogram_with_anoter_random_alphabet2() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String randomAlphabet = "ZQABMUYDXCJRTWNVKPGIFHLOES";
		String alphabet = dba.guessApproximatedAlphabet(randomAlphabet);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void construcroWithInvalidCryptogram() {
		  assertThrows(IllegalArgumentException.class, () -> new DictionaryBasedAnalysis("", dictionary));
	}
	
	@Test
	void guessApproximatedAlphabetTest_AllLetters() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 24, "Toutes les lettres n'ont pas été trouvées [" + score + "]");
	}
	
	
	@Test
	void guessApproximatedAlphabetTest_ShortWord() {
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(DictionaryBasedAnalysis.applySubstitution("NEZ", ENCODING_ALPHABET), dictionary);
		assertNotNull(dba);
		
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 1, "Score inférieur à 1 :  [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlphabetTest_AllGoodLetters() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		
		assertNotNull(dba);
		
		String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Score inférieur à 26 :  [" + score + "]");
	}
	
	@Test
    void guessApproximatedAlphabetTest_AllGoodLetters_SmallCryptogram() {
        DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis("OMCCYRM", dictionary);

        assertNotNull(dba);

        String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);

        int score = 0;
        for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
            if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
        }
        assertTrue(score == 26, "Score inférieur à 15 :  [" + score + "]");
    }
	
	@Test
    void guessApproximatedAlphabetTest_OneWord() {
		LexicographicTree dictionary = new LexicographicTree();
		dictionary.insertWord("consciencieusement");
        DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis("SJBCSFMBSFMLCMOMBD ", dictionary);

        assertNotNull(dba);
        String alphabet = dba.guessApproximatedAlphabet(LETTERS);
        
        int score = 0;
        for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
            if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
        }
       
        assertEquals("ANSTJIGHFOKUEBMPQRCDLVWXYZ", alphabet);
        assertTrue(score == 10, "Score inférieur à 10 :  [" + score + "]");
    }
	
	@Test
	void applySubstitutionEmptyMessage() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("", ENCODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionEmptyMessageAndAlphabet() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("", ""));
	}
	
	
	@Test
	void applySubstitutionOneLetter() {
		String message = "M";
		String encoded = "O";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionAllSameLetters() {
		String message = "EEEEEEE";
		String encoded = "MMMMMMM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionTest_spaces() {
		String message = "MESSAGE MESSAGE MESSAGE MESSAGE MESSAGE";
		String encoded = "OMCCYRM OMCCYRM OMCCYRM OMCCYRM OMCCYRM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}

	
	@Test
	void applySubstitutionTest_GoodCase() {
	    String message = "GOOD MESSAGE";
	    String encoded = "RJJU OMCCYRM";
	    assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
	    assertEquals(message.toUpperCase(), DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET).toUpperCase());
	}
	
	@Test
	void guessApproximatedAlphabetWithDecodingAlphabet() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 9, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlphabetShortAlphabet() {
		// Given
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		String shortAlphabet = "AZERTYUIOP";
		
		// Then
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet(shortAlphabet));
	}
	
	
	
	// Substitution
	
	@Test
	void applySubstitutionTestOnArtiste() {
		String message = "ARTISTE";
		String encoded = "YPDFCDM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	

	

	
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
}
