package com.habeebcycle.reactivesse.messaging;

import com.habeebcycle.reactivesse.model.Comment;
import com.habeebcycle.reactivesse.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.BlockingQueue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProducerTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MessageCollector collector;

    @Autowired
    private MessageSource channel;

    BlockingQueue<Message<?>> queueComments = null;

    @BeforeEach
    void setUp() {
        queueComments = collector.forChannel(channel.outputComments());
    }

    @Test
    void getAndVerifyCommentInTopic() throws InterruptedException {
        commentService.sendOneComment();

        assertEquals(1, queueComments.size());
        String topicComment = queueComments.take().getPayload().toString();
        assertEquals(0, queueComments.size());
        System.out.println(topicComment);
    }
}
