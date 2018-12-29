import java.util.ArrayList;
import java.util.List;

import com.lmax.disruptor.EventHandler;
import org.junit.Test;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.BillingBusinessEventProcessor;
import org.lemonframework.disruptor.sample.billing.disruptor.eventprocessor.BillingValidationEventProcessor;
import org.lemonframework.disruptor.sample.billing.model.BillingRecord;

/**
 * desc.
 *
 * @author jiawei zhang
 * @since 0.0.1
 */
public class TestList {

    @Test
    public void testList() {
        final BillingBusinessEventProcessor billingBusinessEventProcessor = new BillingBusinessEventProcessor();
        final BillingValidationEventProcessor billingValidationEventProcessor = new BillingValidationEventProcessor();
        List<EventHandler<BillingRecord>> handlerList = new ArrayList<EventHandler<BillingRecord>>();
        handlerList.add(billingBusinessEventProcessor);
        handlerList.add(billingValidationEventProcessor);
//        final EventHandler<BillingRecord>[] o = (EventHandler<BillingRecord>[])Array.newInstance(handlerList.get(0).getClass(), handlerList.size());
//        System.out.println(o);
        final EventHandler<BillingRecord>[] objects = handlerList.<EventHandler<BillingRecord>>toArray(new EventHandler[handlerList.size()]);

//        final EventHandler<String>[] objects = (EventHandler<String>[]) Array.newInstance(handlerList.getClass().getComponentType(),
//                handlerList.size());

        System.out.println(objects[0]);
        System.out.println(objects[1]);

        List<Integer> objectList = new ArrayList<Integer>();
    }

}
