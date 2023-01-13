package test;

import searchengine.util.texts.TextUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
public class Main {

    public static void main(String[] args) {


        /*try {
            LuceneMorphology luceneMorph =
                    new RussianLuceneMorphology();
            List<String> wordBaseForms =
                    luceneMorph.getNormalForms("леса");
            wordBaseForms.forEach(System.out::println);
        } catch (IOException e) {

        }


        try {
            LuceneMorphology luceneMorph =
                    new RussianLuceneMorphology();
            List<String> wordBaseForms =
                    luceneMorph.getMorphInfo("леопард");
            wordBaseForms.forEach(System.out::println);
        } catch (IOException e) {

        }*/

        String html = "Из России нет хороших новостей";

        TextUtil textUtil = new TextUtil(html);
        try {
            LinkedHashMap<String, Integer> stringIntegerLinkedHashMap = textUtil.countWords();
            System.out.println(stringIntegerLinkedHashMap);
        } catch (IOException e) {

        }

        String str = "Повторное появление леопарда в Осетии позволяет предположить, " +
                "что леопард постоянно обитает в некоторых районах Северного Кавказа.";
        /*try {
            HashMap<String, Integer> stringIntegerHashMap = countWords(str);

            System.out.println(stringIntegerHashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
