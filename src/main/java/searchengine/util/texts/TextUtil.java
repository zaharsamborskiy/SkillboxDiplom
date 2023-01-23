package searchengine.util.texts;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringJoiner;

public class TextUtil {
    private String str;

    public TextUtil(String html) {
        this.str = html2text(html);
    }

    public String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    public LinkedHashMap<String, Integer> countWords() throws IOException {
        str = str.toLowerCase();
        str = str.replaceAll("[^А-Яа-я ]", "");
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        String[] split = str.split(" ");
        for (String s : split) {
            if (s.isEmpty()) continue;
            List<String> wordBaseForms =
                    luceneMorph.getNormalForms(s);
            s = wordBaseForms.get(0);
            List<String> morphInfo = luceneMorph.getMorphInfo(s);
            String info = morphInfo.get(0);
            if (info.split(" ")[1].equals("С")
                    || info.split(" ")[1].equals("ИНФИНИТИВ")
                    || info.split(" ")[1].equals("П")
                    || info.split(" ")[1].equals("Н")) {
                int count = res.getOrDefault(s, 0);
                count++;
                res.put(s, count);
            }
        }
        return res;
    }

    public static String getSnippet(String line, String word) {
        int start = line.indexOf(word);
        if (start == -1)
            return "";
        int s = start - 10;
        if (s < 0)
            s = 0;
        int end = start + word.length() + 10;
        if (end > line.length())
            end = line.length();
        return line.substring(s, start) + "<b>" + word + "</b>"
                + line.substring(start + word.length(), end);
    }

    public static String getSnippets(String line, List<String> words) {
        StringJoiner stringJoiner = new StringJoiner("... ");
        for (String word : words) {
            String snippet = getSnippet(line, word);
            if (!snippet.equals(""))
                stringJoiner.add(snippet);
        }
        return stringJoiner.toString();
    }
}
