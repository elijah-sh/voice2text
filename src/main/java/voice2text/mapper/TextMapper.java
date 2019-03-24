package voice2text.mapper;


import org.apache.ibatis.annotations.Mapper;
import voice2text.entity.Order;
import voice2text.entity.Text;

import java.util.List;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 15:14
 * @Description:
 */
@Mapper
public interface TextMapper {

    Integer insert(Text text);

    void batchInsert(List<Text> texts);

}
