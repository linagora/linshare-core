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
package org.linagora.linshare.view.tapestry.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.ActionFromBarDocument;
import org.linagora.linshare.view.tapestry.models.SorterModel;
import org.linagora.linshare.view.tapestry.models.impl.UserSorterModel;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;

/** This component gives ability to search users.
 *
 */
@SupportsInformalParameters
@Import(library = {"UserSearchResults.js"})
public class UserSearchResults {

    /* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    private List<UserVo> userShareList;
    
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<UserVo> users;
    
    @Parameter(required = false, defaultPrefix = BindingConstants.PROP)
    @Property
    private boolean inSearch;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private UserFacade userFacade;
    @Inject
    private Logger logger;
    @Inject
    private Messages messages;
    @InjectComponent
    private UserDetailsDisplayer userDetailsDisplayer;

    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=550", "height=400"})
    private WindowWithEffects userEditWindow;

    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=400", "height=120"})
    private WindowWithEffects zoneDomainMoveWindow;

    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=550", "height=350"})
    private WindowWithEffects userAddToThreadWindow;

    @InjectComponent
    private Zone userEditTemplateZone;

    @InjectComponent
    private Zone zoneDomainFormMove;

    @InjectComponent
	private Zone userAddToThreadTemplateZone;

    @Inject
    private ComponentResources componentResources;

	@Environmental
	private JavaScriptSupport renderSupport;


	@Inject @Symbol("linshare.users.internal.defaultView.showAll")
	@Property
	private boolean showAll;

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @SessionState
    private UserVo userLoggedIn;
    @SessionState
    private ShareSessionObjects shareSessionObjects;
    @Property
    private UserVo user;
    @Property
    private UserVo detailedUser = new UserVo("", "", "", "", AccountType.GUEST);

    @SuppressWarnings("unused")
	@Property
    private Boolean valueCheck;
    
    private List<UserVo> selectedUsers;

    @Property
    @Persist
	private List<UserVo> userAddToThreadList;

    private boolean share = false;

    @Property
    @Persist(value="flash")
    private String selectedLogin;

	/**
	 * Components Model.
	 */
	@SuppressWarnings("unused")
	@Property
	@Persist
	private SorterModel<UserVo> sorterModel;

    @Persist
    private boolean refreshFlag;

    @Persist
    private List<UserVo> usr;

    @Persist("flash")
    private ActionFromBarDocument actionbutton;

    @Property
    private String action;

    @Property
 	@Persist
	private boolean memberAddShowPopup;
    
    @Persist
    @Property
    private List<AbstractDomainVo> domains;

    @Persist
    @Property
    private AbstractDomainVo selectedDomain;
    @Inject
    private AbstractDomainFacade domainFacade;

    @Inject
    private ThreadEntryFacade threadEntryFacade;

	@Persist
	@Property
	private List<ThreadVo> threads;

    @Property
    @Persist
    private boolean showBreakedUsers;

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void initUserSearch() throws BusinessException {
        if (selectedUsers == null) {
            selectedUsers = new ArrayList<UserVo>();
        }
        if (userAddToThreadList == null) {
            userAddToThreadList = new ArrayList<UserVo>();
        }
        threads = threadEntryFacade.getAllMyThreadWhereAdmin(userLoggedIn);
        if (users == null || users.size() == 0) {
            if (userLoggedIn.isSuperAdmin() && showBreakedUsers) {
                users = userFacade.searchAllBreakedUsers(userLoggedIn);
            } else if (!inSearch) {
                if (showAll || userLoggedIn.isRestricted()) {
                    users = userFacade.searchUser("", "", "", userLoggedIn);
                } else {
                    users = userFacade.searchGuest(userLoggedIn);
                }
            }
        }
        if (refreshFlag){
            users = usr;
            refreshFlag = false;
        }
        sorterModel = new UserSorterModel(users);

        if (userLoggedIn.isSuperAdmin()) {
            domains = domainFacade.findAllTopAndSubDomain();
        } else {
            domains = new ArrayList<AbstractDomainVo>();
        }
    }

