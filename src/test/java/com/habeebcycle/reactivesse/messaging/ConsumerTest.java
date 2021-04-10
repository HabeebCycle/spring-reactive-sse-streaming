package com.habeebcycle.reactivesse.messaging;

import com.habeebcycle.reactivesse.event.CommentEvent;
import com.habeebcycle.reactivesse.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConsumerTest {

    @Autowired
    private Sink channels;

    @Autowired
    private CommentEvent commentEvent;

    private Flux<Comment> events;

    private AbstractMessageChannel input = null;

    @BeforeEach
    void setUpDb() {
        input = (AbstractMessageChannel) channels.input();
        events = Flux.create(commentEvent).share();
        assertNotNull(input);
    }

    @Test
    void consumeMessage() {
        Comment sendComment = new Comment("Author", "Message", "Timestamp");
        assertTrue(sendCommentEvent(sendComment));

        Comment receivedComment = events.blockFirst();

        assertEquals(sendComment, receivedComment);
    }

    private boolean sendCommentEvent(Comment comment) {
        return input.send(new GenericMessage<>(comment));
    }
}
