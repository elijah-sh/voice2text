/**
 * 文件名: ConfigService.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import voice2text.entity.SysConfig;
import voice2text.mapper.SysConfigMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-23 14:47
 */
@Service
public class SysConfigService {

    @Autowired
    private SysConfigMapper mapper;

    public List<SysConfig> getSysConfigData() {
        List<SysConfig> configs = mapper.select();
        return configs;
    }

}
