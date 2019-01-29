/*
 * Copyright 2015-2016 LMAX Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.lemonframework.disruptor.proxy;

import com.lmax.disruptor.EventFactory;

/**
 * A Disruptor EventFactory used to create ring-buffer entries
 */
public final class RingBufferProxyEventFactory implements EventFactory<ProxyMethodInvocation>
{
    public static final EventFactory<ProxyMethodInvocation> FACTORY = new RingBufferProxyEventFactory();

    @Override
    public ProxyMethodInvocation newInstance()
    {
        return new ProxyMethodInvocation();
    }
}
