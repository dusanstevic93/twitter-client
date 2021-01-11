package com.dusan.twitterclient.tokenstorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

import static org.assertj.core.api.Assertions.*;

@DataRedisTest
class OAuthTokenRepositoryTest {

    @Autowired
    private OAuthTokenRepository tokenRepository;

    @AfterEach
    void cleanUp() {
        tokenRepository.deleteAll();
    }

    @Test
    void saveToken() {
        // given
        OAuthToken tokenToSave = getOAuthToken();

        // when
        OAuthToken savedToken = tokenRepository.save(tokenToSave);

        // then
        assertThat(savedToken).isEqualTo(tokenToSave);
    }

    @Test
    void findTokenById() {
        // given
        OAuthToken savedToken = getOAuthToken();
        tokenRepository.save(savedToken);

        // when
        OAuthToken loadedToken = tokenRepository.findById(savedToken.getUsername()).get();

        // then
        assertThat(loadedToken).isEqualTo(savedToken);
    }

    private OAuthToken getOAuthToken() {
        String username = "dusan";
        String token = "token";
        String tokenSecret = "tokenSecret";
        return new OAuthToken(username, token, tokenSecret);
    }
}