	/**
	 * Initialize the JS value
	 */
	@AfterRender
	public void afterRender() {
		if ((users != null) && (users.size() > 0))
			renderSupport.addScript(String.format("countUserCheckbox('');"));
        if (memberAddShowPopup) {
            renderSupport.addScript(String.format("userAddToThreadWindow.showCenter(true)"));
            memberAddShowPopup=false;
        }
	}

    public void onSuccess() {
		actionbutton = ActionFromBarDocument.fromString(action);
		switch (actionbutton) {
		case SHARED_ACTION:
			if (userShareList == null) {
                userShareList = new ArrayList<UserVo>();
            }
            for (UserVo userVo : selectedUsers) {
                if (!userShareList.contains(userVo)) {
                    userShareList.add(userVo);
                }
            }
            selectedUsers = new ArrayList<UserVo>();
			break;
		case MEMBER_ADD_ACTION:
			userAddToThreadList = new ArrayList<UserVo>();
            for (UserVo userVo : selectedUsers) {
                if (!userAddToThreadList.contains(userVo)) {
                    userAddToThreadList.add(userVo);
                }
            }
            selectedUsers = new ArrayList<UserVo>();
            memberAddShowPopup = true;
			break;
		case NO_ACTION:
		default:
			break;
		}

		actionbutton = ActionFromBarDocument.NO_ACTION;
    }

    public Zone onActionFromShowUser(String mail) throws BusinessException {
        return userDetailsDisplayer.getShowUser(mail);
    }

    public void onActionFromDelete(String login) {
        this.selectedLogin = login;
    }

    public Zone onActionFromDomainMove(String login) {
        this.selectedLogin = login;
        for (UserVo user : users) {
			if (user.getLogin().equals(selectedLogin)) {
				this.selectedDomain = getValueEncoder().toValue(user.getDomainIdentifier());
				break;
			}
		}
        return zoneDomainFormMove;
    }

    public Zone onActionFromEdit(String login) {
        this.selectedLogin = login;
        return userEditTemplateZone;
    }

    public void onActionFromShare(String login) {
        if (userShareList == null) {
            userShareList = new ArrayList<UserVo>();
        }
        UserVo user_ = getUserFromLogin(login);
        if (!userShareList.contains(user_)) {
            userShareList.add(user_);
        }
        shareSessionObjects.setMultipleSharing(true);
    }

    public Zone onActionFromAdd(String login) {
        userAddToThreadList = new ArrayList<UserVo>();
        UserVo selectedUserVo = getUserFromLogin(login);
        UserVo user = userFacade.loadUserDetails(selectedUserVo.getMail(), userLoggedIn.getDomainIdentifier());
        userAddToThreadList.add(user);
        return userAddToThreadTemplateZone;
    }


    public Object onActionFromBreakedUsers() {
        showBreakedUsers = !showBreakedUsers;
        inSearch = false;
        users = null;
        return this;
    }
    
    public boolean getIsSuperAdmin() {
    	return userLoggedIn.isSuperAdmin();
    }
    
    public ValueEncoder<AbstractDomainVo> getValueEncoder() {
    	return new ValueEncoder<AbstractDomainVo>() {
    		public String toClient(AbstractDomainVo value) {
    			return value.getIdentifier();
    		}
    		public AbstractDomainVo toValue(String clientValue) {
    			for (AbstractDomainVo domain : domains) {
    	    		if (domain.getIdentifier().equals(clientValue)) {
    	    			return domain;
    	    		}
    			}
    			return null;
    		}
		};
    }
    
    public Object onSubmitFromUpdateDomain() throws BusinessException {
    	try {
    		UserVo selectedUserVo = getUserFromLogin(selectedLogin);
    		userFacade.updateUserDomain(selectedUserVo.getLogin(), selectedDomain, userLoggedIn);
    	} catch (BusinessException e) {
    		logger.error(e.getMessage());
			logger.debug(e.toString());
			businessMessagesManagementService.notify(e);
			return this;
    	}
        for (UserVo user : users) {
			if (user.getLogin().equals(selectedLogin)) {
				user.setDomainIdentifier(selectedDomain.getIdentifier());
				break;
			}
		}
    	return this;
    }

    
    @SuppressWarnings("unchecked")
	@OnEvent(value="eventReorderList")
    public void refreshUser(Object[] o1){
    	if(o1!=null && o1.length>0){
	    	this.usr=((List<UserVo>)Arrays.copyOf(o1,1)[0]);
			this.sorterModel=new UserSorterModel(this.usr);
	    	refreshFlag=true;
    	}
    }
    
