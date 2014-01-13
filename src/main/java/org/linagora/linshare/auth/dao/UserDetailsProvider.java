package org.linagora.linshare.auth.dao;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.exceptions.BadDomainException;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.UserProviderService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsProvider {

	private final static Log logger = LogFactory
			.getLog(UserDetailsProvider.class);
	
	private UserRepository<User> userRepository;
	
	private AbstractDomainService abstractDomainService;
	
	private LogEntryService logEntryService;
	
	private UserProviderService userProviderService;
	
	public User retrieveUser(String domainIdentifier, String login) {
		User foundUser = null;
		// if domain was specified at the connection, we try to search the
		// user on this domain and its sub domains.
		if (domainIdentifier != null) {
			foundUser = findUserInDomainAndSubdomains(login, domainIdentifier);
		} else {
			// There is no constraints, we have to search the current user in
			// all domains.
			foundUser = findUserInAllDomain(login);
		}
		return foundUser;
	}
	
	
	private User findUserInDomainAndSubdomains(String login,
			String domainIdentifier) {
		User foundUser;
		logger.debug("The domain was specified at the connection time : "
				+ domainIdentifier);
		// check if domain really exist.
		AbstractDomain domain = retrieveDomain(login, domainIdentifier);

		// looking in database for a user.
		foundUser = userRepository.findByLoginAndDomain(domainIdentifier, login);
		if (foundUser == null) {
			logger.debug("Can't find the user in the DB. Searching in LDAP.");
			// searching in LDAP
			try {
				foundUser = userProviderService.searchForAuth(
						domain.getUserProvider(), login);
				if (foundUser != null) {
					// if found we set the domain which belong the user.
					foundUser.setDomain(domain);
				} else {
					Set<AbstractDomain> subdomains = domain.getSubdomain();
					for (AbstractDomain subdomain : subdomains) {
						foundUser = userProviderService.searchForAuth(
								subdomain.getUserProvider(), login);
						if (foundUser != null) {
							// if found we set the domain which belong the user.
							foundUser.setDomain(subdomain);
							logger.debug("User found and authenticated in domain "
									+ subdomain.getIdentifier());
							break;
						}
					}
				}
			} catch (NamingException e) {
				logger.error("Couldn't find user during authentication process : "
						+ e.getMessage());
				logAuthError(login, domainIdentifier, e.getMessage());
				throw new AuthenticationServiceException(
						"Could not authenticate user: " + login);
			} catch (IOException e) {
				logger.error("Couldn't find user during authentication process : "
						+ e.getMessage());
				logAuthError(login, domainIdentifier, e.getMessage());
				throw new AuthenticationServiceException(
						"Could not authenticate user: " + login);
			}
		}

		if (foundUser == null) {
			String message = "User not found ! Login : '" + login
					+ "' in domain : '" + domainIdentifier + "'";
			logAuthError(login, domainIdentifier, message);
			throw new UsernameNotFoundException(message);
		} else {
			logger.debug("User found in ldap : "
					+ foundUser.getAccountReprentation() + " (domain:"
					+ foundUser.getDomainId() + ")");
		}
		return foundUser;
	}

	private User findUserInAllDomain(String login) {
		User foundUser = null;
		try {
			foundUser = userRepository.findByLogin(login);
		} catch (IllegalStateException e) {
			throw new AuthenticationServiceException(
					"Could not authenticate user: " + login);
		}
		if (foundUser != null) {
			// The user was found in the database, we just need to auth against
			// LDAP.
			logger.debug("User found in DB : "
					+ foundUser.getAccountReprentation());
			logger.debug("The user domain stored in DB was : "
					+ foundUser.getDomainId());
		} else {
			logger.debug("Can't find the user in DB. Searching user in all LDAP domains.");
			List<AbstractDomain> domains = abstractDomainService
					.getAllDomains();
			for (AbstractDomain loopedDomain : domains) {
				try {
					foundUser = userProviderService.searchForAuth(
							loopedDomain.getUserProvider(), login);
				} catch (NamingException e) {
					logger.error("Couldn't find user during authentication process : "
							+ e.getMessage());
					logAuthError(login, null, e.getMessage());
					throw new AuthenticationServiceException(
							"Could not authenticate user: " + login);
				} catch (IOException e) {
					logger.error("Couldn't find user during authentication process : "
							+ e.getMessage());
					logAuthError(login, null, e.getMessage());
					throw new AuthenticationServiceException(
							"Could not authenticate user: " + login);
				}
				if (foundUser != null) {
					foundUser.setDomain(loopedDomain);
					logger.debug("User found in domain "
							+ loopedDomain.getIdentifier());
					break;
				}
			}
		}

		if (foundUser == null) {
			String message = "User not found ! Login : " + login;
			logAuthError(login, null, message);
			throw new UsernameNotFoundException("No user found for login: "
					+ message);
		} else {
			logger.debug("User found in ldap : "
					+ foundUser.getAccountReprentation() + " (domain:"
					+ foundUser.getDomainId() + ")");
		}

		return foundUser;
	}

	private AbstractDomain retrieveDomain(String login, String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain == null) {
			logger.error("Can't find the specified domain : "
					+ domainIdentifier);
			logAuthError(login, domainIdentifier, "Bad domain.");
			throw new BadDomainException("Domain '" + domainIdentifier
					+ "' not found", domainIdentifier);
		}
		return domain;
	}
	

	public void logAuthError(String login, String domainIdentifier,
			String message) {
		try {
			logEntryService.create(new UserLogEntry(login, domainIdentifier,
					LogAction.USER_AUTH_FAILED, message));
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e.getMessage());
		} catch (BusinessException e1) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e1.getMessage());
		}
	}

	public void logAuthError(User user, String domainIdentifier, String message) {
		try {
			logEntryService.create(new UserLogEntry(user,
					LogAction.USER_AUTH_FAILED, message, user));
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e.getMessage());
		} catch (BusinessException e1) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e1.getMessage());
		}
	}

	public UserRepository<User> getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository<User> userRepository) {
		this.userRepository = userRepository;
	}

	public AbstractDomainService getAbstractDomainService() {
		return abstractDomainService;
	}

	public void setAbstractDomainService(AbstractDomainService abstractDomainService) {
		this.abstractDomainService = abstractDomainService;
	}

	public LogEntryService getLogEntryService() {
		return logEntryService;
	}

	public void setLogEntryService(LogEntryService logEntryService) {
		this.logEntryService = logEntryService;
	}

	public UserProviderService getUserProviderService() {
		return userProviderService;
	}

	public void setUserProviderService(UserProviderService userProviderService) {
		this.userProviderService = userProviderService;
	}
}
