package org.lemonframework.disruptor.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class AbstractRingBufferProxyGeneratorTest
{
    private final GeneratorType generatorType;

    protected AbstractRingBufferProxyGeneratorTest(final GeneratorType generatorType)
    {
        this.generatorType = generatorType;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    public void shouldProxy()
    {
        final Disruptor<ProxyMethodInvocation> disruptor = createDisruptor(Executors.newSingleThreadExecutor(), 1024);
        final RingBufferProxyGeneratorFactory generatorFactory = new RingBufferProxyGeneratorFactory();
        final RingBufferProxyGenerator ringBufferProxyGenerator = generatorFactory.newProxy(generatorType);

        final ListenerImpl implementation = new ListenerImpl();
        final Listener listener = ringBufferProxyGenerator.createRingBufferProxy(Listener.class, disruptor, OverflowStrategy.DROP, implementation);
        disruptor.start();

        for(int i = 0; i < 3; i++)
        {
            listener.onString("single string " + i);
            listener.onFloatAndInt((float) i, i);
            listener.onVoid();
            listener.onObjectArray(new Double[]{(double) i});
            listener.onMixedMultipleArgs(0, 1, "a", "b", 2);
        }

        RingBuffer<ProxyMethodInvocation> ringBuffer = disruptor.getRingBuffer();
        while (ringBuffer.getMinimumGatingSequence() != ringBuffer.getCursor())
        {
            // Spin
        }

        disruptor.shutdown();
        Executors.newSingleThreadExecutor().shutdown();

        assertThat(implementation.getLastStringValue(), is("single string 2"));
        assertThat(implementation.getLastFloatValue(), is((float) 2));
        assertThat(implementation.getLastIntValue(), is(2));
        assertThat(implementation.getVoidInvocationCount(), is(3));
        assertThat(implementation.getMixedArgsInvocationCount(), is(3));
        assertThat(implementation.getLastDoubleArray(), is(equalTo(new Double[] {(double) 2})));
    }

    private Disruptor<ProxyMethodInvocation> createDisruptor(final ExecutorService executor, final int ringBufferSize)
    {
        final Disruptor<ProxyMethodInvocation> disruptor = new Disruptor<ProxyMethodInvocation>(new RingBufferProxyEventFactory(), ringBufferSize, executor);
        disruptor.handleExceptionsWith(new FatalExceptionHandler());
        return disruptor;
    }
}