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
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.view.tapestry.beans.MenuEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyBorderLayout {

	private static final Logger logger = LoggerFactory.getLogger(MyBorderLayout.class);

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
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block searchWidget;
	
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block infoWidget;

	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block actionsWidget;
	
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block shareWidget;
	
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block eventsWidget;

	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block membersWidget;
	
	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	@Inject
	@Path("context:css/theme0-ie9.css")
	private Asset ie9CssAsset;
	
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

	@Property
	private boolean admin;

	@Property
	private boolean superadmin;

	@Property
	private boolean userExt;

	@Property
	private boolean user;

	@Property
	private List<Locale> excludeLocales;

	@Property
	private List<Locale> includeLocales;

	@Property
	private String ie10Script;

	@Property
	private String ie9Css;

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

	@Inject @Symbol("linshare.logo.webapp.visible")
	@Property
	private boolean linshareLogoVisible;
	
	@Inject @Symbol("linshare.display.licenceTerm")
	@Property
	private boolean linshareLicenceTerm;
	
	private static final String helpLabelKey = "components.myborderlayout.help.title";
	private static final String groupsLabelKey = "components.myborderlayout.group.title";
	
	@Property
	private String logoLink;
	
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
			if(functionalityFacade.isEnableCustomLogoLink(userVo.getDomainIdentifier())) {
				logoLink = domainFacade.getCustomLogoLink(userVo);
			}
			
		} else if (domainFacade.isCustomLogoActiveInRootDomain()) {
			customLogoUrl = domainFacade.getCustomLogoUrlInRootDomain();
		}

		ie10Script = "<script> " +
				"if(Function('/*@cc_on return document.documentMode===10@*/')()){" +
					"document.documentElement.className+=' ie10';" +
				"}" +
				"</script>";
		
		ie9Css = "<!--[if IE 9]><link href='"+ie9CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";
		
		ie8Css = "<!--[if IE 8]><link href='"+ie8CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";

		ie7Css = "<!--[if IE 7]><link href='"+ie7CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";

		ie6Css = "<!--[if lte IE 6]><link href='"+ie6CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";
//				+ "<script src='"+ie6DDPNGAsset.toClientURL()+"' ></script><script>DD_belatedPNG.fix('img, h1, a.button, a.button span');</script><![endif]--> ";
		
		defaultCss = "<link href='"+defaultCssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/>";
		includeLocales = new ArrayList<Locale>();
		includeLocales.add(Locale.FRENCH);
		includeLocales.add(Locale.ENGLISH);
		
		menu.clearMenuEntry();
		MenuEntry homeMenu;
		MenuEntry fileMenu;
		MenuEntry userMenu;
		MenuEntry threadAdminMenu;
		MenuEntry threadMenu;
		MenuEntry adminMenu;
		MenuEntry domainMenu;
		MenuEntry auditMenu;
		MenuEntry helpMenu;
		
		
		// Menu : Home / File 
		homeMenu = new MenuEntry(response.encodeURL("index"),messages.get("components.myborderlayout.home.title"),null,null,"home");
		fileMenu = new MenuEntry(response.encodeURL("files/index"),messages.get("components.myborderlayout.file.title"),null,null,"files");

		// Menu : User
		userMenu = new MenuEntry(response.encodeURL("user/index"),messages.get("components.myborderlayout.user.title"),null,null,"user");
	
		// Menu : Thread

		threadAdminMenu = new MenuEntry(response.encodeURL("administration/thread/index"),messages.get("components.myborderlayout.thread.title"),null,null,"thread");
		threadMenu = new MenuEntry(response.encodeURL("thread/index"),messages.get("components.myborderlayout.thread.title"),null,null,"thread");
		
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
			// users : Accueil / Fichiers / List / Threads / Users / History / help
			// admin : Accueil / Fichiers / List / Threads / Users/ Admin /History / help
			// root : Admin / Domain / Users / Threads / List / History / help
			
			if (superadmin) {
				menu.addMenuEntry(adminMenu);
				menu.addMenuEntry(domainMenu);
				menu.addMenuEntry(userMenu);
				if (showThreadTab())
					menu.addMenuEntry(threadAdminMenu);
				
			} else {
				menu.addMenuEntry(homeMenu);
				menu.addMenuEntry(fileMenu);
				if (showUserTab())
					menu.addMenuEntry(userMenu);
				if (showThreadTab()) 
					menu.addMenuEntry(threadMenu);
				if (admin)
					menu.addMenuEntry(adminMenu);
			}
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
			return functionalityFacade.isEnableThreadTab(userVo.getDomainIdentifier());
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
	
	public boolean getDisplayLogo() {
		if(linshareLicenceTerm || linshareLogoVisible) {
			return true;
		}
		return false;
	}
	
	public boolean getCustomLogoLink(){
		if(userVoExists){
			return functionalityFacade.isEnableCustomLogoLink(userVo.getDomainIdentifier());
		}
		return false;
	}
	
}
