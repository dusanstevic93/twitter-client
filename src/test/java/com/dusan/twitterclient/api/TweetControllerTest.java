package com.dusan.twitterclient.api;

import com.dusan.twitterclient.service.TweetService;
import com.dusan.twitterclient.service.exception.TwitterApiException;
import com.dusan.twitterclient.service.model.PostTweetCommand;
import com.dusan.twitterclient.service.model.TweetResource;
import com.dusan.twitterclient.service.model.UserResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TweetController.class)
class TweetControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TweetService tweetService;

    @Nested
    class PostTweetEndpointTests {

        @Test
        @DisplayName("Create and return new tweet resource when the input is valid")
        void postTweetShouldBeSuccessful() throws Exception {
            // given
            String username = "dusan";
            PostTweetCommand postCommand = new PostTweetCommand();
            postCommand.setText("test tweet");
            TweetResource tweetResource = getTweetResource();
            given(tweetService.postTweet(username, postCommand)).willReturn(tweetResource);

            // when
            MvcResult result = callPostTweetUrl(username, postCommand);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 201;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);

            String actualResponseBody = result.getResponse().getContentAsString();
            String expectedResponseBody = mapper.writeValueAsString(tweetResource);
            assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
        }

        @Test
        @DisplayName("Return http status - bad request when a username is not provided")
        void postTweetMissingUsername() throws Exception {
            // given
            PostTweetCommand postCommand = new PostTweetCommand();

            // when
            MvcResult result = callPostTweetUrl(null, postCommand);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 400;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
        }

        private MvcResult callPostTweetUrl(String username, PostTweetCommand postTweetCommand) throws Exception {
            return mvc.perform(post("/api/tweets")
                    .param("username", username)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(postTweetCommand)))
                    .andReturn();
        }

        @Test
        @DisplayName("Return http status - bad request when a request body is not provided")
        void postTweetMissingRequestBody() throws Exception {
            // given
            String username = "dusan";

            // when
            MvcResult result = callPostTweetUrlNoBody(username);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 415;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
        }

        private MvcResult callPostTweetUrlNoBody(String username) throws Exception {
            return mvc.perform(post("/api/tweets")
                    .param("username", username)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();
        }
    }

    @Nested
    class GetTweetByIdEndpointTests {

        @Test
        @DisplayName("Return tweet resource when the input is valid")
        void getTweetByIdShouldReturnTweetResource() throws Exception {
            // given
            long tweetId = 1L;
            TweetResource tweetResource = getTweetResource();
            given(tweetService.getTweetById(tweetId)).willReturn(tweetResource);

            // when
            MvcResult result = callGetTweetByIdUrl(tweetId);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 200;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);

            String actualResponseBody = result.getResponse().getContentAsString();
            String expectedResponseBody = mapper.writeValueAsString(tweetResource);
            assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
        }

        @Test
        @DisplayName("Return error response when a tweet is not found")
        void getTweetByIdTweetNotFound() throws Exception {
            // given
            long tweetId = 1L;
            HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
            String expectedMessage = "Tweet not found";
            given(tweetService.getTweetById(tweetId)).willThrow(new TwitterApiException(expectedStatus.value(), expectedMessage));

            // when
            MvcResult result = callGetTweetByIdUrl(tweetId);

            // then
            String jsonBody = result.getResponse().getContentAsString();
            ErrorResponse actualErrorResponse = mapper.readValue(jsonBody, ErrorResponse.class);
            assertThat(actualErrorResponse.getTimestamp()).isNotNull();
            assertThat(actualErrorResponse.getStatus()).isEqualTo(expectedStatus.value());
            assertThat(actualErrorResponse.getError()).isEqualTo(expectedStatus.name());
            assertThat(actualErrorResponse.getMessage()).isEqualTo(expectedMessage);
            assertThat(actualErrorResponse.getPath()).isEqualTo(result.getRequest().getRequestURI());
        }

        private MvcResult callGetTweetByIdUrl(long tweetId) throws Exception {
            return mvc.perform(get("/api/tweets/{tweetId}",tweetId)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();
        }
    }

    @Nested
    class GetUserTweetsEndpointTests {

        @Test
        @DisplayName("Return list of user tweets when the input is valid")
        void getUserTweetsShouldReturnUserTweets() throws Exception {
            // given
            String username = "dusan";
            TweetResource tweetResource = getTweetResource();
            List<TweetResource> userTweets = List.of(tweetResource);
            given(tweetService.getUserTweets(username)).willReturn(userTweets);

            // when
            MvcResult result = callGetUserTweetsUrl(username);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 200;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);

            String actualResponseBody = result.getResponse().getContentAsString();
            String expectedResponseBody = mapper.writeValueAsString(userTweets);
            assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
        }

        @Test
        @DisplayName("Return http status - bad request when a username is not provided")
        void getUserTweetsMissingUsername() throws Exception {
            // given

            // when
            MvcResult result = callGetUserTweetsUrl(null);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 400;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
        }

        @Test
        @DisplayName("Return correct error response when a service method throw exception")
        void getUserTweetsThrowException() throws Exception {
            // given
            String username = "dusan";
            HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
            String expectedMessage = "test message";
            given(tweetService.getUserTweets(anyString())).willThrow(new TwitterApiException(expectedStatus.value(), expectedMessage));

            // when
            MvcResult result = callGetUserTweetsUrl(username);

            // then
            String jsonBody = result.getResponse().getContentAsString();
            ErrorResponse actualErrorResponse = mapper.readValue(jsonBody, ErrorResponse.class);
            assertThat(actualErrorResponse.getTimestamp()).isNotNull();
            assertThat(actualErrorResponse.getStatus()).isEqualTo(expectedStatus.value());
            assertThat(actualErrorResponse.getError()).isEqualTo(expectedStatus.name());
            assertThat(actualErrorResponse.getMessage()).isEqualTo(expectedMessage);
            assertThat(actualErrorResponse.getPath()).isEqualTo(result.getRequest().getRequestURI());
        }

        private MvcResult callGetUserTweetsUrl(String username) throws Exception {
            return mvc.perform(get("/api/tweets")
                    .param("username", username)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();
        }
    }

    @Nested
    class DeleteTweetByIdTests {

        @Test
        @DisplayName("Return http status - ok when the input is valid")
        void deleteTweetByIdValidInput() throws Exception {
            // given
            String username = "dusan";
            long tweetId = 1L;
            TweetResource tweetResource = getTweetResource();
            given(tweetService.deleteTweetById(username, tweetId)).willReturn(tweetResource);

            // when
            MvcResult result = callDeleteTweetByIdUrl(tweetId, username);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 200;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);

            String actualResponseBody = result.getResponse().getContentAsString();
            String expectedResponseBody = mapper.writeValueAsString(tweetResource);
            assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
        }

        @Test
        @DisplayName("Return http status - bad request when a username is not provided")
        void deleteTweetByIdMissingUsername() throws Exception {
            // given
            long tweetId = 1L;

            // when
            MvcResult result = callDeleteTweetByIdUrl(tweetId, null);

            // then
            int actualStatusCode = result.getResponse().getStatus();
            int expectedStatusCode = 400;
            assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
        }

        @Test
        @DisplayName("Return correct error response when a tweet is not found")
        void deleteTweetByIdTweetNotFound() throws Exception {
            // given
            long tweetId = 1L;
            String username = "dusan";
            HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
            String expectedMessage = "Tweet not found";
            given(tweetService.deleteTweetById(username, tweetId)).willThrow(new TwitterApiException(expectedStatus.value(), expectedMessage));

            // when
            MvcResult result = callDeleteTweetByIdUrl(tweetId, username);

            // then
            String jsonBody = result.getResponse().getContentAsString();
            ErrorResponse actualErrorResponse = mapper.readValue(jsonBody, ErrorResponse.class);

            assertThat(actualErrorResponse.getTimestamp()).isNotNull();
            assertThat(actualErrorResponse.getStatus()).isEqualTo(expectedStatus.value());
            assertThat(actualErrorResponse.getError()).isEqualTo(expectedStatus.name());
            assertThat(actualErrorResponse.getMessage()).isEqualTo(expectedMessage);
            assertThat(actualErrorResponse.getPath()).isEqualTo(result.getRequest().getRequestURI());
        }

        private MvcResult callDeleteTweetByIdUrl(long tweetId, String username) throws Exception {
            return mvc.perform(delete("/api/tweets/{tweetId}", tweetId)
                    .param("username", username)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();
        }
    }

    private TweetResource getTweetResource() {
        UserResource userResource = UserResource.builder()
                .id(1L)
                .description("test description")
                .screen_name("dusan")
                .name("Test Name")
                .location("test location")
                .build();

        return TweetResource.builder()
                .id(1L)
                .text("some random tweet")
                .created_at(Instant.now())
                .user(userResource)
                .build();
    }
}