package org.lemonframework.disruptor.publisher;

import org.lemonframework.disruptor.DisruptorConfig;

/**
 * Ring buffer publisher should use this interface.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public interface EventPublisher<T> {

    /**
     * Publish to the ring buffer. Use a Event Translator.
     *
     * @param t
     */
    void publish(T t);

    /**
     * Set the DisruptorConfig spring bean.
     *
     * @param disruptorConfig
     */
    void setDisruptorConfig(DisruptorConfig<T> disruptorConfig);
}
