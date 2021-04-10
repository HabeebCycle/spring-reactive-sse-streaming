package com.habeebcycle.reactivesse.event;

import com.habeebcycle.reactivesse.model.Comment;
import org.springframework.context.ApplicationEvent;

public class CommentEvent extends ApplicationEvent {

    public CommentEvent(Comment comment) {
        super(comment);
    }
}
