/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.services.ContextResource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.Context;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.vo.HelpVO;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.enums.HelpType;
import org.linagora.linshare.view.tapestry.objects.HelpsASO;
import org.linagora.linshare.view.tapestry.objects.Subsection;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This component permits to display all help categories.
 * It supports two type of help -> Text or Video. 
 * It is based on conventions for internationalization.
 * 
 * 
 * @author ngapaillard
 *
 */

@Import(library = {"Help.js"})
public class Help {

	private static Logger logger = LoggerFactory.getLogger(Help.class);

	@SessionState
	private HelpsASO helpsASO;
	

	
	private final static String LOCALIZATION_PREFIX="manual";
	private final static String HELP_PREFIX="help";
	private final static String SUBSECTION_PREFIX="subsection";
	private final static String TITLE_PREFIX="title";
	private final static String IMAGE_DIR="images/screenshots/";
	private final static String VIDEO_DIR="videos/";
	private final static String IMAGE_TITLE="image.title";
	private final static String CLASS_CONTENT_HOVER="content visible";
	private final static String CLASS_CONTENT="content";
	private final static String CLASS_NAVIGATION_HOVER="current";
	private final static String CLASS_NAVIGATION="";
	




	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */	



	@Inject
	private ComponentResources componentResources;

	@Inject
	private AssetSource assetSource;


	@Inject
	private PersistentLocale locale;

	@Inject
	private Context context;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	/* ***********************************************************
	 *                Properties & injected symbol, ASO, etc
	 ************************************************************ */
	@Persist(value="session")
	private String uuid;
	
	

	private HelpType helpType;
	
	@Persist(value="session")
	private boolean video;
	
	@Property
	private boolean helpsASOExists;

	@SuppressWarnings("unused")
	@Property
	private String helpTitle;
	
	@Property
	private List<Subsection> subsections;

	@Property
	private List<String> descriptions;

	@Property
	private Subsection subsection;

	@SuppressWarnings("unused")
	@Property
	private String description;

	@Persist
	private Integer descLength;

	@SuppressWarnings("unused")
	private String videoUrl;
	
	private String videoFile;
	
	@SetupRender
	public void initProperties(){
		if(helpsASOExists){
			if(video){
				helpType=HelpType.VIDEO;
			}else{
				helpType=HelpType.TEXT;
			}
			HelpVO help=helpsASO.getHelpVO(uuid);
			Messages messagesComponent=componentResources.getMessages();
			this.helpTitle=messagesComponent.get(HELP_PREFIX+"."+LOCALIZATION_PREFIX+"."+help.getRole()+"."+help.getIdSection()+"."+TITLE_PREFIX);
			
			if(!video){
			
				initText(help,messagesComponent);
			

			}else{
				initVideo(help,messagesComponent);
			}

		}else{
			businessMessagesManagementService.notify(new BusinessException(BusinessErrorCode.WRONG_URL,"Wrong url from the user"));
		}
		
	}
	
	
	/**
	 * check if the help is with video format.
	 * @return true if the help is in video.
	 */
	public boolean isVideoMode(){
		return video;
	}
	
	/**
	 * Check if the image exists in the webapp directory.
	 * @return true if the image specified exists.
	 */
	public boolean isImage(){
		return(null!=subsection && null!=subsection.getImage());
	}

	/**
	 * Check if the video exists in the webapp directory.
	 * @return true if the image specified exists.
	 */
	public boolean isVideo(){
		return(videoUrl!=null);
	}
	
	/**
	 * Give the number of descriptions contained inside the current subsection.
	 * @return descLength the number of descriptions inside the current subsection.
	 */
	public Integer getSubsectionLength(){
		return this.descLength;
	}

	
	
	/**
	 * give the class name adapted to the element in the subsection.
	 * 
	 * @return className the class name adapted to the element in the subsection.
	 */
	public String getClassName(){
		if(isCurrent()){
			return CLASS_CONTENT_HOVER;
		}else{
			return CLASS_CONTENT;
		}
	}

