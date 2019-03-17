package org.lemonframework.disruptor;

import com.lmax.disruptor.EventTranslator;

/**
 * Disruptor configuration settings.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public interface DisruptorConfig<T> {

    /**
     * Publish an event to the ring buffer using event translator.
     *
     * @param eventTranslator
     */
    void publish(EventTranslator<T> eventTranslator);

    /**
     * Design a Event Processor/Consumer definition.
     */
    void disruptorEventHandler();

    /**
     * Handle Disruptor exceptions
     */
    void disruptorExceptionHandler();
}
