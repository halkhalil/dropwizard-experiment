package bo.gotthardt.oauth2.authentication;

import bo.gotthardt.oauth2.OAuth2AccessToken;
import bo.gotthardt.model.User;
import com.avaje.ebean.EbeanServer;
import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dropwizard {@link Authenticator} that authenticates {@link OAuth2AccessToken} into {@link User}s.
 * <br/><br/>
 * The tokens are provided as the String value extracted from the Authorization header.
 *
 * @author Bo Gotthardt
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth2Authenticator implements Authenticator<String, User> {
    private final EbeanServer ebean;

    @Override
    public Optional<User> authenticate(String credentials) throws AuthenticationException {
        OAuth2AccessToken token = ebean.find(OAuth2AccessToken.class, credentials);

        if (token == null) {
            log.info("Access token '%s' not found.", credentials);
            return Optional.absent();
        }

        if (token.isValid()) {
            log.info("Access token '%s' is no longer valid, expired at %s.", credentials, token.getExpirationDate());
            return Optional.absent();
        }

        User user = token.getUser();
        log.info("Authenticated user %s with access token '%s'", user, credentials);
        return Optional.of(user);
    }
}
