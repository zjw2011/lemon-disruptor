package org.lemonframework.disruptor.sample.datastream.service;

import org.lemonframework.disruptor.sample.datastream.model.DataStream;

public interface DatastreamService {
    long delay = 555555L;

    void journalDatastream(DataStream dataStream);

    void processDatastream_A(DataStream dataStream);

    void processDatastream_B(DataStream dataStream);

    DataStream formatDatastream(DataStream dataStream);
}