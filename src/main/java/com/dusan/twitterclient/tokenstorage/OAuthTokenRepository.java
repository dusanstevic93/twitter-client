package com.dusan.twitterclient.tokenstorage;

import org.springframework.data.repository.CrudRepository;

public interface OAuthTokenRepository extends CrudRepository<OAuthToken, String> {
}
