package com.dusan.twitterclient.service.converter;

import com.dusan.twitterclient.service.model.UserResource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import twitter4j.User;

@Component
class UserToUserResource implements Converter<User, UserResource> {

    @Override
    public UserResource convert(User user) {
        return UserResource.builder()
                .id(user.getId())
                .name(user.getName())
                .screen_name(user.getScreenName())
                .location(user.getLocation())
                .description(user.getDescription())
                .profile_image_url(user.getProfileImageURL())
                .build();
    }
}
