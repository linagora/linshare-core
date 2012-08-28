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
package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.Facade.AbstractDomainFacade;
import org.linagora.linshare.core.Facade.FunctionalityFacade;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.MenuEntry;


public class MyBorderLayout {


 	/* ***********************************************************
	 *                         Parameters
	 ************************************************************ */
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String title;
	
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String identifier;
	
	@SuppressWarnings("unused")
	@Parameter(required=false,defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String linkTracker;

	@SuppressWarnings("unused")
	@Parameter(required=false,value="",defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String currentHighlight;
	
	@Parameter(required=false,value="false",defaultPrefix = BindingConstants.LITERAL)
	@Property
	private Boolean isHelpPage;
	
	/**
	 * Widgets in the sideBar
	 */
	@SuppressWarnings("unused")
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block searchWidget;
	
	@SuppressWarnings("unused")
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block infoWidget;

	@SuppressWarnings("unused")
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block actionsWidget;
	
	@SuppressWarnings("unused")
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block shareWidget;
	
	@SuppressWarnings("unused")
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block eventsWidget;

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	@Inject
	@Path("context:css/theme0-ie8.css")
	private Asset ie8CssAsset;

	@Inject
	@Path("context:css/theme0-ie7.css")
	private Asset ie7CssAsset;

	@Inject
	@Path("context:css/theme0-ie6.css")
	private Asset ie6CssAsset;

	@Inject
	@Path("context:css/theme0.css")
	private Asset defaultCssAsset;

	@Inject
	@Path("context:js/DD_belatedPNG.js")
	private Asset ie6DDPNGAsset;

	@Inject
	private Messages messages;

	@Inject
	private AbstractDomainFacade domainFacade;
	
	@Inject
	private FunctionalityFacade functionalityFacade;

    /* ***********************************************************
	 *                Properties & injected symbol, ASO, etc
	 ************************************************************ */
	 
	@SessionState
	@Property
	private UserVo userVo;
	
	@Property
	private boolean userVoExists;
	
	@SuppressWarnings("unused")
	@Property
	private boolean admin;
	
	@SuppressWarnings("unused")
	@Property
	private boolean superadmin;
	
	@SuppressWarnings("unused")
	@Property
	private boolean userExt;
	
	@SuppressWarnings("unused")
	@Property
	private boolean user;
	
	@SuppressWarnings("unused")
	@Property
	private List<Locale> excludeLocales;
	
	@Property
	private List<Locale> includeLocales;
	
	@Property
	private String ie8Css;

	@Property
	private String ie7Css;

	@Property
	private String ie6Css;

	@Property
	private String defaultCss;

    @Property(write=false)
    private String customLogoUrl;
    
	@Inject @Symbol("linshare.googleChromeFrame.enabled")
	@Property
	private boolean enableChromeForIE;

	@SuppressWarnings("unused")
	@Inject @Symbol("linshare.secured-storage.disallow")
	@Property
	private boolean securedStorageDisallowed;

	@SuppressWarnings("unused")
	@Inject @Symbol("linshare.logo.webapp.visible")
	@Property
	private boolean linshareLogoVisible;
	
	private static final String helpLabelKey = "components.myborderlayout.help.title";
	private static final String groupsLabelKey = "components.myborderlayout.group.title";
	
	
	
	@SuppressWarnings("unused")
	@Inject
	private Response response;
	
	@InjectComponent
	private Menu menu;
	
	
	
	/* ***********************************************************
	 *                       Phase processing
	 ************************************************************ */
	
	@SetupRender
	public void init() throws BusinessException {

		if (userVoExists && userVo.getDomainIdentifier() != null && userVo.getDomainIdentifier().length() > 0) {
			if(domainFacade.isCustomLogoActive(userVo)) {
				customLogoUrl = domainFacade.getCustomLogoUrl(userVo);
			}
		}
		
		ie8Css="<!--[if IE 8]><link href='"+ie8CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";

		ie7Css="<!--[if IE 7]><link href='"+ie7CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";

		ie6Css="<!--[if lte IE 6]><link href='"+ie6CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";
//				+ "<script src='"+ie6DDPNGAsset.toClientURL()+"' ></script><script>DD_belatedPNG.fix('img, h1, a.button, a.button span');</script><![endif]--> ";
		
		defaultCss="<link href='"+defaultCssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/>";
		includeLocales=new ArrayList<Locale>();
		includeLocales.add(Locale.FRENCH);
		includeLocales.add(Locale.ENGLISH);
		
		menu.clearMenuEntry();
		MenuEntry homeMenu;
		MenuEntry fileMenu;
		MenuEntry userMenu;
		MenuEntry threadMenu;
		MenuEntry groupMenu;
		MenuEntry adminMenu;
		MenuEntry domainMenu;
		MenuEntry auditMenu;
		MenuEntry helpMenu;
		
		// Menu : Home / File 
		if(securedStorageDisallowed) {
			homeMenu = new MenuEntry(response.encodeURL("index"),messages.get("components.myborderlayout.securedStorageDisallowed.home.title"),null,null,"home");
			fileMenu = new MenuEntry(response.encodeURL("files/index"),messages.get("components.myborderlayout.securedStorageDisallowed.file.title"),null,null,"files");
		} else {
			homeMenu = new MenuEntry(response.encodeURL("index"),messages.get("components.myborderlayout.home.title"),null,null,"home");
			fileMenu = new MenuEntry(response.encodeURL("files/index"),messages.get("components.myborderlayout.file.title"),null,null,"files");
		}

		// Menu : User
		userMenu = new MenuEntry(response.encodeURL("thread/index"),messages.get("components.myborderlayout.thread.title"),null,null,"thread");
	
		// Menu : Thread
		threadMenu = new MenuEntry(response.encodeURL("user/index"),messages.get("components.myborderlayout.user.title"),null,null,"user");
		
		// Menu : Groups
		groupMenu = new MenuEntry(response.encodeURL("groups/index"),messages.get("components.myborderlayout.group.title"),null,null,"groups");
		
		// Menu : Administration
		adminMenu = new MenuEntry(response.encodeURL("administration/index"),messages.get("components.myborderlayout.administration.title"),null,null,"administration");
		
		// Menu : Domains
		domainMenu = new MenuEntry(response.encodeURL("administration/domains/index"),messages.get("components.myborderlayout.administration.domains.title"),null,null,"domains");
		
		// Menu : History / Audit
		if(superadmin) {
			auditMenu = new MenuEntry(response.encodeURL("administration/audit"),messages.get("components.myborderlayout.audit.title"),null,null,"audit");
		} else {
			auditMenu = new MenuEntry(response.encodeURL("history/index"),messages.get("components.myborderlayout.history.title"),null,null,"history");
		}
		
		// Menu : Help
		helpMenu = new MenuEntry(response.encodeURL("help/index"),messages.get("components.myborderlayout.help.title"),null,null,"help");

		
		// home files user groups administration domains audit help
		if (userVoExists) {
			admin=(userVo.getRole().equals(Role.ADMIN));
			superadmin=(userVo.getRole().equals(Role.SUPERADMIN));
			user=(userVo.getRole().equals(Role.SIMPLE));
			// just home and help page
			userExt=(userVo.isGuest() && !userVo.isUpload());
		} else {
			admin=false;
			superadmin=false;
			user=false;
			userExt=false;
		}
		
		if(userVoExists && !userExt) {
			if (!superadmin) {
				menu.addMenuEntry(homeMenu);
				menu.addMenuEntry(fileMenu);
			}
			if (showUserTab())
				menu.addMenuEntry(userMenu);
			if (!superadmin && showThreadTab())
				menu.addMenuEntry(threadMenu);
			if (superadmin || admin)
				menu.addMenuEntry(adminMenu);
			if (superadmin)
				menu.addMenuEntry(domainMenu);			
			if (showAuditTab())
				menu.addMenuEntry(auditMenu);
			if (showHelpTab())
				menu.addMenuEntry(helpMenu);
		} else {
			menu.addMenuEntry(homeMenu);
			if (showHelpTab())
				menu.addMenuEntry(helpMenu);
		}
	}
	
	boolean showThreadTab() {
		if (userVoExists && userVo.getDomainIdentifier() != null && userVo.getDomainIdentifier().length() > 0) {
			// TODO :
			// return functionalityFacade.isEnableThreadTab(userVo.getDomainIdentifier());
			return true;
		}
		return false;
	}

	boolean showUserTab() {
		if (userVoExists && userVo.getDomainIdentifier() != null && userVo.getDomainIdentifier().length() > 0) {
			if(superadmin) {
				return true;
			}
			return functionalityFacade.isEnableUserTab(userVo.getDomainIdentifier());
		}
		return false;
	}
	
	
	boolean showAuditTab() {
		if (userVoExists && userVo.getDomainIdentifier() != null && userVo.getDomainIdentifier().length() > 0) {
			if(superadmin) {
				return true;
			}
			return functionalityFacade.isEnableAuditTab(userVo.getDomainIdentifier());
		}
		return false;
	}
	
	boolean showHelpTab() {
		if (userVoExists && userVo.getDomainIdentifier() != null && userVo.getDomainIdentifier().length() > 0) {
			if(superadmin) {
				return true;
			}
			return functionalityFacade.isEnableHelpTab(userVo.getDomainIdentifier());
		}
		return false;
	}
}
