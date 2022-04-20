/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.service.impl;

import static org.linagora.linshare.core.domain.constants.LinShareConstants.defaultWelcomeMessagesUuid;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.WelcomeMessagesBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.WelcomeMessagesEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.WelcomeMessagesService;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class WelcomeMessagesServiceImpl implements WelcomeMessagesService {

	private final WelcomeMessagesBusinessService businessService;

	private final DomainBusinessService domainBusinessService;

	private final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	public WelcomeMessagesServiceImpl(
			final WelcomeMessagesBusinessService wlcmBusinessService,
			final DomainBusinessService domainBusinessService,
			final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		this.businessService = wlcmBusinessService;
		this.domainBusinessService = domainBusinessService;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
	}

	@Override
	public List<WelcomeMessages> findAll(User actor, String domainId, boolean parent)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		AbstractDomain currDomain = actor.getDomain();
		if (domainId != null) {
			currDomain = domainBusinessService.findById(domainId);
		}
		if (parent) {
			return findAll(actor, currDomain);
		}
		return businessService.findAll(currDomain);
	}

	private  List<WelcomeMessages> findAll(User actor, AbstractDomain domain) {
		List<WelcomeMessages> res = Lists.newArrayList();
		res.addAll(businessService.findAll(domain));
		if (domain.getParentDomain() != null) {
			res.addAll(findAll(actor, domain.getParentDomain()));
		}
		return res;
	}

	@Override
	public WelcomeMessages find(Account actor, String uuid)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "Welcome message uuid must be set.");
		WelcomeMessages wlcm = businessService.find(uuid);

		if (wlcm == null)
			throw new BusinessException(
					BusinessErrorCode.WELCOME_MESSAGES_NOT_FOUND,
					"Welcome message with uuid :" + uuid + " not found.");
		return wlcm;
	}
	
	@Override
	public WelcomeMessages createByCopy(User actor, WelcomeMessages wlcmInput,
	                                    String domainId) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(wlcmInput, "Welcome message must be set.");
		Validate.notNull(wlcmInput.getUuid(),
				"Welcome message uuid must be set in order to duplicate it.");
		Validate.notNull(domainId, "Welcome message domain must be set.");

		AbstractDomain domain = domainBusinessService.findById(domainId);
		WelcomeMessages wlcm = find(actor, wlcmInput.getUuid());
		WelcomeMessages welcomeMessage = new WelcomeMessages(wlcm);
		addDefaultLanguagesEntriesIfNeeded(actor, welcomeMessage);
		welcomeMessage.setBussinessName(sanitizerInputHtmlBusinessService.strictClean(wlcmInput.getName()));
		welcomeMessage.setBussinessDescription(getDescription(wlcmInput));
		welcomeMessage.setDomain(domain);
		return businessService.create(welcomeMessage);
	}

	private String getDescription(WelcomeMessages welcomeMessages) {
		String description = Strings.nullToEmpty(welcomeMessages.getDescription());
		if (!Strings.isNullOrEmpty(description)) {
			description = sanitizerInputHtmlBusinessService.strictClean(description);
		}
		return description;
	}

	@Override
	public WelcomeMessages create(User actor, WelcomeMessages welcomeMessages) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(welcomeMessages, "Welcome message must be set.");

		addDefaultLanguagesEntriesIfNeeded(actor, welcomeMessages);
		welcomeMessages.setBussinessName(sanitizerInputHtmlBusinessService.strictClean(welcomeMessages.getName()));
		welcomeMessages.setBussinessDescription(getDescription(welcomeMessages));
		return businessService.create(welcomeMessages);
	}

	private void addDefaultLanguagesEntriesIfNeeded(User actor, WelcomeMessages welcomeMessages) {
		Map<SupportedLanguage, WelcomeMessagesEntry> entries = welcomeMessages.getWelcomeMessagesEntries();
		if (entries.size() != SupportedLanguage.values().length) {
			Map<SupportedLanguage, WelcomeMessagesEntry> defaultEntries = find(actor, defaultWelcomeMessagesUuid)
				.getWelcomeMessagesEntries();
			for (SupportedLanguage language : SupportedLanguage.values()) {
				if (!entries.containsKey(language)) {
					entries.put(language, new WelcomeMessagesEntry(language, defaultEntries.get(language).getValue()));
				}
			}
		}
	}

	@Override
	public WelcomeMessages update(User actor, WelcomeMessages wlcm,
			List<String> domainUuids) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(wlcm, "Welcome message object must be set.");
		Validate.notEmpty(wlcm.getUuid(), "Welcome message uuid must be set.");
		Map<SupportedLanguage, WelcomeMessagesEntry> tmpMsg = wlcm
				.getWelcomeMessagesEntries();
		Validate.notEmpty(tmpMsg, "Wecolme message entries must be set.");

		WelcomeMessages welcomeMessage = find(actor, wlcm.getUuid());
		if (welcomeMessage.getWelcomeMessagesEntries().keySet().size() != tmpMsg
				.keySet().size()) {
			throw new BusinessException(
					BusinessErrorCode.WELCOME_MESSAGES_ILLEGAL_KEY,
					"Invalid number of keys.");
		}

		for (SupportedLanguage lang : wlcm.getWelcomeMessagesEntries().keySet()) {
			if (!welcomeMessage.getWelcomeMessagesEntries().keySet()
					.contains(lang))
				throw new BusinessException(
						BusinessErrorCode.WELCOME_MESSAGES_ILLEGAL_KEY,
						"Invalid number of keys.");
		}
		for (SupportedLanguage lang : tmpMsg.keySet()) {
			if (!welcomeMessage.getWelcomeMessagesEntries().keySet()
					.contains(lang))
				throw new BusinessException(
						BusinessErrorCode.WELCOME_MESSAGES_ILLEGAL_KEY,
						"Invalid number of keys.");
		}
		// Updating current WM.
		welcomeMessage.setDescription(getDescription(wlcm));
		if (wlcm.getName() != null)
			welcomeMessage.setName(sanitizerInputHtmlBusinessService.strictClean(wlcm.getName()));
		Map<SupportedLanguage, WelcomeMessagesEntry> welcomeMessagesEntries = welcomeMessage
				.getWelcomeMessagesEntries();
		for (SupportedLanguage key : tmpMsg.keySet()) {
			WelcomeMessagesEntry welcomeMessagesEntry = welcomeMessagesEntries
					.get(key);
			welcomeMessagesEntry.setValue(tmpMsg.get(key).getValue());
		}
		welcomeMessage = businessService.update(welcomeMessage);

		if (domainUuids != null) {
			WelcomeMessages defaultWM = businessService
					.find(LinShareConstants.defaultWelcomeMessagesUuid);
			List<AbstractDomain> relativeDomains = domainBusinessService
					.loadRelativeDomains(welcomeMessage);
			for (AbstractDomain domain : relativeDomains) {
				String domainId = domain.getUuid();
				if (domainUuids.contains(domainId)) {
					// Already affected.
					domainUuids.remove(domainId);
				} else {
					// current domain is not using this WM.
					domain.setCurrentWelcomeMessages(defaultWM);
					domainBusinessService.update(domain);
				}
			}
			for (String id : domainUuids) {
				AbstractDomain domain = domainBusinessService.findById(id);
				domain.setCurrentWelcomeMessages(welcomeMessage);
				domainBusinessService.update(domain);
			}
		}
		return welcomeMessage;
	}

	private void reinitToDefault(WelcomeMessages welcomeMessage) {
		List<AbstractDomain> domains = domainBusinessService
				.loadRelativeDomains(welcomeMessage);
		WelcomeMessages defaultWM = businessService
				.find(LinShareConstants.defaultWelcomeMessagesUuid);
		for (AbstractDomain d : domains) {
			d.setCurrentWelcomeMessages(defaultWM);
			domainBusinessService.update(d);
		}
	}

	@Override
	public WelcomeMessages delete(User actor, String uuid)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "Welcome message uudi must be set.");

		WelcomeMessages wlcm = find(actor, uuid);
		if (LinShareConstants.defaultWelcomeMessagesUuid.equals(uuid)) {
			throw new BusinessException(BusinessErrorCode.WELCOME_MESSAGES_FORBIDDEN,
					"Cannot delete default welcome message " + wlcm.getName());
		}
		reinitToDefault(wlcm);
		businessService.delete(wlcm);
		return wlcm;
	}
}
