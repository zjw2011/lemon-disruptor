package org.lemonframework.disruptor.sample.billing.disruptor.publisher;

import org.lemonframework.disruptor.DefaultDisruptorConfig;
import org.lemonframework.disruptor.DisruptorConfig;
import org.lemonframework.disruptor.publisher.EventPublisher;
import org.lemonframework.disruptor.sample.billing.disruptor.eventtranslator.BillingEventTranslator;
import org.lemonframework.disruptor.sample.billing.model.BillingRecord;

/**
 * billing event publisher.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public class BillingEventPublisher implements EventPublisher<BillingRecord> {

    private DisruptorConfig<BillingRecord> disruptorConfig;

    @Override
    public void publish(BillingRecord billingRecord){
        DefaultDisruptorConfig<BillingRecord> defaultDisruptorConfig = (DefaultDisruptorConfig<BillingRecord>) disruptorConfig;
//        defaultDisruptorConfig.setEventHandlerChain();
        disruptorConfig.publish(new BillingEventTranslator(billingRecord));
    }

    @Override
    public void setDisruptorConfig(DisruptorConfig<BillingRecord> disruptorConfig) {
        this.disruptorConfig = disruptorConfig;
    }

}
