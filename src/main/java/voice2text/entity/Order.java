package voice2text.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 14:06
 * @Description: 实体对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order implements Serializable {

    private static final long serialVersionUID = -8274169358168672740L;

    private String id;

    private String name;

    private String messageId;

}
