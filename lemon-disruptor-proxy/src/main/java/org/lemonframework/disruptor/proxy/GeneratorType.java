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

/**
 * Specifies the proxy generator
 */
public enum GeneratorType
{
    /**
     * Use JDK reflection to create a proxy (creates garbage, has reflection overhead)
     */
    JDK_REFLECTION("org.lemonframework.disruptor.proxy.reflect.ReflectiveRingBufferProxyGenerator"),
    /**
     * Use a bytecode-generation library to create a proxy (garbage-free, no reflection overhead)
     */
    BYTECODE_GENERATION("org.lemonframework.disruptor.proxy.bytecode.GeneratedRingBufferProxyGenerator");

    private final String generatorClassname;

    GeneratorType(final String generatorClassName)
    {
        this.generatorClassname = generatorClassName;
    }

    public String getGeneratorClassName()
    {
        return generatorClassname;
    }
}
