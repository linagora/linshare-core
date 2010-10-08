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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;


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
    private ParameterFacade parameterFacade;

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

	@Property
	private String helpLink ="";
	
	@Property
	private String helpLabel ="";
	
	@Property
	private String helpHighlight ="";

	@Property
	private String groupsLink ="";
	
	@Property
	private String groupsLabel ="";
	
	@Property
	private String groupsHighlight ="";

    @Property(write=false)
    private String customLogoUrl;
    
	@Inject @Symbol("linshare.groups.activated")
	@Property
	private boolean showGroups;
	
	private static final String helpLabelKey = "components.myborderlayout.help.title";
	private static final String groupsLabelKey = "components.myborderlayout.group.title";
	
	/* ***********************************************************
	 *                       Phase processing
	 ************************************************************ */
	
	@SetupRender
	public void init() throws BusinessException {
		
        ParameterVo parameterVo = parameterFacade.loadConfig();
        customLogoUrl = parameterVo.getCustomLogoUrl();
		
		ie8Css="<!--[if IE 8]><link href='"+ie8CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";

		ie7Css="<!--[if IE 7]><link href='"+ie7CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";

		ie6Css="<!--[if lte IE 6]><link href='"+ie6CssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/><![endif]-->";
//				+ "<script src='"+ie6DDPNGAsset.toClientURL()+"' ></script><script>DD_belatedPNG.fix('img, h1, a.button, a.button span');</script><![endif]--> ";
		
		defaultCss="<link href='"+defaultCssAsset.toClientURL()+"' rel='stylesheet' type='text/css'/>";
		
		if(userVoExists){
			admin=(userVo.getRole().equals(Role.ADMIN));
			user=(userVo.getRole().equals(Role.SIMPLE));
			userExt=(userVo.isGuest() && !userVo.isUpload());
		}else{
			admin=false;
			user=false;
			userExt=false;
		}
		includeLocales=new ArrayList<Locale>();
		includeLocales.add(Locale.FRENCH);
		includeLocales.add(Locale.ENGLISH);
	//	includeLocales.add(Locale.JAPANESE);
	//	includeLocales.add(Locale.CHINESE);
		initHelpInfo();
		initGroupsInfo();
	}
	
	
	/* ***********************************************************
	 *                          Helpers
	 ************************************************************ */
	
	void initHelpInfo() {
		String pre = "";
		if (userVoExists) {
			pre = ";";
		}
		
		if ((null!=isHelpPage)&&(isHelpPage)) {
			helpLink = pre+"help";
			helpLabel = pre+messages.get(helpLabelKey);
			helpHighlight = pre+"help";
		}
		
	}
	void initGroupsInfo() {
		String pre = ";";
		
		if (showGroups) {
			groupsLink = pre+"groups";
			groupsLabel = pre+messages.get(groupsLabelKey);
			groupsHighlight = pre+"groups";
		}
		
	}
		
}
