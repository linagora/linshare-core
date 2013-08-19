package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;


public class MailingListFacadeImpl implements MailingListFacade {
	
	private final MailingListService mailingListService;
	private final UserService userService;
	private final AbstractDomainService abstractDomainService;
	private final AccountService accountService;

	public MailingListFacadeImpl(MailingListService mailingListService,UserService userService,AbstractDomainService abstractDomainService,AccountService accountService) {
		
		super();
		this.mailingListService = mailingListService;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
		this.accountService = accountService;
	}

	@Override
	public MailingListVo retrieveMailingList(long persistenceId) {
		MailingList mailingList = mailingListService.retrieveMailingList(persistenceId);
		return new MailingListVo(mailingList);
	}

	@Override
	public MailingListVo createMailingList(MailingListVo mailingListVo) throws BusinessException {
		MailingList mailingList = new MailingList(mailingListVo);
		User actor = userService.findOrCreateUser(mailingListVo.getOwner().getMail(), mailingListVo.getOwner().getDomainIdentifier());
		mailingList.setOwner(actor);
		mailingList.setMails(null);
		AbstractDomain domain = abstractDomainService.retrieveDomain(mailingListVo.getDomain().getIdentifier());
		mailingList.setDomain(domain);
		mailingListService.createMailingList(mailingList);
		return mailingListVo;
	}

	@Override
	public List<MailingListVo> findAllMailingList() {
		List<MailingListVo> list = new ArrayList<MailingListVo>();

		for (MailingList mailingList : mailingListService.findAllMailingList()) {
			list.add(new MailingListVo(mailingList));
		}
		return list;
	}

