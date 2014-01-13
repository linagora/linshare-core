package org.linagora.linshare.auth.dao;

import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class DatabaseAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {

	// ~ Instance fields
	// ================================================================================================

	private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();

	private GuestRepository guestRepository;

	// ~ Methods
	// ========================================================================================================

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), userDetails);
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!passwordEncoder.isPasswordValid(userDetails.getPassword(),
				presentedPassword, null)) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), userDetails);
		}
	}

	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.guestRepository,
				"A userService must be set");
	}

	@Override
	protected final UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

		if (username == null || username.length() == 0)
			throw new UsernameNotFoundException("username must not be null");
		logger.debug("Trying to load '" + username + "' account detail ...");

		try {
			String password = null;
			Account account = null;
			
			Object details = authentication.getDetails();
			String domain = (String) details;
			if (domain == null) {
				// looking into the database for a user with his login ie username (could be a mail or a LDAP uid)
				try {
					account = guestRepository.findByLogin(username);
				} catch (IllegalStateException e) {
					throw new AuthenticationServiceException(
							"Could not authenticate user: " + username);
				}
			} else {
				// TODO FMA Auth multi domain for guests
				account = guestRepository.findByLoginAndDomain(domain, username);
			}
			
			if (account != null) {
				logger.debug("Account in database found : " + account.getAccountReprentation());
				password = account.getPassword();

				// If the password field is not set (only Ldap user), we set it to
				// an empty string.
				if (password == null)
					password = "";
			}
			if (account == null || password == null
					|| Role.SYSTEM.equals(account.getRole())) {
				logger.debug("throw UsernameNotFoundException: Account not found");
				throw new UsernameNotFoundException("Account not found");
			}

			loadedUser = new User(account.getLsUuid(), password, true, true, true, true,
					RoleProvider.getRoles(account));

		} catch (DataAccessException repositoryProblem) {
			throw new AuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}
		return loadedUser;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public GuestRepository getGuestRepository() {
		return guestRepository;
	}

	public void setGuestRepository(GuestRepository guestRepository) {
		this.guestRepository = guestRepository;
	}
}
