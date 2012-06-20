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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.LogAction;
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.AllowedContact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.Functionality;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.GuestDomain;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserLogEntry;
import org.linagora.linShare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.AllowedContactRepository;
import org.linagora.linShare.core.repository.GuestRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.FunctionalityService;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.core.service.LogEntryService;
import org.linagora.linShare.core.service.MailContentBuildingService;
import org.linagora.linShare.core.service.NotifierService;
import org.linagora.linShare.core.service.PasswordService;
import org.linagora.linShare.core.service.RecipientFavouriteService;
import org.linagora.linShare.core.service.ShareService;
import org.linagora.linShare.core.service.UserService;
import org.linagora.linShare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Services for User management.
 */
public class UserServiceImpl implements UserService {

	final private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
    /** User repository. */
    private final UserRepository<User> userRepository;

    /** User repository. */
    private final GuestRepository guestRepository;
    
    private final AllowedContactRepository allowedContactRepository;

    /** Notifier service. */
    private final NotifierService notifierService;
    
    private final LogEntryService logEntryService;
    
    private final ShareService shareService;
    
    private final RecipientFavouriteService recipientFavouriteService;
    
    private final MailContentBuildingService mailElementsFactory;
    
    private final FileSystemDao fileSystemDao;
    
    private final LDAPQueryService ldapQueryService;
    
    private final AbstractDomainService abstractDomainService;
    
    private final FunctionalityService functionalityService;
    private final PasswordService passwordService;

    /** Constructor.
     * @param userRepository repository.
     * @param notifierService notifier service.
     * @param ldapDao LDAP DAO.
     */
    public UserServiceImpl(final UserRepository userRepository,
    		final NotifierService notifierService, 
    		final LogEntryService logEntryService,
    		final GuestRepository guestRepository, 
    		final ShareService shareService,
    		final RecipientFavouriteService recipientFavouriteService,
    		final AllowedContactRepository allowedContactRepository,
    		final MailContentBuildingService mailElementsFactory,
    		final FileSystemDao fileSystemDao,
    		final LDAPQueryService ldapQueryService,
    		final FunctionalityService functionalityService,
    		final AbstractDomainService abstractDomainService,
    		final PasswordService passwordService) {
        this.userRepository = userRepository;
        this.notifierService = notifierService;
        this.logEntryService = logEntryService;
        this.guestRepository = guestRepository;
		this.shareService = shareService;
		this.recipientFavouriteService = recipientFavouriteService;
		this.allowedContactRepository = allowedContactRepository;
		this.mailElementsFactory = mailElementsFactory;
		this.fileSystemDao = fileSystemDao;
		this.ldapQueryService = ldapQueryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
		this.passwordService = passwordService;
		
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
    @Override
    public Guest createGuest(String login, String firstName, String lastName, String mail, Boolean canUpload, Boolean canCreateGuest, String comment,
    		MailContainer mailContainer, String ownerLogin, String ownerDomain) throws BusinessException {

    	AbstractDomain domain = abstractDomainService.retrieveDomain(ownerDomain);
    	
		if(domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"Domain was not found");
		}
		
		User ownerUser = findUserInDB(ownerDomain, ownerLogin);
		if(ownerUser == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,"Owner was not found");
		}
		
		if(!abstractDomainService.userCanCreateGuest(ownerUser)) {
			throw new BusinessException(BusinessErrorCode.USER_CANNOT_CREATE_GUEST,"Owner can not create guest");
		}
		
		GuestDomain guestDomain = abstractDomainService.getGuestDomain(ownerDomain);
		if(guestDomain!=null) {
			
			//We need to check that the guest email isn't registered
			List<User> listUsers= abstractDomainService.searchUserRecursivelyWithoutRestriction(ownerDomain,mail,"","");
			if(listUsers != null) {
				if(listUsers.size() > 0) {
					throw new BusinessException(BusinessErrorCode.DUPLICATE_USER_ENTRY, "A user with the same email already exists");
				}
			}
			
			logger.debug("We can create guest, all checks are ok.");
			logger.debug("guest mail :" + mail);
			// generate a password.
			String password = passwordService.generatePassword();
			
			String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());
			
