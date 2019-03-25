package voice2text.utils;

import voice2text.entity.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FastJsonConvertUtilTest {
    private static final String local_file = "E:/2.lfasr/1.json";

    public static void main(String[] args) {
        FileUtils  fileUtils = new FileUtils();
        String jsonString = fileUtils.ReadFile(local_file);
        List<Text> textList =  FastJsonConvertUtil.jsonToList(jsonString, Text.class);

        /*for (Text text : textList ) {
            System.out.println(text.toString());
            System.out.println(text.getWordsResultList().toString());
        }*/
       String a = textList.stream().map(e -> e.getOnebest()).collect(Collectors.joining()).toString();
        System.out.println(a.length() );
        File fp = new File("E:/2.lfasr/0317.txt");
        PrintWriter pfp = null;
        try {
            pfp = new PrintWriter(fp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pfp.print(a);
        pfp.close();

    }
}