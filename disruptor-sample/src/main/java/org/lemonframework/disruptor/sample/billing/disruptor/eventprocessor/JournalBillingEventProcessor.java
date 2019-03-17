package org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.lmax.disruptor.EventHandler;
import org.lemonframework.disruptor.sample.billing.model.BillingRecord;
import org.lemonframework.disruptor.sample.billing.service.BillingService;

/**
 * journal billing.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public class JournalBillingEventProcessor implements EventHandler<BillingRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(JournalBillingEventProcessor.class);
    private BillingService billingService;

    @Override
    public void onEvent(BillingRecord billingRecord, long sequence, boolean endOfBatch) throws Exception {
        LOG.trace("Sequence: {}. Going to process {}",sequence, billingRecord.toString());
        billingService.journalBillingRecord(billingRecord);
        if(sequence%10==0){
            LOG.info("Sequence: {}. {}",sequence, billingRecord.toString());
        }
    }

    @Required
    public void setBillingService(BillingService billingService) {
        this.billingService = billingService;
    }
}
