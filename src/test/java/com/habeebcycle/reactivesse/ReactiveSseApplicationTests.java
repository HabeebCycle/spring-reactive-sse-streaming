package com.habeebcycle.reactivesse;

import com.habeebcycle.reactivesse.model.Comment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReactiveSseApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void testCommentStream() {
		List<Comment> commentList = client
				.get().uri("/comment/stream")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus().isOk()
				.returnResult(Comment.class)
				.getResponseBody()
				.take(3)
				.collectList().block();

		commentList.forEach(System.out::println);

		Assertions.assertEquals(3, commentList.size());
	}

}
