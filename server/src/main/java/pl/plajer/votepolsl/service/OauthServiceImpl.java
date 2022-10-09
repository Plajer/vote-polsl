package pl.plajer.votepolsl.service;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pl.plajer.votepolsl.data.User;
import pl.plajer.votepolsl.data.dto.PostAuthAccessDto;
import pl.plajer.votepolsl.exception.RestException;
import pl.plajer.votepolsl.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Service
public class OauthServiceImpl implements OauthService {

  private final Cache<String, Pair<OAuthConsumer, OAuthProvider>> cache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
  private final UserRepository userRepository;

  @Autowired
  public OauthServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public String requestToken() {
    OAuthConsumer consumer = new DefaultOAuthConsumer(
            System.getenv("OAUTH_CONSUMER_KEY"),
            System.getenv("OAUTH_CONSUMER_SECRET")
    );

    OAuthProvider provider = new DefaultOAuthProvider(
            "https://usosapi.polsl.pl/services/oauth/request_token?scopes=studies",
            "https://usosapi.polsl.pl/services/oauth/access_token",
            "https://usosapi.polsl.pl/services/oauth/authorize"
    );
    try {
      String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
      cache.put(authUrl, Pair.of(consumer, provider));
      return authUrl;
    } catch(OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException e) {
      e.printStackTrace();

      throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd logowania z USOS po stronie serwera.");
    }
  }

  @Override
  public String requestData(PostAuthAccessDto dto) {
    Pair<OAuthConsumer, OAuthProvider> data = cache.getIfPresent(dto.getAuthUrl());
    if(data == null) {
      throw new RestException(HttpStatus.BAD_REQUEST, "Nie zlecono logowania.");
    }
    OAuthConsumer consumer = data.getLeft();
    OAuthProvider provider = data.getRight();
    try {
      String code = dto.getCode().trim();
      provider.retrieveAccessToken(consumer, code);
      URL url = new URL("https://usosapi.polsl.pl/services/users/user?fields=first_name%7Clast_name%7Cstudent_number");
      HttpURLConnection request = (HttpURLConnection) url.openConnection();

      consumer.sign(request);

      StringBuilder textBuilder = new StringBuilder();
      try (Reader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
        int c = 0;
        while ((c = reader.read()) != -1) {
          textBuilder.append((char) c);
        }
      }
      JsonObject jsonObject = new Gson().fromJson(textBuilder.toString(), JsonObject.class);
      long index = Long.parseLong(jsonObject.get("student_number").getAsString().trim());
      createNewUserOrGet(index);
      return textBuilder.toString();
    } catch(IOException | OAuthCommunicationException | OAuthExpectationFailedException | OAuthMessageSignerException | OAuthNotAuthorizedException e) {
      e.printStackTrace();

      throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd logowania z USOS po stronie serwera.");
    }
  }

  private User createNewUserOrGet(long indexId) {
    Optional<User> optional = userRepository.findByIndexId(indexId);
    if(optional.isPresent()) {
      return optional.get();
    }
    User user = new User();
    user.setIndexId(indexId);
    user = userRepository.save(user);
    return user;
  }

}
