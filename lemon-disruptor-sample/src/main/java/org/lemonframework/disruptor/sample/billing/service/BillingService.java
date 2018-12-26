package org.lemonframework.disruptor.sample.billing.service;

import org.lemonframework.disruptor.sample.billing.model.BillingRecord;

/**
 * interface billing service.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public interface BillingService {
    long delay = 999999L;

    void validateBillingRecord(BillingRecord billingRecord);

    void formatBillingRecord(BillingRecord billingRecord);

    void journalBillingRecord(BillingRecord billingRecord);

    void processBillingRecord(BillingRecord billingRecord);

    void processCorporateBillingRecord(BillingRecord billingRecord);

    void processCustomerSpecificBillingRecord(BillingRecord billingRecord);
}
