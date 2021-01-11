package com.dusan.twitterclient.service;

import com.dusan.twitterclient.service.model.MediaResource;

import java.io.InputStream;

public interface MediaService {

    MediaResource uploadImage(String username, String imageName, InputStream inputStream);
    MediaResource uploadVideo(String username, String videoName, InputStream inputStream);
}
