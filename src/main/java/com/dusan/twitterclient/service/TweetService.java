package com.dusan.twitterclient.service;

import com.dusan.twitterclient.service.model.PostTweetCommand;
import com.dusan.twitterclient.service.model.TweetResource;

import java.util.List;

public interface TweetService {

    TweetResource postTweet(String username, PostTweetCommand postTweetCommand);
    TweetResource getTweetById(long tweetId);
    TweetResource deleteTweetById(String username, long tweetId);
    List<TweetResource> getUserTweets(String username);
}