	@OnEvent(value="userDeleteEvent")
    public void deleteUser() {
		UserVo selectedUserVo = getUserFromLogin(selectedLogin);
        userFacade.deleteUser(selectedUserVo.getLogin(), userLoggedIn);
        shareSessionObjects.addMessage(messages.format("components.userSearch.action.delete.confirm", selectedUserVo.getCompleteName()));
        componentResources.triggerEvent("resetListUsers", null, null);
    }

    public boolean isSelected() {
        return false;
    }

    public void setSelected(boolean selected) {
        if (selectedUsers == null) {
            selectedUsers = new ArrayList<UserVo>();
        }
        if (selected) {
            selectedUsers.add(user);
        }
    }

    @OnEvent(value = "share")
    public void addInShareList() {
        share = true;
    }

    @AfterRender
    public void finish() throws BusinessException {
    }
    
    /* ***********************************************************
     *                     HELPERS
     ************************************************************ */

    /**
     * only a user that have the admin role in a thread can add new users in it
     * @return true if the logged in user is admin in a thread
     * @throws BusinessException 
     */
    public boolean isUserAbleToAddToThread() {
        try {
            return threadEntryFacade.isUserAdminOfAnyThread(userLoggedIn);
		} catch (BusinessException e) {
			logger.error(e.getErrorCode().toString());
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
    }

    /**
     * an admin can edit any user
     * an simple user can edit only his users (owner of the guest account)
     * @return true if the logged in user on the application can edit the data of one user on the grid
     * @throws BusinessException 
     */
    public boolean isUserEditable() {
    	try {
			return userFacade.isAdminForThisUser(userLoggedIn, user);
		} catch (BusinessException e) {
			logger.error(e.getErrorCode().toString());
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
    }
    
    /**
     * an admin can delete any Guest user and can purge internal user
     * an simple user can delete only his users (owner of the guest)
     * @return true if the logged in user on the application can edit the data of one user on the grid
     */
    public boolean isUserDeletable() {
    	try {
			return userFacade.isAdminForThisUser(userLoggedIn, user);
		} catch (BusinessException e) {
			logger.error(e.getErrorCode().toString());
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
    }
    
    public boolean isUserDomainMovable() {
    	return userLoggedIn.isSuperAdmin();
    }
        
    public boolean isUserGuest() {
    	return user.isGuest();
    }
    
    public boolean isUserGuestRestricted() {
    	return (user.isGuest()&&user.isRestricted());
    }
    
    public boolean isUserAdmin() {
    	return (!user.isGuest()&&user.isAdministrator());
    }
    
    /**
     * is the logged user an administrator ?
     * @return
     */
    public boolean isAdmin() {
    	return userLoggedIn.isAdministrator();
    }
    public boolean isSuperAdmin() {
    	return userLoggedIn.isSuperAdmin();
    }
    
    public String getShowUserTooltip() {
    	if (user.isGuest()) {
    		if ((user.getOwnerLogin().equals(userLoggedIn.getLogin()))&&(user.getComment()!=null)&&(!user.getComment().equals(""))) {
    			return user.getComment();
    		}
    	}
    	return messages.get("pages.user.search.popup.welcome");
    }

    public String getUserCommentExtract() {
    	if (user.isGuest()) {
    		if ((user.getOwnerLogin().equals(userLoggedIn.getLogin()))&&(user.getComment()!=null)&&(!user.getComment().equals(""))) {
    			String extract = null;
                if (user.getComment().length() > 15) {
                    extract = user.getComment().substring(0, 15);
                } else {
                    extract = user.getComment();
                }
                extract = extract + "...";
                return extract;
    		}
    	}
        return null;
    }

    public String getFormattedUserExpiryDate() {
        if (user.getExpirationDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.date"));
            return formatter.format(user.getExpirationDate());
        }
        return null;
    }

    private UserVo getUserFromLogin(String login) {
        if (login == null) {
            return null;
        }
        for (UserVo userVo_ : users) {
            if (login.equals(userVo_.getLogin())) {
                return userVo_;
            }
        }
        return null;
    }

}
