package com.dusan.twitterclient.auth;

import com.dusan.twitterclient.tokenstorage.OAuthToken;
import com.dusan.twitterclient.tokenstorage.OAuthTokenRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class OAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);

    private final TwitterFactory twitterFactory;
    private final OAuthTokenRepository tokenRepository;

    @GetMapping("/authorize")
    public String showAuthPage() {
        return "auth";
    }

    @PostMapping("/authorize")
    public String authorizeApp(HttpSession session) {
        Twitter twitter = twitterFactory.getInstance();

        final String callbackURL = "http://localhost:8080/oauth-callback";
        RequestToken requestToken;
        try {
            requestToken = twitter.getOAuthRequestToken(callbackURL);
            logger.debug("Obtained request token. Redirecting to {}", requestToken.getAuthorizationURL());
        } catch (TwitterException e) {
            throw new OAuthException("Error when try to obtain a request token", e);
        }

        session.setAttribute("twitter", twitter);
        session.setAttribute("requestToken", requestToken);
        session.setMaxInactiveInterval(300);
        return "redirect:" + requestToken.getAuthorizationURL();
    }

    @GetMapping("/oauth-callback")
    public String callback(HttpServletRequest request, HttpSession session) {
        Twitter twitter = (Twitter) session.getAttribute("twitter");
        RequestToken requestToken = (RequestToken) session.getAttribute("requestToken");
        String verifier = request.getParameter("oauth_verifier");
        session.invalidate();

        if (twitter == null || requestToken == null || verifier == null) {
            logger.debug("Required field is missing. Redirecting to authorization page");
            return "redirect:/authorize";
        }

        AccessToken accessToken;
        try {
            accessToken = obtainAccessToken(twitter, requestToken, verifier);
        } catch (TwitterException e) {
            throw new OAuthException("Error when try to obtain an access token", e);
        }

        saveTokenInDatabase(accessToken);
        return "auth_success";
    }

    private AccessToken obtainAccessToken(Twitter twitter, RequestToken requestToken, String verifier) throws TwitterException {
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
        logger.debug("User {} is successfully authenticated", accessToken.getScreenName());
        return accessToken;
    }

    private void saveTokenInDatabase(AccessToken accessToken) {
        String username = accessToken.getScreenName().toLowerCase(); // make username case insensitive
        String token = accessToken.getToken();
        String tokenSecret = accessToken.getTokenSecret();
        OAuthToken oAuthToken = new OAuthToken(username, token, tokenSecret);
        tokenRepository.save(oAuthToken);
        logger.debug("Token {} is saved in a database", oAuthToken);
    }
}
