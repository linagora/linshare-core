/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.auth;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linShare.auth.exceptions.BadDomainException;
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.UserProviderService;
import org.linagora.linShare.core.service.UserService;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class DomainAuthProviderDao extends AbstractUserDetailsAuthenticationProvider {
	
	private UserService userService;
	private AbstractDomainService abstractDomainService;
	
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
	
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
	}

	protected UserDetails retrieveUser(String login, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		logger.debug("Retrieving user detail for ldap authentication : " + login);
		
		String password = (String)authentication.getCredentials();
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
				if (foundUser != null && !foundUser.getUserType().equals(UserType.INTERNAL) && domainIdentifier.equals(foundUser.getDomainId())) {
					logger.debug("User found in DB but authentification failed");
					throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
			          "Bad credentials"), domainIdentifier);
				} else {
					logger.debug("Can't find the user in DB, BadDomainException for : " + domainIdentifier);
					throw new BadDomainException(e.getMessage(), domainIdentifier);
				}
			} catch (Exception e) {
				throw new AuthenticationServiceException("Could not authenticate user: "+login, e);
			}
			
			if(foundUser == null) {
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
						logger.error("The user found in the database contain a null domain reference.");
						throw new BadCredentialsException("Could not retrieve user : "+login);
					}
					
					domainIdentifier = foundUser.getDomain().getIdentifier();
					try {
						logger.debug("The user domain stored in DB was : " + domainIdentifier);
						foundUser = abstractDomainService.auth(foundUser.getDomain(), login, password);
					} catch (NameNotFoundException e) {
						throw new BadDomainException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
					} catch (IOException e) {
						throw new AuthenticationServiceException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
					} catch (NamingException e) {
						throw new BadCredentialsException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
					}
				}
			} catch (BusinessException e) {
				throw new AuthenticationServiceException("Could not retrieve user : "+login+" in domain : "+domainIdentifier, e);
			}
		}
		
		// invisible domain and user not found (uid login or found in no domain)
		if (foundUser == null || domainIdentifier == null) {
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
				throw new BadDomainException("User "+user.getLogin()+" was found but not in the domain referenced in DB (DB: "+user.getDomainId()+", found: "+domainIdentifier);
			}
			
		} catch (BusinessException e) {
			logger.error(e);
			throw new AuthenticationServiceException("Could not create user account: "+foundUser.getMail(), e);
		}

        List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(user);

		return new org.springframework.security.userdetails.User(user.getLogin(), "", true, true, true, true,
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
