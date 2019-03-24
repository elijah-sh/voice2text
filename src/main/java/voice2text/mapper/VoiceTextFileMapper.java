package voice2text.mapper;

import voice2text.entity.VoiceTextFile;

import java.util.List;

public interface VoiceTextFileMapper {
    //int deleteByPrimaryKey(Integer id);

    int insert(VoiceTextFile record);

    VoiceTextFile selectByPrimaryKey(Integer id);

    List<VoiceTextFile> selectAll();

    int updateByPrimaryKey(VoiceTextFile record);
}