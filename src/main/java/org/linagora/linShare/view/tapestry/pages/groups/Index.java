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
package org.linagora.linShare.view.tapestry.pages.groups;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.GroupFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.GroupMemberType;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.GroupMemberVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.components.CreateGroupPopup;
import org.linagora.linShare.view.tapestry.components.UserDetailsDisplayer;
import org.linagora.linShare.view.tapestry.components.WindowWithEffects;
import org.linagora.linShare.view.tapestry.models.SorterModel;
import org.linagora.linShare.view.tapestry.models.impl.MemberSorterModel;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index {
	@Inject
	private GroupFacade groupFacade;
	@Inject
	private ShareFacade shareFacade;
	@Inject
	private UserFacade userFacade;
	
	@Inject
	private MailContainerBuilder mailContainerBuilder;

	@SessionState
	@Property
	private UserVo userVo;

	@Property
	@Persist
	private List<GroupVo> groups;
	
	@Property
	@Persist
	private List<String> blockIds;

	@Property
	@Persist
	private GroupVo group;

	@Property
	@Persist
	private GroupVo groupSelected;

	@Property
	@Persist
	private boolean inModify;

	@Property
	@Persist
	private GroupMemberVo member;

	@Property
	@Persist
	private GroupMemberVo waitingForApprovalMember;

	@Property
	@Persist
	private GroupMemberVo memberConnected;
	
	@Property
	@Persist
	private GroupMemberVo memberToChange;
	
	@Property
	@Persist
	private SorterModel<GroupMemberVo> sorterModel;

	@Property
	@Persist
	private List<GroupMemberVo> members;
	@Property
	@Persist
	private List<GroupMemberVo> waitingForApprovalMembers;

	@Property
	@Persist
	private List<ShareDocumentVo> documents;	
	
	private GroupMemberType memberToChangeType;
    
	@Environmental
	private RenderSupport renderSupport;
    @ApplicationState
    private ShareSessionObjects shareSessionObjects;
    @Inject
    private Messages messages;
    @InjectComponent
    private UserDetailsDisplayer userDetailsDisplayer;
    @InjectComponent
    private CreateGroupPopup createGroupPopup;  
    
	@Component(parameters = {"style=bluelighting", "show=false","width=500", "height=100"})
	private WindowWithEffects windowConfirmDeleteMembership;
    
	@Component(parameters = {"style=bluelighting", "show=false","width=500", "height=100"})
	private WindowWithEffects windowConfirmDeleteGroup;
    
	@Component(parameters = {"style=bluelighting", "show=false","width=600", "height=200"})
	private WindowWithEffects windowChangeMemberType;

    private Logger logger = LoggerFactory.getLogger(Index.class);
    
    @Persist
    private String loginOfMemberToConsider;

	@SetupRender
	public void setupRender() {
		if (shareSessionObjects.isReloadGroupsNeeded()) {
			groups = null;
			group = null;
			shareSessionObjects.setReloadGroupsNeeded(false);
		}
		
		if (groups == null) { //first display of the page for the session
			groups = groupFacade.findByUser(userVo.getLogin());
			if (groups!=null && groups.size()>0) {
				group=groups.get(0); //display the first group of the list
			}
			blockIds = new ArrayList<String>(); //tabset titles
			for (GroupVo groupForBlock : groups) {
				blockIds.add(groupForBlock.getName());
			}
		}
		
		if (group != null) {//at least one group membership for this user
			documents = shareFacade.getAllSharingReceivedByUser(group.getGroupUser());
			
			List<GroupMemberVo> groupMembers = new ArrayList<GroupMemberVo>(group.getMembers());
			members = new ArrayList<GroupMemberVo>();
			waitingForApprovalMembers = new ArrayList<GroupMemberVo>();
			
			for (GroupMemberVo groupMemberVo : groupMembers) {
				if (groupMemberVo.isWaitingForApproval()) {
					waitingForApprovalMembers.add(groupMemberVo);
				}
				else {
					members.add(groupMemberVo);
				}
			}
			
	        sorterModel=new MemberSorterModel(members);
			memberConnected = group.findMember(userVo.getLogin());
		}
	}
	
	@AfterRender
	public void afterRender() {
		if ((members != null) && (members.size() > 0))
			renderSupport.addScript(String.format("$('actionsWidget').style.display='block';"));
	}
	
	Object onActionFromChangeGroup(String groupName) {
		if (groups != null) { //session expired but user clicked on the tabset => groups==null
			group = getGroupVoFromName(groupName);
		}
		inModify = false;
		return this; // =>setupRender
	}
	
	void onActionFromGetGroupModify() {
		inModify=true;
	}
	
	void onActionFromDeleteGroup() { //only show popup
	}
	
	void onActionFromEditMember(String login) {
		memberToChange = group.findMember(login);
	}
	
	void onActionFromEditManager(String login) {
		memberToChange = group.findMember(login);
	}
	
	void onActionFromDeleteMembership(String login) {
		loginOfMemberToConsider = login;
	}
	
	void onActionFromDeleteManagerMembership(String login) {
		onActionFromDeleteMembership(login);
	}
	
	void onActionFromDeleteSelfMembership(String login) {
		logger.debug("onActionFromDeleteSelfMembership: "+login);
		onActionFromDeleteMembership(login);
	}
	
	Object onActionFromConfirmDeleteMembership() throws BusinessException {

		if (loginOfMemberToConsider!=null) {
			GroupMemberVo memberToRemove = group.findMember(loginOfMemberToConsider);
			GroupMemberType memberType = memberToRemove.getType();
			
			if (memberToRemove != null) {
				if ((memberConnected.isAllowedToManageUser() && memberType.equals(GroupMemberType.MEMBER))
						||(memberConnected.isAllowedToManageManager() && memberType.equals(GroupMemberType.MANAGER))
						||(userVo.getLogin().equals(memberToRemove.getUserVo().getLogin()) && !(memberType.equals(GroupMemberType.OWNER)))) {
					groupFacade.removeMember(group, memberConnected.getUserVo(), memberToRemove.getUserVo());
					shareSessionObjects.addMessage(messages.format("pages.groups.deleteMember.success", memberToRemove.getFirstName(), memberToRemove.getLastName(), group.getName()));
				}
			}
		}
		loginOfMemberToConsider=null;
		groups = null;
		return this;
	}
	
	Object onActionFromConfirmDeleteGroup() throws BusinessException {
		if (memberConnected.isAllowedToManageGroup()) {
			groupFacade.delete(group, userVo);
			shareSessionObjects.addMessage(messages.format("pages.groups.delete.success", group.getName()));
		}
		groups = null;
		group = null;
		return this;
	}
	
	Object onActionFromAcceptNewMember(String login) {
		if (memberConnected.isAllowedToManageUser()) {
			GroupMemberVo memberToAccept = group.findMember(login);
			try {
				MailContainer mailContainer = mailContainerBuilder.buildMailContainer(userVo, null);
				groupFacade.acceptNewMember(group, memberConnected, memberToAccept, mailContainer);
				shareSessionObjects.addMessage(messages.format("pages.groups.acceptMember.success", login, group.getName()));
				groups = null;
				group = null;
			} catch (BusinessException e) {
				shareSessionObjects.addError(messages.format("pages.groups.acceptMember.failure", login, group.getName()));
			}
		}
		return this;
	}
	
	Object onActionFromRejectNewMember(String login) {
		if (memberConnected.isAllowedToManageUser()) {
			GroupMemberVo memberToAccept = group.findMember(login);
			try {
				MailContainer mailContainer = mailContainerBuilder.buildMailContainer(userVo, null);
				groupFacade.rejectNewMember(group, memberConnected, memberToAccept, mailContainer);
				shareSessionObjects.addMessage(messages.format("pages.groups.rejectMember.success", login, group.getName()));
				groups = null;
				group = null;
			} catch (BusinessException e) {
				shareSessionObjects.addError(messages.format("pages.groups.rejectMember.failure", login, group.getName()));
			}
		}
		return this;
	}

	private GroupVo getGroupVoFromName(String groupName) {
		for (GroupVo groupVo : groups) {
			if (groupVo.getName().equals(groupName)) {
				return groupVo;
			}
		}
		return null;
	}
	
	public boolean getHaveAtLeastOneGroup() {
		return groups.size() > 0;
	}
	
	@OnEvent(value="eventUpdateListGroups")
	void onEventFromUpdateGroups() {
		groups = null; //will be refilled by setupRender
	}
	
	@OnEvent(value="eventDeleteUniqueFromListDocument")
	public void onEventFromDeleteShare(Object[] object) throws BusinessException {
        String uuid = (String) object[0];
		try {
	
			ShareDocumentVo shareddoc=getDocumentByUUIDInList(uuid);
			if(null!=shareddoc){
				UserVo groupUser = userFacade.findUser(group.getGroupLogin());
		        shareFacade.deleteSharing(shareddoc, groupUser);
				MailContainer mailContainer = mailContainerBuilder.buildMailContainer(userVo, null);
		        groupFacade.notifySharingDeleted(shareddoc, userVo, group, mailContainer);
				shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"),
						shareddoc.getFileName()) );
			} else {
				throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid");
			}
		} catch (BusinessException e) {
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid",e);
		}
	}
	
    private ShareDocumentVo getDocumentByUUIDInList(String UUId) {
    	for (ShareDocumentVo doc : documents) {
			if ((doc.getIdentifier()).equals(UUId)) {
				return doc;
			}
		}
    	throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not find the document" );
    }

	public String getUserMemberType() {
		return messages.get("GroupMemberType."+group.getMemberType(userVo.getLogin()).name());
	}
	
	public boolean getIsTheOneConnected() {
		return (userVo.getLogin().equals(member.getUserVo().getLogin()));
	}

	public String getClassListe() {
		if (groupSelected!=null && group!=null && groupSelected.getName().equals(group.getName())) {
			return "selected";
		}
		return "notSelected";
	}
	
	public GroupMemberType getMemberType() {
		return GroupMemberType.MEMBER;
	}
	
	public GroupMemberType getManagerType() {
		return GroupMemberType.MANAGER;
	}
	
	public GroupMemberType getMemberToChangeType() {
		if (memberToChange == null) {
			memberToChangeType =  GroupMemberType.MEMBER;
		}
		else {
			memberToChangeType = memberToChange.getType();
		}
		return memberToChangeType;
	}
	
	public void setMemberToChangeType(GroupMemberType type) {
		memberToChangeType = type;
	}
	
	public boolean getDisableFilesDeletion() {
		if (group == null || memberConnected == null) {
			return true;
		}
		return !(memberConnected.isAllowedToDeleteFile());
	}

    public Zone onActionFromCreateGroup() {
        return createGroupPopup.getShowPopup();
    }

    public Zone onActionFromShowUser(String mail) {
        return userDetailsDisplayer.getShowUser(mail);
    }

    public Zone onActionFromShowWaitingUser(String mail) {
        return userDetailsDisplayer.getShowUser(mail);
    }
    
    public String getShowUserTooltip() {
    	if (member.getUserVo().isGuest()) {
    		if ((member.getUserVo().getOwnerLogin().equals(userVo.getLogin()))&&(member.getUserVo().getComment()!=null)&&(!member.getUserVo().getComment().equals(""))) {
    			return member.getUserVo().getComment();
    		}
    	}
    	return messages.get("pages.user.search.popup.welcome");
    }
    
    public String getShowWaitingUserTooltip() {
    	if (waitingForApprovalMember.getUserVo().isGuest()) {
    		if ((waitingForApprovalMember.getUserVo().getOwnerLogin().equals(userVo.getLogin()))&&(waitingForApprovalMember.getUserVo().getComment()!=null)&&(!waitingForApprovalMember.getUserVo().getComment().equals(""))) {
    			return waitingForApprovalMember.getUserVo().getComment();
    		}
    	}
    	return messages.get("pages.user.search.popup.welcome");
    }

    public String getFormattedMembershipDate() {
        if (member.getMembershipDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.date"));
            return formatter.format(member.getMembershipDate().getTime());
        }
        return null;
    }

    public String getMembershipDate() {
        if (waitingForApprovalMember.getMembershipDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.date"));
            return formatter.format(waitingForApprovalMember.getMembershipDate().getTime());
        }
        return null;
    }
    
    public String getMessageConfirmDeleteGroup() {
    	if (group!=null) {
    		return messages.format("pages.groups.delete.confirm.message", group.getName());
    	}
    	return "";
	}
    
    public String getMessageConfirmDeleteMembership() {
    	if (group!=null) {
    		return messages.format("pages.groups.members.delete.confirm.message", group.getName());
    	}
    	return "";
	}

	public Object onSuccessFromEditGroupForm() {
		try {
			groupFacade.update(group, userVo);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		inModify = false;
		return this;
	}

	public Object onSuccessFromEditMemberForm() {
		try {
			if (memberToChange.getType().equals(memberToChangeType)) {
				shareSessionObjects.addWarning(String.format(messages.get("pages.groups.members.edit.unchanged"),
						memberToChange.getFirstName(), memberToChange.getLastName()) );
			}
			else {
				groupFacade.updateMember(group, userVo, memberToChange.getUserVo(), memberToChangeType);
				memberToChange.setType(memberToChangeType);
				shareSessionObjects.addMessage(String.format(messages.get("pages.groups.members.edit.success"),
						memberToChange.getFirstName(), memberToChange.getLastName()) );
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return this;
	}
}
