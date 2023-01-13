package searchengine.util.texts;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

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
        for(String s : split){
            //System.out.println(s);
            if(s.isEmpty()) continue;
            List<String> wordBaseForms =
                    luceneMorph.getNormalForms(s);
            s = wordBaseForms.get(0);
            List<String> morphInfo = luceneMorph.getMorphInfo(s);
            String info = morphInfo.get(0);
            //System.out.println(info);
            if(info.split(" ")[1].equals("С")
                    || info.split(" ")[1].equals("ИНФИНИТИВ")
                    || info.split(" ")[1].equals("П")
                    || info.split(" ")[1].equals("Н")){
                int count = res.getOrDefault(s, 0);
                count++;
                res.put(s, count);
            }
        }
        return res;
    }
}
