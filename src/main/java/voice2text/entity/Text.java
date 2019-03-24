/**
 * 文件名: Text.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-03-17 16:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Text {
    private Integer id;

    private String bg;
    private String ed;
    private String onebest;
    private String si;
    private String speaker;
    private Integer wordsResultSum;
    private String title;
    private Date dateTime;
    private List<WordsResult> wordsResultList;


}
