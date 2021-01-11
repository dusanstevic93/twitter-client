package com.dusan.twitterclient.service.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResource {

    private final long id;
    private final String name;
    private final String screen_name;
    private final String location;
    private final String description;
    private final String profile_image_url;
}
