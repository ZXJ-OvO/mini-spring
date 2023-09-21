package com.zxj.ovo.application.eventListener;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@EnableAsync
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/order")
    public String pay(String orderNo) {
        PayEvent payEvent = PayEvent.builder()
                .code(200)
                .orderNo("No-" + orderNo)
                .userId(1)
                .build();
        applicationContext.publishEvent(payEvent);
        return "pay success";

    }
}


/**
 * 异步执行监听，发送优惠券
 */
@Component
class CardService {
    @Async
    @EventListener
    public void sendCard(PayEvent payEvent) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        System.out.println("发送优惠！" + payEvent.getOrderNo());
    }
}


/**
 * 异步执行监听，修改订单状态
 */
@Component
class OrderService {
    @Async
    @EventListener
    public void updateStatus(PayEvent payEvent) {
        System.out.println("修改订单状态！" + payEvent.getOrderNo());
    }
}


/**
 * 支付事件的普通类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PayEvent {
    //订单号
    private String orderNo;
    //支付状态
    private Integer code;
    //支付状态
    private Integer userId;
}
