package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;

public class MailingListFacadeImpl implements MailingListFacade {

	private final MailingListService mailingListService;
    private final UserService userService;
    private final AbstractDomainService abstractDomainService;
    
    public MailingListFacadeImpl(MailingListService mailingListService, UserService userService,AbstractDomainService abstractDomainService) {
        
    	super();
        this.mailingListService = mailingListService;
        this.userService = userService;
        this.abstractDomainService=abstractDomainService;
    }
    
    @Override
    public MailingListVo retrieveMailingList(long persistenceId) {
    	MailingList mailingList =mailingListService.retrieveMailingList(persistenceId);
    	return new MailingListVo(mailingList);
    }
    
    @Override
    public MailingListVo createMailingList(MailingListVo mailingListVo) throws BusinessException {
    	MailingList mailingList=new MailingList(mailingListVo);
        User actor = userService.findOrCreateUser(mailingListVo.getOwner().getMail(),mailingListVo.getOwner().getDomainIdentifier());
    	mailingList.setOwner(actor);
    	mailingList.setMails(null);
  	  	AbstractDomain domain = abstractDomainService.retrieveDomain(mailingListVo.getDomain().getIdentifier());
  	  	mailingList.setDomain(domain);
        mailingListService.createMailingList(mailingList);
    	return mailingListVo ;
    }
    
    @Override
    public List<MailingListVo> findAllMailingList() {
    	List<MailingListVo> list =new ArrayList<MailingListVo>();
    	
    	for(MailingList current : mailingListService.findAllMailingList()) {
    		list.add(new MailingListVo(current));
    	}
    	return list;
    }
    
    @Override
    public List<MailingListVo> findAllMailingListByUser(UserVo actorVo) throws BusinessException {
    	List<MailingListVo> list =new ArrayList<MailingListVo>();
        User actor = userService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());
        
    	for(MailingList current : mailingListService.findAllMailingListByUser(actor)) {
    		list.add(new MailingListVo(current));
    	}
    	return list;
    }
    
    @Override
    public void deleteMailingList(long persistenceId) throws BusinessException {
    	mailingListService.deleteMailingList(persistenceId);
    }
    
    @Override
    public void updateMailingList(MailingListVo mailingListVo) throws BusinessException {
    	MailingList mailingList=new MailingList(mailingListVo);
    	User actor = userService.findOrCreateUser(mailingListVo.getOwner().getMail(),mailingListVo.getOwner().getDomainIdentifier());
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
    public List<MailingListVo> findAllMailingListByIdentifier(String identifier, UserVo actorVo) throws BusinessException {
    	List<MailingListVo> list = new ArrayList<MailingListVo>();
    	List<MailingListVo> listByUser = new ArrayList<MailingListVo>();
    	listByUser = findAllMailingListByUser(actorVo);
    	
    	for(MailingListVo current : listByUser) {
    		if(current.getIdentifier().equals(identifier)) {
    			list.add(current);
    		}
    	}
    	return list;	
    }

	@Override
	public void deleteMailingListContact(MailingListVo listVo,long persistenceId) throws BusinessException { 
		MailingList list =new MailingList(listVo);
		mailingListService.deleteMailingListContact(list,persistenceId);
    	User actor = userService.findOrCreateUser(listVo.getOwner().getMail(),listVo.getOwner().getDomainIdentifier());
    	list.setOwner(actor);
  	  	AbstractDomain domain = abstractDomainService.retrieveDomain(listVo.getDomain().getIdentifier());
  	  	list.setDomain(domain);
		mailingListService.updateMailingList(list);
	}
   
   @Override
   public MailingListContactVo retrieveMailingListContact(String mail , MailingListVo list) {
	   
	   MailingList mailingList = mailingListService.retrieveMailingList(list.getPersistenceId());
	   long persistenceId = 0;
	   
	   for(MailingListContact current : mailingList.getMails()){
		   if(current.getMails().equals(mail)){
			   persistenceId = current.getPersistenceId();
		   }
	   }
	   return new MailingListContactVo(mailingListService.retrieveMailingListContact(persistenceId));
   }
   
   @Override
   public String checkUniqueId(String value,UserVo user) throws BusinessException {
	   List<MailingListVo> list = new ArrayList<MailingListVo>();
	   list = findAllMailingListByOwner(user);
	   int i = 0;
	   String copy = value;
	   
	   for(MailingListVo current : list){  
		   while(current.getIdentifier().equals(copy)){
			   copy = value+i;  
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
	   for(MailingList current : listFromDb){
		   list.add(new MailingListVo(current));
	   }
	   return list;
   }
   
   @Override
   public List<MailingListVo> copyList(List<MailingListVo> list) {
	   List<MailingListVo> copy = new ArrayList<MailingListVo>();
	   
	   for(MailingListVo current : list) { 
		   copy.add(current);
	   }
	return copy;	
   }
   
   @Override
   public List<MailingListVo> getMailingListFromQuickShare(final String mailingLists,UserVo user) {
	   List<MailingListVo> finalList = new ArrayList<MailingListVo>();
	   if(mailingLists!=null){
	   String[] recipients = mailingLists.replaceAll(";", ",").split(",");
	   	if(mailingLists.startsWith("\"") && mailingLists.endsWith(")")){
	   		
	   		for (String oneMailingList : recipients) {
			   int index1 = oneMailingList.indexOf("(");
			   String owner = oneMailingList.substring(index1+1, oneMailingList.length()-1);
			   
			   int index2 = oneMailingList.indexOf(" ");
			   String identifier = oneMailingList.substring(1, index2-1);
			   
			   if(owner.equals("Me")){
				   owner = user.getFullName();
			   }
			   MailingListVo current = retrieveMailingListByOwnerAndIdentifier(identifier,owner);
			   finalList.add(current);
			}
		}
	} else {
		finalList = new ArrayList<MailingListVo>() ;
	}
	   return finalList;
   }
   
   @Override
   public MailingListVo retrieveMailingListByOwnerAndIdentifier(String identifier, String ownerFullName) {
	   List<MailingList> listFromDb = new ArrayList<MailingList>();
	   listFromDb = mailingListService.findAllMailingList();
	   
	   for(MailingList current : listFromDb){
		   if((current.getIdentifier().equals(identifier)) && ((current.getOwner().getFirstName()+" "+current.getOwner().getLastName()).equals(ownerFullName))){
			   return new MailingListVo(current);
		   }
	   }
	   return new MailingListVo();
   }
}
