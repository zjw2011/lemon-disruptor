package org.lemonframework.disruptor.sample.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.lmax.disruptor.EventHandler;
import org.lemonframework.disruptor.DefaultDisruptorConfig;
import org.lemonframework.disruptor.EventHandlerChain;
import org.lemonframework.disruptor.sample.Animal;
import org.lemonframework.disruptor.sample.Person;
import org.lemonframework.disruptor.sample.User;
import org.lemonframework.disruptor.sample.billing.disruptor.eventfactory.BillingEvent;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.BillingBusinessEventProcessor;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.BillingOutboundFormattingEventProcessor;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.BillingValidationEventProcessor;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.CorporateBillingBusinessEventProcessor;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.CustomerSpecificBillingBusinessEventProcessor;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.JournalBillingEventProcessor;
import org.lemonframework.disruptor.sample.billing.disruptor.publisher.BillingEventPublisher;
import org.lemonframework.disruptor.sample.billing.model.BillingRecord;
import org.lemonframework.disruptor.sample.billing.service.BillingService;
import org.lemonframework.disruptor.sample.billing.service.BillingServiceImpl;

/**
 * billing config.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
@Configuration
public class BillingDisruptorConfig {

    @Bean
    public BillingService billingService() {
        return new BillingServiceImpl();
    }

    @Bean
    public JournalBillingEventProcessor journalBillingEventProcessor() {
        final JournalBillingEventProcessor journalBillingEventProcessor = new JournalBillingEventProcessor();
        journalBillingEventProcessor.setBillingService(billingService());
        return journalBillingEventProcessor;
    }

    @Bean
    public BillingValidationEventProcessor billingValidationEventProcessor() {
        final BillingValidationEventProcessor billingValidationEventProcessor = new BillingValidationEventProcessor();
        billingValidationEventProcessor.setBillingService(billingService());
        return billingValidationEventProcessor;
    }

    @Bean
    public BillingBusinessEventProcessor billingBusinessEventProcessor() {
        final BillingBusinessEventProcessor billingBusinessEventProcessor = new BillingBusinessEventProcessor();
        billingBusinessEventProcessor.setBillingService(billingService());
        return billingBusinessEventProcessor;
    }

    @Bean
    public CorporateBillingBusinessEventProcessor corporateBillingBusinessEventProcessor() {
        final CorporateBillingBusinessEventProcessor corporateBillingBusinessEventProcessor = new CorporateBillingBusinessEventProcessor();
        corporateBillingBusinessEventProcessor.setBillingService(billingService());
        return corporateBillingBusinessEventProcessor;
    }

    @Bean
    public CustomerSpecificBillingBusinessEventProcessor customerSpecificBillingBusinessEventProcessor() {
        final CustomerSpecificBillingBusinessEventProcessor customerSpecificBillingBusinessEventProcessor = new CustomerSpecificBillingBusinessEventProcessor();
        customerSpecificBillingBusinessEventProcessor.setBillingService(billingService());
        return customerSpecificBillingBusinessEventProcessor;
    }

    @Bean
    public BillingOutboundFormattingEventProcessor billingOutboundFormattingEventProcessor() {
        final BillingOutboundFormattingEventProcessor billingOutboundFormattingEventProcessor = new BillingOutboundFormattingEventProcessor();
        billingOutboundFormattingEventProcessor.setBillingService(billingService());
        return billingOutboundFormattingEventProcessor;
    }

    @Bean(initMethod = "init", destroyMethod = "controlledShutdown")
    public DefaultDisruptorConfig<BillingRecord> billingDisruptor() {
        DefaultDisruptorConfig<BillingRecord> defaultDisruptorConfig = new DefaultDisruptorConfig<>();
        defaultDisruptorConfig.setThreadName("billingThread");
        BillingEvent billingEvent = new BillingEvent();
        defaultDisruptorConfig.setEventFactory(billingEvent);

        final EventHandler[] currentEventHandlers = {
                journalBillingEventProcessor(),
                billingValidationEventProcessor()
        };

        final EventHandler[] nextEventHandlers = {
                billingBusinessEventProcessor(),
                corporateBillingBusinessEventProcessor(),
                customerSpecificBillingBusinessEventProcessor()
        };



        final EventHandler[] nextEventHandlers2 = {
                billingOutboundFormattingEventProcessor()
        };

        //每次都要创建新的bean
        EventHandlerChain<BillingRecord> eventHandlerChain = new EventHandlerChain<>(currentEventHandlers, nextEventHandlers);
        EventHandlerChain<BillingRecord> eventHandlerChain2 = new EventHandlerChain<>(nextEventHandlers, nextEventHandlers2);

        defaultDisruptorConfig.setEventHandlerChain(new EventHandlerChain[] {eventHandlerChain, eventHandlerChain2});

        return defaultDisruptorConfig;
    }

    @Bean
    @Scope("prototype")
    public EventHandlerChain<BillingRecord> eventHandlerChain() {
        final EventHandler[] currentEventHandlers = {
                journalBillingEventProcessor(),
                billingValidationEventProcessor()
        };
        final EventHandler[] nextEventHandlers = {
                billingBusinessEventProcessor(),
                corporateBillingBusinessEventProcessor(),
                customerSpecificBillingBusinessEventProcessor()
        };
        EventHandlerChain<BillingRecord> eventHandlerChain = new EventHandlerChain<>(currentEventHandlers, nextEventHandlers);
        return eventHandlerChain;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public EventHandlerChain<BillingRecord> eventHandlerChain2() {
        final EventHandler[] nextEventHandlers = {
                billingBusinessEventProcessor(),
                corporateBillingBusinessEventProcessor(),
                customerSpecificBillingBusinessEventProcessor()
        };
        final EventHandler[] nextEventHandlers2 = {
                billingOutboundFormattingEventProcessor()
        };
        EventHandlerChain<BillingRecord> eventHandlerChain = new EventHandlerChain<>(nextEventHandlers, nextEventHandlers2);
        return eventHandlerChain;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public User user() {
        return new User();
    }

    @Bean
    public Person person() {
        Person p = new Person();
        p.setUser(user());
        return p;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Animal animal() {
        return new Animal();
    }

    @Bean
    @ConditionalOnBean(name = "billingDisruptor")
    public BillingEventPublisher billingEventPublisher() {
        BillingEventPublisher billingEventPublisher = new BillingEventPublisher();
        billingEventPublisher.setDisruptorConfig(billingDisruptor());
        return billingEventPublisher;
    }

}