	/**
	 * Give the css class name for the content when it activated.
	 * @return className the css class name for the content when it activated.
	 */
	public String getClassContentHover(){
		return CLASS_CONTENT_HOVER;
	}
	
	/**
	 * Give the css class name for the content when it wasn't activated.
	 * @return className the css class name for the content when it wasn't activated.
	 */
	public String getClassContent(){
		return CLASS_CONTENT;
	}
	

	/**
	 * Give the css class name for the navigation when it activated.
	 * @return className the css class name for the navigation when it activated.
	 */
	public String getClassNavigationHover(){
		return CLASS_NAVIGATION_HOVER;
	}
	
	/**
	 * Give the css class name for the navigation when it wasn't activated.
	 * @return className the css class name for the navigation when it wasn't activated.
	 */
	public String getClassNavigation(){
		return CLASS_NAVIGATION;
	}
	
	
	/**
	 * tell if the subsection is the first subsection.
	 * @return true if the subsection is the first subsection.
	 */
	public boolean isCurrent(){
		
		return this.subsection.getIndex()==1;

	}

	/**
	 * give the id of the section.
	 * @return idSection the id of the section.
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * set the id of the section from the page.
	 * @param idSection the id of the section.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * set the id of the section from the page.
	 * @param idSection the id of the section.
	 */
	public void setVideo(boolean video) {
		this.video = video;
	}
	/**
	 * tell if the list of subsection is empty.
	 */
	public boolean isEmpty(){
		return this.subsections.isEmpty();
	}
	
	
	public String getVideoUrl() {
		
		return (videoUrl!=null)?videoUrl:videoFile;
	}


	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}


	private void initText(HelpVO help,Messages messagesComponent){
		
		String subsection=HELP_PREFIX+"."+LOCALIZATION_PREFIX+"."+help.getRole()+"."+help.getIdSection()+"."+SUBSECTION_PREFIX; 
		String subsectionImg=LOCALIZATION_PREFIX+"_"+help.getRole()+"_"+help.getIdSection()+"_"+SUBSECTION_PREFIX; 
		
		
		this.subsections=new ArrayList<Subsection>();
		this.descriptions=new ArrayList<String>();
		String[] desc;
		if(null!=help.getDescItems()){
			desc=help.getDescItems().split(",");
		}else{
			desc=new String[0];
		}

		this.descLength=desc.length;

		for(int i=1;i<=desc.length;i++){

			for(int j=1;j<=Integer.parseInt(desc[i-1]);j++){
				descriptions.add(messagesComponent.get(subsection+i+"."+"desc"+j));
			}
			Asset imgAsset;
			try{
				imgAsset=assetSource.getAsset(new ContextResource(context,IMAGE_DIR), subsectionImg+i+"."+help.getExtension(),locale.get());
			}catch(RuntimeException exception){
				/*
				 * We do nothing, just set the asset to null, for checking if the asset exist or not.
				 */
				imgAsset=null;
				logger.debug(exception.toString());
			}
			String subsectionTitle=messagesComponent.get(subsection+i+"."+TITLE_PREFIX);
			String imgTitle=(imgAsset!=null) ? messagesComponent.get(subsection+i+"."+IMAGE_TITLE):null;

			subsections.add(new Subsection(subsectionTitle,descriptions,imgAsset,i,imgTitle));
			descriptions=new ArrayList<String>();
		}
	}
	
	private void initVideo(HelpVO help,Messages messagesComponent){
		this.videoFile=LOCALIZATION_PREFIX+"_"+help.getRole()+"_"+help.getIdSection()+"."+help.getExtension(); 
		Asset videoAsset;
		try{
			videoAsset=assetSource.getAsset(new ContextResource(context,VIDEO_DIR), videoFile,locale.get());
		}catch(RuntimeException exception){
			/*
			 * We do nothing, just set the asset to null, for checking if the asset exist or not.
			 */
			videoAsset=null;
			logger.debug(exception.toString());
		}
		if(videoAsset==null){
			videoUrl=null;
		}else{
			videoUrl=videoAsset.toClientURL();
		}
	}


	
	
}
