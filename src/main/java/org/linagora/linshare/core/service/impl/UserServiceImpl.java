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
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.EntryService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.service.RecipientFavouriteService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.utils.HashUtils;
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
    
    private final RecipientFavouriteService recipientFavouriteService;
    
    private final MailContentBuildingService mailElementsFactory;
    
    private final AbstractDomainService abstractDomainService;
    
    private final FunctionalityService functionalityService;
    private final PasswordService passwordService;
    
    private final EntryService entryService;

	private final ThreadService threadService;

    public UserServiceImpl(final UserRepository<User> userRepository,
    		final NotifierService notifierService, 
    		final LogEntryService logEntryService,
    		final GuestRepository guestRepository, 
    		final RecipientFavouriteService recipientFavouriteService,
    		final AllowedContactRepository allowedContactRepository,
    		final MailContentBuildingService mailElementsFactory,
    		final FunctionalityService functionalityService,
    		final AbstractDomainService abstractDomainService,
    		final PasswordService passwordService,
    		final EntryService entryService,
    		final ThreadService threadService) {
        this.userRepository = userRepository;
        this.notifierService = notifierService;
        this.logEntryService = logEntryService;
        this.guestRepository = guestRepository;
		this.recipientFavouriteService = recipientFavouriteService;
		this.allowedContactRepository = allowedContactRepository;
		this.mailElementsFactory = mailElementsFactory;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
		this.passwordService = passwordService;
		this.entryService = entryService;
		this.threadService = threadService;
    }

    /** Create a guest.
     * @param login login.
     * @param firstName first name.
     * @param lastName last name.
     * @param mail guest email address.
     * @param canUpload : if the user can upload file
     * @param canCreateGuest : if the user can create other users
     * @param ownerLogin login of the user who create the guest.
     * @return persisted guest.
     */
    @Override
    public Guest createGuest(String login, String firstName, String lastName, String mail, Boolean canUpload, Boolean canCreateGuest, String comment,
    		String ownerLogin, String ownerDomain) throws BusinessException {

    	AbstractDomain domain = abstractDomainService.retrieveDomain(ownerDomain);
    	
		if(domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"Domain was not found");
		}
		
		
		User ownerUser = userRepository.findByLsUuid(ownerLogin); 
		
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
			
			if (comment == null) comment="";
			Guest guest = new Guest(firstName, lastName, mail, hashedPassword, canUpload, comment);
			guest.setDomain(guestDomain);
			guest.setOwner(ownerUser);
			guest.setComment(comment);
			
			
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
			
			UserLogEntry logEntry = new UserLogEntry(ownerUser, LogAction.USER_CREATE, "Creation of a guest", guest, expDate);
			
			logEntryService.create(logEntry);
			
			
			// Send an email to the guest.
			notifierService.sendAllNotification(mailElementsFactory.buildMailNewGuest(ownerUser, guest, password));
			logger.info("Guest " + mail + " was successfully created.");
			return guest;
		} else {
			logger.error("Can not create guest : no guest domain created.");
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXISTS,"Guest domain was not found");
		}
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
	public User findByLsUid(String lsUid) {
    	return userRepository.findByLsUuid(lsUid);
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
        expiryDate.add(func.toCalendarUnitValue(), func.getValue());
        
        return expiryDate.getTime();
    }
    
    
    @Override
    public void deleteUser(String login, Account actor) throws BusinessException {
    	User userToDelete = userRepository.findByLsUuid(login);

    	if (userToDelete != null) {
    		boolean hasRightToDeleteThisUser = isAdminForThisUser(actor, userToDelete.getDomainId(), userToDelete.getMail());

    		logger.debug("Has right ? : " + hasRightToDeleteThisUser);

    		if (!hasRightToDeleteThisUser) {
    			throw new BusinessException(BusinessErrorCode.CANNOT_DELETE_USER, "The user " + login 
    					+" cannot be deleted, he is not a guest, or "+ actor.getAccountReprentation() + " is not an admin");
    		} else {
    			doDeleteUser(actor, userToDelete);
    		}
    	} else {
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
        
    
    @Override
	public boolean isAdminForThisUser(Account actor, String userDomainToManage, String userMailToManage) {
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
    

	private void doDeleteUser(Account actor, User userToDelete) throws BusinessException {
		try {
			
			entryService.deleteAllReceivedShareEntries(actor, userToDelete);
			entryService.deleteAllShareEntriesWithDocumentEntries(actor, userToDelete);
			
			//clearing the favorites
			recipientFavouriteService.deleteFavoritesOfUser(userToDelete);
			
			//clearing allowed contacts
			allowedContactRepository.deleteAllByUserBothSides(userToDelete);
			
			//clear all thread memberships
			threadService.deleteAllUserMemberships(userToDelete);
			
//			// clearing all signatures
//			Set<Signature> ownSignatures = userToDelete.getOwnSignatures();
//			ownSignatures.clear();
//			userRepository.update(userToDelete);
			
			userRepository.delete(userToDelete);
			
			UserLogEntry logEntry = new UserLogEntry(actor, LogAction.USER_DELETE, "Deleting an user", userToDelete);
			logEntryService.create(logEntry);
		    
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't find the user " + userToDelete.getAccountReprentation() +" to be deleted", e);
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Couldn't find the user " + userToDelete.getAccountReprentation() +" to be deleted");
		}
	}

    /** Clean outdated guest accounts.
     * @throws BusinessException
     */
	@Override
    public void cleanExpiredGuestAcccounts(SystemAccount systemAccount) {
		   
		logger.debug("system account found : " + systemAccount.getAccountReprentation());
        
        List<Guest> guests = guestRepository.findOutdatedGuests();
        logger.info(guests.size() + " guest(s) have been found to be removed");
        for (User guest : guests) {
            try {
                deleteUser(guest.getLsUuid(), systemAccount);
                logger.info("Removed expired user : " + guest.getAccountReprentation());
            } catch (BusinessException ex) {
                logger.warn("Unable to remove expired user : " + guest.getAccountReprentation() + "\n" + ex.toString());
            }
        }
    }
	   
	   
	private List<User> completionSearchForRestrictedGuest(String mail, String firstName, String lastName, Guest currentGuest) {
		List<User> users=new ArrayList<User>();
		logger.debug("special search for restricted guest ");
		List<AllowedContact> contacts = allowedContactRepository.searchContact(mail, firstName, lastName, currentGuest);
		for (AllowedContact allowedContact : contacts) {
			if (allowedContact.getContact().getAccountType().equals(AccountType.GUEST)) {
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
	public List<User> searchUser(String mail, String firstName, String lastName, AccountType userType, User currentUser) throws BusinessException {
		
		logger.debug("Begin searchUser");
		List<User> users=new ArrayList<User>();
	
		if (currentUser !=null && currentUser.getAccountType()==AccountType.GUEST){ //GUEST RESTRICTED MUST NOT SEE ALL USERS
			Guest currentGuest = guestRepository.findByMail(currentUser.getMail());
			if (currentGuest.isRestricted()) {
				return completionSearchForRestrictedGuest(mail,firstName,lastName,currentGuest);
			}
		}
		
		if(null==userType || userType.equals(AccountType.GUEST)){
        	users.addAll(completionSearchForGuest(mail,firstName,lastName,currentUser));
		}
		if(null==userType || userType.equals(AccountType.INTERNAL)){
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
	public void updateGuest(String guestUuid, String domain, String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest, UserVo ownerVo) throws BusinessException {
		// TODO : mail is useless, use getLsUuid and remove it
		Guest guest = guestRepository.findByLsUuid(guestUuid);
		User owner = userRepository.findByLsUuid(ownerVo.getLsUid());
		
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

        UserLogEntry logEntry = new UserLogEntry(owner, LogAction.USER_UPDATE, "Update of a guest:" + guest.getMail(), guest);
        logEntryService.create(logEntry);
	}

	@Override
	public void updateUserRole(String userUuid, String domain, String mail, Role role, UserVo ownerVo) throws BusinessException{
		// TODO : mail is useless, use getLsUuid and remove it
		User user = userRepository.findByLsUuid(userUuid);
		if(user == null) {
			logger.debug("User " + mail + " was not found in the database. Searching in directories ...");
			user = searchAndCreateUserEntityFromDirectory(domain, mail);
		}
		
		logger.debug("User " + mail + " found.");
		if(user == null) {
			 throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user : " + mail + " in domain : " + domain);
		} else {
			User owner = userRepository.findByLsUuid(ownerVo.getLsUid());
			user.setRole(role);
			userRepository.update(user);
			UserLogEntry logEntry = new UserLogEntry(owner, LogAction.USER_UPDATE, "Update of a guest:" + user.getMail(), user);
			logEntryService.create(logEntry);
		}
	}
	
	@Override
	public void updateUserLocale(String domain, String mail, String locale) {
		// TODO : mail is useless, use getLsUuid and remove it
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
	public void changePassword(String login, String oldPassword, String newPassword) throws BusinessException {
		// TODO : mail is useless, use getLsUuid and remove it
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
	public void resetPassword(String login) throws BusinessException {
		Guest guest = guestRepository.findByMail(login);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the login " + login);
		}
		
		// generate a password.
        String password = passwordService.generatePassword();
        String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());
        
        
        // Send an email to the guest.
        notifierService.sendAllNotification(mailElementsFactory.buildMailResetPassword(guest, password));
        
		guest.setPassword(hashedPassword);
		guestRepository.update(guest);
	}
	
	@Override
	public void removeGuestContactRestriction(String uuid) throws BusinessException {
		Guest guest = guestRepository.findByLsUuid(uuid);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the uuid " + uuid);
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
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the uuid " + uuid);
		} catch (BusinessException e1) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not update guest restriction of " + uuid);
		}
	}
	
	@Override
	public void addGuestContactRestriction(String ownerUuid, String contactUuid) throws BusinessException {

		Guest guest = guestRepository.findByLsUuid(ownerUuid);
		
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the Uuid " + ownerUuid);
		}
		
		try {
//			Guest currentGuest = guestRepository.findByMail(currentUser.getMail());
//			User contact = findOrCreateUserWithDomainPolicies(contactUuid, guest.getDomain().getIdentifier());
			User contact = userRepository.findByLsUuid(contactUuid);
			
			AllowedContact allowedContact = new AllowedContact(guest, contact);
			allowedContactRepository.create(allowedContact);
		} catch (IllegalArgumentException e) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Couldn't find the user " + contactUuid);
		} catch (BusinessException e) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Could not add the contact restriction");
		}
	}
	
	@Override
	public void setGuestContactRestriction(String uuid, List<String> mailContacts) throws BusinessException {
		Guest guest = guestRepository.findByLsUuid(uuid);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the uuid " + uuid);
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
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't set contacts restriction for uuid " + uuid);
		} catch (BusinessException e1) {
			logger.debug("BusinessErrorCode.USER_NOT_FOUND : Couldn't set contacts restriction for user");
			for (AllowedContact entity : precedents) { //set old contacts list
				allowedContactRepository.create(entity);				
			}
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Couldn't set contacts restriction for uuid " + uuid);
		}
	}
	
	@Override
	public List<User> fetchGuestContacts(String uuid) throws BusinessException {
		Guest guest = guestRepository.findByLsUuid(uuid);
		if (guest == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find a guest with the uuid " + uuid);
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
					+ " cannot be moved to " + selectedDomain + " domain, " + ownerVo.getMail() + " is not a superadmin");
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
			if (user.getAccountType().equals(AccountType.INTERNAL)) {
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
//    	AccountRepository<Account>
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
