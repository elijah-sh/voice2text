package voice2text.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 14:19
 * @Description:  消息日志类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
//@Table(name = "mq_broker_message_log")
public class BrokerMessageLog  {

    private String messageId;

    private String message;

    private Integer tryCount = 0 ;

    private String status;

    private Date nextRetry;

    private Date createTime;

    private Date updateTime;

    /*public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
*/

}
