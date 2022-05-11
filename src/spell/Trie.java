package spell;

public class Trie implements ITrie {

    private final Node root = new Node();
    private int nodeCount = 1; // includes root node;
    private int wordCount = 0;

    @Override
    public void add(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); ++i) {
            int index = word.charAt(i) - 'a';
            if(node.getChildren()[index] == null) {
                node.getChildren()[index] = new Node();
                nodeCount++;
                if (i == word.length() - 1) {
                    wordCount++;
                    node.getChildren()[index].incrementValue();
                }
                node = (Node)node.getChildren()[index];
            } else {
                if (i == word.length() - 1) {
                    if(node.getChildren()[index].getValue() == 0){
                        wordCount++;
                    }
                    node.getChildren()[index].incrementValue();
                } else {
                    node = (Node) node.getChildren()[index];
                }
            }
        }
    }

    @Override
    public INode find(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - 'a';
            if (node.getChildren()[index] == null) {
                return null;
            }
            else {
                if (i == word.length() - 1) {
                    if (node.getChildren()[index].getValue() > 0) {
                        return node.getChildren()[index];
                    } else return null;
                }
                node = (Node) node.getChildren()[index];
            }
        }
        return null;
    }

    @Override
    public int getWordCount() {
        return wordCount;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public String toString() {
        // recursive
        StringBuilder currentWord = new StringBuilder();
        StringBuilder output = new StringBuilder();

        toStringHelper(root, currentWord, output);

        // returns a list of all unique words in a trie in alphabetical order on separate lines
        return output.toString();
    }

    private void toStringHelper(Node n, StringBuilder currentWord, StringBuilder output) {
        //pre-order traversal
        if (n.getValue() > 0) {
            // Append the node's word to the output
            output.append(currentWord.toString());
            output.append("\n");
        }

        for (int i = 0; i < n.getChildren().length; ++i) {
            Node child = (Node) n.getChildren()[i];

            if (child != null) {
                char childLetter = (char)('a' + i);
                currentWord.append(childLetter);
                toStringHelper(child, currentWord, output);
                currentWord.deleteCharAt(currentWord.length() - 1);
            }
        }
    }

    @Override
    public int hashCode() {
        int indexSum = 0;
        for (int i = 0; i < root.getChildren().length; i++) {
            if (root.getChildren()[i] != null) {
                indexSum += i;
            }
        }
        return (wordCount * 3)  + (nodeCount * 7) + (indexSum * 11);
    }

    @Override
    public boolean equals(Object o) {
        // do this and o have the same class?
        // is o null? return false
        if (o == null || getClass() != o.getClass()) { return false; }
        Trie t = (Trie) o;

        // is o == this? return true
        if (this == o) { return true; }

        // do this and t have the same wordCount and nodeCount? If no, return false;
        if (getWordCount() != t.getWordCount() || getNodeCount() != t.getNodeCount()) { return false; }
        return equalsHelper(this.root, t.root);
    }

    private boolean equalsHelper(Node n1, Node n2) {
        if (n1 == null && n2 == null) return true;
        if (n1 == null && n2 != null) return false;
        if (n1 != null && n2 == null) return false;
        if (n1.getValue() != n2.getValue()) return false;
        Node[] n1Array = (Node[]) n1.getChildren();
        Node[] n2Array = (Node[]) n2.getChildren();
        boolean equals = true;
        for (int i = 0; i < 26; i++) {
            INode child1 = n1.getChildren()[i];
            INode child2 = n2.getChildren()[i];
            if ((child1 == null && child2 != null) || (child1 != null && child2 == null))
                return false;
            if (child1 != null && child2 != null) {
                equals = equalsHelper(n1Array[i], n2Array[i]);
            }
        }
        return equals;
    }
}
