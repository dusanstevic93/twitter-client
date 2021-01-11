package com.dusan.twitterclient.service.implementation;

import com.dusan.twitterclient.service.MediaService;
import com.dusan.twitterclient.service.TweetService;
import com.dusan.twitterclient.service.exception.TwitterApiException;
import com.dusan.twitterclient.service.model.MediaResource;
import com.dusan.twitterclient.service.model.PostTweetCommand;
import com.dusan.twitterclient.service.model.TweetResource;
import com.dusan.twitterclient.tokenstorage.OAuthTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class TwitterApi implements TweetService, MediaService {

    private final TwitterFactory twitterFactory;
    private final OAuthTokenRepository tokenRepository;
    private final ConversionService converter;

    @Override
    public TweetResource postTweet(String username, PostTweetCommand postTweetCommand) {
        StatusUpdate statusUpdate = new StatusUpdate(postTweetCommand.getText());
        statusUpdate.setMediaIds(postTweetCommand.getMedia_ids());
        try {
            Status status = getTwitterObject(username).updateStatus(statusUpdate);
            return converter.convert(status, TweetResource.class);
        } catch (TwitterException e) {
            throw new TwitterApiException(e.getStatusCode(), e.getErrorMessage());
        }
    }

    @Override
    public TweetResource getTweetById(long tweetId) {
        try {
            Status status = getTwitterObject().showStatus(tweetId);
            return converter.convert(status, TweetResource.class);
        } catch (TwitterException e) {
            throw new TwitterApiException(e.getStatusCode(), e.getErrorMessage());
        }
    }

    @Override
    public TweetResource deleteTweetById(String username, long tweetId) {
        try {
            Status status = getTwitterObject(username).destroyStatus(tweetId);
            return converter.convert(status, TweetResource.class);
        } catch (TwitterException e) {
            throw new TwitterApiException(e.getStatusCode(), e.getErrorMessage());
        }
    }

    @Override
    public List<TweetResource> getUserTweets(String username) {
        try {
            List<Status> statuses = getTwitterObject().getUserTimeline(username);
            return statuses.stream()
                    .map(status -> converter.convert(status, TweetResource.class))
                    .collect(Collectors.toList());
        } catch (TwitterException e) {
            throw new TwitterApiException(e.getStatusCode(), e.getErrorMessage());
        }
    }

    @Override
    public MediaResource uploadImage(String username, String fileName, InputStream inputStream) {
        try {
            UploadedMedia uploadedMedia = getTwitterObject(username).uploadMedia(fileName, inputStream);
            return converter.convert(uploadedMedia, MediaResource.class);
        } catch (TwitterException e) {
            throw new TwitterApiException(e.getStatusCode(), e.getErrorMessage());
        }
    }

    @Override
    public MediaResource uploadVideo(String username, String videoName, InputStream inputStream) {
        try {
            UploadedMedia uploadedMedia = getTwitterObject(username).uploadMediaChunked(videoName, inputStream);
            return converter.convert(uploadedMedia, MediaResource.class);
        } catch (TwitterException e) {
            throw new TwitterApiException(e.getStatusCode(), e.getErrorMessage());
        }
    }

    private Twitter getTwitterObject(String username) {
        AccessToken accessToken = tokenRepository.findById(username)
                .map(token -> new AccessToken(token.getToken(), token.getTokenSecret()))
                .orElseThrow(() -> new TwitterApiException(401, "Unauthorized"));
        return twitterFactory.getInstance(accessToken);
    }

    private Twitter getTwitterObject() {
        AccessToken accessToken = new AccessToken("", "");
        return twitterFactory.getInstance(accessToken);
    }
}