	@Override
	public List<MailingListVo> findAllMailingListByUser(UserVo actorVo) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		User actor = userService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());

		for (MailingList mailingList : mailingListService.findAllMailingListByUser(actor)) {
			list.add(new MailingListVo(mailingList));
		}
		return list;
	}

	@Override
	public void deleteMailingList(long persistenceId) throws BusinessException {
		mailingListService.deleteMailingList(persistenceId);
	}

	@Override
	public void updateMailingList(MailingListVo mailingListVo) throws BusinessException {
		MailingList mailingList = new MailingList(mailingListVo);
		User actor = userService.findOrCreateUser(mailingListVo.getOwner().getMail(), mailingListVo.getOwner().getDomainIdentifier());
		mailingList.setOwner(actor);
		AbstractDomain domain = abstractDomainService.retrieveDomain(mailingListVo.getDomain().getIdentifier());
		mailingList.setDomain(domain);
		mailingListService.updateMailingList(mailingList);
	}

	@Override
	public void updateMailingListContact(MailingListContactVo contactToUpdate) throws BusinessException {
		mailingListService.updateMailingListContact(new MailingListContact(contactToUpdate));
	}

	@Override
	public List<MailingListVo> findAllMailingListByIdentifier(UserVo actorVo, String identifier) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		List<MailingListVo> listByUser = new ArrayList<MailingListVo>();
		listByUser = findAllMailingListByUser(actorVo);

		for (MailingListVo mailingListVo : listByUser) {
			if (mailingListVo.getIdentifier().equals(identifier)) {
				list.add(mailingListVo);
			}
		}
		return list;
	}

	@Override
	public void deleteMailingListContact(MailingListVo listVo, long persistenceId) throws BusinessException {
		MailingList list = new MailingList(listVo);
		mailingListService.deleteMailingListContact(list, persistenceId);
		User actor = userService.findOrCreateUser(listVo.getOwner().getMail(),listVo.getOwner().getDomainIdentifier());
		list.setOwner(actor);
		AbstractDomain domain = abstractDomainService.retrieveDomain(listVo.getDomain().getIdentifier());
		list.setDomain(domain);
		mailingListService.updateMailingList(list);
	}

	@Override
	public MailingListContactVo retrieveMailingListContact(MailingListVo list, String mail) {

		MailingList mailingList = mailingListService.retrieveMailingList(list.getPersistenceId());
		long persistenceId = 0;

		for (MailingListContact mailingListContact : mailingList.getMails()) {
			if (mailingListContact.getMails().equals(mail)) {
				persistenceId = mailingListContact.getPersistenceId();
			}
		}
		return new MailingListContactVo(
				mailingListService.retrieveMailingListContact(persistenceId));
	}

	@Override
	public String checkUniqueId(UserVo user, String value) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		list = findAllMailingListByOwner(user);
		int i = 0;
		String copy = value;

		for (MailingListVo mailingListVo : list) {
			while (mailingListVo.getIdentifier().equals(copy)) {
				copy = value + i;
				i++;
			}
		}
		return copy;
	}

	@Override
	public List<MailingListVo> findAllMailingListByOwner(UserVo user) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		List<MailingList> listFromDb = new ArrayList<MailingList>();
		User actor = userService.findOrCreateUser(user.getMail(),user.getDomainIdentifier());

		listFromDb = mailingListService.findAllMailingListByOwner(actor);
		for (MailingList mailingList : listFromDb) {
			list.add(new MailingListVo(mailingList));
		}
		return list;
	}

	@Override
	public List<MailingListVo> getMailingListFromQuickShare(UserVo user, final String mailingLists) {
		List<MailingListVo> finalList = new ArrayList<MailingListVo>();
		if (mailingLists != null) {
			String[] recipients = mailingLists.replaceAll(";", ",").split(",");
			if (mailingLists.startsWith("\"") && mailingLists.endsWith(")")) {

				for (String oneMailingList : recipients) {
					int index1 = oneMailingList.indexOf("(");
					String owner = oneMailingList.substring(index1 + 1,oneMailingList.length() - 1);

					int index2 = oneMailingList.indexOf(" ");
					String identifier = oneMailingList.substring(1, index2 - 1);

					if (owner.equals("Me")) {
						owner = user.getFullName();
					}
					MailingListVo mailingListVo = retrieveMailingListByOwnerAndIdentifier(identifier, owner);
					finalList.add(mailingListVo);
				}
			}
		} else {
			finalList = new ArrayList<MailingListVo>();
		}
		return finalList;
	}

	@Override
	public MailingListVo retrieveMailingListByOwnerAndIdentifier(String identifier, String ownerFullName) {
		List<MailingList> listFromDb = new ArrayList<MailingList>();
		listFromDb = mailingListService.findAllMailingList();

		for (MailingList mailingList : listFromDb) {
			if ((mailingList.getIdentifier().equals(identifier)) && ((mailingList.getOwner().getFirstName() + " " + mailingList.getOwner().getLastName()).equals(ownerFullName))) {
				return new MailingListVo(mailingList);
			}
		}
		return new MailingListVo();
	}

	@Override
	public List<String> completionsForSearchList(UserVo loginUser, String input, String criteriaOnSearch) throws BusinessException {
		List<MailingListVo> searchResults = performSearch(input, loginUser);
		List<String> elements = new ArrayList<String>();

		for (MailingListVo mailingListVo : searchResults) {
			String completeName;
			if (!(mailingListVo.getOwner().equals(loginUser))) {
				completeName = "\"" + mailingListVo.getIdentifier() + "\" ("+ mailingListVo.getOwner().getFullName() + ")";
			} else {
				completeName = "\"" + mailingListVo.getIdentifier()+ "\" (Me)";
			}
			
			if (criteriaOnSearch.equals("public")) {
				if (mailingListVo.isPublic() == true) {
					elements.add(completeName);
				}
			} else if (criteriaOnSearch.equals("private")) {
				if (mailingListVo.isPublic() == false) {
					elements.add(completeName);
				}
			} else {
				elements.add(completeName);
			}
		}
		return elements;
	}

	@Override
	public List<MailingListVo> setListFromSearch(UserVo loginUser, String targetLists, String criteriaOnSearch) throws BusinessException {
		List<MailingListVo> lists = new ArrayList<MailingListVo>();
		List<MailingListVo> searchResults = new ArrayList<MailingListVo>();
		if (targetLists != null) {
			if (targetLists.startsWith("\"") && targetLists.endsWith(")")) {
				lists = this.getMailingListFromQuickShare(loginUser,targetLists);
			} else {
				if (targetLists.equals("*")) {
					if (loginUser.isSuperAdmin()) {
						searchResults = this.findAllMailingList();
					} else {
						searchResults = this.findAllMailingListByUser(loginUser);
					}
					if (criteriaOnSearch.equals("all")) {
						lists = searchResults;
					} else {

						for (MailingListVo mailingListVo : searchResults) {
							if (criteriaOnSearch.equals("private") && mailingListVo.isPublic() == false) {
								lists.add(mailingListVo);
							} else if (criteriaOnSearch.equals("public") && mailingListVo.isPublic() == true) {
								lists.add(mailingListVo);
							}
						}
					}
				} else {
					lists = performSearch(targetLists, loginUser);
					if (criteriaOnSearch.equals("public")) {
						List<MailingListVo> finalList = lists;
						lists.clear();

						for (MailingListVo mailingListVo : finalList) {
							if (mailingListVo.isPublic() == true) {
								lists.add(mailingListVo);
							}
						}
					} else if (criteriaOnSearch.equals("private")) {
						List<MailingListVo> finalList = lists;
						lists.clear();

						for (MailingListVo mailingListVo : finalList) {
							if (mailingListVo.isPublic() == false) {
								lists.add(mailingListVo);
							}
						}
					}
				}
			}
		} else {
			return new ArrayList<MailingListVo>();
		}
		return lists;
	}

	private List<MailingListVo> performSearch(String input, UserVo loginUser) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		List<MailingListVo> finalList = new ArrayList<MailingListVo>();
		if (loginUser.isSuperAdmin()) {
			list = this.findAllMailingList();
		} else {
			list = this.findAllMailingListByUser(loginUser);
		}

		for (MailingListVo mailingListVo : list) {
			if (mailingListVo.getIdentifier().indexOf(input) != -1) {
				finalList.add(mailingListVo);
			}
		}
		return finalList;
	}

	public boolean checkUserIsContact(List<MailingListContactVo> contacts, String mail) {

		for (MailingListContactVo contact : contacts) {
			if (contact.getMail().equals(mail)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLogin());
		List<String> ret = new ArrayList<String>();

		List<User> userSet = performSearchUser(actor, pattern);

		for (User user : userSet) {
			if (!(user.equals(actor))) {
				String completeName = MailCompletionService.formatLabel(new UserVo(user)).substring(0,MailCompletionService.formatLabel(new UserVo(user)).length());
				if (!ret.contains(completeName)) {
					ret.add(completeName);
				}
			}
		}
		return ret;
	}

	private List<User> performSearchUser(User loginUser, String input) throws BusinessException {
		String firstName_ = null;
		String lastName_ = null;

		if (input != null && input.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

		Set<User> userSet = new HashSet<User>();

		if (input != null) {
			userSet.addAll(userService.searchUser(input.trim(), null, null,null, loginUser));
		} else {
			userSet.addAll(userService.searchUser(null, firstName_, lastName_,null, loginUser));
		}

		return new ArrayList<User>(userSet);
	}

	@Override
	public List<UserVo> searchAmongUsers(UserVo userVo, String input) throws BusinessException {
		List<User> results = new ArrayList<User>();
		List<UserVo> finalResults = new ArrayList<UserVo>();
		User owner = (User) accountService.findByLsUuid(userVo.getLogin());
		if (input != null) {
			if (input.startsWith("\"") && input.endsWith(">")) {
				UserVo tmp = MailCompletionService.getUserFromDisplay(input);
				results = userService.searchUser(tmp.getMail(),tmp.getFirstName(), tmp.getLastName(), null, owner);
			} else {
				results = performSearchUser(owner, input);
			}
			for (User currentUser : results) {
				if (!(currentUser.equals(owner))) {
					finalResults.add(new UserVo(currentUser));
				}
			}
		}
		return finalResults;
	}

	@Override
	public void addUserToMailingListContact(MailingListVo mailingListVo, String domain, String mail) throws BusinessException {
		User selectedUser = userService.findOrCreateUser(mail, domain);
		if (selectedUser != null) {
			String display = MailCompletionService.formatLabel(selectedUser.getMail(), selectedUser.getFirstName(),selectedUser.getLastName(), false);
			MailingListContactVo newContact = new MailingListContactVo(mail,display);
			mailingListVo.addContact(newContact);
		}
	}

	@Override
	public long removeContactFromMailingList(MailingListVo mailingListVo,String domain, String mail) throws BusinessException {
		User selectedUser = userService.findOrCreateUser(mail, domain);
		if (selectedUser != null) {

			for (MailingListContactVo mailingListContactVo : mailingListVo.getMails()) {
				if (mailingListContactVo.getMail().equals(selectedUser.getMail())) {
					return mailingListContactVo.getPersistenceId();
				}
			}
		}
		return 0;
	}

	@Override
	public void setNewOwner(MailingListVo mailingListVo, String input) throws BusinessException {

		UserVo selectedUser = MailCompletionService.getUserFromDisplay(input);
		User user = userService.findUnkownUserInDB(selectedUser.getMail());
		mailingListVo.setOwner(new UserVo(user));
		AbstractDomain domain = abstractDomainService.retrieveDomain(user.getDomainId());
		mailingListVo.setDomain(new AbstractDomainVo(domain));

	}
	
	@Override
	public void refreshListOfMailingList(List<MailingListVo> list){
		List<MailingListVo> refreshList = new ArrayList<MailingListVo>(list);
		list.clear();
		for(MailingListVo mailingListVo : refreshList){
			list.add(this.retrieveMailingList(mailingListVo.getPersistenceId()));
		}
	}
}
