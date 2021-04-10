package com.habeebcycle.reactivesse.repository;

import com.habeebcycle.reactivesse.model.Comment;
import com.habeebcycle.reactivesse.utils.CommentGenerator;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class ReactiveCommentRepository implements CommentRepository{

    @Override
    public Flux<Comment> findAll() {
        return Flux.interval(Duration.ofSeconds(3))
                .onBackpressureDrop()
                .map(this::generateComment)
                .flatMapIterable(x -> x);
    }

    private List<Comment> generateComment(long interval) {



        Comment obj = new Comment(
                CommentGenerator.randomAuthor(),
                CommentGenerator.randomMessage(),
                CommentGenerator.getCurrentTimeStamp());

        return Collections.singletonList(obj);

    }
}
