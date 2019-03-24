package voice2text.mapper;

import org.apache.ibatis.annotations.Param;
import voice2text.entity.BrokerMessageLog;

import java.util.Date;
import java.util.List;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 15:11
 * @Description:
 */

    // @Repository  @Mapper  // 作用于持久层  已经进行扫描，不必注入
public interface BrokerMessageLogMapper {

    void insert(BrokerMessageLog brokerMessageLog);
    /**
     * 查询消息状态为0(发送中) 且已经超时的消息集合
     * @return
     */
    List<BrokerMessageLog> query4StatusAndTimeoutMessage();

    /**
     * 重新发送统计count发送次数 +1
     * @param messageId
     * @param updateTime
     */
    void update4ReSend(@Param("messageId") String messageId, @Param("updateTime") Date updateTime);
    /**
     * 更新最终消息发送结果 成功 or 失败
     * @param messageId
     * @param status
     * @param updateTime
     */
    void changeBrokerMessageLogStatus(@Param("messageId") String messageId, @Param("status") String status, @Param("updateTime") Date updateTime);


}
