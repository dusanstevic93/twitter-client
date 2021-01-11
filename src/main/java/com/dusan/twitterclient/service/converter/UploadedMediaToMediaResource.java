package com.dusan.twitterclient.service.converter;

import com.dusan.twitterclient.service.model.MediaResource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import twitter4j.UploadedMedia;

@Component
class UploadedMediaToMediaResource implements Converter<UploadedMedia, MediaResource> {

    @Override
    public MediaResource convert(UploadedMedia uploadedMedia) {
        return MediaResource.builder()
                .id(uploadedMedia.getMediaId())
                .type(uploadedMedia.getImageType())
                .size(uploadedMedia.getSize())
                .build();
    }
}
