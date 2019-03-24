package voice2text.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import voice2text.entity.Order;
import voice2text.service.OrderService;
import voice2text.utils.KeyUtil;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/2 14:46
 * @Description:
 */
@RestController
public class SendMessageController {


    @Autowired
    private OrderService orderService;

    @RequestMapping("/sendMessages")
    public Order testCreateOrder() throws Exception {
        Order order = new Order();
        order.setId(KeyUtil.genUniqueKeyOrderId());
        order.setName("测试创建订单");
        order.setMessageId(KeyUtil.genUniqueKeyMessageId());
        orderService.createOrder(order);
        return order;
    }

}
