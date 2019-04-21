/**
 * 文件名: PropConfig.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.xunfei;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-06 22:15
 */
import com.iflytek.msp.cpdb.lfasr.exception.ConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import voice2text.entity.SysConfig;
import voice2text.mapper.SysConfigMapper;

import javax.validation.constraints.Max;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;


public class PropConfig {
    public PropConfig() {
    }

    @Autowired
    private SysConfigMapper mapper;

    public static   Map<String, String> LoadPoperties(String filePath) throws ConfigException {

        //  原逻辑读取配置文件 ， 现改为读取数据库

        List<SysConfig> configs = new ArrayList<>();
       // configs = mapper.select();


        Map<String, String> poperties = new HashMap();

        for (SysConfig sysConfig : configs) {
            poperties.put(sysConfig.getKey(), sysConfig.getValue());
        }
        return poperties;
       /*
        if (filePath != null && !"".equals(filePath.trim())) {
            filePath = filePath.trim();
            InputStream is = PropConfig.class.getClassLoader().getResourceAsStream(filePath);
            Properties prop = new Properties();

            try {
                prop.load(is);
            } catch (IOException var9) {
                throw new ConfigException("The config file load ioexception");
            } catch (Exception var10) {
                throw new ConfigException("The config file load exception");
            }

            Set<Entry<Object, Object>> set = prop.entrySet();
            Iterator<Entry<Object, Object>> it = set.iterator();
            String key = null;
            String value = null;

            while(it.hasNext()) {
                Entry<Object, Object> entry = (Entry)it.next();
                key = String.valueOf(entry.getKey());
                value = String.valueOf(entry.getValue());
                key = key == null ? key : key.trim();
                value = value == null ? value : value.trim();
                poperties.put(key, value);
            }

            return poperties;
        } else {
            throw new ConfigException("The config file path is null");
        }*/
    }
}

