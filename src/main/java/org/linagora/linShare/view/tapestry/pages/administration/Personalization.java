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

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.MailSubject;
import org.linagora.linShare.core.domain.entities.MailTemplate;
import org.linagora.linShare.core.domain.entities.WelcomeText;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Personalization {

    private static final int FACTORMULTI = 1024;
    
	private static Logger logger = LoggerFactory.getLogger(Personalization.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
	private Messages messages;
    @Inject
    private ParameterFacade parameterFacade;
    @Inject
    private UserFacade userFacade;
    
    @SessionState
    private UserVo loginUser;
    
	// The form that holds the admin params
	@InjectComponent
	private Form personalizationForm;
    
    

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @Property
    private String customLogoUrl;

    @Property
    @Persist
    private Set<WelcomeText> welcomeTexts;
    
    @Property
    @Persist
    private Set<MailTemplate> mailTemplates;
    
    @Property
    @Persist
    private Set<MailSubject> mailSubjects;
    

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() throws BusinessException {
        ParameterVo p = parameterFacade.loadConfig();

        customLogoUrl = p.getCustomLogoUrl();


        if (welcomeTexts == null) {
            welcomeTexts = p.getWelcomeTexts();
        }
        
        if (mailTemplates == null) {
        	mailTemplates = p.getMailTemplates();
        }
        
        if (mailSubjects == null) {
        	mailSubjects = p.getMailSubjects();
        }
    }   
    
    
    public void onSuccessFromPersonalizationForm() throws BusinessException {
        ParameterVo p = parameterFacade.loadConfig();

        ParameterVo params = new ParameterVo(p.getFileSizeMax(), p.getUserAvailableSize(), p.getActiveMimeType(), p.getActiveSignature(),p.getActiveEncipherment(),p.getActiveDocTimeStamp(),p.getGuestAccountExpiryTime(),
            p.getGuestAccountExpiryUnit(), customLogoUrl,p.getDefaultShareExpiryUnit(),  p.getDefaultShareExpiryTime(), p.getShareExpiryRules(), p.getDeleteDocWithShareExpiryTime(),welcomeTexts, mailTemplates, mailSubjects);
        parameterFacade.createConfig(params);
        
    }

    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
    
}
