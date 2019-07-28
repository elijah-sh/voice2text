package voice2text.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/12/13 19:28
 * @Description: 读取配置文件
 */
@Component
@Getter
//@Profile("dev")
public class PathConfig {

    @Value("${path.voice}")
    private String voicePath;

    @Value("${path.tmp}")
    private String tmpPath;

    @Value("${path.text}")
    private String textPath;
}
