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

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.linagora.linShare.core.domain.vo.HelpVO;
import org.linagora.linShare.view.tapestry.enums.HelpType;
import org.linagora.linShare.view.tapestry.objects.HelpsASO;
import org.linagora.linShare.view.tapestry.pages.help.ManualText;
import org.linagora.linShare.view.tapestry.pages.help.ManualVideo;


/**
 * this components launches the help.
 * @author ngapaillard
 *
 */
public class HelpLauncher {

	@SessionState
	private HelpsASO helpASO;

	

	@SuppressWarnings("unused")
	@Property
	private String uuid;
	
	private boolean helpASOExists;
	/**
	 * The id of the section.
	 */
	
	@Parameter(required=true,allowNull=false,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String idSection;
	
	/**
	 * The css class
	 */
	@SuppressWarnings("unused")
	@Parameter(name="class",required=true,allowNull=false,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String cssClass;
	
	
	/**
	 * The role associated with the section.
	 */
	@Parameter(required=true,allowNull=false, defaultPrefix=BindingConstants.LITERAL)
	private String role;
	
	
	/**
	 * The number of descriptions by subsections.
	 * For ex.: 3,2,1
	 * 3 descriptions for the first subsection.
	 * 2 descriptions for the second subsection.
	 * 1 description for the third subsection.
	 * 
	 */
	@Parameter(required=false,allowNull=false, defaultPrefix=BindingConstants.LITERAL)
	private String descItems;
	
	/**
	 * The extension of the image.
	 * By default png.
	 */
	@Parameter(required=false,value="png",allowNull=true, defaultPrefix=BindingConstants.LITERAL)
	private String imgExtension;
	
	/**
	 * The extension of the video.
	 * By default swf.
	 */
	@Parameter(required=false,value="swf",allowNull=false, defaultPrefix=BindingConstants.LITERAL)
	private String videoExtension;
	
	
	/**
	 * The title of the help.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=true,allowNull=false, defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String title;
	
	/**
	 * The type of the help.
	 * video or text.
	 * by default text.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=false,allowNull=false, defaultPrefix=BindingConstants.LITERAL)
	@Property
	private boolean video;
	
	@InjectPage
	private ManualText textPage;
	
	@InjectPage
	private ManualVideo videoPage;
	
	@SetupRender
	public void init(){
		if(!helpASOExists){
			helpASO=new HelpsASO();
		}
		if(!video){
			HelpVO helpVo=new HelpVO(idSection,role, descItems,imgExtension,HelpType.TEXT);
			this.uuid=helpVo.getUuid();
			helpASO.add(helpVo);
		}else{
			HelpVO helpVo=new HelpVO(idSection,role,videoExtension);
			this.uuid=helpVo.getUuid();
			helpASO.add(helpVo);
				
		}
		
	}
	
	public Object onActionFromHelp(String uuid){
		if(!video){
			textPage.setUuid(uuid);
			
			return textPage;
		}else{
			videoPage.setUuid(uuid);
			
			return videoPage;
		}
	}

	
	
	 
}
