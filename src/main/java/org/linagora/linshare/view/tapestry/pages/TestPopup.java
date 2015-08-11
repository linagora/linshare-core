/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.linagora.linshare.core.batches.DocumentManagementBatch;
import org.linagora.linshare.core.batches.ShareManagementBatch;
import org.linagora.linshare.core.batches.UploadRequestBatch;
import org.linagora.linshare.core.batches.generics.GenericBatch;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.view.tapestry.components.PasswordPopup;
import org.linagora.linshare.view.tapestry.components.WindowWithEffects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.unbound.tapestry.tagselect.LabelAwareValueEncoder;

/**
 * Test showing how to use the PasswordPopup
 * @author ncharles
 *
 */
public class TestPopup {

	private static final Logger logger = LoggerFactory.getLogger(TestPopup.class);
	
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

	@Property
	private Date date;
	
	@InjectComponent
	private Form dateTest;

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
	private GenericBatch deleteGuestBatch;
	
	@Inject
	private DocumentManagementBatch documentManagementBatch;
	
	@Inject
	private UploadRequestBatch uploadRequestBatch;

	void onActionFromCleanOutDatedShares()
	{
		logger.debug("begin method onActionFromCleanOutDatedShares");
		shareManagementBatch.cleanOutdatedShares();
		logger.debug("endmethod onActionFromCleanOutDatedShares");
	}

	void onActionFromRemoveMissingDocuments()
	{
		logger.debug("begin method onActionFromRemoveMissingDocuments");
		documentManagementBatch.removeMissingDocuments();
		logger.debug("endmethod onActionFromRemoveMissingDocuments");
	}

	void onActionFromRemoveOldDocuments()
	{
		logger.debug("begin method onActionFromRemoveOldDocuments");
		documentManagementBatch.cleanOldDocuments();
		logger.debug("endmethod onActionFromRemoveOldDocuments");
	}

	void onActionFromCheckDocumentsMimeType()
	{
		logger.debug("begin method onActionFromCheckDocumentsMimeType");
		documentManagementBatch.checkDocumentsMimeType();
		logger.debug("endmethod onActionFromCheckDocumentsMimeType");
	}

	void onActionFromUploadRequestUpdateStatus()
	{
		logger.debug("begin method onActionFromUploadRequestUpdateStatus");
		uploadRequestBatch.updateStatus();
		logger.debug("endmethod onActionFromUploadRequestUpdateStatus");
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
	private SelectModelFactory selectModelFactory;

	@Inject
	private MailingListFacade mailingListFacade;

    public void onPrepare() {
        if (this.tags == null) {
            this.tags = new ArrayList<String>();
        }
    }
    
    SelectModel onProvideCompletionsFromTags(final String input) throws BusinessException {
		List<MailingListVo> lists = mailingListFacade.completionForUploadForm(userVo, input);
		SelectModel ret = selectModelFactory.create(lists, "identifier");

		return ret;
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

	public LabelAwareValueEncoder<MailingListVo> getEncoder() {
		return new LabelAwareValueEncoder<MailingListVo>() {
			@Override
			public String toClient(MailingListVo value) {
				return value.getUuid();
			}

			@Override
			public MailingListVo toValue(String clientValue) {
				MailingListVo ret = new MailingListVo();
				ret.setUuid(clientValue);
				return ret;
			}

			@Override
			public String getLabel(MailingListVo arg0) {
				return arg0.getIdentifier();
			}
		};
	}
	
	public void onSuccessFromDateTestForm() {
		logger.debug("Date: " + date);
	}
}
