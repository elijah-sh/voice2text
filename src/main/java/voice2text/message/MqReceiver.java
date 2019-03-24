package voice2text.message;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import voice2text.entity.Order;

import java.util.Map;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/10/17 16:24
 * @Description:  接受消息内容
 */
//@Component
@Slf4j
public class MqReceiver {

    //  1 收到创建消息队列
    //@RabbitListener(queues = "myQueues")
    // 2 自动创建消息队列  Declare 声明
    // @RabbitListener(queuesToDeclare = @Queue("AutoQueues"))
    // 3. 自动创建 并绑定 queues与exchange
    // @RabbitListener(  bindings = @QueueBinding(
    //         value = @Queue("myQueue3"),
    //         exchange = @Exchange("myExchange")
    //     ))
    // public void process (String message){
    //     log.info("Receiver Message :  {}",message);
    // }
    //

  /*  *//**
     *  水果供应商
     * @param message
     *//*
    @RabbitListener(  bindings = @QueueBinding(
            value = @Queue("fruit-queue"),  //  消息队列名
            key = "order.fruit",            //  消息 接受exchange规则
            exchange = @Exchange(value = "order-exchange",type = "topic")  //   exchange名称
    ))
    public void computerProcess (String message){
        log.info("Receiver Fruit Message :  {}",message);
    }

    *//**
     *  数码供应商  接收
     * @param message
     *//*
    @RabbitListener(  bindings = @QueueBinding(
            value = @Queue(value = "computer-queue",durable = "true"),
            key = "order.computer",
            exchange = @Exchange(value = "order-exchange",type = "topic")
    ))
    public void fruitProcess (String message){
        log.info("Receiver Computer Message :  {}",message);
    }
*/

    /**
     *  BOSS  接收
     * @param order
     */
       @RabbitListener(  bindings = @QueueBinding(
            value = @Queue(value = "order-queue",durable = "true"),
            key = "order.*",
            exchange = @Exchange(value = "order-exchange",type = "topic")
    ))
    @RabbitHandler
    public void bossProcess (@Payload Order order,
                             @Headers Map<String,Object> headers ,
                             Channel channel ) throws Exception{
        log.info("Receiver order-boss Message :  {}",order);
        long l = (long) headers.get(AmqpHeaders.DELIVERY_TAG);

        //  ACK   自动签收 与配置呼应
          channel.basicAck(l,false); //  收到消息之后进行回复
    }
}
