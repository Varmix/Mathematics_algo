package tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrieNodeTest {

	private TrieNode node;

    @BeforeEach
    public void setUp() {
        node = new TrieNode();
    }

    @Test
    public void testIsWord() {
        assertFalse(node.isWord());
        node.setIsWord(true);
        assertTrue(node.isWord());
    }

    @Test
    public void testGetChild() {
        assertNull(node.getChild('a'));
        TrieNode child = new TrieNode();
        node.setChild('a', child);
        assertEquals(child, node.getChild('a'));
    }

    @Test
    public void testSetChild() {
        TrieNode child = new TrieNode();
        node.setChild('a', child);
        assertEquals(child, node.getChild('a'));
        node.setChild('z', child);
        assertEquals(child, node.getChild('z'));
        assertEquals(26, node.getNumbersOfChildren());
    }

    @Test
    public void testCharToIndex() {
        assertEquals(0, node.charToIndex('a'));
        assertEquals(25, node.charToIndex('z'));
        assertEquals(26, node.charToIndex('-'));
        assertEquals(27, node.charToIndex('\''));
        assertEquals(-1, node.charToIndex('1'));  
    }

    @Test
    public void testIndexToChar() {
        assertEquals('a', node.indexToChar(0));
        assertEquals('b', node.indexToChar(1));
        assertEquals('z', node.indexToChar(25));
        assertEquals('-', node.indexToChar(26));
        assertEquals('\'', node.indexToChar(27));

        assertThrows(IllegalArgumentException.class, () -> {
            node.indexToChar(28);  
        });
    }

    @Test
    public void testGetNumberOfChildren() {
    	assertNull(node.getChildren());
        assertEquals(0, node.getNumbersOfChildren()); 
        node.setChild('a', new TrieNode());
        assertEquals(1, node.getNumbersOfChildren()); 
    }

    @Test
    public void testGetChildren() {
        TrieNode[] children = node.getChildren();
        assertNull(children); 

        TrieNode child = new TrieNode();
        node.setChild('a', child);
        children = node.getChildren();
        assertNotNull(children);
        assertEquals(child, children[node.charToIndex('a')]);
    }

}
