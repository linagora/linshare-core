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
package org.linagora.linShare.view.tapestry.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.Facade.GroupFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.enums.ActionFromBarDocument;
import org.linagora.linShare.view.tapestry.models.SorterModel;
import org.linagora.linShare.view.tapestry.models.impl.UserSorterModel;
import org.slf4j.Logger;

/** This component gives ability to search users.
 *
 */
@SupportsInformalParameters
@IncludeJavaScriptLibrary(value = {"UserSearchResults.js"})
public class UserSearchResults {

    /* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    private List<UserVo> userShareList;
    
    @Property
    @Persist
    private List<UserVo> userAddToGroupsList;

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
    private GroupFacade groupFacade;
    @Inject
    private Logger logger;
    @Inject
    private Messages messages;
    @InjectComponent
    private UserDetailsDisplayer userDetailsDisplayer;
    
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=550", "height=400"})
    private WindowWithEffects userEditWindow;
    
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=550", "height=350"})
    private WindowWithEffects userAddToGroupWindow;
    
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=400", "height=120"})
    private WindowWithEffects zoneDomainMoveWindow;

    @InjectComponent
    private Zone userEditTemplateZone;

    @InjectComponent
    private Zone zoneDomainFormMove;

    @InjectComponent
    private Zone userAddToGroupTemplateZone;

    @Inject
    private ComponentResources componentResources;
    
	@Environmental
	private RenderSupport renderSupport;
    
    
	@Inject @Symbol("linshare.users.internal.defaultView.showAll")
	@Property
	private boolean showAll;
    
    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @ApplicationState
    private UserVo userLoggedIn;
    @ApplicationState
    private ShareSessionObjects shareSessionObjects;
    @Property
    private UserVo user;
    @Property
    private UserVo detailedUser = new UserVo("", "", "", "", UserType.GUEST);

    @SuppressWarnings("unused")
	@Property
    private Boolean valueCheck;
    
    private List<UserVo> selectedUsers;

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
	private List<GroupVo> groups;
	
	@Property
	@Persist
	private boolean memberAddShowPopup;
	
	@Persist
	@Property
	private List<DomainVo> domains;
	
	@Persist
	@Property
	private DomainVo selectedDomain;
    @Inject
    private DomainFacade domainFacade;
    
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
        if (userAddToGroupsList == null) {
        	userAddToGroupsList = new ArrayList<UserVo>();
        }
        if (users == null || users.size() == 0) {
        	if (userLoggedIn.isSuperAdmin() && showBreakedUsers) {
    			users = userFacade.searchAllBreakedUsers(userLoggedIn);
    		} else if (inSearch==false) {
        		if (showAll || userLoggedIn.isRestricted()) {
        			users = userFacade.searchUser("", "", "", userLoggedIn);
        		} else {
        			users = userFacade.searchGuest(userLoggedIn.getMail());
        		}
        	}
    	}
        groups = groupFacade.findByUser(userLoggedIn.getLogin());
        
        if(refreshFlag==true){
			users=usr;
			refreshFlag=false;
		}
        sorterModel=new UserSorterModel(users);
        
        if (userLoggedIn.isSuperAdmin()) {
    		domains = domainFacade.findAllDomains();
    	} else {
    		domains = new ArrayList<DomainVo>();
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
            renderSupport.addScript(String.format("userAddToGroupWindow.showCenter(true)"));
            memberAddShowPopup=false;
		}
	}

    public void onSuccess() {
		actionbutton =  ActionFromBarDocument.fromString(action);
		
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
			userAddToGroupsList = new ArrayList<UserVo>();
            for (UserVo userVo : selectedUsers) {
                if (!userAddToGroupsList.contains(userVo)) {
                	userAddToGroupsList.add(userVo);
                }
            }
            selectedUsers = new ArrayList<UserVo>();
            memberAddShowPopup=true;
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
    
    public Zone onActionFromAddToGroup(String userLogin) {
    	userAddToGroupsList = new ArrayList<UserVo>();
    	UserVo user = userFacade.loadUserDetails(userLogin, userLoggedIn.getDomainIdentifier());
    	userAddToGroupsList.add(user);
        return userAddToGroupTemplateZone;
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
    
    public ValueEncoder<DomainVo> getValueEncoder() {
    	return new ValueEncoder<DomainVo>() {
    		public String toClient(DomainVo value) {
    			return value.getIdentifier();
    		}
    		public DomainVo toValue(String clientValue) {
    			for (DomainVo domain : domains) {
    	    		if (domain.getIdentifier().equals(clientValue)) {
    	    			return domain;
    	    		}
    			}
    			return null;
    		}
		};
    }
    
    public Object onSubmitFromUpdateDomain() throws BusinessException {
    	userFacade.updateUserDomain(selectedLogin, selectedDomain, userLoggedIn);
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
        userFacade.deleteUser(selectedLogin, userLoggedIn);
        shareSessionObjects.addMessage(messages.format("components.userSearch.action.delete.confirm", selectedLogin));
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
    public void finish() {
    }
    
    /* ***********************************************************
     *                     HELPERS
     ************************************************************ */
    
    /**
     * an admin can edit any user
     * an simple user can edit only his users (owner of the guest account)
     * @return true if the logged in user on the application can edit the data of one user on the grid
     */
    
    public boolean isUserEditable() {

        if (userLoggedIn.isSuperAdmin()) {
        	return true;
        }
        if (userLoggedIn.isAdministrator() && userLoggedIn.getDomainIdentifier().equals(user.getDomainIdentifier())) {
            return true;
        } else {
            return isOwner();
        }
    }
    
    /**
     * an admin can delete any Guest user and can purge internal user
     * an simple user can delete only his users (owner of the guest)
     * @return true if the logged in user on the application can edit the data of one user on the grid
     */
    
    public boolean isUserDeletable() {
        if (user.getLogin().trim().equals(userLoggedIn.getLogin().trim())) {
        	return false;
        }
        if (userLoggedIn.isSuperAdmin()) {
        	return true;
        }
        if (userLoggedIn.isAdministrator() && userLoggedIn.getDomainIdentifier().equals(user.getDomainIdentifier())) {
        	return true;
        } 
        return isOwner();
    }
    
    public boolean isUserDomainMovable() {
    	return userLoggedIn.isSuperAdmin();
    }
    
    /**
     * is the logged in user the owner of the user guest account ?
     * @return
     */
    private boolean isOwner() {
        String userLogin = userLoggedIn.getLogin();

        if (UserType.GUEST.equals(user.getUserType()) && userLogin != null) {
            return userLogin.equals(user.getOwnerLogin());
        } else {
            return false;
        }
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
    public boolean isAdmin(){
    	return userLoggedIn.isAdministrator();
    }
    public boolean isSuperAdmin(){
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
