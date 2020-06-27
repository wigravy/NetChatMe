package source.client;

import java.util.Map;
import java.util.TreeMap;

public class CensoredWords {
    private static final Map<String, String> censoredWords = new TreeMap<>();
    {
        censoredWords.put("россия", "Россия");
        censoredWords.put("дурак", "д***к");
        censoredWords.put("мудак", "м***к");
        censoredWords.put("ксяоми", "сяоми");
    }

    public Map getCensoredWords() {
        return censoredWords;
    }
}
