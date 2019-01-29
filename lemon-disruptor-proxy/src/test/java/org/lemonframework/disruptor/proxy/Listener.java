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

@DisruptorProxy
public interface Listener
{
    void onString(final String value);
    void onFloatAndInt(final Float value, final int intValue);
    void onVoid();
    void onObjectArray(final Double[] value);
    void onMixedMultipleArgs(final int int0, final int int1, final String s0, final String s1, final int i2);
}
