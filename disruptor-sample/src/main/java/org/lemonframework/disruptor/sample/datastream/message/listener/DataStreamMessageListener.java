package org.lemonframework.disruptor.sample.datastream.message.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;
import org.lemonframework.disruptor.publisher.EventPublisher;
import org.lemonframework.disruptor.sample.datastream.model.DataStream;

@Component
public class DataStreamMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(DataStreamMessageListener.class);

    @Autowired
    private EventPublisher<DataStream> dataStreamEventPublisher;

    public void inMessage(String number) {
        if (StringUtils.isBlank(number) || !StringUtils.isNumeric(number.trim())) {
            LOG.error("Enter a valid long number");
            return;
        }
        long inboundMessageCount = Long.parseLong(number.trim());
        for (long l = 0; l < inboundMessageCount; l++) {
            DataStream dataStream = unmarshallDataStream(l);
            dataStreamEventPublisher.publish(dataStream);
        }
        LOG.debug("Processed {} inbound data stream records", inboundMessageCount);
    }

    private DataStream unmarshallDataStream(long i) {
        DataStream dataStream = new DataStream();
        dataStream.setDataStreamId(i);
        dataStream.setDataStreamType("type");
        dataStream.setDataSource("mainframe");
        dataStream.setDataTarget("midrange");
        dataStream.setDataStream("blah blah blah blah data");
        return dataStream;
    }

    public void setEventPublisher(EventPublisher<DataStream> dataStreamEventPublisher) {
        this.dataStreamEventPublisher = dataStreamEventPublisher;
    }
}