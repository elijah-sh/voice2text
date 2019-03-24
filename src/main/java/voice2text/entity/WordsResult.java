/**
 * 文件名: WordsResult.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-03-17 16:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WordsResult {
    private Integer id;
    private String wc;
    private String wordBg;
    private String wordEd;
    private String wordsName;
    private String wp;
    private String alterNative;
    private Integer textId;
    private Date dateTime;

}
