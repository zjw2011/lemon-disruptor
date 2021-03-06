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

/**
 * 异步事件.
 *
 * @author jiawei
 * @since 1.0.0
 */
public class DisruptorEvent {

//    private Queue<AsyncData> queue = new ConcurrentLinkedQueue<>();
//
//    public AsyncData getData() {
//        return queue.poll();
//    }
//
//    public void setData(AsyncData data) {
//        queue.add(data);
//    }
//
//    public int size() {
//        return queue.size();
//    }
//
//    public String getSimpleName() {
//        return "disruptorEvent";
//    }

    private AsyncData data;

    public AsyncData getData() {
        return this.data;
    }

    public void setData(AsyncData data) {
        this.data = data;
    }

    public int size() {
        return 0;
    }

    public void clear() {
        this.data = null;
    }

    public String getSimpleName() {
        return "disruptorEvent";
    }
}
