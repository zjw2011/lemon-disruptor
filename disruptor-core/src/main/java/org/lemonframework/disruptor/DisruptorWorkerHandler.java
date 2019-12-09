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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.WorkHandler;

/**
 * lemon-disruptor.
 *
 * @author jiawei
 * @since 1.0.0
 */
public class DisruptorWorkerHandler implements WorkHandler<DisruptorEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DisruptorWorkerHandler.class);

    private final Executor executor;

    private final AsyncConsumer consumer;

    /**
     * Instantiates a new Route data handler.
     *
     * @param executor        the executor
     */
    public DisruptorWorkerHandler(final Executor executor, final AsyncConsumer consumer) {
        this.executor = executor;
        this.consumer = consumer;
    }

    @Override
    public void onEvent(final DisruptorEvent event) {
        final AsyncData data = event.getData();
        final int size = event.size();
        executor.execute(() -> {
            if (logger.isInfoEnabled()) {
                logger.info("Thread:{}, sequence:{}, size: {}",
                        Thread.currentThread().getName(),
                        data.getSequence(),
                        size);
            }
            consumer.fire(data);
        });
    }

}
