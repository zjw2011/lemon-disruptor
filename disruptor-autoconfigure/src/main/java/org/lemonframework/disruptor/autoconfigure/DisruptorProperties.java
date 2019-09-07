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

package org.lemonframework.disruptor.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DisruptorProperties.
 *
 * @author jiawei
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = DisruptorProperties.DISRUPTOR_PREFIX)
public class DisruptorProperties {

    public static final String DISRUPTOR_PREFIX = "lemon.disruptor";

    private int bufferSize = 4096;

    private int consumerSize = 1;

    private int threadPoolSize = 16;

    private String consumerName = "lemon";

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getConsumerSize() {
        return consumerSize;
    }

    public void setConsumerSize(int consumerSize) {
        this.consumerSize = consumerSize;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }
}
