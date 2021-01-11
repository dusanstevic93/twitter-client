package com.dusan.twitterclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@SpringBootApplication
public class TwitterClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterClientApplication.class, args);
	}

	@Bean
	public TwitterFactory twitterFactory(@Value("${consumer.key}") String consumerKey,
										 @Value("${consumer.secret}") String consumerSecret) {
		Configuration config = new ConfigurationBuilder()
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.build();
		return new TwitterFactory(config);
	}
}
