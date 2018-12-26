package org.lemonframework.disruptor.sample.datastream.disruptor.eventfactory;

import com.lmax.disruptor.EventFactory;
import org.lemonframework.disruptor.sample.datastream.model.DataStream;

public class DataStreamEvent implements EventFactory<DataStream> {

	@Override
	public DataStream newInstance() {
		return new DataStream();
	}
	
}