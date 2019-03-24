package voice2text.config.database;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 15:06
 * @Description:
 */
@Configuration
@AutoConfigureAfter(MybatisDataSourceConfig.class)   //  先初始化数据源
public class MybatisMapperScanerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("voice2text.mapper");   // 基础的dao操作
        return mapperScannerConfigurer;
    }

}

