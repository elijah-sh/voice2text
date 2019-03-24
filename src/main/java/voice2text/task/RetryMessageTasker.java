package voice2text.task;

import voice2text.constant.Constants;
import voice2text.entity.BrokerMessageLog;
import voice2text.entity.Order;
import voice2text.mapper.BrokerMessageLogMapper;
import voice2text.service.RabbitOrderSender;
import voice2text.utils.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 15:55
 * @Description: 定时任务 用户从新发送消息
 * 重数据库中抽取中发送消息超时的，重新投递 投递次数+1
 * 投递次数如果大于3，则放弃投递
 */
//@Component
public class RetryMessageTasker {


    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void reSend(){
        System.out.println("=============定时任务开始================");
        //pull status = 0 and timeout message  抽取出该规则
        List<BrokerMessageLog> list = brokerMessageLogMapper.query4StatusAndTimeoutMessage();
        list.forEach(messageLog -> {
            if(messageLog.getTryCount() >= 3){
                //update fail message
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageLog.getMessageId(), Constants.ORDER_SEND_FAILURE, new Date());
            } else {
                // resend  再次投递  只有执行 rabbit的业务 ， 不用新增数据
                brokerMessageLogMapper.update4ReSend(messageLog.getMessageId(),  new Date());
                Order reSendOrder = (Order) FastJsonConvertUtil.convertJSONToObject(messageLog.getMessage(),Order.class);
                try {
                    rabbitOrderSender.sendOrder(reSendOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("-----------异常处理-----------");
                }
            }
        });
    }
}
