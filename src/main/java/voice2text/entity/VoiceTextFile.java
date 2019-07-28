package voice2text.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/12/6 17:10
 * @Description:  用于上传时文件的实体类  数据库不做存储
 */
@ApiModel("文件的实体类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VoiceTextFile  {

    private String id;

    /**
     * 文件原名称
     */
    @ApiModelProperty("文件原名称")
    private String titleName;

    /**
     * 文件数据库保存的名称
     */
    private String textName;


    /**
     * 文件数据库保存的名称
     */
    private String voiceName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件路径
     */
    private String voicePath;

    /**
     * 文件路径
     */
    private String textPath;
    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 数据总数
     */
    private Long dataCount;

    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 处理时间
     */
    private Date updateDate;

}
