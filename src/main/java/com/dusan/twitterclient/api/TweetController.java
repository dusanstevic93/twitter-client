package com.dusan.twitterclient.api;

import com.dusan.twitterclient.service.model.PostTweetCommand;
import com.dusan.twitterclient.service.model.TweetResource;
import com.dusan.twitterclient.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResource postTweet(@RequestParam("username") String username, @RequestBody PostTweetCommand postTweetCommand) {
        return tweetService.postTweet(username, postTweetCommand);
    }

    @GetMapping(value = "/{tweetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TweetResource getTweetById(@PathVariable long tweetId) {
        return tweetService.getTweetById(tweetId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TweetResource> getUserTweets(@RequestParam("username") String username) {
        return tweetService.getUserTweets(username);
    }

    @DeleteMapping(value = "/{tweetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TweetResource deleteTweetById(@RequestParam("username") String username, @PathVariable long tweetId) {
        return tweetService.deleteTweetById(username, tweetId);
    }
}
