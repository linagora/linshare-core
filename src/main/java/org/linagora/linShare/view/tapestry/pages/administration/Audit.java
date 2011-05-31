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
package org.linagora.linShare.view.tapestry.pages.administration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.util.EnumSelectModel;
import org.apache.tapestry5.util.EnumValueEncoder;
import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.Facade.LogEntryFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.core.domain.vo.DisplayableLogEntryVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.beans.LogCriteriaBean;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.enums.CriterionMatchMode;
import org.linagora.linShare.view.tapestry.streams.CsvStreamResponse;
import org.slf4j.Logger;




public class Audit {
	@Inject 
	private Logger logger;

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @SessionState
    private UserVo userLoggedIn;
	
	/* ***********************************************************
	 *                Injected services 
	 ************************************************************ */  

	@Inject
	private Messages messages;

	@Inject
	private LogEntryFacade logEntryFacade;
	
	
	@Inject
	private UserFacade userFacade;
	
	@Environmental
	private FormSupport formSupport;
	
	@SuppressWarnings("unused")
	@InjectComponent
	private TextArea actorMails;

	@SuppressWarnings("unused")
	@InjectComponent
	private TextArea targetMails;
	
	@Component
	private Form formReport;

    @InjectPage
    private org.linagora.linShare.view.tapestry.pages.history.Index historyPage;
	/* ***********************************************************
	 *                         Properties
	 ************************************************************ */    

	@Property
	private String actorListMails;

	@Property
	private String targetListMails;


	@Property(write=false) @SuppressWarnings("unused") //used in tml
	private ValueEncoder<LogAction> logActionEncoder;

	@Property(write=false)  @SuppressWarnings("unused") //used in tml
	private SelectModel logActionModel;
	
	/**
	 * the list of traces matching the request
	 */
	@Persist @Property
	private List<DisplayableLogEntryVo> logEntries;
	
	@SuppressWarnings("unused")
	@Property //used in the tml for the grid
	private DisplayableLogEntryVo logEntry;
	
	
	@Persist @Property(write=false) @SuppressWarnings("unused") //used in tml
	private boolean displayGrid;
	
	private boolean generateCsv;
	private boolean reset;
	
	@Property
	@Persist
	private LogCriteriaBean criteria;
	
	@Persist
	@Property
	private List<String> domains;
	
	@Inject
	private DomainFacade domainFacade;
	
	@Property
	@Persist
	private boolean superadmin;
	
	
	/* ***********************************************************
	 *                       Phase processing
	 ************************************************************ */
	
	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */	
	
	@SetupRender
	public void init() throws BusinessException {
		domains = domainFacade.getAllDomainIdentifiers();
		superadmin = userLoggedIn.isSuperAdmin();
	}
	
	public void onActivate() {
		logActionEncoder = new EnumValueEncoder<LogAction>(LogAction.class);
		logActionModel = new EnumSelectModel(LogAction.class, messages);
		if(null == criteria) {
			criteria = new LogCriteriaBean();
		} else {
			if ((criteria.getActorMails()!=null) && (criteria.getActorMails().size()>0)) {
				actorListMails = "";
				for (String mail : criteria.getActorMails()) {
					actorListMails += mail + ",";
				}	
			}
			if ((criteria.getTargetMails()!=null) && (criteria.getTargetMails().size()>0)) {
				targetListMails = "";
				for (String mail : criteria.getTargetMails()) {
					targetListMails += mail + ",";
				}
				
			}
		}
	}
	
	public void onValidateFormFromFormReport() {
		if(null!=criteria.getBeforeDate() 
				&& null !=criteria.getAfterDate() 
				&& criteria.getAfterDate().before(criteria.getBeforeDate())){
			formReport.recordError(messages.get("pages.administration.audit.error.invalidDate"));
		}
	}
	
