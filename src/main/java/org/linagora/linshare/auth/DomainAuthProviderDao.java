/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.auth;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linshare.auth.exceptions.BadDomainException;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.UserService;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;


public class DomainAuthProviderDao extends AbstractUserDetailsAuthenticationProvider {
	
	private UserService userService;
	private AbstractDomainService abstractDomainService;
	private LogEntryService logEntryService;
	
    private final static Log logger = LogFactory.getLog(DomainAuthProviderDao.class);
    
	public AbstractDomainService getAbstractDomainService() {
		return abstractDomainService;
	}

	public void setAbstractDomainService(AbstractDomainService abstractDomainService) {
		this.abstractDomainService = abstractDomainService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public LogEntryService getLogEntryService() {
		return logEntryService;
	}

	public void setLogEntryService(LogEntryService logEntryService) {
		this.logEntryService = logEntryService;
	}
	
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
	}

	protected UserDetails retrieveUser(String login, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		logger.debug("Retrieving user detail for ldap authentication : " + login);
		
		String password = (String)authentication.getCredentials();
		if (password.isEmpty()) {
			logger.debug("User password is empty, authentification failed");
			throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
		
		
		String domainIdentifier = null;
		if (authentication.getDetails() != null && authentication.getDetails() instanceof String) {
			domainIdentifier = (String)authentication.getDetails();
		}
		User user = null;
		User foundUser = null;		
		
		// if domain was specified at the connection, we try to authenticate the user on this domain only
		if (domainIdentifier != null) {
			logger.debug("The domain was specified at the connection time : " + domainIdentifier);
			try {
				AbstractDomain domainObject = abstractDomainService.retrieveDomain(domainIdentifier);
				foundUser = abstractDomainService.auth(domainObject, login, password);
			} catch (NameNotFoundException e) {
				logger.debug("Can't find the user in the directory. Search in DB.");
				
				foundUser = userService.findUserInDB(domainIdentifier,login);
				try {
					if (foundUser != null && !foundUser.getAccountType().equals(AccountType.INTERNAL) && domainIdentifier.equals(foundUser.getDomainId())) {
						logger.debug("User found in DB but authentification failed");
						logEntryService.create(new UserLogEntry(foundUser, LogAction.USER_AUTH_FAILED, "Bad credentials", foundUser));
						throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), domainIdentifier);
					} else {
						logger.debug("Can't find the user in DB, BadDomainException for : " + domainIdentifier);
						logEntryService.create(new UserLogEntry(foundUser, LogAction.USER_AUTH_FAILED, "Bad domain", foundUser));
						throw new BadDomainException(e.getMessage(), domainIdentifier);
					}
				} catch (BusinessException be) {
					logger.error("Couldn't log an authentication failure");
					logger.debug(be.getMessage());
				}
			} catch (Exception e) {
				throw new AuthenticationServiceException("Could not authenticate user: "+login, e);
			}
			
			if(foundUser == null) {
				try {
					logEntryService.create(new UserLogEntry(foundUser, LogAction.USER_AUTH_FAILED, "Bad credentials", foundUser));
				} catch(BusinessException be) {
					logger.error("Couldn't log an authentication failure");
					logger.debug(be.getMessage());
				}
				throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
						"Bad credentials"), domainIdentifier);
			} 
		}
		
		/* 
		 * if the domain is not specified (invisible domains), we try to
		 * find the user in the DB to know its domain.
		 * if we can't find it (first login), we search all the domains.
		 * in the case of invisible domains, the user has to provide
		 * its email address and not LDAP uid to log in. 
		 */
		
		// TODO : To be check : Why do we test the presence of "@" ?
		if (domainIdentifier == null && login.indexOf("@") != -1) {
			try {
				
				foundUser = userService.findUnkownUserInDB(login);
				if (foundUser == null) {
					logger.debug("Can't find the user in DB. Searching user in all domains.");
					List<AbstractDomain> domains = abstractDomainService.getAllDomains();
					for (AbstractDomain loopedDomain : domains) {
						try {
							foundUser = abstractDomainService.auth(loopedDomain, login, password);
							if (foundUser != null) {
								domainIdentifier = loopedDomain.getIdentifier();
								logger.debug("User found in domain "+domainIdentifier);
								break;
							}
						} catch (NameNotFoundException e) {
							// just not found in this domain
							logger.error(e);
						} catch (IOException e) {
							//TLS negociation problem
							logger.error(e);
						} catch (NamingException e) {
							logger.error(e);
						}
					}
				} else {
					logger.debug("User found in DB : " + foundUser.getMail());
			
					if(foundUser.getDomain() == null) {
						try {
							logEntryService.create(new UserLogEntry(foundUser, LogAction.USER_AUTH_FAILED, "Bad credentials", foundUser));
						} catch(BusinessException be) {
							logger.error("Couldn't log an authentication failure");
							logger.debug(be.getMessage());
						}
						logger.error("The user found in the database contain a null domain reference.");
						throw new BadCredentialsException("Could not retrieve user : "+login);
					}
					
					domainIdentifier = foundUser.getDomain().getIdentifier();
					try {
						logger.debug("The user domain stored in DB was : " + domainIdentifier);
						foundUser = abstractDomainService.auth(foundUser.getDomain(), login, password);
					} catch (NameNotFoundException e) {
						try {
							logEntryService.create(new UserLogEntry(foundUser, LogAction.USER_AUTH_FAILED, "Bad domain", foundUser));
						} catch(BusinessException be) {
							logger.error("Couldn't log an authentication failure");
							logger.debug(e.getMessage());
						}
						throw new BadDomainException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
					} catch (IOException e) {
						throw new AuthenticationServiceException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
					} catch (NamingException e) {
						try {
							logEntryService.create(new UserLogEntry(foundUser, LogAction.USER_AUTH_FAILED, "Bad credentials", foundUser));
						} catch(BusinessException be) {
							logger.error("Couldn't log an authentication failure");
							logger.debug(be.getMessage());
						}
						throw new BadCredentialsException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
					}
				}
			} catch (BusinessException e) {
				throw new AuthenticationServiceException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
			}
		}
		
		// invisible domain and user not found (uid login or found in no domain)
		if (foundUser == null || domainIdentifier == null) {
			try {
				logEntryService.create(new UserLogEntry(login, LogAction.USER_AUTH_FAILED, "Bad credentials", login));
			} catch(BusinessException be) {
				logger.error("Couldn't log an authentication failure");
				logger.debug(be.getMessage());
			}
			throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials, no domain specified and user found in no domain"), domainIdentifier);
		}

		// invisible domain and user found or visible domain and user found
		try {
			user = userService.findOrCreateUser(foundUser.getMail(), domainIdentifier);
			
			// if we already have a guest with the same mail, and then, a domain with
			// this mail is added in linshare, when the domain user connects he should not
			// retrieve the guest account. if the two user are in the same domain, we can't
			// do anything I think...
			if (!domainIdentifier.equals(user.getDomainId())) {
				try {
					logEntryService.create(new UserLogEntry(user, LogAction.USER_AUTH_FAILED, "Bad domain", user));
				} catch(BusinessException be) {
					logger.error("Couldn't log an authentication failure");
					logger.debug(be.getMessage());
				}
				throw new BadDomainException("User "+user.getMail()+" was found but not in the domain referenced in DB (DB: "+user.getDomainId()+", found: "+domainIdentifier);
			}
			
		} catch (BusinessException e) {
			logger.error(e);
			throw new AuthenticationServiceException("Could not create user account: "+foundUser.getMail(), e);
		}

        List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(user);

		return new org.springframework.security.core.userdetails.User(user.getLsUuid(), "", true, true, true, true,
		                grantedAuthorities.toArray(new GrantedAuthority[0]));
	}
	
	
	 public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		 
		 Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
		            messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
		                "Only UsernamePasswordAuthenticationToken is supported"));

		        // Determine username
		        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

		        boolean cacheWasUsed = true;
		        UserDetails user = this.getUserCache().getUserFromCache(username);

		        if (user == null) {
		            cacheWasUsed = false;

		            try {
		                user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
		            } catch (UsernameNotFoundException notFound) {
		                if (hideUserNotFoundExceptions) {
		                    throw new BadCredentialsException(messages.getMessage(
		                            "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		                } else {
		                    throw notFound;
		                }
		            }

		            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
		        }

		        this.getPreAuthenticationChecks().check(user);
		        
		        try {
		            additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
		        } catch (AuthenticationException exception) {
		            if (cacheWasUsed) {
		                // There was a problem, so try again after checking
		                // we're using latest data (ie not from the cache)
		                cacheWasUsed = false;
		                user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
		                additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
		            } else {
		                throw exception;
		            }
		        }

		        this.getPostAuthenticationChecks().check(user);

		        if (!cacheWasUsed) {
		            this.getUserCache().putUserInCache(user);
		        }

		        Object principalToReturn = user;

		        if (this.isForcePrincipalAsString()) {
		            principalToReturn = user.getUsername();
		        }

		        return createSuccessAuthentication(principalToReturn, authentication, user);	 }

}
