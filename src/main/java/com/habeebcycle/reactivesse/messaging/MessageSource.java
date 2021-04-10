package com.habeebcycle.reactivesse.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MessageSource {

    String OUTPUT_COMMENTS = "output-comments";

    @Output(OUTPUT_COMMENTS)
    MessageChannel outputComments();
}
