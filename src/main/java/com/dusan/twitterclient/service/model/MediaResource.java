package com.dusan.twitterclient.service.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaResource {

    private final long id;
    private final String type;
    private final long size;
}
