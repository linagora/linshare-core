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
package org.linagora.linshare.view.tapestry.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.batches.ShareManagementBatch;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.impl.UserAndDomainMultiServiceImpl;
import org.linagora.linshare.view.tapestry.components.PasswordPopup;
import org.linagora.linshare.view.tapestry.components.WindowWithEffects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test showing how to use the PasswordPopup
 * @author ncharles
 *
 */
public class TestPopup {

	private static final Logger logger = LoggerFactory.getLogger(UserAndDomainMultiServiceImpl.class);
	
	@Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
	
	@Inject
	private Messages messages;
	
	@Component
	private PasswordPopup passwordPopup; 

    
    private final String intendedPassword = "bob";
    
    @Inject
    private  UserService userService;
    
 
	@SuppressWarnings("unused")
	@Component(parameters = {"style=dialog", "show=false","width=360", "height=335"})
	private WindowWithEffects dialog;
	
	@SuppressWarnings("unused")
	@Component(parameters = {"style=alert", "show=false","width=360", "height=335"})
	private WindowWithEffects alert;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=alert_lite", "show=false","width=360", "height=335"})
	private WindowWithEffects alert_lite;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=alphacube", "show=false","width=360", "height=335"})
	private WindowWithEffects alphacube;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=mac_os_x", "show=false","width=360", "height=335"})
	private WindowWithEffects mac_os_x;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=blur_os_x", "show=false","width=360", "height=335"})
	private WindowWithEffects blur_os_x;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=mac_os_x_dialog", "show=false","width=360", "height=335"})
	private WindowWithEffects mac_os_x_dialog;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=nuncio", "show=false","width=360", "height=335"})
	private WindowWithEffects nuncio;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=spread", "show=false","width=360", "height=335"})
	private WindowWithEffects spread;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=darkX", "show=false","width=360", "height=335"})
	private WindowWithEffects darkX;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=greenlighting", "show=false","width=360", "height=335"})
	private WindowWithEffects greenlighting;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=bluelighting", "show=false","width=360", "height=335"})
	private WindowWithEffects bluelighting;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=greylighting", "show=false","width=360", "height=335"})
	private WindowWithEffects greylighting;
	@SuppressWarnings("unused")
	@Component(parameters = {"style=darkbluelighting", "show=false","width=360", "height=335"})
	private WindowWithEffects darkbluelighting;
	
	Zone onValidateFormFromPasswordPopup()
	{

		if (intendedPassword.equals(passwordPopup.getPassword())) {
			passwordPopup.getFormPassword().clearErrors();
			return passwordPopup.formSuccess();
		} else {
			passwordPopup.getFormPassword().recordError(messages.get("testpopup.errormessage"));
			return passwordPopup.formFail();
		}	
		
	} 


	@Inject
	private ShareManagementBatch repo;
	
	void onActionFromTest1()
    {
		logger.debug("methode fred");
		repo.cleanOutdatedShares(); 
		
    }
	
	
	
}
