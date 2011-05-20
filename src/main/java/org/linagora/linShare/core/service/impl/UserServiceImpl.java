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
package org.linagora.linShare.core.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.core.domain.entities.AllowedContact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserLogEntry;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.AllowedContactRepository;
import org.linagora.linShare.core.repository.GuestRepository;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.GroupService;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.core.service.MailContentBuildingService;
import org.linagora.linShare.core.service.NotifierService;
import org.linagora.linShare.core.service.RecipientFavouriteService;
import org.linagora.linShare.core.service.ShareService;
import org.linagora.linShare.core.service.UserService;
import org.linagora.linShare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Services for User management.
 */
public class UserServiceImpl implements UserService {

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    /** User repository. */
    private final UserRepository<User> userRepository;

    /** User repository. */
    private final GuestRepository guestRepository;
    
    private final AllowedContactRepository allowedContactRepository;

    /** Notifier service. */
    private final NotifierService notifierService;
    
  //  private final SharedDocumentService sharedDocumentService;
    
    private final LogEntryRepository logEntryRepository;
    
    private final ShareService shareService;
    
    private final RecipientFavouriteService recipientFavouriteService;
    
    private final MailContentBuildingService mailElementsFactory;
    
    private final GroupService groupService;
    
    private final FileSystemDao fileSystemDao;
    
    private final LDAPQueryService ldapQueryService;
    
    private final DomainService domainService;

    /** Constructor.
     * @param userRepository repository.
     * @param notifierService notifier service.
     * @param ldapDao LDAP DAO.
     */
    public UserServiceImpl(final UserRepository userRepository,
    		final NotifierService notifierService, 
    		final LogEntryRepository logEntryRepository,
    		final GuestRepository guestRepository, 
    		final ShareService shareService,
    		final RecipientFavouriteService recipientFavouriteService,
    		final AllowedContactRepository allowedContactRepository,
    		final MailContentBuildingService mailElementsFactory,
    		final GroupService groupService,
    		final FileSystemDao fileSystemDao,
    		final LDAPQueryService ldapQueryService,
    		final DomainService domainService) {
        this.userRepository = userRepository;
        this.notifierService = notifierService;
        this.logEntryRepository = logEntryRepository;
        this.guestRepository = guestRepository;
		this.shareService = shareService;
		this.recipientFavouriteService = recipientFavouriteService;
		this.allowedContactRepository = allowedContactRepository;
		this.mailElementsFactory = mailElementsFactory;
		this.groupService = groupService;
		this.fileSystemDao = fileSystemDao;
		this.ldapQueryService = ldapQueryService;
		this.domainService = domainService;
    }

    /** Create a guest.
     * @param login login.
     * @param firstName first name.
     * @param lastName last name.
     * @param mail guest email address.
     * @param canUpload : if the user can upload file
     * @param canCreateGuest : if the user can create other users
     * @param mailContainer : the informations for the notification
     * @param ownerLogin login of the user who create the guest.
     * @return persisted guest.
     */
    public Guest createGuest(String login, String firstName, String lastName, String mail, Boolean canUpload, Boolean canCreateGuest, String comment,
    		MailContainer mailContainer, String ownerLogin, String ownerDomain) throws BusinessException {

    	//We need to check that the guest email isn't registered
    	
    	if (findUser(mail, ownerDomain) != null) {
    		throw new BusinessException(BusinessErrorCode.DUPLICATE_USER_ENTRY, "A user with the same email already exists");
    	}

    	// generate a password.
        String password = generatePassword();


        String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());

        User owner = userRepository.findByLogin(ownerLogin);

        Guest guest = new Guest(login, firstName, lastName, mail, hashedPassword, canUpload, canCreateGuest, comment);
        guest.setOwner(owner);
		guest.setExpiryDate(calculateUserExpiryDate(owner.getDomain()));
		Guest created = guestRepository.create(guest);
		Domain domain = domainService.retrieveDomain(owner.getDomain().getIdentifier());
		created.setDomain(domain);
		guestRepository.update(created);
        
