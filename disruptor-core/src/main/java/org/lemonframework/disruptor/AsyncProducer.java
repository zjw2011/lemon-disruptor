/*
 * Copyright 2017-2019 Lemonframework Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.lemonframework.disruptor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.lemonframework.commons.Disposable;
import org.lemonframework.commons.LemonThreadFactory;
import org.lemonframework.commons.MixUtil;
import org.lemonframework.commons.ShutdownHook;

/**
 * 异步生产者.
 *
 * @author jiawei
 * @since 1.0.0
 */
public class AsyncProducer implements Disposable {

    private static final Logger logger = LoggerFactory.getLogger(AsyncProducer.class);

    private final ExecutorService executorService;

    private final Disruptor<DisruptorEvent> disruptor;

    private final long coolingDownPeriod;
    private volatile boolean started = true;
    private volatile boolean disruptorShutDown = false;

    public static Builder builder() {
        return new Builder();
    }

    protected AsyncProducer(Builder builder) {

        builder.validate();

        Executor executor = builder.executor;
        if (executor == null) {
            //优化
            executorService = MixUtil.createFixThreadPool(builder.threadPoolSize,
                    builder.consumerName);
            executor = executorService;
        } else {
            executorService = null;
        }

        coolingDownPeriod = builder.coolingDownPeriod;

        disruptor = new Disruptor<>(() -> new DisruptorEvent(),
                builder.bufferSize,
                new LemonThreadFactory(builder.consumerName),
                builder.producerType,
                builder.waitStrategy);

        if (builder.autoDestroy) {
            ShutdownHook.getInstance().addDisposable(this);
        }

        // Configure invoker Threads
        final DisruptorWorkerHandler[] handlers = new DisruptorWorkerHandler[builder.consumerCount];
        for (int i = 0; i < handlers.length; i++) {
            handlers[i] = new DisruptorWorkerHandler(executor, builder.consumer);
        }

        disruptor.setDefaultExceptionHandler(new ExceptionHandler());
        disruptor.handleEventsWithWorkerPool(handlers);
        disruptor.start();
    }

    /**
     * send disruptor data.
     *
     * @param data 用户数据.
     */
    public void send(final AsyncData data) {
        final RingBuffer<DisruptorEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new DisruptorEventTranslator(), data);
    }

    public void stop() {
        if (!started) {
            return;
        }
        started = false;
        long lastChangeDetected = System.currentTimeMillis();
        long lastKnownCursor = disruptor.getRingBuffer().getCursor();
        while (System.currentTimeMillis() - lastChangeDetected < coolingDownPeriod && !Thread.interrupted()) {
            if (disruptor.getRingBuffer().getCursor() != lastKnownCursor) {
                lastChangeDetected = System.currentTimeMillis();
                lastKnownCursor = disruptor.getRingBuffer().getCursor();
            }
        }
        disruptorShutDown = true;
        disruptor.shutdown();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void destroy() {
        stop();
    }

    public static class Builder {

        private static final int DEFAULT_BUFFER_SIZE = 4096;

        private Executor executor;
        private String consumerName = "disruptor";
        private long coolingDownPeriod = 1000;
        private int bufferSize = DEFAULT_BUFFER_SIZE;
        private ProducerType producerType = ProducerType.MULTI;
        private WaitStrategy waitStrategy = new BlockingWaitStrategy();
        private int consumerCount = 1;
        private int threadPoolSize = 16;
        private AsyncConsumer consumer = data -> {
            throw new RuntimeException("Not Config Asynchronous Conusmer!!");
        };
        private boolean autoDestroy = true;

        public Builder setAutoDestroy(boolean autoDestroy) {
            this.autoDestroy = autoDestroy;
            return this;
        }

        public Builder setThreadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder setConsumerCount(int consumerCount) {
            this.consumerCount = consumerCount;
            return this;
        }

        public Builder setConsumerName(String consumerName) {
            this.consumerName = consumerName;
            return this;
        }

        public Builder setCoolingDownPeriod(long coolingDownPeriod) {
            this.coolingDownPeriod = coolingDownPeriod;
            return this;
        }

        public Builder setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setProducerType(ProducerType producerType) {
            this.producerType = producerType;
            return this;
        }

        public Builder setWaitStrategy(WaitStrategy waitStrategy) {
            this.waitStrategy = waitStrategy;
            return this;
        }

        public Builder setConsumer(AsyncConsumer consumer) {
            this.consumer = consumer;
            return this;
        }

        /**
         * Initializes a {@link AsyncProducer} as specified through this Builder.
         *
         * @return a {@link AsyncProducer} as specified through this Builder
         */
        public AsyncProducer build() {
            return new AsyncProducer(this);
        }

        /**
         * Validate whether the fields contained in this Builder as set accordingly.
         *
         * @throws if one field is asserted to be incorrect according to the Builder's
         *                                    specifications
         */
        protected void validate() {

        }
    }

    private class ExceptionHandler implements com.lmax.disruptor.ExceptionHandler {

        @Override
        public void handleEventException(Throwable ex, long sequence, Object event) {
            logger.error("Exception occurred while processing a {}.",
                    ((DisruptorEvent) event).getSimpleName(), ex);
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            logger.error("Failed to start the DisruptorCommandBus.", ex);
            disruptor.shutdown();
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            logger.error("Error while shutting down the DisruptorCommandBus", ex);
        }
    }
}