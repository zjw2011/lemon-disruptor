package org.lemonframework.disruptor.sample.billing.disruptor.eventfactory;

import com.lmax.disruptor.EventFactory;
import org.lemonframework.disruptor.sample.billing.model.BillingRecord;

/**
 * event factory.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public class BillingEvent implements EventFactory<BillingRecord> {
    @Override
    public BillingRecord newInstance() {
        return new BillingRecord();
    }
}
