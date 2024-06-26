package tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class LexicographicTree {
	
	  private TrieNode root;
	  private int size;

	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
	    size = 0;
	}
	
	/**
	 * Constructor : creates a lexicographic tree populated with words 
	 * @param filename A text file containing the words to be inserted in the tree 
	 */
	public LexicographicTree(String filename) {
		this();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                insertWord(line.trim());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Nom de fichier invalide ! Un LexicographcTree vide a été créé !");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/*
	 * PUBLIC METHODS
	 */
	
	/**
	 * Returns the number of words present in the lexicographic tree.
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 * @param word A word
	 */
	public void insertWord(String word) {
		// Lazy initalisation, on crée le noeud racine lorsqu'on en aura besoin et non
		// au démarrage de l'application.
		if(root == null) {
			root = new TrieNode();
		}
	    if (word.isEmpty()) {
	        if (!root.isWord()) {
	            root.setIsWord(true);
	            size++;
	        }
	        return;
	    }
		// Consignes : si le mot est déjà présent, cette méthode ne modifie pas l’arbre.
		if(containsWord(word)) {
			return;
		}
	    char[] wordChars = word.toCharArray();
	    TrieNode current = root;
	    TrieNode next = null;
	    char c;
	    int i;
	    for (i= 0; i < wordChars.length; i++) {
	    	// Récupération de chaque caractère du mot reçu en paramètre + validation
	        c = wordChars[i];
	        if ((c < 'a' || c > 'z') && c != '-' && c != '\'') { 
	        	continue;
	        }
	        // Vérification que le caractère est contenu dans le tableau du noeud courant
	        next = current.getChild(c);
	        // Si le caractère n'est pas dans le noeud courant
	        if (next == null) {
	            next = new TrieNode();
	            current.setChild(c, next);
	        }
	        current = next;
	    }
	    // Marquer le noeud comme final
	    if (!current.isWord() && i == wordChars.length) {
	        current.setIsWord(true);
	        size++;
	    }
	}


	
	
	/**
	 * Determines if a word is present in the lexicographic tree.
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		if(root == null) {
			root = new TrieNode();
		}
		if(word.length() == 0) {
			return root.isWord();
		}
		if(word == null || root.getChild(word.charAt(0)) == null) {
			return false;
		}
		char[] wordChars = word.toCharArray();
        TrieNode current = root.getChild(wordChars[0]);
        char c;
        for (int i = 1; i < wordChars.length; i++) {
        	// Explorer de manière arborescente
            c = wordChars[i];
            current = current.getChild(c);
            if (current == null) {
                return false;
            }
        }
        return current.isWord();
	}
	
	/**
	 * Cette méthode permet de déterminer si un préfixe donné
	 * se trouve dans l'arbre.
	 * @param prefix le préfixe souhaité
	 * @return true si le préfixe donné en paramètre se trouve dans l'arbre, false sinon.
	 */
	public boolean containsPrefix(String prefix) {
		// Si la première lettre n'est pas contenu dans les lettres racine, ça ne sert à rien d'aller plus loin.
		if(prefix == null || prefix.length() == 0 || root.getChild(prefix.charAt(0)) == null) {
			return false;
		}
		char[] prefixChars = prefix.toCharArray();
		TrieNode current = root;
		// Parcours de chaque caractère du préfixe donné en paramètre en appliquant des conditions afin de savoir s'il se trouve dans l'arbre
		// pour pouvoir construire un mot
		for (char c : prefixChars) {
			int charIndex = root.charToIndex(c);
			if(charIndex == -1 || current.getChildren() == null || current.getNumbersOfChildren() <= charIndex || current.getChildren()[charIndex] == null) {
				return false;
			}
			current = current.getChild(c);
		}
		return true;
	}

	/**
	 * Returns an alphabetic list of all words starting with the supplied prefix.
	 * If 'prefix' is an empty string, all words are returned.
	 * @param prefix Expected prefix
	 * @return The list of words starting with the supplied prefix
	 */
	public List<String> getWords(String prefix) {
		if (root == null) {
			root = new TrieNode();
		}
		List<String> words = new ArrayList<>();
		TrieNode node = root;

		// Il faut obligatoirement trouver le préfixe dans l'arbre afin de construire
		// les mots. Sinon, on retourne la liste vide.
		for (int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			node = node.getChild(c);
			if (node == null) {
				return words;
			}
		}

		// Une fois arrivé au dernier noeud du préfixe, il est temps de construire tous
		// les mots possibles.
		searchWordsRecursivity(node, words, new StringBuilder(prefix));

		return words;
	}



	/**
	 * Returns an alphabetic list of all words of a given length.
	 * If 'length' is lower than or equal to zero, an empty list is returned.
	 * @param length Expected word length
	 * @return The list of words with the given length
	 */
	public List<String> getWordsOfLength(int length) {
		if(root == null) {
			root = new TrieNode();
		}
	    List<String> words = new ArrayList<>();
	    // 27 caractères correspond au plus long mot que l'on peut trouver en français, à savoir "intergouvernementalisations"
	    if(length <= 0 || length > 27) {
	    	return words;
	    }
	    StringBuilder sb = new StringBuilder(length);
	    searchWordsOfLenghRecursivity(length, sb, root, words);
	    return words;
	}

	
	
	/**
	 * Cette méthode de permet de vérifier si des mots ont des lettres répétées.
	 * @param word un mot
	 * @return true si le mot possède des lettres répétées, false sinon (lorsque le mot
	 * ne possède que des caractères uniques)
	 */
	public boolean hasRepeatedLetters(String word) {
	    Set<Character> uniqueChars = new HashSet<>();
	    for (char c : word.toCharArray()) {
	        if (!uniqueChars.add(c)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Cette méthode permet de récupérer le "pattern" d'un mot. On entend par là, 
	 * que chaque lettre unique dans le mot est remplacée par une lettre de l'alphabet.
	 * On commence par 'a', puis 'b', puis 'c' et ainsi de suite.
	 * Les lettres qui se répètent au sein du mot sont remplacées par la même lettre dans le pattern.
	 * Par exemple le pattern serait 'abccd' pour le mot 'pomme'
	 * @param word la chaine de caractère pour laquelle un "pattern" va être généré.
	 * @return le "pattern" de la chaine de caractères
	 */
	public String getLetterPattern(String word) {
	    Map<Character, Character> charMap = new HashMap<>();
	    StringBuilder pattern = new StringBuilder();
	    char nextReplacementChar = 'a';

	    for (char c : word.toCharArray()) {
	    	if(!charMap.containsKey(c)) {
				charMap.put(c, nextReplacementChar);
				nextReplacementChar++;
			}
	        pattern.append(charMap.get(c));
	    }
	    return pattern.toString();
	}
	
	

	/*
	 * PRIVATE METHODS
	 */
	
	
	/**
	 * Cette méthode permet de rechercher récursivement tous les mots de l'arbre.
	 * Le mots trouvés sont ajoutés à la liste "words"
	 * @param node le noeud courant de l'arbre
	 * @param words la liste des mots trouvés
	 * @param currentWord le mot en cours de construction
	 */
	private void searchWordsRecursivity(TrieNode node, List<String> words, StringBuilder currentWord) {
	    if (node.isWord()) {
	        words.add(currentWord.toString());
	    }
	    // Il va falloir construire tous les mots possibles à partir du noeud
	    // correspondant à celui du dernier caractère du préfixe. 
	    TrieNode[] children = node.getChildren();
	    if (children != null) {
	        for (int i = 0; i < children.length; i++) {
	            TrieNode child = children[i];
	            if (child != null) {
	                char c = root.indexToChar(i);
	                currentWord.append(c);
	                searchWordsRecursivity(child, words, currentWord);
	                // Enlever le caractère qui vient d'être ajouté pour ce tour de boucle
	                // car d'autres caractères aux prochains tours sont à explorer en conservant
	                // le préfixe actuel
	                currentWord.setLength(currentWord.length() - 1);
	            }
	        }
	    }
	}
	
	/**
	 * Cette méthode permet de rechercher récursivement tous les mots d'une longueur spécifiée dans l'arbre.
	 * Les mots trouvés sont ajoutés à la liste "words".
	 * @param length la longueur des mots que l'on souhaite chercher
	 * @param currentWord le mot en cours de construction
	 * @param node le noeud courant de l'arbre
	 * @param words la liste des mots trouvés
	 */
	private void searchWordsOfLenghRecursivity(int length, StringBuilder currentWord, TrieNode node, List<String> words) {
		// Si le mot formé est de taille atendu et que c'est un mot de fin, on l'ajoute à la liste.
	    if (currentWord.length() == length && node.isWord()) {
	        words.add(currentWord.toString());
	        // Si la taille est inférieure, il faut poursuivre la recherche
	    } else if (currentWord.length() < length) {
	    	// On récupère les enfants du noeud (caractère) en cours de traitement
	    	 TrieNode[] children = node.getChildren();
	         if (children != null) {
	             for (int i = 0; i < children.length; i++) {
	                 TrieNode child = children[i];
	                 if (child != null) {
	                     char c = root.indexToChar(i);
	                     currentWord.append(c);
	                     // si le mot courant est de même taille que la longueur souhaitée alors on continue la récursion.
	                     // ou si le noeud courant a des enfants, on continue également la récursion (car cela signfie qu'on n'a pas encore
	                     // atteint la taille souhaitée)
	                     if (currentWord.length() == length || child.getChildren() != null && child.getNumbersOfChildren() != 0) {
	                    	 searchWordsOfLenghRecursivity(length, currentWord, child, words);
	                     }
	                     // Enlever le caractère que l'on vient d'ajouter afin de poursuivre la recherche avec d'autres caractères
	                     currentWord.setLength(currentWord.length() - 1);
	                 }
	             }
	         }
	    }
	}
	
	/*
	 * TEST FUNCTIONS
	 */
		
	private static String numberToWordBreadthFirst(long number) {
		String word = "";
		int radix = 13;
		do {
			word = (char)('a' + (int)(number % radix)) + word;
			number = number / radix;
		} while(number != 0);
		return word;
	}
	
	private static void testDictionaryPerformance(String filename) {
		long startTime;
		int repeatCount = 20;
		
		// Create tree from list of words
		startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary...");
		LexicographicTree dico = null;
		for (int i = 0; i < repeatCount; i++) {
			dico = new LexicographicTree(filename);
		}
		System.out.println("Load time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("Number of words : " + dico.size());
		System.out.println();
		
	
		// Search existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching existing words in dictionary...");
		File file = new File(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine();
				    boolean found = dico.containsWord(word);
				    if (!found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search non-existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching non-existing words in dictionary...");
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine() + "xx";
				    boolean found = dico.containsWord(word);
				    if (found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
		
		

		// Search words of increasing length in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching for words of increasing length...");
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			if (dico.size() != total) {
				System.out.printf("Total mismatch : dict size = %d / search total = %d\n", dico.size(), total);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
	}

	private static void testDictionarySize() {
		final int MB = 1024 * 1024;
		System.out.print(Runtime.getRuntime().totalMemory()/MB + " / ");
		System.out.println(Runtime.getRuntime().maxMemory()/MB);

		LexicographicTree dico = new LexicographicTree();
		long count = 0;
		while (true) {
			dico.insertWord(numberToWordBreadthFirst(count));
			count++;
			if (count % MB == 0) {
				System.out.println(count / MB + "M -> " + Runtime.getRuntime().freeMemory()/MB);
			}
		}
	}
	
	/*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		// CTT : test de performance insertion/recherche
		testDictionaryPerformance("mots/dictionnaire_FR_sans_accents.txt");;
		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		testDictionarySize();
	}

}
