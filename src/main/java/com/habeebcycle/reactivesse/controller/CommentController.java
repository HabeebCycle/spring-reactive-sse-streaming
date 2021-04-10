package com.habeebcycle.reactivesse.controller;

import com.habeebcycle.reactivesse.model.Comment;
import com.habeebcycle.reactivesse.service.CommentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CommentController {

    private final CommentService commentService;
    private final Flux<Comment> events;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
        this.events = Flux.create(commentService).share();
    }

    @GetMapping(path = "/comment/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Comment> feed() {
        return commentService.getIntervalComment();
    }

    @GetMapping(path = "/comment", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Comment> processComment() {
        commentService.sendOneComment();
        return events.next();
    }
}
