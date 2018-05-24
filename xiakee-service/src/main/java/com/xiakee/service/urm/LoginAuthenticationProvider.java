package com.xiakee.service.urm;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class LoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
{

    // ~ Instance fields
    // ================================================================================================

    private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();

    private SaltSource saltSource;

    private LoginUserDetailsService userDetailsService;

    // ~ Methods
    // ========================================================================================================

    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException
    {
        Object salt = null;

        if (this.saltSource != null)
        {
            salt = this.saltSource.getSalt(userDetails);
        }

        if (authentication.getCredentials() == null)
        {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException("Bad credentials:" + userDetails);
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.isPasswordValid(userDetails.getPassword(), presentedPassword, salt))
        {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException("Bad credentials:" + userDetails);
        }
    }

    protected void doAfterPropertiesSet() throws Exception
    {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }

    protected PasswordEncoder getPasswordEncoder()
    {
        return passwordEncoder;
    }

    protected SaltSource getSaltSource()
    {
        return saltSource;
    }

    protected LoginUserDetailsService getUserDetailsService()
    {
        return userDetailsService;
    }

    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException
    {
        UserDetails loadedUser;

        try
        {
            String password = (String) authentication.getCredentials();
            loadedUser = getUserDetailsService().loadUserByUsername(username, password);//区别在这里
        }
        catch (UsernameNotFoundException notFound)
        {
            throw notFound;
        }
        catch (Exception repositoryProblem)
        {
            throw new AuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null)
        {
            throw new AuthenticationServiceException(
                                                     "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    /**
     * Sets the PasswordEncoder instance to be used to encode and validate
     * passwords. If not set, the password will be compared as plain text.
     * <p>
     * For systems which are already using salted password which are encoded
     * with a previous release, the encoder should be of type
     * {@code org.springframework.security.authentication.encoding.PasswordEncoder}
     * . Otherwise, the recommended approach is to use
     * {@code org.springframework.security.crypto.password.PasswordEncoder}.
     *
     * @param passwordEncoder
     *            must be an instance of one of the {@code PasswordEncoder}
     *            types.
     */
    public void setPasswordEncoder(Object passwordEncoder)
    {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

        if (passwordEncoder instanceof PasswordEncoder)
        {
            this.passwordEncoder = (PasswordEncoder) passwordEncoder;
            return;
        }

        if (passwordEncoder instanceof org.springframework.security.authentication.encoding.PasswordEncoder)
        {
            final org.springframework.security.authentication.encoding.PasswordEncoder delegate = (org.springframework.security.authentication.encoding.PasswordEncoder) passwordEncoder;
//            this.passwordEncoder = new MyPasswordEncoder();

            return;
        }

        throw new IllegalArgumentException("passwordEncoder must be a PasswordEncoder instance");
    }

    /**
     * The source of salts to use when decoding passwords. <code>null</code> is
     * a valid value, meaning the <code>DaoAuthenticationProvider</code> will
     * present <code>null</code> to the relevant <code>PasswordEncoder</code>.
     * <p>
     * Instead, it is recommended that you use an encoder which uses a random
     * salt and combines it with the password field. This is the default
     * approach taken in the
     * {@code org.springframework.security.crypto.password} package.
     *
     * @param saltSource
     *            to use when attempting to decode passwords via the
     *            <code>PasswordEncoder</code>
     */
    public void setSaltSource(SaltSource saltSource)
    {
        this.saltSource = saltSource;
    }

    public void setUserDetailsService(LoginUserDetailsService userDetailsService)
    {
        this.userDetailsService = userDetailsService;
    }
}