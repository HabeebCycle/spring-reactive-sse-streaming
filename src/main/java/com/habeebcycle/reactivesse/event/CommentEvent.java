package com.habeebcycle.reactivesse.event;

import com.habeebcycle.reactivesse.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
@EnableBinding(Sink.class)
public class CommentEvent implements Consumer<FluxSink<Comment>> {

    private static final Logger LOG = LoggerFactory.getLogger(CommentEvent.class);

    private final BlockingQueue<Comment> queue = new LinkedBlockingQueue<>();

    @StreamListener(Sink.INPUT)
    public void getOneComment(Comment comment) {
        LOG.info("Got {} from Kafka/RabbitMQ topic", comment);
        this.queue.offer(comment);
    }

    @Override
    public void accept(FluxSink<Comment> commentFluxSink) {
        try {
            Comment comment = queue.take();
            commentFluxSink.next(comment);
        } catch (InterruptedException ie) {
            ReflectionUtils.rethrowRuntimeException(ie);
        }
    }

}
