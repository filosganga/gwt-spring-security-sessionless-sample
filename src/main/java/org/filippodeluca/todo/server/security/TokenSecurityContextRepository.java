package org.filippodeluca.todo.server.security;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class TokenSecurityContextRepository implements SecurityContextRepository {

    private AuthenticationManager authenticationManager;
    private TokenService tokenService;

    public TokenSecurityContextRepository(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    /**
     * Gets the security context for the current request (if available) and returns it.
     * <p/>
     * If the session is null, the context object is null or the context object stored in the session
     * is not an instance of {@code SecurityContext}, a new context object will be generated and
     * returned.
     */
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {

        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();

        SecurityContext context = resolveContextFromRequest(request);
        if (context == null) {
            context = generateNewContext();
            requestResponseHolder.setResponse(new SaveToCookieResponseWrapper(response, false));
        }

        return context;
    }

    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {



    }

    public boolean containsContext(HttpServletRequest request) {

        return resolveContextFromRequest(request) != null;
    }

    protected SecurityContext resolveContextFromRequest(HttpServletRequest request) {

        SecurityContext context = null;

        Cookie cookie = WebUtils.getCookie(request, "SECURITY_TOKEN");
        if (cookie != null) {
            Token token = null;
            try {
                token = tokenService.verifyToken(cookie.getValue());
            } catch (Exception e) {
                // invalid tken
            }
            if(token!=null) {
                Iterable<String> values = Splitter.on(":").split(token.getExtendedInformation());
                String username = Iterables.get(values, 0);
                String password = Iterables.get(values, 1);

                Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

                // TODO validate token

                context = generateNewContext();
                context.setAuthentication(auth);
            }
        }


        return context;
    }

    /**
     * By default, calls {@link org.springframework.security.core.context.SecurityContextHolder#createEmptyContext()} to obtain a new context (there should be
     * no context present in the holder when this method is called). Using this approach the context creation
     * strategy is decided by the {@link org.springframework.security.core.context.SecurityContextHolderStrategy} in use. The default implementations
     * will return a new <tt>SecurityContextImpl</tt>.
     *
     * @return a new SecurityContext instance. Never null.
     */
    protected SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    final class SaveToCookieResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {

        public SaveToCookieResponseWrapper(HttpServletResponse response, boolean disableUrlRewriting) {
            super(response, disableUrlRewriting);
        }

        @Override
        protected void saveContext(SecurityContext context) {

            Authentication auth = context.getAuthentication();
            if(auth!=null && auth.isAuthenticated()) {
                UserDetails ud = (UserDetails)auth.getPrincipal();

                String username = ud.getUsername();
                String password = ud.getPassword();

                String info = Joiner.on(":").join(username, password);
                Token token = tokenService.allocateToken(info);

                addCookie(new Cookie("SECURITY_TOKEN", token.getKey()));
            }
        }
    }

}
