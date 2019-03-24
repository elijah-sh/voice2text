package voice2text.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import voice2text.entity.Order;
import voice2text.entity.Text;
import java.io.IOException;
import java.util.List;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 15:54
 * @Description:
 */
public class FastJsonConvertUtil {

    static ObjectMapper mapper = new ObjectMapper();


    /**
     *  JSON 转 实体类
     * @param json
     * @param object
     * @return
     */
   public static Object convertJSONToObject (String json , Class<Order> object) {
       Object order = new Object();
       try {
           //JSON.toJavaObject((JSON)json,object);
             order =   mapper.readValue(json, object);
       } catch (IOException e) {
           e.printStackTrace();
       }
       return  order;
   }

    /**
     * 实体类转JSON
     * @param object
     * @return
     */
    public static String convertObjectToJSON (Object object) {

        //Convert object to JSON string
        try {
            String text = JSON.toJSONString(object);
            return text;
        } catch (Exception e){
            return null;
        }
       /* String jsonInString = mapper.writeValueAsString(object);

        //Convert object to JSON string and pretty print
        jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        System.out.println(jsonInString);
        return jsonInString;*/
    }

    public static Object convertJSONToExecResult (String json , Class<ExecResult> object) {
        ExecResult order = new ExecResult();
        try {
            //JSON.toJavaObject((JSON)json,object);
            order =   mapper.readValue(json, object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  order;
    }

    /**
     *  JSON 转 实体类
     * @param json
     * @param object
     * @return
     */
    public static Object convertJSONToText (String json , Class<Text> object) {
        Object text = new Object();
        try {
            //JSON.toJavaObject((JSON)json,object);
            text =   mapper.readValue(json, object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  text;
    }
    /**
     * json 转 List<T>
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
        return ts;
    }

}