        Calendar expDate = new GregorianCalendar();
        expDate.setTime(guest.getExpiryDate());
        UserLogEntry logEntry = new UserLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
        		LogAction.USER_CREATE, "Creation of a guest", guest.getMail(), guest.getFirstName(), guest.getLastName(), expDate);
        
        logEntryRepository.create(logEntry);
        
        mailContainer = mailElementsFactory.buildMailNewGuest(owner, mailContainer, owner, guest, password);

        // Send an email to the guest.
        notifierService.sendNotification(owner.getMail(), mail, mailContainer);

        return guest;
    }

 /** Find a user (based on mail address).
     * Search first in database, then on ldap if not found.
     * @param login user login.
     * @return founded user.
 * @throws BusinessException 
     */
    public User findUserInDB(String mail) {
        return userRepository.findByMail(mail);
    }
    public User findUser(String mail, String domain) throws BusinessException {
        User user = userRepository.findByMail(mail);
        if (user == null) {
            List<User> users = domainService.searchUser(mail, "", "", domain, null);
            if (users != null && users.size() == 1) {
            	user = users.get(0);
            }
        }
        return user;
    }
    public User findUser(String mail, String domain, User actor) throws BusinessException {
        User user = userRepository.findByMail(mail);
        if (user == null) {
            List<User> users = domainService.searchUser(mail, "", "", domain, actor);
            if (users != null && users.size() == 1) {
            	user = users.get(0);
            }
        }
        return user;
    }
    

    /** Find a user (based on mail address).
     * Search first in database, then on ldap if not found, and create him
     * The User MUST exist
     * @param mail user mail.
     * @return founded user.
     * @throws BusinessException 
     * @throws TechnicalException if the user could not be found nor created 
     */ 
    public User findAndCreateUser(String mail, String domainId) throws BusinessException {
        User user = userRepository.findByMail(mail);
        if (user == null && domainId != null) {
            List<User> users = domainService.searchUser(mail, "", "", domainId, null);
            if (users!=null && users.size()==1) {
            	user = users.get(0);
            	try {
					user = userRepository.create(user);
				} catch (IllegalArgumentException e) {
					logger.error("Could not create the user " + user.getLogin()+" in the database ", e);
					throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user could not be created in the DB " + e);
				} catch (BusinessException e) {
					logger.error("Could not create the user " + user.getLogin()+" in the database ", e);
					throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user could not be created in the DB " + e);
				}
            } else {
            	logger.error("Could not find the user " + mail +" in the database nor in the LDAP");
            	// this should really not happened
            	throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The user could not be found in the DB nor in the LDAP");
            }
        }
        return user;
    }
    
    /** Calculate the user expiry date.
     * @return user expiry date.
     */
    private Date calculateUserExpiryDate(Domain domain) {
        Calendar expiryDate = Calendar.getInstance();
        Parameter parameter = domain.getParameter();


        if (parameter == null) 
            throw new IllegalStateException("No configuration found for linshare");
        expiryDate.add(parameter.getGuestAccountExpiryUnit().toCalendarValue(), parameter.getGuestAccountExpiryTime());
        
        return expiryDate.getTime();
    }
    
    /** Generate a password for guest user. */
    public String generatePassword() {

   		SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
        	logger.error("Algorithm \"SHA1PRNG\" not supported");
            throw new TechnicalException("Algorithm \"SHA1PRNG\" not supported");
        }

        return Long.toString(sr.nextLong() & Long.MAX_VALUE , 36 );
    }

	public void deleteUser(String login, User owner, boolean checkOwnership) throws BusinessException {
		User userToDelete = userRepository.findByLogin(login);
		
		if (userToDelete!=null) {
			boolean hasRightToDeleteThisUser = isAdminForThisUser(owner, userToDelete, checkOwnership);
			
			logger.debug("As right ? : "+hasRightToDeleteThisUser);
			
			if (!hasRightToDeleteThisUser) {
				throw new BusinessException(BusinessErrorCode.CANNOT_DELETE_USER, "The user " + login 
						+" cannot be deleted, he is not a guest, or "+ owner.getLogin()+ " is not an admin");
			} else {
				doDeleteUser(login, owner, userToDelete);
			}
			
		}
	}

	private boolean isAdminForThisUser(User owner, User userToAdministrate, boolean checkOwnership) {
		boolean hasRightToAdministrateThisUser = owner.getRole().equals(Role.SUPERADMIN) 
			|| owner.getRole().equals(Role.SYSTEM)
			|| (owner.getRole().equals(Role.ADMIN) && owner.getDomain().getIdentifier().equals(userToAdministrate.getDomain().getIdentifier()))
			|| (checkOwnership && userToAdministrate instanceof Guest && ((Guest)userToAdministrate).getOwner().equals(owner));
		
		return hasRightToAdministrateThisUser;
	}

	private void doDeleteUser(String login, User owner, User userToDelete)
			throws BusinessException {
		try {
			// The list of all document that were in the received shares
			Set<Document> documentsToClean = new HashSet<Document>();
			
			
			// clearing received shares
			Set<Share> receivedShare = userToDelete.getReceivedShares();
			
			for (Share share : receivedShare) {
				ShareLogEntry logEntry = new ShareLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
		        		LogAction.SHARE_DELETE, "Deleting a user-Removing shares", 
		        		share.getDocument().getName(),share.getDocument().getSize(),share.getDocument().getType(),
		        		userToDelete.getMail(), 
		        		userToDelete.getFirstName(), userToDelete.getLastName(), null);
				 logEntryRepository.create(logEntry);
				 documentsToClean.add(share.getDocument());
			}
			
			receivedShare.clear();
			
			// clearing sent urls
			Set<SecuredUrl> sentUrls = userToDelete.getSecuredUrls();
			for (SecuredUrl url : sentUrls) {
				String docs = "";
				for (Document doc : url.getDocuments()) {
					docs += doc.getName()+";";
				}
				ShareLogEntry logEntry = new ShareLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
		        		LogAction.SHARE_DELETE, "Deleting a user-Removing url shares", 
		        		docs,null,null,
		        		userToDelete.getMail(), 
		        		userToDelete.getFirstName(), userToDelete.getLastName(), null);
				 logEntryRepository.create(logEntry);
			}
			sentUrls.clear();
			
			// clearing sent shares
			Set<Share> sentShare = userToDelete.getShares();
			
			for (Share share : sentShare) {
				ShareLogEntry logEntry = new ShareLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
		        		LogAction.SHARE_DELETE, "Deleting of a guest-Removing shares", 
		        		share.getDocument().getName(),share.getDocument().getSize(),share.getDocument().getType(),
		        		userToDelete.getMail(), 
		        		userToDelete.getFirstName(), userToDelete.getLastName(), null);
				 logEntryRepository.create(logEntry);
			}
			
			sentShare.clear();
			
			//clearing user documents
			Set<Document> documents = userToDelete.getDocuments();
			for (Document document : documents) {
				String fileUUID = document.getIdentifier();
				String thumbnailUUID = document.getThmbUUID();
				if (thumbnailUUID != null && thumbnailUUID.length()>0) {
					fileSystemDao.removeFileByUUID(thumbnailUUID);
				}
				fileSystemDao.removeFileByUUID(fileUUID);
				FileLogEntry logEntry = new FileLogEntry(owner.getMail(), 
						owner.getFirstName(), owner.getLastName(),
						LogAction.USER_DELETE, "User deleted", document.getName(), 
						document.getSize(), document.getType());
				logEntryRepository.create(logEntry);
			}
			
			//clearing the favorites
			recipientFavouriteService.deleteFavoritesOfUser(userToDelete);
			
			//clearing allowed contacts
			allowedContactRepository.deleteAllByUserBothSides(userToDelete);
			
			//clearing groups memberships
			groupService.deleteAllMembershipOfUser(userToDelete);
			
			// clearing all signatures
			Set<Signature> ownSignatures = userToDelete.getOwnSignatures();
			ownSignatures.clear();
			
			//a guest can create guest (since evolution guest with grant privilege)...
			//so when deleting a guest (A) you may need to delete the guests (B, C, D) which were created by this guest (A).
			//to fix this: deleting a guest means you will be the new owner of the guest account which were created (B, C, D)
			List<Guest> usersCreatedByTheUserToDelete = guestRepository.searchGuest(null, null, null, userToDelete);
			for (Guest guest : usersCreatedByTheUserToDelete) {
				guest.setOwner(owner);
				guestRepository.update(guest);
				if (guest.isRestricted()) { //if restricted guest, needs to have the new owner as contact
					addGuestContactRestriction(guest.getLogin(), owner.getLogin());
				}
			}
			
			
			userRepository.update(userToDelete);
			userRepository.delete(userToDelete);
			UserLogEntry logEntry = new UserLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
		        		LogAction.USER_DELETE, "Deleting an user", userToDelete.getMail(), 
		        		userToDelete.getFirstName(), userToDelete.getLastName(), null);
      
		    logEntryRepository.create(logEntry);
		    
		    for (Document document : documentsToClean) {
		    	shareService.refreshShareAttributeOfDoc(document);
				
			}
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't find the user " + login +" to be deleted", e);
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Couldn't find the user " + login +" to be deleted");
		}
	}

    /** Clean outdated guest accounts.
     * @throws BusinessException
     */
    public void cleanExpiredGuestAcccounts() {
        User owner = userRepository.findByLogin("system");
        List<Guest> guests = guestRepository.findOutdatedGuests();
        logger.info(guests.size() + " guest(s) have been found to be removed");
        for (User guest : guests) {
            try {
                deleteUser(guest.getLogin(), owner, false);
                logger.info("Removed expired user : " + guest.getLogin());
            } catch (BusinessException ex) {
                logger.warn("Unable to remove expired user : " + guest.getLogin() + "\n" + ex.toString());
            }
        }
    }

	public List<User> searchUser(String mail, String firstName,
			String lastName, UserType userType, User currentUser) throws BusinessException {
		List<User> users=new ArrayList<User>();
		
		if (currentUser !=null && currentUser.getUserType()==UserType.GUEST){ //GUEST RESTRICTED MUST NOT SEE ALL USERS
			Guest currentGuest = guestRepository.findByLogin(currentUser.getLogin());
			if (currentGuest.isRestricted() == true) {
				List<AllowedContact> contacts = allowedContactRepository.searchContact(mail, firstName, lastName, currentGuest);
				for (AllowedContact allowedContact : contacts) {
					if (allowedContact.getContact().getUserType().equals(UserType.GUEST)) {
						Guest guest = guestRepository.findByLogin(allowedContact.getContact().getLogin());
						users.add(guest);
					}
					else {
						users.add(allowedContact.getContact());
					}
				}
				return users;
			}
		}
		
		if(null==userType || userType.equals(UserType.GUEST)){
			List<Guest> guests = null;
	        if (currentUser !=null && currentUser.getUserType()==UserType.GUEST){
	        	//if guest type, we give only the account he has created 
	        	guests = guestRepository.searchGuestAnyWhere(mail, firstName, lastName, currentUser.getLogin());
	        }	else {
	        	guests = guestRepository.searchGuestAnyWhere(mail, firstName, lastName, null);
	        }
	        users.addAll(guests);
		}
		if(null==userType || userType.equals(UserType.INTERNAL)){
			String domainId = (currentUser.getDomain() == null) ? null : currentUser.getDomain().getIdentifier();
			List<User> internals = domainService.searchUser(mail, firstName, lastName, domainId, currentUser);
        
			//need linshare local information for these internals user
			for (User ldapuser : internals) {
				User userdb = userRepository.findByMail(ldapuser.getMail());
				if (userdb!=null)  ldapuser.setRole(userdb.getRole());
			}
        
			users.addAll(internals);
		}

		return users;
	}

	public void updateGuest(String mail, String firstName, String lastName,
			Boolean canUpload, Boolean canCreateGuest, UserVo ownerVo)
			throws BusinessException {
		
		Guest guest = guestRepository.findByLogin(mail);
		User owner = userRepository.findByMail(ownerVo.getMail());
		
		boolean hasRightToDeleteThisUser = isAdminForThisUser(owner, guest, true);
		
		if (!hasRightToDeleteThisUser) {
			logger.error("The user " + mail +" cannot be updated by "+owner.getMail());
			throw new BusinessException(BusinessErrorCode.CANNOT_UPDATE_USER, "The user " + mail 
					+" cannot be deleted, he is not a guest, or "+ owner.getLogin()+ " is not an admin");
		}
        
		guest.setFirstName(firstName);
		guest.setLastName(lastName);
		guest.setCanUpload(canUpload);
		guest.setCanCreateGuest(canCreateGuest);
        guestRepository.update(guest);

        UserLogEntry logEntry = new UserLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
        		LogAction.USER_UPDATE, "Update of a guest", guest.getMail(), guest.getFirstName(), guest.getLastName(), null);
        
        logEntryRepository.create(logEntry);

	}

	
	public void updateUser(String mail,Role role, UserVo ownerVo) throws BusinessException{
		
        User owner = userRepository.findByLogin(ownerVo.getLogin());
        Domain domain = owner.getDomain();
		
		if ((domain == null || domain.getIdentifier() == null) 
				&& owner.getRole() == Role.SUPERADMIN) {
			domain = guessUserDomain(mail, owner);
		}
		
		//search in database internal user, next in ldap, create it in db if needed
		if (domain == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find user in db nor in domains " + mail);
		}
		
		User user = findAndCreateUser(mail, domain.getIdentifier());
		
		user.setRole(role);
		userRepository.update(user);
		
		
        UserLogEntry logEntry = new UserLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
        		LogAction.USER_UPDATE, "Update role of a user", user.getMail(), user.getFirstName(), user.getLastName(), null);
        
        logEntryRepository.create(logEntry);
		
	}

	private Domain guessUserDomain(String mail, User owner) {
		User foundUser = null;
		Domain domain = null;
		try {
			foundUser = findUser(mail, null);
			if (foundUser == null) {
				List<Domain> domains = domainService.findAllDomains();
				for (Domain loopedDomain : domains) {
					List<User> founds = ldapQueryService.searchUser(mail, "", "", loopedDomain, owner);
					if (founds != null && founds.size() == 1) {
						foundUser = founds.get(0);
						domain = foundUser.getDomain();
						break;
					}
				}
			} else {
				domain = foundUser.getDomain();
			}
		} catch (BusinessException e) {
			// cannot be because throwed when domain != null
			logger.error("BusinessException while trying to find the user", e);
		}
		return domain;
	}
	
	public void updateUserLocale(String mail, String locale) {
		
		 User user = userRepository.findByMail(mail);
		 if (user == null) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user " + mail);
		 }
		 user.setLocale(locale);
		 try {
			user = userRepository.update(user);
		} catch (IllegalArgumentException e) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user " + mail);
		} catch (BusinessException e) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't save the locale " + locale);
		}
		
	}

	public void updateUserEnciphermentKey(String mail, byte[] challenge) {
		 User user = userRepository.findByMail(mail);
		 if (user == null) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user " + mail);
		 }
		 user.setEnciphermentKeyPass(challenge);
		 try {
			user = userRepository.update(user);
		} catch (IllegalArgumentException e) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user " + mail);
		} catch (BusinessException e) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't save the Encipherment Key challenge for "+ mail);
		}
		
	}
	
	public void changePassword(String login, String oldPassword, String newPassword) throws BusinessException {
		User user = userRepository.findByLogin(login);
		if (user == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a user with the login " + login);
		}
		
		if (!user.getPassword().equals(HashUtils.hashSha1withBase64(oldPassword.getBytes()))) {
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_ERROR, "The supplied password is invalid");
		}
		
		user.setPassword(HashUtils.hashSha1withBase64(newPassword.getBytes()));
		userRepository.update(user);
	}

	public void resetPassword(String login, MailContainer mailContainer) throws BusinessException {
		Guest guest = guestRepository.findByLogin(login);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		}
		
		// generate a password.
        String password = generatePassword();
        String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());
        
        mailContainer = mailElementsFactory.buildMailResetPassword(guest, mailContainer, guest, password);

        // Send an email to the guest.
        notifierService.sendNotification(guest.getMail(), guest.getMail(), mailContainer);
        
		guest.setPassword(hashedPassword);
		guestRepository.update(guest);
	}
	
	public void removeGuestContactRestriction(String login) throws BusinessException {
		Guest guest = guestRepository.findByLogin(login);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		}
		
		//clean contacts
		List<AllowedContact> precedents = allowedContactRepository.findByOwner(guest);
		if (precedents!=null && !precedents.isEmpty()) {
			for (AllowedContact allowedContact : precedents) {
				allowedContactRepository.delete(allowedContact);
			}
		}
		
		try {
			guest.setRestricted(false);
			guestRepository.update(guest);
		} catch (IllegalArgumentException e1) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		} catch (BusinessException e1) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not update guest restriction of " + login);
		}
	}
	

	public void addGuestContactRestriction(String ownerLogin, String contactLogin) throws BusinessException {

		Guest guest = guestRepository.findByLogin(ownerLogin);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + ownerLogin);
		}
		
		try {
			User contact = findAndCreateUser(contactLogin, guest.getDomain().getIdentifier());
			AllowedContact allowedContact = new AllowedContact(guest, contact);
			allowedContactRepository.create(allowedContact);
		} catch (IllegalArgumentException e) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user " + contactLogin);
		} catch (BusinessException e) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Could not add the contact restriction");
		}
	}
	
	public void setGuestContactRestriction(String login, List<String> mailContacts) throws BusinessException {
		Guest guest = guestRepository.findByLogin(login);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		}
		List<AllowedContact> precedents = new ArrayList<AllowedContact>();
		try {
			//clean actual contacts
			precedents.addAll(allowedContactRepository.findByOwner(guest));
			if (precedents!=null && !precedents.isEmpty()) {
				for (AllowedContact allowedContact : precedents) {
					allowedContactRepository.delete(allowedContact);
				}
			}
			//add new contacts
			for (String mailContact : mailContacts) {
				User contact=findAndCreateUser(mailContact, guest.getDomain().getIdentifier());
				AllowedContact allowedContact = new AllowedContact(guest, contact);
				allowedContactRepository.create(allowedContact);
			}
			//set boolean restricted
			guest.setRestricted(true);
			guestRepository.update(guest);
		} catch (IllegalArgumentException e1) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't set contacts restriction for user " + login);
		} catch (BusinessException e1) {
			for (AllowedContact entity : precedents) { //set old contacts list
				allowedContactRepository.create(entity);				
			}
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Couldn't set contacts restriction for user " + login);
		}
	}
	
	public List<User> fetchGuestContacts(String login) throws BusinessException {
		Guest guest = guestRepository.findByLogin(login);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		}
		if (!guest.isRestricted()) {
			return null;
		}
		List<User> contactsUsers = new ArrayList<User>();
		List<AllowedContact> contacts = allowedContactRepository.findByOwner(guest);
		for (AllowedContact allowedContact : contacts) {
			contactsUsers.add(allowedContact.getContact());
		}
		
		return contactsUsers;
	}
}
