package com.habeebcycle.reactivesse.repository;

import com.habeebcycle.reactivesse.model.Comment;
import com.habeebcycle.reactivesse.utils.CommentGenerator;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Repository
public class ReactiveCommentRepository implements CommentRepository{

    @Override
    public Flux<Comment> findAll() {
        return Flux.interval(Duration.ofSeconds(3))
                .onBackpressureDrop()
                .map(this::generateAllComment)
                .flatMapIterable(x -> x);
    }

    @Override
    public Mono<Comment> findOne() {
        return Mono.just(generateComment());
    }

    private List<Comment> generateAllComment(long interval) {
        return Collections.singletonList(generateComment());

    }

    private Comment generateComment() {
        return new Comment(
                CommentGenerator.randomAuthor(),
                CommentGenerator.randomMessage(),
                CommentGenerator.getCurrentTimeStamp());
    }
}
