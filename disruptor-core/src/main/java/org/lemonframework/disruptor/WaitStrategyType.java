package org.lemonframework.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

/**
 * Defines Ring buffer Wait strategies and initializes them.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public enum WaitStrategyType {

    /**
     * @see com.lmax.disruptor.BlockingWaitStrategy
     */
    BLOCKING {
        @Override
        public WaitStrategy instance() {
            return new BlockingWaitStrategy();
        }
    },

    /**
     * @see com.lmax.disruptor.BusySpinWaitStrategy
     */
    BUSY_SPIN {
        @Override
        public WaitStrategy instance() {
            return new BusySpinWaitStrategy();
        }
    },

    /**
     * @see com.lmax.disruptor.LiteBlockingWaitStrategy
     */
    LITE_BLOCKING {
        @Override
        public WaitStrategy instance() {
            return new LiteBlockingWaitStrategy();
        }
    },

    /**
     * @see com.lmax.disruptor.SleepingWaitStrategy
     */
    SLEEPING_WAIT {
        @Override
        public WaitStrategy instance() {
            return new SleepingWaitStrategy();
        }
    },

    /**
     * @see com.lmax.disruptor.YieldingWaitStrategy
     */
    YIELDING {
        @Override
        public WaitStrategy instance() {
            return new YieldingWaitStrategy();
        }
    };

    abstract WaitStrategy instance();

}
