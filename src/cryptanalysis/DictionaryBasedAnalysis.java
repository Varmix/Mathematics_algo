package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tree.LexicographicTree;

public class DictionaryBasedAnalysis {
	
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";
	
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String CLEAR_FILE = "txt/Plus fort que Sherlock Holmes MAJ.txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private final LexicographicTree DICT;
	private List<String> cryptogramWords;
	private Set<String> wordsOfCryptogramSplitted;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		if (cryptogram.length() == 0) {
			throw new IllegalArgumentException("Le cryptogramme doit être défini ou d'une longueur supérieure à 0");
		}
		this.wordsOfCryptogramSplitted = new LinkedHashSet<>(Arrays.asList(cryptogram.split("\\s+")));
		this.wordsOfCryptogramSplitted.removeIf(word -> word.length() < 3);
		this.wordsOfCryptogramSplitted.removeIf(word -> !dict.hasRepeatedLetters(word));
		this.cryptogramWords = new LinkedList<>(wordsOfCryptogramSplitted);

		cryptogramWords.sort(Comparator.comparingInt(String::length).reversed());
		this.DICT = dict;
	}
	
	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an approximated decoding alphabet.
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String alphabet) {
		if (alphabet.length() != 26) {
			throw new IllegalArgumentException("L'alphabet doit être défini ou d'une longueur 26");
		}
		alphabet = alphabet.toUpperCase();
		int bestScore = getDecryptionQuality(alphabet);
		
		String bestAlphabet = alphabet;
		int currentWordLength = 0;
		List<String> wordsWithSameLength = new ArrayList<>();

		for (int i = 0; i < wordsOfCryptogramSplitted.size(); i++) {
			if (cryptogramWords.isEmpty()) {
				return bestAlphabet;
			}
			String currentWord = cryptogramWords.remove(0);
			int lengthOfCryptedword = currentWord.length();

			// Mise à jour de la liste des mots de même longueur si nécessaire
			if(lengthOfCryptedword != currentWordLength) {
				wordsWithSameLength = DICT.getWordsOfLength(lengthOfCryptedword);
				currentWordLength = lengthOfCryptedword;
			}
				
			for(String dictionaryWord: wordsWithSameLength) {
				// Trouver le mot compatible
				if (DICT.getLetterPattern(dictionaryWord).equals(DICT.getLetterPattern(currentWord))) {
					alphabet = updateAlphabet(alphabet, currentWord, dictionaryWord.toUpperCase());
					if (!bestAlphabet.equals(alphabet)) {
						int newScore = getDecryptionQuality(alphabet);
						if (newScore > bestScore) {
							bestScore = newScore;
							bestAlphabet = alphabet;
						}
					}
					//Dès qu'un mot compatible est trouvé, arrêt de l'itération sur les mots de même longueur
					break;
				}
			}
		}
		
		return bestAlphabet;
	}



	/**
	 * Applies an alphabet-specified substitution to a text.
	 * @param text A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		if(text.length() == 0) {
			throw new IllegalArgumentException("Impossible de comparer un texte n'étant pas défini ou ne faisant pas au moins un caractère");
		}
		StringBuilder sb = new StringBuilder();
		char[] textInChar = text.toCharArray();
		char currentChar;
		int indexOfTheCharacter;
		for (int i = 0; i < textInChar.length; i++) {
			// Calculer la position du caractère dans l'alphabet français du caractère récupéré.
			// Ensuite, on appliquera à cette lettre, le caractère correspondant dans notre alphabet.
			// Ce mécanisme se répète pour chaque lettre.
			currentChar = textInChar[i];
			indexOfTheCharacter = Character.toUpperCase(currentChar) - 'A';
			if(indexOfTheCharacter >= 0 && indexOfTheCharacter < 26) {
				// Modifier le caractère de notre texte par le caractère dans l'alphaet à l'indice trouvée
				sb.append(alphabet.charAt(indexOfTheCharacter));
			} else if (currentChar == ' ' || currentChar == '\n') {
				sb.append(currentChar);
			}
			
		}
		return sb.toString();
	}
	
	/**
	 * Cette méthode permet de mettre à jour l'alphabet de substitution inverse basé sur les correspondances
	 * trouvées entre un mot du cryptogramme et un mot du dictionnaire.
	 * @param alphabet l'alphabet actuel de déchiffrement
	 * @param cryptogramWord le mot provenant du cryptogramme
	 * @param dictionaryWord le mot du dictionnaire compatible au mot du cryptogramme
	 * @return l'alphabet mis à jour en fonction des nouvelles correspondances trouvées entre le mot du
	 * cryptogramme et le mot compatible du dictionnaire.
	 */
	private String updateAlphabet(String alphabet, String cryptogramWord, String dictionaryWord) {
	    StringBuilder updatedAlphabet = new StringBuilder(alphabet);
	    for (int i = 0; i < cryptogramWord.length(); i++) {
	        char cryptedChar = cryptogramWord.charAt(i);
	        char dictChar = dictionaryWord.charAt(i);
	        // Récupérer la position du caractère chiffré dans l'alphabet français
	        int indexCryptedChar = LETTERS.indexOf(cryptedChar);
	        int indexDictChar = updatedAlphabet.indexOf(String.valueOf(dictChar));
	     // Intervertir les lettres dans l'alphabet uniquement si elles ne sont pas déjà à la bonne place
	        if (indexCryptedChar >= 0 && indexCryptedChar < 26) {
	            if (indexDictChar != -1 && indexDictChar != indexCryptedChar) {
	                char temp = updatedAlphabet.charAt(indexCryptedChar);
	                updatedAlphabet.setCharAt(indexCryptedChar, updatedAlphabet.charAt(indexDictChar));
	                updatedAlphabet.setCharAt(indexDictChar, temp);
	            } else if (indexDictChar == -1) {
	                // Mettre à jour l'alphabet avec la correspondance trouvée
	                updatedAlphabet.setCharAt(indexCryptedChar, dictChar);
	            }
	        }
        }
	    
	    return updatedAlphabet.toString();
	}
	
	/*
	 * PRIVATE METHODS
	 */
	/**
	 * Compares two substitution alphabets.
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}
	
	/**
	 * Load the text file pointed to by pathname into a String.
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * Cette méthode permet d'évaluer la qualité de déchiffrement par rapport
	 * à un alphabet donné en paramètre. Pour cela, on parcourt tous les mots
	 * du cryptogramme en appliquant l'alphabet donné en paramètre 
	 * sur chacun d'eux. Ensuite, on vérifie si on trouve une correspondance 
	 * dans le dictionnaire. Si c'est le cas, le score est incrémenté. 
	 * @param alphabet un alphabet à appliquer
	 * @return un score équivalent au nombre de mots du cryptogramme qui ont une correspondance
	 * dans le dictionnaire après l'application de l'alphabet donné en paramètre.
	 */
	private int getDecryptionQuality(String alphabet) {
	    int score = 0;
	    for (String wordsInCryptogram : wordsOfCryptogramSplitted) {
	        String decryptedWord = applySubstitution(wordsInCryptogram, alphabet);
	        if (DICT.containsWord(decryptedWord.toLowerCase())) {
	            cryptogramWords.remove(wordsInCryptogram);
	            score++;
	        }
	    }
	    return score;
	}
	
	
    /*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		//long startTime = System.currentTimeMillis(); // ajout
		System.out.println("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		//long loadDictTime = System.currentTimeMillis(); // ajout
		//System.out.println("Duration : " + (loadDictTime - startTime)/1000.0); // ajout
		System.out.println("done.");
		System.out.println();
		
		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
//		System.out.println();

		/*
		 *  Decode cryptogram
		 */
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
		String startAlphabet = LETTERS;
		//String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC";
		//String startAlphabet = "ZQABMUYDXCJRTWNVKPGIFHLOES";
		//String startAlphabet = "JGXFZUMTVABWLDKPQYCNESIOHR";
		//String startAlphabet = "RSLZQUBKJXETWPINMFVYOGHACD";
		//String startAlphabet = "KQHJNUYTRGBVLCOAZWXPISFEMD";
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);
		//long startDecrypting = System.currentTimeMillis(); // ajout
		//System.out.println("Duration : " + (startDecrypting - loadDictTime)/1000.0);
		
		// Display final results
		System.out.println();
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();
		
		// Display decoded text
		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		System.out.println();
	}
}
