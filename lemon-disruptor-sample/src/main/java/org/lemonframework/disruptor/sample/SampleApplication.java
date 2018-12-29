package org.lemonframework.disruptor.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.lemonframework.disruptor.sample.billing.message.listener.BillingMessageListener;
import org.lemonframework.disruptor.sample.datastream.message.listener.DataStreamMessageListener;

/**
 * startup.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
@SpringBootApplication
@RestController
public class SampleApplication {

    @Autowired
    private BillingMessageListener billingMessageListener;

    @Autowired
    private DataStreamMessageListener dataStreamMessageListener;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @GetMapping("/test/billing")
    public void testBilling() {
        billingMessageListener.inMessage("20");
    }

    @GetMapping("/test/datastream")
    public void testDatastream() {
        dataStreamMessageListener.inMessage("100");
    }

}
