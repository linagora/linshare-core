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

package org.linagora.linshare.cmis.utils.impl;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AllowableActionsImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringImpl;
import org.linagora.linshare.cmis.constants.CmisConstants;
import org.linagora.linshare.cmis.facade.impl.MainCmisFacadeImpl;
import org.linagora.linshare.cmis.utils.CmisHelpers;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class CmisHelpersImpl implements CmisHelpers {

	private final Logger logger = LoggerFactory.getLogger(MainCmisFacadeImpl.class);
	private final AccountService accountService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public CmisHelpersImpl(AccountService accountService,
			FunctionalityReadOnlyService functionalityReadOnlyService) {
		this.accountService = accountService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ObjectData> getMemoizer() {
		Map<String, ObjectData> memoizer = ((Map<String, ObjectData>) RequestContextHolder
				.getRequestAttributes().getAttribute(
						"org.linagora.linshare.cmis.objectDataMap",
						RequestAttributes.SCOPE_REQUEST));
		if (memoizer == null) {
			memoizer = new HashMap<String, ObjectData>();
			RequestContextHolder.getRequestAttributes().setAttribute(
					"org.linagora.linshare.cmis.objectDataMap", memoizer,
					RequestAttributes.SCOPE_REQUEST);
		}
		return memoizer;
	}

	@Override
	public Account prepare(String repositoryId, boolean thread, boolean document) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth == null) {
			logger.debug("Not authenticated access");
			throw new CmisPermissionDeniedException();
		}
		Account currentReq = (Account) RequestContextHolder
				.getRequestAttributes().getAttribute(
						"org.linagora.linshare.cmis.currentUser",
						RequestAttributes.SCOPE_REQUEST);
		if (currentReq != null)
			return currentReq;
		logger.debug("Authenticated Principal :" + auth.getName());
		if (!auth.getName().equals(repositoryId)) {
			throw new CmisPermissionDeniedException();
		}
		Account actor = accountService.findByLsUuid(auth.getName());
		Functionality cmisFunc = null;
		String message = "";
		if (thread) {
		cmisFunc = functionalityReadOnlyService
				.getCmisThreadsFunctionality(actor.getDomain());
		message = "Not Authorized to use CMIS to synchronize Threads !";
		}
		if (document) {
			cmisFunc = functionalityReadOnlyService
					.getCmisDocumentsFunctionality(actor.getDomain());
			message = "Not Authorized to use CMIS to synchronize documents !";
		}
		if (!cmisFunc.getActivationPolicy().getStatus()) {
			throw new CmisPermissionDeniedException(
					message);
		}
		RequestContextHolder.getRequestAttributes().setAttribute(
				"org.linagora.linshare.cmis.currentUser", actor,
				RequestAttributes.SCOPE_REQUEST);
		return actor;
	}

	@Override
	public String getObjectUuid(String objectId) {
		return objectId.split("\\$")[1];
	}

	@Override
	public PropertiesImpl setEntryProperty(String objectId, String path, String name) {
		PropertiesImpl properties = new PropertiesImpl();
		@SuppressWarnings("rawtypes")
		PropertyData prop = new PropertyIdImpl(PropertyIds.OBJECT_ID,
				objectId);
		properties.addProperty(prop);

		prop = new PropertyIdImpl(PropertyIds.BASE_TYPE_ID,
				BaseTypeId.CMIS_FOLDER.value());
		properties.addProperty(prop);

		prop = new PropertyIdImpl(PropertyIds.OBJECT_TYPE_ID,
				BaseTypeId.CMIS_FOLDER.value());
		properties.addProperty(prop);

		prop = new PropertyStringImpl(PropertyIds.PATH, path);
		properties.addProperty(prop);
		if (name.length() > 1) {
			prop = new PropertyStringImpl(PropertyIds.NAME,name);
			properties.addProperty(prop);
		}
		return properties;
	}

	@Override
	public AllowableActionsImpl setEntryAllowableActions(Account actor,
			String objectId) {
		AllowableActionsImpl allowable = new AllowableActionsImpl();
		Set<Action> actions = new HashSet<Action>();
		if (objectId.equals(CmisConstants.tagDocument)
				|| objectId.equals(CmisConstants.tagRoot)
				|| objectId.startsWith(CmisConstants.tagThreadChildren) || objectId.equals(CmisConstants.tagThread)) {
			actions.add(Action.CAN_ADD_OBJECT_TO_FOLDER);
			actions.add(Action.CAN_CREATE_DOCUMENT);
			actions.add(Action.CAN_CREATE_FOLDER);
			actions.add(Action.CAN_DELETE_OBJECT);
			actions.add(Action.CAN_GET_CHILDREN);
			actions.add(Action.CAN_GET_DESCENDANTS);
			actions.add(Action.CAN_REMOVE_OBJECT_FROM_FOLDER);
			actions.add(Action.CAN_SET_CONTENT_STREAM);
		} else {
			actions.add(Action.CAN_DELETE_OBJECT);
			actions.add(Action.CAN_REMOVE_OBJECT_FROM_FOLDER);
			actions.add(Action.CAN_SET_CONTENT_STREAM);
		}
		allowable.setAllowableActions(actions);
		return allowable;
	}

	@Override
	public PropertiesImpl setAllPropertyToEntry(Account actor, String objectId,
			Entry entry, String type, Long size) {
		User user = (User) actor;
		PropertiesImpl properties = new PropertiesImpl();
		@SuppressWarnings("rawtypes")
		PropertyData prop = new PropertyIdImpl(PropertyIds.OBJECT_ID, objectId);
		properties.addProperty(prop);

		prop = new PropertyIdImpl(PropertyIds.BASE_TYPE_ID,
				BaseTypeId.CMIS_DOCUMENT.value());
		properties.addProperty(prop);

		prop = new PropertyIdImpl(PropertyIds.OBJECT_TYPE_ID,
				BaseTypeId.CMIS_DOCUMENT.value());
		properties.addProperty(prop);

		prop = new PropertyIdImpl(PropertyIds.NAME, entry.getName());
		properties.addProperty(prop);

		prop = new PropertyStringImpl(PropertyIds.CONTENT_STREAM_FILE_NAME,
				entry.getName());
		properties.addProperty(prop);

		prop = new PropertyIdImpl(PropertyIds.CONTENT_STREAM_ID, "stream$"
				+ entry.getUuid());
		properties.addProperty(prop);

		prop = new PropertyStringImpl(PropertyIds.CONTENT_STREAM_MIME_TYPE,
				type);
		properties.addProperty(prop);

		prop = new PropertyDateTimeImpl(PropertyIds.CREATION_DATE,
				(GregorianCalendar) entry.getCreationDate());
		properties.addProperty(prop);

		prop = new PropertyDateTimeImpl(PropertyIds.LAST_MODIFICATION_DATE,
				(GregorianCalendar) entry.getCreationDate());
		properties.addProperty(prop);

		prop = new PropertyStringImpl(PropertyIds.CREATED_BY,
				user.getFirstName() + " " + user.getLastName());
		properties.addProperty(prop);

		prop = new PropertyIntegerImpl(PropertyIds.CONTENT_STREAM_LENGTH,
				BigInteger.valueOf(size));
		properties.addProperty(prop);

		prop = new PropertyStringImpl(PropertyIds.LAST_MODIFIED_BY,
				user.getFirstName() + " " + user.getLastName());
		properties.addProperty(prop);

		prop = new PropertyBooleanImpl(
				PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, false);
		properties.addProperty(prop);
		return properties;
	}
}
