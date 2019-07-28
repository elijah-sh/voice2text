/**
 * 文件名: LocalPersistenceMeta.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.xunfei;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-06 22:19
 */
@Component
@ConfigurationProperties(prefix = "xunfei")    //  读取 application.yml 中spring.datasource为前缀的内容
@Data
public class LocalPersistenceMeta {

    private String app_id;
    private String secret_key;
    private String signa;
    private String ts;
    private int lfasr_type;
    private int file_piece_size;
    private String task_id;
    private String file;
    private HashMap<String, String> params;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properdtyConfigure(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}
