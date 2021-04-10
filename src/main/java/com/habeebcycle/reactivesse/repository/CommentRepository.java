package com.habeebcycle.reactivesse.repository;

import com.habeebcycle.reactivesse.model.Comment;
import reactor.core.publisher.Flux;

public interface CommentRepository {

    Flux<Comment> findAll();
}
