package com.dusan.twitterclient.service.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class PostTweetCommand {

    private String text;
    private long[] media_ids;
}