	public Object onSuccessFromFormReport()  {
		
		if (reset){
			criteria = new LogCriteriaBean();
			logEntries = null;
			return null;
		}
		
		if ((actorListMails != null) &&(actorListMails.length()>0)) {
			criteria.setActorMails(Arrays.asList(actorListMails.split(",")));
		}
		if ((targetListMails != null) &&(targetListMails.length()>0)) {
			criteria.setTargetMails(Arrays.asList(targetListMails.split(",")));
		}

		logEntries = logEntryFacade.findByCriteria(criteria, userLoggedIn);
		displayGrid = true;

		if (generateCsv) {
			return  new CsvStreamResponse(generateCsvData(logEntries).toString(), "applicationHistory.csv");
		} else {	
			// we stay on the same page, so we don't return anything
			return null;
		}
	}
	
	
	Object onActionFromGenerateCsv() { generateCsv = true; return onSuccessFromFormReport();}
	Object onActionFromReset() { reset = true; return onSuccessFromFormReport();}
	
	
	/**
	 * AutoCompletion for name field.
	 * @param value the value entered by the user
	 * @return list the list of string matched by value.
	 */
	public List<String> onProvideCompletionsFromActorMails(String value){
		List<String> res = new ArrayList<String>();
		try {
			List<UserVo> founds = userFacade.searchUser(value, null, null, userLoggedIn, userLoggedIn.isSuperAdmin());

			if (founds != null && founds.size() > 0) {
				for (UserVo userVo : founds) {
					res.add(userVo.getMail());
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * AutoCompletion for name field.
	 * @param value the value entered by the user
	 * @return list the list of string matched by value.
	 */
	public List<String> onProvideCompletionsFromTargetMails(String value){
		return onProvideCompletionsFromActorMails(value);
	}

	/* ***********************************************************
	 *                          Helpers
	 ************************************************************ */
	

	public Date getBeforeDate() {
		return null == criteria.getBeforeDate() ? null : criteria.getBeforeDate().getTime();
	}
	
	public void setBeforeDate(final Date d) {
		Calendar c = null;
		if(null!= d){
			c = new GregorianCalendar();
			c.setTime(d);
			//Initialize to the starting day at 00h
			c.set(GregorianCalendar.HOUR, 0);
			c.set(GregorianCalendar.MINUTE, 0);
			c.set(GregorianCalendar.SECOND, 0);
		} 
		criteria.setBeforeDate(c);
	}
	
	public Date getAfterDate() {
		return null == criteria.getAfterDate() ? null : criteria.getAfterDate().getTime();
	}
	
	public void setAfterDate(final Date d) {
		Calendar c = null;
		if(null!= d){
			c = new GregorianCalendar();
			c.setTime(d);
			//Initialize to the next day at 00h
			c.set(GregorianCalendar.HOUR, 23);
			c.set(GregorianCalendar.MINUTE, 59);
			c.set(GregorianCalendar.SECOND, 59);
		}
		criteria.setAfterDate(c);
	}

	protected StringBuilder generateCsvData(List<DisplayableLogEntryVo> listLogEntry) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		StringBuilder csv = new StringBuilder();
	
		
		csv
			.append(messages.get("pages.administration.audit.actionDate")).append(",")
			.append(messages.get("pages.administration.audit.actorMail")).append(",")
			.append(messages.get("pages.administration.audit.actorFirstname")).append(",")
			.append(messages.get("pages.administration.audit.actorLastname")).append(",")
			.append(messages.get("pages.administration.audit.action")).append(",")
			.append(messages.get("pages.administration.audit.targetMail")).append(",")
			.append(messages.get("pages.administration.audit.targetFirstname")).append(",")
			.append(messages.get("pages.administration.audit.targetLastname")).append(",")
			.append(messages.get("pages.administration.audit.fileName")).append(",")
			.append(messages.get("pages.administration.audit.fileSize")).append(",")
			.append(messages.get("pages.administration.audit.fileType")).append("\n");
		for (DisplayableLogEntryVo logEntry : listLogEntry) {
			csv
			.append(dateFormat.format(logEntry.getActionDate())).append(",")
			.append(logEntry.getActorMail()).append(",")
			.append(logEntry.getActorFirstname()).append(",")
			.append(logEntry.getActorLastname()).append(",")
			.append(logEntry.getLogAction()).append(",")
			.append(logEntry.getTargetMail()).append(",")
			.append(logEntry.getTargetFirstname()).append(",")
			.append(logEntry.getTargetLastname()).append(",")
			.append(logEntry.getFileName()).append(",")
			.append(logEntry.getFileSize()).append(",")
			.append(logEntry.getFileType()).append("\n");
		}
		return csv;
	}

	public String getFilesize() {
		if (logEntry.getFileSize()==null)
			return "";
		return FileUtils.getFriendlySize(logEntry.getFileSize(), messages);
	}
	
	public String getActionDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(logEntry.getActionDate().getTime());
	}

    public Object onActionFromPersonalHistory() {
        return historyPage;
    }
    
	public CriterionMatchMode getFileNameMatchModeStart() { return CriterionMatchMode.START; }
	public CriterionMatchMode getFileNameMatchModeAnywhere() { return CriterionMatchMode.ANYWHERE; }

    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
}
