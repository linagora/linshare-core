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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.constants.DocumentType;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.SearchDocumentFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.enums.SharedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchFile {
	private static final Logger logger = LoggerFactory.getLogger(SearchFile.class);


	/* ***********************************************************
	 *                         Parameters
	 ************************************************************ */


	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	private UserVo userlogin;

	/**
	 * flag to always filter on shared file
	 * desactive also search criteria on sharing state
	 */
	@Property
	@Parameter(required=false,defaultPrefix=BindingConstants.PROP)
	private boolean forceFilterOnSharedFile;


	/**
	 * if present, remote control (with an external link) is activated
	 * to show basic or advanced search window. the wanted state is given through this property.
	 * if not present, the component display its own switch to show basic or advanced search window
	 */
	@Property
	@Parameter(required=false,defaultPrefix=BindingConstants.PROP)
	private Boolean advancedmode;


	@Persist
	private UserVo userTemp;

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */

	@Inject
	private UserAutoCompleteFacade userAutoCompleteFacade;

	@Inject
	private SearchDocumentFacade searchDocumentFacade;

	@Inject
	private UserFacade userFacade;

    @Inject
    private ShareFacade shareFacade;

	@Inject
	private Messages messages;

	@Environmental
	private JavaScriptSupport renderSupport;

	/* ***********************************************************
	 *                Properties & injected symbol, ASO, etc
	 ************************************************************ */

	/*	@Persist*/
	@Persist
	@Property
	private String name;

	@Persist
	@Property
	private String sizeMin;

	@Persist
	@Property
	private String sizeMax;

	@Persist
	@Property
	private Date beginDate;

	@Persist
	@Property
	private Date endDate;

	@Persist
	@Property
	private String mimetype;

	@Persist
	@Property
	private SharedType shared;

	//fileNamePattern is used with advanced search
	@Persist
	@Property
	private String fileNamePattern;

	@Persist
	@Property
	private String extension;

	@Persist
	@Property
	private String sharedFrom;


	private Calendar calBegin;

	private Calendar calEnd;

	@Persist
	private boolean reset;

	@Persist
	private boolean resetSimple;


	@Persist
	@Property
	private String login;

	@InjectComponent
	private Form advancedSearchForm;

	@Inject
	private ComponentResources componentResources;

	@Property
	@Persist
	private boolean advancedSearch;

	/** type of document
	 * true -> my own document
	 * false -> the document i've been sent
	 */
	@Persist
	@Property
	private Boolean documentType;


	@Property
	private int autocompleteMin;

	@Inject
	private FunctionalityFacade functionalityFacade;


	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */


	@SetupRender
	void setupRender() {

		autocompleteMin = functionalityFacade.completionThreshold(userlogin.getDomainIdentifier());

		if(isRemoteToggleAdvancedSearch()){
			advancedSearch = advancedmode;
		}

		if (shared==null) shared = SharedType.ALL_FILES;
		if (documentType==null) { documentType = true; }
		userTemp=userlogin;

		if(advancedSearch) {
			//get the name (already entered) inside advanced search
			if (name!=null) fileNamePattern = name;
			name = null;
		}

	}


	@AfterRender
	public void afterRender() {
		if(advancedSearch){
			/**
			 * Script for init values in the size field.
			 */
			if(sizeMin==null){
				renderSupport.addScript("document.forms['advancedSearchForm'].sizeMin.value='"+messages.get("components.searchfile.sizeMin")+"'");	
			}
			if(sizeMax==null){
				renderSupport.addScript("document.forms['advancedSearchForm'].sizeMax.value='"+messages.get("components.searchfile.sizeMax")+"'");	
			}
		}
	}


	/**
	 * Prior to adding the files, we need to clear the array, otherwise we might add them twice
	 */
	public void onPrepare() {


	}

	/**
	 * Action triggered when the user click on advanced search button.
	 * We use a boolean which is a switch.
	 */
	public void onActionFromToggleSearch(){

		advancedSearch=!advancedSearch;
	}


	/**
	 * here we add the file uploaded in the array. It is a good place to check it
	 * @param aFile
	 * @throws BusinessException 
	 */
	public void onValidateFormFromAdvancedSearchForm()  {


		if(beginDate!=null && endDate!=null){
			if(beginDate.after(endDate)){
				advancedSearchForm.recordError(messages.get("components.searchfile.errors.beginDateAfterEndDate"));
			}


		}
		/**
		 * We search from one second yesterday 23h59:59s chosen to the tomorrow day at 00h.
		 * In this way the search engine doesn't forget any file.
		 */
		if(null!=beginDate){
			calBegin=GregorianCalendar.getInstance();
			calBegin.setTime(beginDate);
			calBegin.add(Calendar.SECOND, -1);

		}
		if(null!=endDate){
			calEnd=GregorianCalendar.getInstance();
			calEnd.setTime(endDate);
			calEnd.add(Calendar.DAY_OF_MONTH, 1);

		}

		sizeMin=(messages.get("components.searchfile.sizeMin").equals(sizeMin))?null:sizeMin;
		sizeMax=(messages.get("components.searchfile.sizeMax").equals(sizeMax))?null:sizeMax;
		if(sizeMin!=null && sizeMax!=null){
			try{


				if(Long.parseLong(sizeMin)>Long.parseLong(sizeMax)){
					advancedSearchForm.recordError(messages.get("components.searchfile.errors.size.badrange"));
				}
			}catch(NumberFormatException e){
				advancedSearchForm.recordError(messages.get("components.searchfile.errors.size.notNumeric"));
				logger.debug(e.toString());
			}
		}else{
			try{
				Long size=(sizeMin!=null)?Long.parseLong(sizeMin):null;
				size=(sizeMax!=null)?Long.parseLong(sizeMax):null;

			}catch(NumberFormatException e){
				advancedSearchForm.recordError(messages.get("components.searchfile.errors.size.notNumeric"));
				logger.debug(e.toString());
			}
		}
	}


	/**
	 * AutoCompletion for name field.
	 * @param value the value entered by the user
	 * @return list the list of string matched by value.
	 * @throws BusinessException 
	 */
	public List<String> onProvideCompletionsFromFileNamePattern(String value) throws BusinessException {
		return onProvideCompletionsFromName(value);
	}
	public List<String> onProvideCompletionsFromName(String value) throws BusinessException{

		if(forceFilterOnSharedFile) {
			shared = SharedType.SHARED_ONLY;
		}

		SearchDocumentCriterion searchDocumentCriterion=new SearchDocumentCriterion(userlogin,value,null,null,null,isShared(this.shared),null,null,null,null, DocumentType.BOTH);

		if (!forceFilterOnSharedFile) {
			List<DocumentVo> documents=searchDocumentFacade.retrieveDocumentContainsCriterion(userlogin, searchDocumentCriterion);

			if(documents.size()>0){
				ArrayList<String> names=new ArrayList<String>();
				for(DocumentVo documentVo:documents){
					names.add(documentVo.getFileName());
				}
				return names;

			}else{
				return null;
			}

		} else {
			List<ShareDocumentVo> sharings=shareFacade.getAllSharingReceivedByUser(userlogin);

			if(sharings.size()>0){
				ArrayList<String> names=new ArrayList<String>();
				for(ShareDocumentVo documentVo:sharings){
					if(documentVo.getFileName().contains(value)) {
						names.add(documentVo.getFileName());
					}
				}
				return names;

			}else{
				return null;
			}
		}
	}


	/**
	 * provide completion for users email
	 * @param input
	 * @return
	 */
	public List<String> onProvideCompletionsFromSharedFrom(String input) {
		try {
			return userAutoCompleteFacade.autoCompleteMail(userlogin, input);
		} catch (BusinessException e) {
			logger.error("Failed to autocomplete user on ConfirmSharePopup", e);
		}
		return new ArrayList<String>();
	}


	public Object onSuccessFromAdvancedSearchForm(){


		if (reset){
			this.fileNamePattern = null;
			this.sizeMin = null;
			this.sizeMax = null;
			this.mimetype = null;
			this.shared = null;
			this.beginDate = null;
			this.endDate = null;
			this.calBegin= null; 
			this.calEnd= null;
			this.extension = null;
			this.sharedFrom = null;
			this.name = null;
			this.reset = false;

			componentResources.triggerEvent("resetListFiles", null, null);

			return null;
		}


		Boolean sharedboo = null;
		Long sizeMinInByte = null;
		Long sizeMaxInByte = null; 


		if (forceFilterOnSharedFile)
		sharedboo = true;
		else		
		sharedboo=isShared(this.shared);

		if (sizeMin!=null)
			sizeMinInByte = Long.parseLong(sizeMin)*1024;
		if (sizeMax!=null)
			sizeMaxInByte = Long.parseLong(sizeMax)*1024;


		SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(userTemp,fileNamePattern, sizeMinInByte,sizeMaxInByte, mimetype, sharedboo, calBegin, calEnd, extension,sharedFrom,DocumentType.BOTH);

		/**
		 * call a different facade given the type of search ?
		 */
		List<DocumentVo> docs;

		if(forceFilterOnSharedFile){
			List<ShareDocumentVo> sharedocs = searchDocumentFacade.retrieveShareDocumentContainsCriterion(userlogin, searchDocumentCriterion);	
			docs = new ArrayList<DocumentVo>(sharedocs);
		} else {
			docs = searchDocumentFacade.retrieveDocumentContainsCriterion(userlogin, searchDocumentCriterion);
		}		

		Object[] objects=new Object[1];
		objects[0]=docs;
		componentResources.getPage().getComponentResources().triggerEvent("eventDocument", objects,null);

		componentResources.triggerEvent("inFileSearch", null, null);
		return null;
	}

	@OnEvent(value="simpleSearch")
	public void initSimpleSubmit(){

	}

	public Object onSuccessFromSimpleSearchForm(){

		if (resetSimple) {
			this.name = null;
			this.resetSimple = false;
			componentResources.triggerEvent("resetListFiles", null, null);
			return null;
		}


		Boolean shared = null;
		if (forceFilterOnSharedFile) shared = forceFilterOnSharedFile;

		SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(userTemp,name, null,null, null, shared, null, null, null, null, null);

		/**
		 * call a different facade given the type of search ?
		 */
		List<DocumentVo> docs;

		if(forceFilterOnSharedFile){
			List<ShareDocumentVo> shareddocs = searchDocumentFacade.retrieveShareDocumentContainsCriterion(userlogin, searchDocumentCriterion);
			docs =  new ArrayList<DocumentVo>(shareddocs);
		} else {
			docs = searchDocumentFacade.retrieveDocumentContainsCriterion(userlogin, searchDocumentCriterion);
		}

		Object[] objects=new Object[1];
		objects[0]=docs;
		componentResources.getPage().getComponentResources().triggerEvent("eventDocument", objects,null);

		componentResources.triggerEvent("inFileSearch", null, null);
		return null;
	}

	void onSelectedFromReset() { 
		reset = true; 
	}

	void onSelectedFromResetSimple() {
		resetSimple = true;
	}




	/* ***********************************************************
	 *                   Helpers
	 ************************************************************ */



	public SharedType getSharedAll(){
		return SharedType.ALL_FILES; 
	}

	public SharedType getSharedOnly(){
		return SharedType.SHARED_ONLY; 
	}

	public SharedType getSharedNone(){
		return SharedType.SHARED_NONE; 
	}

	public Boolean getSharedDocumentType() {
		return false;
	}

	public Boolean getUserDocumentType() {
		return true;
	}



	private Boolean isShared(SharedType shared){
		if(SharedType.ALL_FILES.equals(shared))
			return null;
		else if (SharedType.SHARED_NONE.equals(shared))
			return false;
		else if (SharedType.SHARED_ONLY.equals(shared))	
			return true;
		else{
			return null;
		}
	}

	/**
	 * check if advancedmode is present in the parameter of the component
	 * if present, remote control (with an external link) is activated to show basic or advanced search window
	 * if not present, the component display its own switch to show basic or advanced search window
	 * @return true if advancedmode is given
	 */
	public boolean isRemoteToggleAdvancedSearch(){
		return componentResources.isBound("advancedmode");
	}
}
