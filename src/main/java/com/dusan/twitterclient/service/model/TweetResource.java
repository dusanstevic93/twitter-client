package com.dusan.twitterclient.service.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class TweetResource {

    private final Instant created_at;
    private final long id;
    private final String text;
    private final int favorite_count;
    private final UserResource user;
}
