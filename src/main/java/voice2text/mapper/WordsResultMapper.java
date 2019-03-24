package voice2text.mapper;


import org.apache.ibatis.annotations.Mapper;
import voice2text.entity.Text;
import voice2text.entity.WordsResult;

import java.util.List;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 15:14
 * @Description:
 */
@Mapper
public interface WordsResultMapper {

   void insert(WordsResult wordsResult);

   void batchInsert(List<WordsResult> wordsResults);

   void batchUpdate(List<WordsResult> wordsResults);

}
