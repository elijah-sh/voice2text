package com;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import voice2text.entity.Text;
import voice2text.utils.FastJsonConvertUtil;
import voice2text.utils.FileUtils;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Voice2TextApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void jsonToObject() {

        FileUtils  fileUtils = new FileUtils();
       String jsonString = fileUtils.ReadFile("json/test.json");
        List<Text> text =  FastJsonConvertUtil.jsonToList(jsonString, Text.class);

        System.out.println(text.toString());
    }

}
