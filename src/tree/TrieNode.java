package tree;


public class TrieNode {
	private TrieNode[] children;
    private boolean isWord;
    
    public TrieNode() {
        isWord = false;
    }
    
    /**
     * Permet de récupérer l'objet TrieNode associé à la position du caractère reçu
     * en paramètre.
     * @param c le caractère souhaité
     * @return null si le caractère n'est pas dans les indices du tableau représentant les enfants ou 
     * si le tableau n'est pas encore initialisé. Sinon, l'objet TrieNode associé à la position du tableau d'enfants du caractère reçu.
     */
    public TrieNode getChild(char c) {
        int index = charToIndex(c);
        // Si l'index est supérieur ou égale à la longeur des enfants, cela signifie qu'il faut agrandir la taille du tableau
        if (children == null || index < 0 || index >= children.length) {
            return null;
        }
        return children[index];
    }
    
    /**
     * Permet d'ajouter un objet TrieNode enfant à la position du caractère reçu en paramètre dans
     * le tableau représentant les enfants du noeud courant.
     * @param c le caractère auquel on souhaite associer l'objet TrieNode
     * @param child le noeud associé au caractère à placer dans le tableau représentant les enfants
     * du noeud courant
     */
    public void setChild(char c, TrieNode child) {
        int index = charToIndex(c);
        if (children == null) {
            children = new TrieNode[index + 1];
        } else if (index >= children.length) {
        	TrieNode[] newChildren = new TrieNode[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
    }
    
    /**
     * 
     * @return true si le TrieNode courant (le caractère) représente une fin d'un mot, false sinon
     */
    public boolean isWord() {
        return isWord;
    }
    
    /**
     * Cette méthode permet de définir si le TrieNode courant (caractère)
     * représente la fin d'un mot
     * @param isWord true si le TrieNode courant (caractère) représente un mot, false sinon
     */
    public void setIsWord(boolean isWord) {
        this.isWord = isWord;
    }
    
    /**
     * Cette méthode permet de récupérer l'indice correspondant au caractère donné en paramètre
     * en se basant sur la valeur ASCII du caractère.
     * 
     * <p>Plus précisement,
     * Pour récupérer l'indice des lettres minuscules, on va soustraire
     * la valeur ASCII du caractère en paramètre de méthode à la valeur ASCII du caractère 'a'.
     * Par exemple, le caractère 'a' a la valeur ASCII 97, si le caractère 'b' est reçu, l'opération
     * effectué est : 98 - 97 = 1, il faudra placer le caractère b à l'indice 1 de notre tableau. Ceci est
     * valable pour les lettres minuscules de 'a' à 'z'.
     * Dans le cas du tiret ou de l'apostrophe, les indices 26 et 27 de notre 
     * tableau sont réservés à cet usage.
     * </p>
     * @param c le caractère à positionner dans le tableau
     * @return un indice compris entre 0 et 25 pour les lettres de l'alphabet, 26 pour 
     * le tiret et 27 pour l'apostrophe, sinon -1.
     */
    public int charToIndex(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c == '-') {
            return 26;
        } else if (c == '\'') {
            return 27;
        } else {
            return -1;
        }
    }
    
    /**
     * Cette méthode retourne la longueur du tableau des enfants.
     * @returnr un entier correspondant à la longueur du tableau des enfants.
     */
    public int getNumbersOfChildren() {
    	return children != null ? children.length : 0;
    }
    
    /**
     * 
     * @return null si le tableau d'enfants n'est pas encore intialisé.
     * Sinon, le tableau représentant les enfants du noeud courant
     */
    public TrieNode[] getChildren() {
    	return this.children;
    }
    
    /**
     * Cette méthode permet de récupérer sur base d'un index
     * le caractère correspondant dans le tableau
     * @param index la position d'un caractère dans le tableau
     * @return le caratère correspondant à l'index spécifié en paramètre.
     */
    public char indexToChar(int index) {
    	if(index < 0 || index > 27) {
    		throw new IllegalArgumentException("L'index demandé n'est pas un caractère valide");
    	}
    	if(index == 26) {
    		return '-';
    	} else if(index == 27) {
    		return '\'';
    	}
    	return (char) ('a' + index);
 
    }
}

