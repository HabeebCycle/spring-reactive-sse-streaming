package com.habeebcycle.reactivesse.service;

import com.habeebcycle.reactivesse.messaging.MessageSource;
import com.habeebcycle.reactivesse.model.Comment;
import com.habeebcycle.reactivesse.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service
@EnableBinding(MessageSource.class)
public class CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final MessageSource messageSource;

    public CommentService(CommentRepository commentRepository, MessageSource messageSource) {
        this.commentRepository = commentRepository;
        this.messageSource = messageSource;
    }

    public Flux<Comment> getIntervalComment() {
        LOG.info("Returning interval comment");
        return commentRepository.findAll();
    }

    public void sendOneComment() {
        Optional<Comment> body = Optional.ofNullable(commentRepository.findOne().block());
        if(body.isPresent()) {
            LOG.info("Sending {} to Kafka/RabbitMQ topic", body.get());
            Message<Comment> message = MessageBuilder.withPayload(body.get()).build();
            messageSource.outputComments().send(message);
        }
    }
}
