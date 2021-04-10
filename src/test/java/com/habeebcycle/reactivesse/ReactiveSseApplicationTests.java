package com.habeebcycle.reactivesse;

import com.habeebcycle.reactivesse.model.Comment;
import com.habeebcycle.reactivesse.repository.CommentRepository;
import com.habeebcycle.reactivesse.service.CommentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.MediaType;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReactiveSseApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private Sink channels;

	@MockBean
	private CommentService commentService;

	@BeforeEach
	void setUp() {
		AbstractMessageChannel input = (AbstractMessageChannel) channels.input();
		Comment comment = new Comment("Author", "Message", "Timestamp");

		Mockito.doAnswer(i -> {
			input.send(new GenericMessage<>(comment));
			return null;
		}).when(commentService).sendOneComment();

		Mockito.when(commentService.getIntervalComment())
				.thenReturn(commentRepository.findAll());
	}

	@Test
	void testIntervalCommentStream() {
		List<Comment> commentList = client
				.get().uri("/comment/stream")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus().isOk()
				.returnResult(Comment.class)
				.getResponseBody()
				.take(3)
				.collectList().block();

		Assertions.assertNotNull(commentList);
		Assertions.assertEquals(3, commentList.size());

		commentList.forEach(System.out::println);
	}

	@Test
	void testMessagingComment() {
		Comment comment = client
				.get().uri("/comment")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus().isOk()
				.returnResult(Comment.class)
				.getResponseBody()
				.next()
				.block();

		Assertions.assertNotNull(comment);
		Assertions.assertEquals("Author", comment.getAuthor());
		Assertions.assertEquals("Message", comment.getMessage());
		Assertions.assertEquals("Timestamp", comment.getTimestamp());
	}

}
