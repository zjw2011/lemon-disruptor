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

package org.lemonframework.disruptor.proxy.handlers;

import com.lmax.disruptor.EventHandler;
import org.lemonframework.disruptor.proxy.ProxyMethodInvocation;

/**
 * A Disruptor EventHandler that will reset the ring-buffer entry
 */
public final class ResetHandler implements EventHandler<ProxyMethodInvocation>
{
    @Override
    public void onEvent(final ProxyMethodInvocation event, final long sequence, final boolean endOfBatch) throws Exception
    {
        event.reset();
    }
}
