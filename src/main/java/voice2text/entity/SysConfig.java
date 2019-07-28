/**
 * 文件名: SysConfig.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-06 22:35
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysConfig {
    private Integer id;
    private String key;
    private String value;
    private String des;
}
