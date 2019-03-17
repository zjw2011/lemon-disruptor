package org.lemonframework.disruptor.sample.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmax.disruptor.EventHandler;
import org.lemonframework.disruptor.DefaultDisruptorConfig;
import org.lemonframework.disruptor.EventHandlerChain;
import org.lemonframework.disruptor.sample.datastream.disruptor.eventfactory.DataStreamEvent;
import org.lemonframework.disruptor.sample.datastream.disruptor.eventprocessor.FormatDataStreamEventProcessor;
import org.lemonframework.disruptor.sample.datastream.disruptor.eventprocessor.JournalDataStreamEventProcessor;
import org.lemonframework.disruptor.sample.datastream.disruptor.eventprocessor.ProcessADataStreamEventProcessor;
import org.lemonframework.disruptor.sample.datastream.disruptor.eventprocessor.ProcessBDataStreamEventProcessor;
import org.lemonframework.disruptor.sample.datastream.disruptor.publisher.DataStreamEventPublisher;
import org.lemonframework.disruptor.sample.datastream.model.DataStream;
import org.lemonframework.disruptor.sample.datastream.service.DatastreamService;
import org.lemonframework.disruptor.sample.datastream.service.DatastreamServiceImpl;

/**
 * datastream config.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
@Configuration
public class DatastreamDisruptorConfig {

    @Bean
    public DatastreamService datastreamService() {
        return new DatastreamServiceImpl();
    }

    @Bean
    public JournalDataStreamEventProcessor journalDataStreamEventProcessor() {
        JournalDataStreamEventProcessor journalDataStreamEventProcessor = new JournalDataStreamEventProcessor();
        journalDataStreamEventProcessor.setDataStreamService(datastreamService());
        return journalDataStreamEventProcessor;
    }

    @Bean
    public ProcessADataStreamEventProcessor processADataStreamEventProcessor() {
        ProcessADataStreamEventProcessor processADataStreamEventProcessor = new ProcessADataStreamEventProcessor();
        processADataStreamEventProcessor.setDataStreamService(datastreamService());
        return processADataStreamEventProcessor;
    }

    @Bean
    public ProcessBDataStreamEventProcessor processBDataStreamEventProcessor() {
        ProcessBDataStreamEventProcessor processBDataStreamEventProcessor = new ProcessBDataStreamEventProcessor();
        processBDataStreamEventProcessor.setDataStreamService(datastreamService());
        return processBDataStreamEventProcessor;
    }

    @Bean
    public FormatDataStreamEventProcessor formatDataStreamEventProcessor() {
        FormatDataStreamEventProcessor formatDataStreamEventProcessor = new FormatDataStreamEventProcessor();
        formatDataStreamEventProcessor.setDataStreamService(datastreamService());
        return formatDataStreamEventProcessor;
    }

    @Bean(initMethod = "init", destroyMethod = "controlledShutdown")
    public DefaultDisruptorConfig<DataStream> dataStreamDisruptor() {
        DefaultDisruptorConfig<DataStream> defaultDisruptorConfig = new DefaultDisruptorConfig<>();
        defaultDisruptorConfig.setThreadName("dataStreamThread");
        DataStreamEvent dataStreamEvent = new DataStreamEvent();
        defaultDisruptorConfig.setEventFactory(dataStreamEvent);

        final EventHandler[] currentEventHandlers = {
                journalDataStreamEventProcessor(),
        };

        final EventHandler[] nextEventHandlers = {
                processADataStreamEventProcessor(),
                processBDataStreamEventProcessor()
        };

        EventHandlerChain<DataStream> eventHandlerChain = new EventHandlerChain<>(currentEventHandlers, nextEventHandlers);

        final EventHandler[] currentEventHandlers2 = {
                processADataStreamEventProcessor(),
                processBDataStreamEventProcessor()
        };

        final EventHandler[] nextEventHandlers2 = {
                formatDataStreamEventProcessor()
        };

        EventHandlerChain<DataStream> eventHandlerChain2 = new EventHandlerChain<>(currentEventHandlers2, nextEventHandlers2);

        defaultDisruptorConfig.setEventHandlerChain(new EventHandlerChain[] {eventHandlerChain, eventHandlerChain2});

        return defaultDisruptorConfig;
    }

    @Bean
    @ConditionalOnBean(name = "dataStreamDisruptor")
    public DataStreamEventPublisher dataStreamEventPublisher() {
        DataStreamEventPublisher dataStreamEventPublisher = new DataStreamEventPublisher();
        dataStreamEventPublisher.setDisruptorConfig(dataStreamDisruptor());
        return dataStreamEventPublisher;
    }

}
