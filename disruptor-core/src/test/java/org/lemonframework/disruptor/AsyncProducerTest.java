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

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;

/**
 * lemon-disruptor.
 *
 * @author jiawei
 * @since 1.0.0
 */
public class AsyncProducerTest {

    @Test
    public void send() {
        final AsyncProducer producer = AsyncProducer.builder()
                        .setAutoDestroy(true)
                        .setConsumerCount(60)
//                        .setBufferSize(1024 * 1024)
                        .setConsumer(new MyConsumer())
                        .setGlobalQueue(false)
                        .build();

//        IntStream.range(0, 100000)
        IntStream.range(0, 100)
                .parallel()
                .forEach(i -> {
                    final MyData myData = new MyData();
                    myData.setName("zhangsan:" + String.valueOf(i));
                    producer.send(myData);
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                });
//
//        try {
//            TimeUnit.HOURS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public static class MyData extends AsyncData {

        private String name;

        public String getName() {
            return name;
        }

        public MyData setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class MyConsumer implements AsyncConsumer {
        private static final CountDownLatch COUNT = new CountDownLatch(100000);
        private static final Logger log = LoggerFactory.getLogger(MyConsumer.class);

        @Override
        public void fire(AsyncData asyncData) {
            final MyData data = (MyData) asyncData;
            if (Objects.isNull(data)) {
                log.warn("consumer->" + Thread.currentThread().getName() + ":" + "data is null");
                return;
            }
            COUNT.countDown();
            log.info("consumer-> Thread:{}, data:{}, count: {}", Thread.currentThread().getName(), data.getName(), COUNT.getCount());
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
