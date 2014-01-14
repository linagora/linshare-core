package org.linagora.linshare.auth.dao;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.auth.exceptions.BadDomainException;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class DatabaseAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {

	// ~ Instance fields
	// ================================================================================================

	private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();

	private UserRepository<User> userRepository;

	private AbstractDomainService abstractDomainService;

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
		Assert.notNull(this.userRepository,
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
			User account = null;
			String domainIdentifier = null;

			// Getting domain from context
			if (authentication.getDetails() != null
					&& authentication.getDetails() instanceof String) {
				domainIdentifier = (String) authentication.getDetails();
			}

			if (domainIdentifier == null) {
				// looking into the database for a user with his login ie username (could be a mail or a LDAP uid)
				try {
					account = userRepository.findByLogin(username);
				} catch (IllegalStateException e) {
					throw new AuthenticationServiceException(
							"Could not authenticate user: " + username);
				}
			} else {
				// check if domain really exist.
				AbstractDomain domain = retrieveDomain(username, domainIdentifier);

				// looking in database for a user.
				account = userRepository.findByLoginAndDomain(domainIdentifier, username);
				if (account == null) {
					Set<AbstractDomain> subdomains = domain.getSubdomain();
					for (AbstractDomain subdomain : subdomains) {
						account = userRepository.findByLoginAndDomain(subdomain.getIdentifier(), username);
						if (account != null) {
							logger.debug("User found and authenticated in domain "
									+ subdomain.getIdentifier());
							break;
						}
					}
				}
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

			List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(account);
			loadedUser = new org.springframework.security.core.userdetails.User(
					account.getLsUuid(), password, true, true, true, true,
					grantedAuthorities);

		} catch (DataAccessException repositoryProblem) {
			throw new AuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}
		return loadedUser;
	}

	private AbstractDomain retrieveDomain(String login, String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain == null) {
			logger.error("Can't find the specified domain : "
					+ domainIdentifier);
//			logAuthError(login, domainIdentifier, "Bad domain.");
			throw new BadDomainException("Domain '" + domainIdentifier
					+ "' not found", domainIdentifier);
		}
		return domain;
	}

	public void setAbstractDomainService(AbstractDomainService abstractDomainService) {
		this.abstractDomainService = abstractDomainService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setUserRepository(UserRepository<User> userRepository) {
		this.userRepository = userRepository;
	}
}
