package org.lemonframework.disruptor.sample.datastream.disruptor.publisher;

import org.lemonframework.disruptor.DisruptorConfig;
import org.lemonframework.disruptor.publisher.EventPublisher;
import org.lemonframework.disruptor.sample.datastream.disruptor.eventtranslator.DataStreamEventTranslator;
import org.lemonframework.disruptor.sample.datastream.model.DataStream;

public class DataStreamEventPublisher implements EventPublisher<DataStream> {

    private DisruptorConfig<DataStream> disruptorConfig;

    @Override
    public void publish(DataStream dataStream) {
        disruptorConfig.publish(new DataStreamEventTranslator(dataStream));
    }

    @Override
    public void setDisruptorConfig(DisruptorConfig<DataStream> disruptorConfig) {
        this.disruptorConfig = disruptorConfig;
    }

}