			User owner = userRepository.findByMail(ownerLogin);
			
			Guest guest = new Guest(firstName, lastName, mail, hashedPassword, canUpload, comment);
			guest.setDomain(guestDomain);
			guest.setOwner(owner);
			guest.setComment(comment);
			guest.setLsUid(generateGuestLsUid(guestDomain,mail));
			
			
			// Guest must not be able to create other guests.
			guest.setCanCreateGuest(false);
			
			Functionality userCanUploadFunc = functionalityService.getUserCanUploadFunctionality(guestDomain);
			guest.setCanUpload(userCanUploadFunc.getActivationPolicy().getStatus());
			
			guest.setCreationDate(new Date());
			guest.setLocale(guestDomain.getDefaultLocale());
			guest.setExpirationDate(calculateUserExpiryDate(guestDomain));
			
			
			guestRepository.create(guest);
			
			Calendar expDate = new GregorianCalendar();
			expDate.setTime(guest.getExpirationDate());
			UserLogEntry logEntry = new UserLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(), owner.getDomainId(),
					LogAction.USER_CREATE, "Creation of a guest", guest.getMail(), guest.getFirstName(), guest.getLastName(), guest.getDomainId(), expDate);
			
			logEntryService.create(logEntry);
			
			
			// Send an email to the guest.
			notifierService.sendAllNotifications(mailElementsFactory.buildMailNewGuestWithOneRecipient(owner, mailContainer, owner, guest, password));
			logger.info("Guest " + mail + " was successfully created.");
			return guest;
		} else {
			logger.error("Can not create guest : no guest domain created.");
		}
		return null;
		
    }

    @Override
    public User findUserInDB(String domain, String mail) {
        return userRepository.findByMailAndDomain(domain, mail);
    }
    
    @Override
    public User findUnkownUserInDB( String mail) {
    	return userRepository.findByMail(mail);
    }
    
    @Override
    public List<User> findUsersInDB(String domain) {
    	return userRepository.findByDomain(domain);
    }
    
    /** Calculate the user expiry date.
     * @return user expiry date.
     */
    private Date calculateUserExpiryDate(AbstractDomain domain) {
        Calendar expiryDate = Calendar.getInstance();

        TimeUnitValueFunctionality func = functionalityService.getGuestAccountExpiryTimeFunctionality(domain);
        expiryDate.add(func.toCalendarValue(), func.getValue());
        
        return expiryDate.getTime();
    }
    
    @Override
	public void deleteUser(String login, User actor) throws BusinessException {
		User userToDelete = userRepository.findByMail(login);
		
		if (userToDelete!=null) {
			boolean hasRightToDeleteThisUser = isAdminForThisUser(actor, userToDelete.getDomainId(), userToDelete.getMail());
			
			logger.debug("As right ? : "+hasRightToDeleteThisUser);
			
			if (!hasRightToDeleteThisUser) {
				throw new BusinessException(BusinessErrorCode.CANNOT_DELETE_USER, "The user " + login 
						+" cannot be deleted, he is not a guest, or "+ actor.getMail()+ " is not an admin");
			} else {
				doDeleteUser( actor, userToDelete);
			}
			
		}else{
			logger.debug("User not found in DB : " + login);
		}
	}
    
    @Override
	public void deleteAllUsersFromDomain(User actor, String domainIdentifier) throws BusinessException {
    	logger.debug("deleteAllUsersFromDomain: begin");
    	
    	List<User> users = userRepository.findByDomain(domainIdentifier);
    	
		logger.info("Delete all user from domain " + domainIdentifier + ", count: "+ users.size() );
    	
    	for (User user : users) {
    		doDeleteUser( actor, user);
		}
    	
    	logger.debug("deleteAllUsersFromDomain: end");
    }
        
    
	public boolean isAdminForThisUser(User actor, String userDomainToManage, String userMailToManage) {
		if(actor.getRole().equals(Role.SUPERADMIN)) {
			return true;
		} else if(actor.getRole().equals(Role.SYSTEM)) {
			return true;
		} else if(actor.getRole().equals(Role.ADMIN)) {
			List<String> allMyDomain = abstractDomainService.getAllMyDomainIdentifiers(actor.getDomain().getIdentifier());
			for (String domain : allMyDomain) {
				if(domain.equals(userDomainToManage)) {
					return true;
				}
			}
		}
		
		User user = findUserInDB(userDomainToManage, userMailToManage);
		if(user instanceof Guest) {
			// At this point the actor object could be an entity or a proxy. No idea why it happens. 
			// That is why we compare IDs.
			if(actor.getId() == ((Guest)user).getOwner().getId()) {
				return true;
			}
		}
		return false;
	}

	private void doDeleteUser(User actor, User userToDelete) throws BusinessException {
		try {
			// The list of all document that were in the received shares
			Set<Document> documentsToClean = new HashSet<Document>();
			
			
			// clearing received shares
			Set<Share> receivedShare = userToDelete.getReceivedShares();
			
			for (Share share : receivedShare) {
				ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
						actor.getDomainId(),
		        		LogAction.SHARE_DELETE, "Deleting a user-Removing shares", 
		        		share.getDocument().getName(),share.getDocument().getSize(),share.getDocument().getType(),
		        		userToDelete.getMail(), 
		        		userToDelete.getFirstName(), userToDelete.getLastName(), userToDelete.getDomainId(), null);
				 logEntryService.create(logEntry);
				 documentsToClean.add(share.getDocument());
			}
			
			// clearing sent shares
			Set<Share> sentShare = userToDelete.getShares();
			
			for (Share share : sentShare) {
				ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
						actor.getDomainId(),
		        		LogAction.SHARE_DELETE, "Deleting of a guest-Removing shares", 
		        		share.getDocument().getName(),share.getDocument().getSize(),share.getDocument().getType(),
		        		userToDelete.getMail(), 
		        		userToDelete.getFirstName(), userToDelete.getLastName(), userToDelete.getDomainId(), null);
				 logEntryService.create(logEntry);
			}
			
			// clearing sent urls
			Set<SecuredUrl> sentUrls = userToDelete.getSecuredUrls();
			for (SecuredUrl url : sentUrls) {
				String docs = "";
				for (Document doc : url.getDocuments()) {
					docs += doc.getName()+";";
				}
				ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
						actor.getDomainId(),
		        		LogAction.SHARE_DELETE, "Deleting a user-Removing url shares", 
		        		docs,null,null,
		        		userToDelete.getMail(), 
		        		userToDelete.getFirstName(), userToDelete.getLastName(), userToDelete.getDomainId(), null);
				 logEntryService.create(logEntry);
			}
			
			sentShare.clear();
			receivedShare.clear();
			sentUrls.clear();
			
			userRepository.update(userToDelete);
			
			//clearing user documents
			Set<Document> documents = userToDelete.getDocuments();
			for (Document document : documents) {
				String fileUUID = document.getIdentifier();
				String thumbnailUUID = document.getThmbUUID();
				if (thumbnailUUID != null && thumbnailUUID.length()>0) {
					fileSystemDao.removeFileByUUID(thumbnailUUID);
				}
				fileSystemDao.removeFileByUUID(fileUUID);
				FileLogEntry logEntry = new FileLogEntry(actor.getMail(), 
						actor.getFirstName(), actor.getLastName(),
						actor.getDomainId(),
						LogAction.USER_DELETE, "User deleted", document.getName(), 
						document.getSize(), document.getType());
				logEntryService.create(logEntry);
			}
			
			//clearing the favorites
			recipientFavouriteService.deleteFavoritesOfUser(userToDelete);
			
			//clearing allowed contacts
			allowedContactRepository.deleteAllByUserBothSides(userToDelete);
			
			// clearing all signatures
			Set<Signature> ownSignatures = userToDelete.getOwnSignatures();
			ownSignatures.clear();
			
			//a guest can create guest (since evolution guest with grant privilege)...
			//so when deleting a guest (A) you may need to delete the guests (B, C, D) which were created by this guest (A).
			//to fix this: deleting a guest means you will be the new owner of the guest account which were created (B, C, D)
			List<Guest> usersCreatedByTheUserToDelete = guestRepository.searchGuest(null, null, null, userToDelete);
			for (Guest guest : usersCreatedByTheUserToDelete) {
				guest.setOwner(actor);
				guestRepository.update(guest);
				if (guest.isRestricted()) { //if restricted guest, needs to have the new owner as contact
					addGuestContactRestriction(guest.getMail(), actor.getMail());
				}
			}
			
			
			userRepository.update(userToDelete);
			userRepository.delete(userToDelete);
			UserLogEntry logEntry = new UserLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
					actor.getDomainId(),
		        	LogAction.USER_DELETE, "Deleting an user", userToDelete.getMail(), 
		        	userToDelete.getFirstName(), userToDelete.getLastName(), userToDelete.getDomainId(), null);
      
		    logEntryService.create(logEntry);
		    
		    for (Document document : documentsToClean) {
		    	shareService.refreshShareAttributeOfDoc(document);
				
			}
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't find the user " + userToDelete.getMail() +" to be deleted", e);
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Couldn't find the user " + userToDelete.getMail() +" to be deleted");
		}
	}

    /** Clean outdated guest accounts.
     * @throws BusinessException
     */
	   @Override
    public void cleanExpiredGuestAcccounts() {
        User owner = userRepository.findByMail("system");
        List<Guest> guests = guestRepository.findOutdatedGuests();
        logger.info(guests.size() + " guest(s) have been found to be removed");
        for (User guest : guests) {
            try {
                deleteUser(guest.getMail(), owner);
                logger.info("Removed expired user : " + guest.getMail());
            } catch (BusinessException ex) {
                logger.warn("Unable to remove expired user : " + guest.getMail() + "\n" + ex.toString());
            }
        }
    }
	   
	   
	private List<User> completionSearchForRestrictedGuest(String mail, String firstName, String lastName, Guest currentGuest) {
		List<User> users=new ArrayList<User>();
		logger.debug("special search for restricted guest ");
		List<AllowedContact> contacts = allowedContactRepository.searchContact(mail, firstName, lastName, currentGuest);
		for (AllowedContact allowedContact : contacts) {
			if (allowedContact.getContact().getAccountType().equals(UserType.GUEST)) {
				Guest guest = guestRepository.findByMail(allowedContact.getContact().getMail());
				users.add(guest);
			}
			else {
				users.add(allowedContact.getContact());
			}
		}
		logger.debug("End searchUser(restricted guests)");
		return users;
	}
	
	private List<User> completionSearchForGuest(String mail, String firstName, String lastName, User currentUser) {
		List<User> result=new ArrayList<User>();
		logger.debug("adding guests to the return list");

		// TODO : It is not the better way ... but it works.
    	List<Guest> list = guestRepository.searchGuestAnyWhere(mail, firstName, lastName);
       	logger.debug("Guest found : size : " + list.size());
       	
    	List<AbstractDomain> allAuthorizedDomain = abstractDomainService.getAllAuthorizedDomains(currentUser.getDomain().getIdentifier());
    	List<String> allAuthorizedDomainIdentifier = new ArrayList<String>();
    	
		for (AbstractDomain d : allAuthorizedDomain) {
			allAuthorizedDomainIdentifier.add(d.getIdentifier());
		}
		
		for (Guest guest : list) {
			if(allAuthorizedDomainIdentifier.contains(guest.getDomainId())) {
				result.add(guest);
			}
		}
    	
    	logger.debug("result guest list : size : " + result.size());
		return result;
	}
	
	private List<User> completionSearchInternal(String mail, String firstName, String lastName, User currentUser) throws BusinessException {
		logger.debug("adding internals to the return list");
		List<User> internals =  abstractDomainService.searchUserWithDomainPolicies(currentUser.getDomain().getIdentifier(), mail, firstName, lastName);
		logger.debug("result internals list : size : " + internals.size());
		for (User ldapuser : internals) {
			User userdb = userRepository.findByMail(ldapuser.getMail());
			if (userdb!=null)  ldapuser.setRole(userdb.getRole());
		}
		
		return internals;
	}
    
	@Override
	public List<User> searchUser(String mail, String firstName, String lastName, UserType userType, User currentUser) throws BusinessException {
		
		logger.debug("Begin searchUser");
		List<User> users=new ArrayList<User>();
	
		if (currentUser !=null && currentUser.getAccountType()==UserType.GUEST){ //GUEST RESTRICTED MUST NOT SEE ALL USERS
			Guest currentGuest = guestRepository.findByMail(currentUser.getMail());
			if (currentGuest.isRestricted() == true) {
				return completionSearchForRestrictedGuest(mail,firstName,lastName,currentGuest);
			}
		}
		
		if(null==userType || userType.equals(UserType.GUEST)){
        	users.addAll(completionSearchForGuest(mail,firstName,lastName,currentUser));
		}
		if(null==userType || userType.equals(UserType.INTERNAL)){
			users.addAll(completionSearchInternal(mail,firstName,lastName,currentUser));
		}

		logger.debug("End searchUser");
		return users;
	}
	
	@Override
	public List<User> searchUserForRestrictedGuestEditionForm(String mail, String firstName, String lastName, User currentGuest) throws BusinessException {
		
		logger.debug("Begin searchUserForRestrictedGuestEditionForm");
		List<User> users=new ArrayList<User>();
	
       	users.addAll(completionSearchForGuest(mail,firstName,lastName,currentGuest));
		users.addAll(completionSearchInternal(mail,firstName,lastName,currentGuest));

		logger.debug("End searchUserForRestrictedGuestEditionForm");
		return users;
	}

	@Override
	public void updateGuest(String domain, String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest, UserVo ownerVo) throws BusinessException {
		
		Guest guest = guestRepository.findByMail(mail);
		User owner = userRepository.findByMail(ownerVo.getMail());
		
		boolean hasRightToDeleteThisUser = isAdminForThisUser(owner, guest.getDomainId(), guest.getMail());
		
		if (!hasRightToDeleteThisUser) {
			logger.error("The user " + mail +" cannot be updated by "+owner.getMail());
			throw new BusinessException(BusinessErrorCode.CANNOT_UPDATE_USER, "The user " + mail 
					+" cannot be deleted, he is not a guest, or "+ owner.getMail()+ " is not an admin");
		}
        
		guest.setFirstName(firstName);
		guest.setLastName(lastName);
		guest.setCanUpload(canUpload);
		guest.setCanCreateGuest(canCreateGuest);
        guestRepository.update(guest);

        UserLogEntry logEntry = new UserLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
				owner.getDomainId(),
        		LogAction.USER_UPDATE, "Update of a guest:" + guest.getMail(), guest.getMail(), guest.getFirstName(), guest.getLastName(), guest.getDomainId(), null);
        
        logEntryService.create(logEntry);

	}

	@Override
	public void updateUserRole(String domain, String mail,Role role, UserVo ownerVo) throws BusinessException{
		
		User user = userRepository.findByMailAndDomain(domain, mail);
		if(user == null) {
			logger.debug("User " + mail + " was not found in the database. Searching in directories ...");
			user = searchAndCreateUserEntityFromDirectory(domain, mail);
		}
		
		logger.debug("User " + mail + " found.");
		if(user == null) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user : " + mail + " in domain : " + domain);
		} else {
			User owner = userRepository.findByMail(ownerVo.getMail());
			user.setRole(role);
			userRepository.update(user);
			UserLogEntry logEntry = new UserLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
					owner.getDomainId(),
					LogAction.USER_UPDATE, "Update role of a user", user.getMail(), user.getFirstName(), user.getLastName(), user.getDomainId(), null);

			
			logEntryService.create(logEntry);
		}
	}
	
	@Override
	public void updateUserLocale(String domain, String mail, String locale) {
		
		User user = userRepository.findByMailAndDomain(domain,mail);
		if(user == null) {
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

	@Override
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
	
	@Override
	public void changePassword(String login, String oldPassword, String newPassword) throws BusinessException {
		User user = userRepository.findByMail(login);
		if (user == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a user with the login " + login);
		}
		
		if (!user.getPassword().equals(HashUtils.hashSha1withBase64(oldPassword.getBytes()))) {
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_ERROR, "The supplied password is invalid");
		}
		
		user.setPassword(HashUtils.hashSha1withBase64(newPassword.getBytes()));
		userRepository.update(user);
	}

	@Override
	public void resetPassword(String login, MailContainer mailContainer) throws BusinessException {
		Guest guest = guestRepository.findByMail(login);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		}
		
		// generate a password.
        String password = passwordService.generatePassword();
        String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());
        
        
        // Send an email to the guest.
        notifierService.sendAllNotifications(mailElementsFactory.buildMailResetPasswordWithOneRecipient(guest, mailContainer, guest, password));
        
		guest.setPassword(hashedPassword);
		guestRepository.update(guest);
	}
	
	@Override
	public void removeGuestContactRestriction(String login) throws BusinessException {
		Guest guest = guestRepository.findByMail(login);
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
	
	@Override
	public void addGuestContactRestriction(String ownerLogin, String contactLogin) throws BusinessException {

		Guest guest = guestRepository.findByMail(ownerLogin);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + ownerLogin);
		}
		
		try {
//			Guest currentGuest = guestRepository.findByMail(currentUser.getMail());
			User contact = findOrCreateUserWithDomainPolicies(contactLogin, guest.getDomain().getIdentifier());
			AllowedContact allowedContact = new AllowedContact(guest, contact);
			allowedContactRepository.create(allowedContact);
		} catch (IllegalArgumentException e) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user " + contactLogin);
		} catch (BusinessException e) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Could not add the contact restriction");
		}
	}
	
	@Override
	public void setGuestContactRestriction(String login, List<String> mailContacts) throws BusinessException {
		Guest guest = guestRepository.findByMail(login);
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
				User contact=findOrCreateUserWithDomainPolicies(mailContact, guest.getDomain().getIdentifier());
				if(contact==null) {
					logger.error("You are not authorized to communicate with " + mailContact);
				} 
				AllowedContact allowedContact = new AllowedContact(guest, contact);
				allowedContactRepository.create(allowedContact);
			}
			//set boolean restricted
			guest.setRestricted(true);
			guestRepository.update(guest);
		} catch (IllegalArgumentException e1) {
			logger.debug("TechnicalErrorCode.GENERIC : Couldn't set contacts restriction for user");
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't set contacts restriction for user " + login);
		} catch (BusinessException e1) {
			logger.debug("BusinessErrorCode.USER_NOT_FOUND : Couldn't set contacts restriction for user");
			for (AllowedContact entity : precedents) { //set old contacts list
				allowedContactRepository.create(entity);				
			}
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Couldn't set contacts restriction for user " + login);
		}
	}
	
	@Override
	public List<User> fetchGuestContacts(String login) throws BusinessException {
		Guest guest = guestRepository.findByMail(login);
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
	
	@Override
	public List<String> getGuestEmailContacts(String login) throws BusinessException {
		Guest guest = guestRepository.findByMail(login);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		}
		if (!guest.isRestricted()) {
			return null;
		}
		List<String> contactsUsers = new ArrayList<String>();
		List<AllowedContact> contacts = allowedContactRepository.findByOwner(guest);
		for (AllowedContact allowedContact : contacts) {
			contactsUsers.add(allowedContact.getContact().getMail());
		}
		return contactsUsers;
	}
	
	@Override
	public void updateUserDomain(String mail, String selectedDomain, UserVo ownerVo) throws BusinessException {
		if (!ownerVo.isSuperAdmin()) {
			throw new BusinessException(BusinessErrorCode.CANNOT_UPDATE_USER, "The user " + mail 
					+" cannot be moved to "+selectedDomain+" domain, "+ ownerVo.getMail()+ " is not a superadmin");
		}
		User user = null;
        // Seek user in base. If not found, try again but in directories
		if ((user = userRepository.findByMail(mail)) == null) {
			try {
				user = findOrCreateUser(mail, ownerVo.getDomainIdentifier());
			} catch (BusinessException e) {
				logger.error(e.toString());
				throw e;
			}
		}
		AbstractDomain newDomain = abstractDomainService.retrieveDomain(selectedDomain);
    	user.setDomain(newDomain);
    	userRepository.update(user);
	}

	@Override
	public List<User> searchAllBreakedUsers(User actor) {
		List<User> users = userRepository.findAll();
		List<User> internalsBreaked = new ArrayList<User>();
		
		for (User user : users) {
			if (user.getAccountType().equals(UserType.INTERNAL)) {
				if (!(user.getRole().equals(Role.SYSTEM) || user.getRole().equals(Role.SUPERADMIN))) { //hide these accounts
					try {
						List<User> found = abstractDomainService.searchUserWithoutRestriction(user.getDomain(), user.getMail(), null, null);
						if (found == null || found.size() != 1) {
							internalsBreaked.add(user);
						}
					} catch (BusinessException e) {
						logger.error("Error while searching inconsistent users", e);
					}
				}
			}
		}
		return internalsBreaked;
	}
	
	private String generateGuestLsUid(AbstractDomain domain, String mail) {
		StringBuffer uid = new StringBuffer("g");
		long cpt = domain.getPersistenceId();
		if (cpt <= 9) 			uid.append("00");
		else if (cpt <= 99) 	uid.append("0");
		uid.append(cpt);
		uid.append(mail);
		return uid.toString();
	}
	
	private String generateInternalLsUid(AbstractDomain domain, String ldapUid) {
		StringBuffer uid = new StringBuffer("i");
		long cpt = domain.getPersistenceId();
		if (cpt <= 9) 			uid.append("00");
		else if (cpt <= 99) 	uid.append("0");
		uid.append(cpt);
		uid.append(ldapUid);
		return uid.toString();
	}

	@Override
	public void  saveOrUpdateUser(User user) throws TechnicalException {
		// User object should be an new entity, or an existing one
		logger.debug("Begin saveOrUpdateUser");
		if(user != null && user.getDomain() != null) {
			logger.debug("Trying to find the current user in the user repository.");
			logger.debug("mail:" + user.getMail());
			logger.debug("domain id:" + user.getDomainId());
			User existingUser = userRepository.findByMailAndDomain(user.getDomain().getIdentifier(), user.getMail());
			if(existingUser != null) {
				// update
				logger.debug("userRepository.update(user)");
				try {
					userRepository.update(user);
				} catch (IllegalArgumentException e) {
					logger.error("Could not update the user " + user.getMail() +" in the database ", e);
					throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user could not be update in the DB " + e);
				} catch (BusinessException e) {
					logger.error("Could not update the user " + user.getMail()+" in the database ", e);
					throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user could not be update in the DB " + e);
				}
			} else {
				logger.debug("userRepository.create(user)");
				// create
				Functionality guestfunc = functionalityService.getGuestFunctionality(user.getDomain());
				user.setCanCreateGuest(guestfunc.getActivationPolicy().getStatus());
				
				Functionality userCanUploadFunc = functionalityService.getUserCanUploadFunctionality(user.getDomain());
				user.setCanUpload(userCanUploadFunc.getActivationPolicy().getStatus());
				
				user.setCreationDate(new Date());
				user.setLsUid(generateInternalLsUid(user.getDomain(),user.getLdapUid()));
				
				user.setLocale(user.getDomain().getDefaultLocale());
				try {
					userRepository.create(user);
				} catch (IllegalArgumentException e) {
					logger.error("Could not create the user " + user.getMail() +" in the database ", e);
					throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user could not be created in the DB " + e);
				} catch (BusinessException e) {
					logger.error("Could not create the user " + user.getMail()+" in the database ", e);
					throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user could not be created in the DB " + e);
				}
			}
		} else {
			String msg;
			if(user != null) {
				msg = "Attempt to create or update an user entity failed : User domain object is null." ;
			} else {
				msg = "Attempt to create or update an user entity failed : User object is null.";
			}
					
			logger.debug(msg);
			logger.debug("End saveOrUpdateUser");
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, msg);
		}
		
		logger.debug("End saveOrUpdateUser");
	}

	private User findOrCreateUserWithDomainPolicies(String mail, AbstractDomain abstractDomain) throws BusinessException {
		
		User user = userRepository.findByMailAndDomain(abstractDomain.getIdentifier(), mail);
		if (user == null) {
			List<User> users = abstractDomainService.searchUserWithoutRestriction(abstractDomain, mail, "", "");
			if (users != null) {
				if(users.size()==1) {
					user = users.get(0);
					saveOrUpdateUser(user);
				} else if(users.size() >= 2) {
					logger.error("Multiple results for user : " + mail);
				}
			}
		}
		return user;
	}
	
    @Override
	public User findOrCreateUserWithDomainPolicies(String mail, String domainId, String ActorDomainId) throws BusinessException {

    	User user = null ;
    	
    	if(ActorDomainId == null) {
    		ActorDomainId = domainId;
    	}
    	List<AbstractDomain> allAuthorizedDomains = abstractDomainService.getAllAuthorizedDomains(ActorDomainId);
    	
    	// We test the domainId parameter, the user we are looking for is supposed to be here.
    	for (AbstractDomain abstractDomain : allAuthorizedDomains) {
    		if(abstractDomain.getIdentifier().equals(domainId)) {
    			user = findOrCreateUserWithDomainPolicies(mail, abstractDomain);
    			// We don't need to continue
    			break;
    		}
    	}
    	
    	if(user == null ) {
    		// Now we search in all authorized domains.
    		for (AbstractDomain abstractDomain : allAuthorizedDomains) {
    			user = findOrCreateUserWithDomainPolicies(mail, abstractDomain);
    			if(user != null) {
    				// We don't need to continue
    				break;
    			}
        	}
    	}
    	
    	if(user == null) {
    		throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,"The user " + mail + " could not be found ! (domain id:" + domainId +", starting point:" + ActorDomainId + ")");
    	}
        return user;
    }

	@Override
    public User findOrCreateUserWithDomainPolicies(String mail, String domainId) throws BusinessException {
    	return findOrCreateUserWithDomainPolicies(mail, domainId, null);
    }
    
    @Override
    public User findOrCreateUser(String mail, String domainId) throws BusinessException {
        User user = userRepository.findByMailAndDomain(domainId, mail);
        
        if (user == null) {
            List<User> users = abstractDomainService.searchUserRecursivelyWithoutRestriction(domainId, mail, "", "");
            if (users!=null && users.size()==1) {
            	user = users.get(0);
        		saveOrUpdateUser(user);
            } else {
            	logger.error("Could not find the user " + mail +" in the database nor in the LDAP");
            	// this should really not happened
            	throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The user could not be found in the DB nor in the LDAP");
            }
        }
        return user;
    }
    
    @Override
	public User searchAndCreateUserEntityFromUnkownDirectory(String mail) throws BusinessException {
		
    	User userDB = userRepository.findByMail(mail);
    	if(userDB==null) {
    		// search user mail in all directories
    		List<User> users = abstractDomainService.searchUserRecursivelyWithoutRestriction(mail, "", "");
    		
    		if (users != null) {
    			if(users.size() == 1) {
    				User userFound = users.get(0);
    				logger.debug("User '" + mail + "'found in domain : " + userFound.getDomainId());
    				saveOrUpdateUser(userFound);
    				return userFound;
    				 
    			} else if(users.size() > 1) {
    				logger.error("Impossible to create an user entity from unknown domain. Multiple results with mail : " + mail);
    			} else if(logger.isDebugEnabled()) {
    				logger.debug("Impossible to create an user entity from unknown domain. No result with mail : " + mail);
    			}
    		} else if(logger.isDebugEnabled()) {
    			logger.error("Impossible to create an user entity from unknown domain. The searchUserRecursivelyWithoutRestriction method returns null.");
    		}
    		return null;
    	}
    	return userDB;
	}

	@Override
	public User searchAndCreateUserEntityFromDirectory(String domainIdentifier, String mail) throws BusinessException {
	
		logger.debug("domainIdentifier : " + domainIdentifier);
		logger.debug("mail : " + mail);
		// search user mail in in specific directory and all its SubDomain
		User userFound = abstractDomainService.searchOneUserRecursivelyWithoutRestriction(domainIdentifier, mail);
		
		if (userFound != null) {
				logger.debug("User '" + mail + "'found in domain : " + userFound.getDomainId());
				saveOrUpdateUser(userFound);
				return userFound;
		} else if(logger.isDebugEnabled()) {
			logger.error("Impossible to create an user entity from domain : " + domainIdentifier + ". The searchUserRecursivelyWithoutRestriction method returns null.");
		}
		return null;
	}
}
