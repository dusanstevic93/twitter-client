package com.dusan.twitterclient.tokenstorage;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("tokens")
public class OAuthToken {

    @Id
    private final String username;
    private final String token;
    private final String tokenSecret;
}
