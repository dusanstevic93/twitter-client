package com.dusan.twitterclient.service.converter;

import com.dusan.twitterclient.service.model.TweetResource;
import com.dusan.twitterclient.service.model.UserResource;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
@AllArgsConstructor
class StatusToTweetResource implements Converter<Status, TweetResource> {

    private final UserToUserResource userToUserResourceConverter;

    @Override
    public TweetResource convert(Status status) {
        UserResource user = userToUserResourceConverter.convert(status.getUser());
        return TweetResource.builder()
                .id(status.getId())
                .created_at(status.getCreatedAt().toInstant())
                .text(status.getText())
                .favorite_count(status.getFavoriteCount())
                .user(user)
                .build();
    }
}
