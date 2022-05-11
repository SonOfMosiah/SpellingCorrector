package spell;

import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class SpellCorrector implements ISpellCorrector {

    private Trie trie = new Trie();

    @Override
    public void useDictionary(String dictionaryFileName) {
        String word;
        Scanner scanner = null;
        try {
            File input = new File(dictionaryFileName);
            scanner = new Scanner(input);

            while (scanner.hasNext()) {
                word = scanner.next();
                trie.add(word);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        String word = inputWord.toLowerCase();

        //if word is in dictionary, not misspelled: return word
        INode node = trie.find(word);
        if (node != null) {
            return word;
        }

        // First, calculate edit distance 1 words
        ArrayList<String> allWords = correctChar(word);
        allWords.addAll(addChar(word));
        allWords.addAll(deleteChar(word));
        allWords.addAll(transposeChar(word));

        String suggestion = frequencyTiebreaker(allWords);
        if (suggestion != null) {
            return suggestion;
        }
        return suggestSimilarWordHelper(allWords);
    }

    private String suggestSimilarWordHelper(ArrayList<String> inputWords) {
        ArrayList<String> newWords = new ArrayList<>();
        for(String inputWord: inputWords) {
            newWords.addAll(correctChar(inputWord));
            newWords.addAll(addChar(inputWord));
            newWords.addAll(deleteChar(inputWord));
            newWords.addAll(transposeChar(inputWord));
        }
        String suggestion = frequencyTiebreaker(newWords);
        if (suggestion != null) {
            return suggestion;
        }
        return null;
    }

    // Word used wrong character
    public ArrayList<String> correctChar(String inputWord) {
        char[] characterArray = inputWord.toCharArray();
        ArrayList<String> newWords = new ArrayList<>();
        for (int i = 0; i < characterArray.length; i++) {
            for (int j = 0; j < 26; ++j) {
                char[] cloneArray = characterArray.clone();
                cloneArray[i] = (char)('a' + j);
                String newWord = new String(cloneArray);
                newWords.add(newWord);
            }
//            characterArray = inputWord.toCharArray(); // reset characterArray for next iteration
        }
        return newWords;
    }

    // Word omits a character
    public ArrayList<String> addChar(String inputWord) {
        ArrayList<String> newWords = new ArrayList<>();
        // iterate over the length of the word
        for (int i = 0; i <= inputWord.length(); i++) {
            for (int j = 0; j < 26; ++j) {
                char insertLetter = (char)('a' + j);
                StringBuilder stringBuilder = new StringBuilder(inputWord);
                stringBuilder.insert(i, insertLetter);
                String newWord = stringBuilder.toString();
                newWords.add(newWord); // build newWords array
            }
        }
        return newWords;
    }

    // Word has an extra character
    public ArrayList<String> deleteChar(String inputWord) {
        ArrayList<String> newWords = new ArrayList<>();
        for (int i = 0; i < inputWord.length(); i++) {
            StringBuilder stringBuilder = new StringBuilder(inputWord);
            stringBuilder.delete(i, i + 1);
            String word = stringBuilder.toString();
            newWords.add(word); // build newWords array
        }
        return newWords;
    }

    // Word transposes two adjacent characters
    public ArrayList<String> transposeChar(String inputWord) {
        char[] characterArray = inputWord.toCharArray();
        ArrayList<String> newWords = new ArrayList<>();
        for (int i = 0; i < characterArray.length; i++) {
            for (int j = 0; j < characterArray.length; j++) {
                char[] cloneArray = characterArray.clone();
                char swap = cloneArray[i];
                cloneArray[i] = cloneArray[j];
                cloneArray[j] = swap;
                String word = new String(cloneArray);
                newWords.add(word); // build newWords array
            }
        }
        return newWords;
    }

    private String frequencyTiebreaker(ArrayList<String> editedWords) {
        ArrayList<String> newArray = new ArrayList<>();
        for (String editedWord: editedWords) {
            if (trie.find(editedWord) != null) {
                newArray.add(editedWord);
            }
        }
        if (newArray.size() > 0) {
            ArrayList<String> sameFrequency = new ArrayList<>();
            int highestFrequency = 0;
            for (String valueWord : newArray) {
                int newHighestFrequency = trie.find(valueWord).getValue();
                if (newHighestFrequency > highestFrequency) {
                    highestFrequency = newHighestFrequency;
                    sameFrequency.clear();
                    sameFrequency.add(valueWord);
                } else if (newHighestFrequency == highestFrequency) {
                    sameFrequency.add(valueWord);
                }
            }
            if (sameFrequency.size() > 1) {
                return abcTiebreaker(sameFrequency);
            }
                return sameFrequency.get(0);
        }
        return null;
    }

    private String abcTiebreaker(ArrayList<String> sameFrequency) {
        Collections.sort(sameFrequency);
        return sameFrequency.get(0);
    }
}


