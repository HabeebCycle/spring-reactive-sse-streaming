package com.habeebcycle.reactivesse.service;

import com.habeebcycle.reactivesse.messaging.MessageSource;
import com.habeebcycle.reactivesse.model.Comment;
import com.habeebcycle.reactivesse.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Service
@EnableBinding({MessageSource.class, Sink.class})
public class CommentService implements Consumer<FluxSink<Comment>> {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final MessageSource messageSource;

    private final BlockingQueue<Comment> queue;

    public CommentService(CommentRepository commentRepository, MessageSource messageSource) {
        this.commentRepository = commentRepository;
        this.messageSource = messageSource;
        this.queue = new LinkedBlockingQueue<>();
    }

    public Flux<Comment> getIntervalComment() {
        LOG.info("Returning interval comment");
        return commentRepository.findAll();
    }

    public void sendOneComment() {
        Optional<Comment> body = Optional.ofNullable(commentRepository.findOne().block());
        if(body.isPresent()) {
            LOG.info("Sending comment {} to Kafka", body.get());
            Message<Comment> message = MessageBuilder.withPayload(body.get()).build();
            messageSource.outputComments().send(message);
        }
    }

    @StreamListener(Sink.INPUT)
    public void getOneComment(Comment comment) {
        LOG.info("Got comment {} to Kafka", comment);

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
