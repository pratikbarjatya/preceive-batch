package io.theysay.preceive.batch.pipeline;

public class PipelineMonitor {
    private final Pipe producer;
    private final Pipe processor;

    public PipelineMonitor(Pipe producer, Pipe processor) {
        this.producer = producer;
        this.processor = processor;
    }

    public long getProduced() {
        return producer.getWrites();
    }

    public long getProcessed() {
        return processor.getWrites();
    }

}
