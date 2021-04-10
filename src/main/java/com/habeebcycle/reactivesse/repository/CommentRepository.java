package com.habeebcycle.reactivesse.repository;

import com.habeebcycle.reactivesse.model.Comment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository {

    Flux<Comment> findAll();

    Mono<Comment> findOne();
}
