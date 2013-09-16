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
package org.linagora.linshare.view.tapestry.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.batches.DocumentManagementBatch;
import org.linagora.linshare.core.batches.ShareManagementBatch;
import org.linagora.linshare.core.batches.UserManagementBatch;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.impl.UserAndDomainMultiServiceImpl;
import org.linagora.linshare.view.tapestry.components.PasswordPopup;
import org.linagora.linshare.view.tapestry.components.WindowWithEffects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.unbound.tapestry.tagselect.LabelAwareValueEncoder;

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
    
 
	@Component(parameters = {"style=dialog", "show=false","width=360", "height=335"})
	private WindowWithEffects dialog;
	
	@Component(parameters = {"style=alert", "show=false","width=360", "height=335"})
	private WindowWithEffects alert;
	
	@Component(parameters = {"style=alert_lite", "show=false","width=360", "height=335"})
	private WindowWithEffects alert_lite;
	
	@Component(parameters = {"style=alphacube", "show=false","width=360", "height=335"})
	private WindowWithEffects alphacube;

	@Component(parameters = {"style=mac_os_x", "show=false","width=360", "height=335"})
	private WindowWithEffects mac_os_x;
	
	@Component(parameters = {"style=blur_os_x", "show=false","width=360", "height=335"})
	private WindowWithEffects blur_os_x;
	
	@Component(parameters = {"style=mac_os_x_dialog", "show=false","width=360", "height=335"})
	private WindowWithEffects mac_os_x_dialog;
	
	@Component(parameters = {"style=nuncio", "show=false","width=360", "height=335"})
	private WindowWithEffects nuncio;
	
	@Component(parameters = {"style=spread", "show=false","width=360", "height=335"})
	private WindowWithEffects spread;
	
	@Component(parameters = {"style=darkX", "show=false","width=360", "height=335"})
	private WindowWithEffects darkX;
	
	@Component(parameters = {"style=greenlighting", "show=false","width=360", "height=335"})
	private WindowWithEffects greenlighting;
	
	@Component(parameters = {"style=bluelighting", "show=false","width=360", "height=335"})
	private WindowWithEffects bluelighting;
	
	@Component(parameters = {"style=greylighting", "show=false","width=360", "height=335"})
	private WindowWithEffects greylighting;
	
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
	private ShareManagementBatch shareManagementBatch;
	
	@Inject
	private AnonymousUrlRepository anonymousUrlRepository;
	
	@Inject
	private DocumentEntryRepository documentEntryRepository;
	
	@Inject
	private UserManagementBatch userManagementBatch;
	
	@Inject
	private DocumentManagementBatch documentManagementBatch;
	
	
	
	void onActionFromTest1()
    {
		logger.debug("begin method onActionFromTest1");
		shareManagementBatch.cleanOutdatedShares();
		documentManagementBatch.checkDocumentsMimeType();
		logger.debug("endmethod onActionFromTest1");
    }
	
	
	void onActionFromTest2()
    {
		logger.debug("begin method onActionFromTest2");
//		shareManagementBatch.notifyUpcomingOutdatedShares();
		
		userManagementBatch.cleanExpiredGuestAccounts();
		
//		List<DocumentEntry> findAllExpiredEntries = documentEntryRepository.findAllExpiredEntries();
//		logger.debug("findAllExpiredEntries size : " + findAllExpiredEntries.size());
//		for (DocumentEntry documentEntry : findAllExpiredEntries) {
//			logger.debug("documentEntry found : " + documentEntry.getId() + ':' + documentEntry.getUuid());
//		}
//		logger.debug("endmethod onActionFromTest2");
    }
	
	
	/**
	 * Testing tapestry-tagselect
	 */
	@SessionState
	private UserVo userVo;

	@Persist
    @Property
    private List<String> tags;

	@Inject
	private UserFacade userFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

    public void onPrepare() {
        if (this.tags == null) {
            this.tags = new ArrayList<String>();
        }
    }
    
    SelectModel onProvideCompletionsFromTags(final String input) {
        final List<String> result = new ArrayList<String>(); 
		List<UserVo> searchResults = performSearch(input);

		for (UserVo user : searchResults) {
			String completeName = MailCompletionService.formatLabel(user);
			if (!result.contains(completeName)) {
				result.add(completeName);
			}
		}
		if (result.isEmpty())
			result.add(input);
        return new StringSelectModel(result);
    }

    private static class StringSelectModel implements SelectModel {
        private final List<String> strings;

        public StringSelectModel(final List<String> strings) {
            this.strings = strings;
        }

        @Override
        public List<OptionModel> getOptions() {
            final List<OptionModel> options = new ArrayList<OptionModel>();

            for (final String string : this.strings) {
                options.add(new OptionModelImpl(string));
            }
            return options;
        }

        @Override
        public List<OptionGroupModel> getOptionGroups() {
            return null;
        }

        @Override
        public void visit(final SelectModelVisitor visitor) {
        }
    }
	
	/**
	 * Perform a user search using the user search pattern.
	 * 
	 * @param input
	 *            user search pattern.
	 * @return list of users.
	 */
	private List<UserVo> performSearch(String input) {
		Set<UserVo> userSet = new HashSet<UserVo>();
		List<UserVo> res = new ArrayList<UserVo>();

		String firstName_ = null;
		String lastName_ = null;
		String input_ = input != null ? input.trim() : null;

		if (input_.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}
		try {
			userSet.addAll(userFacade.searchUser(input_, null, null, userVo));
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_,
					userVo));
			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,
					userVo));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(
					input.trim(), userVo));
			res.addAll(recipientFavouriteFacade.recipientsOrderedByWeightDesc(
					new ArrayList<UserVo>(userSet), userVo));
		} catch (BusinessException e) {
			logger.error("Error while searching user in QuickSharePopup", e);
		}
		return res;
	}
}
