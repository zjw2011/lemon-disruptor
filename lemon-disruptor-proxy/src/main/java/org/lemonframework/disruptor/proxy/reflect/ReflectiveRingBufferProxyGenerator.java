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

package org.lemonframework.disruptor.proxy.reflect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.lemonframework.disruptor.proxy.DropListener;
import org.lemonframework.disruptor.proxy.Invoker;
import org.lemonframework.disruptor.proxy.MessagePublicationListener;
import org.lemonframework.disruptor.proxy.NoMessagePublicationListener;
import org.lemonframework.disruptor.proxy.NoOpDropListener;
import org.lemonframework.disruptor.proxy.OverflowStrategy;
import org.lemonframework.disruptor.proxy.ProxyMethodInvocation;
import org.lemonframework.disruptor.proxy.RingBufferProxyGenerator;
import org.lemonframework.disruptor.proxy.RingBufferProxyValidation;
import org.lemonframework.disruptor.proxy.handlers.ResetHandler;

import static java.lang.Thread.currentThread;
import static java.lang.reflect.Proxy.newProxyInstance;
import static org.lemonframework.disruptor.proxy.handlers.Handlers.createMultipleImplementationHandlerChain;
import static org.lemonframework.disruptor.proxy.handlers.Handlers.createSingleImplementationHandlerChain;

/**
 * {@inheritDoc}
 */
public final class ReflectiveRingBufferProxyGenerator implements RingBufferProxyGenerator
{
    private final RingBufferProxyValidation validator;
    private final DropListener dropListener;
    private final MessagePublicationListener messagePublicationListener;

    public ReflectiveRingBufferProxyGenerator(final RingBufferProxyValidation validator)
    {
        this(validator, NoOpDropListener.INSTANCE, NoMessagePublicationListener.INSTANCE);
    }

    public ReflectiveRingBufferProxyGenerator(final RingBufferProxyValidation validator,
                                              final DropListener dropListener)
    {
        this(validator, dropListener, NoMessagePublicationListener.INSTANCE);
    }

    public ReflectiveRingBufferProxyGenerator(RingBufferProxyValidation validator, DropListener dropListener, MessagePublicationListener messagePublicationListener)
    {
        this.validator = validator;
        this.dropListener = dropListener;
        this.messagePublicationListener = messagePublicationListener;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T createRingBufferProxy(final Class<T> proxyInterface, final Disruptor<ProxyMethodInvocation> disruptor,
                                       final OverflowStrategy overflowStrategy, final T implementation)
    {
        validator.validateAll(disruptor, proxyInterface);

        final ReflectiveRingBufferInvocationHandler invocationHandler =
                createInvocationHandler(proxyInterface, disruptor, overflowStrategy, dropListener,
                        messagePublicationListener);
        preallocateArgumentHolders(disruptor.getRingBuffer());

        disruptor.handleEventsWith(createSingleImplementationHandlerChain(implementation));

        return generateProxy(proxyInterface, invocationHandler);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T createRingBufferProxy(final Class<T> proxyInterface, final Disruptor<ProxyMethodInvocation> disruptor,
                                       final OverflowStrategy overflowStrategy, final T... implementations)
    {
        validator.validateAll(disruptor, proxyInterface);

        if (implementations.length < 1)
        {
            throw new IllegalArgumentException("Must have at least one implementation");
        }
        else if (implementations.length == 1)
        {
            return createRingBufferProxy(proxyInterface, disruptor, overflowStrategy, implementations[0]);
        }

        final ReflectiveRingBufferInvocationHandler invocationHandler =
                createInvocationHandler(proxyInterface, disruptor, overflowStrategy, dropListener, messagePublicationListener);
        preallocateArgumentHolders(disruptor.getRingBuffer());

        final EventHandler<ProxyMethodInvocation>[] handlers = new EventHandler[implementations.length];
        for (int i = 0; i < implementations.length; i++)
        {
            handlers[i] = createMultipleImplementationHandlerChain(implementations[i]);
            disruptor.handleEventsWith(handlers[i]);
        }
        disruptor.after(handlers).then(new ResetHandler());

        return generateProxy(proxyInterface, invocationHandler);
    }

    private static <T> ReflectiveRingBufferInvocationHandler createInvocationHandler(
            final Class<T> proxyInterface,
            final Disruptor<ProxyMethodInvocation> disruptor,
            final OverflowStrategy overflowStrategy,
            final DropListener dropListener, MessagePublicationListener messagePublicationListener)
    {
        final Map<Method, Invoker> methodToInvokerMap = createMethodToInvokerMap(proxyInterface);
        return new ReflectiveRingBufferInvocationHandler(disruptor.getRingBuffer(), methodToInvokerMap, overflowStrategy, dropListener,
                messagePublicationListener);
    }

    @SuppressWarnings("unchecked")
    private static <T> T generateProxy(final Class<T> proxyInterface, final ReflectiveRingBufferInvocationHandler invocationHandler)
    {
        return (T) newProxyInstance(currentThread().getContextClassLoader(), new Class<?>[]{proxyInterface}, invocationHandler);
    }

    private static void preallocateArgumentHolders(final RingBuffer<ProxyMethodInvocation> ringBuffer)
    {
        final int bufferSize = ringBuffer.getBufferSize();
        for(int i = 0; i < bufferSize; i++)
        {
            ringBuffer.get(i).setArgumentHolder(new ObjectArrayHolder());
        }
    }

    private static <T> Map<Method, Invoker> createMethodToInvokerMap(final Class<T> proxyInterface)
    {
        final Map<Method, Invoker> methodToInvokerMap = new ConcurrentHashMap<Method, Invoker>();
        final Method[] declaredMethods = proxyInterface.getMethods();

        for (final Method declaredMethod : declaredMethods)
        {
            methodToInvokerMap.put(declaredMethod, new ReflectiveMethodInvoker(declaredMethod));
        }

        return methodToInvokerMap;
    }

}