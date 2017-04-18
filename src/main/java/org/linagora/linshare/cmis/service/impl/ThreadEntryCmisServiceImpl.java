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
package org.linagora.linshare.cmis.service.impl;

import java.io.File;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectParentDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.linagora.linshare.cmis.constants.CmisConstants;
import org.linagora.linshare.cmis.constants.CmisDirectory;
import org.linagora.linshare.cmis.constants.CmisStrings;
import org.linagora.linshare.cmis.exceptions.CmisExceptionMappingService;
import org.linagora.linshare.cmis.utils.CmisHelpers;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class ThreadEntryCmisServiceImpl extends EntryCmisServiceImpl {
	private final WorkGroupNodeService workGroupNodeService;
	private final CmisExceptionMappingService cmisExceptionMappingService;
	private final ThreadService threadService;
	private final CmisStrings cmisStrings;
	private final CmisHelpers helpers;

	public ThreadEntryCmisServiceImpl(WorkGroupNodeService workGroupNodeService,
			CmisExceptionMappingService cmisExceptionMappingService,
			ThreadService threadService, CmisStrings cmisStrings,
			CmisHelpers cmisHelpers) {
		super();
		this.workGroupNodeService = workGroupNodeService;
		this.cmisExceptionMappingService = cmisExceptionMappingService;
		this.threadService = threadService;
		this.cmisStrings = cmisStrings;
		this.helpers = cmisHelpers;
	}

	private String computeThreadName(Account actor, Thread thread) {
		DateFormat dateFormat = new SimpleDateFormat(
				"MM-dd-yyyy_HH'h'mm'm'ss's'");

		String res = cmisStrings.getName(CmisDirectory.THREAD,
				SupportedLanguage.fromTapestryLocale(actor.getCmisLocale()))
				+ thread.getName()
				+ "_"
				+ dateFormat.format(thread.getCreationDate());

		return res;
	}

//	CMIS: not use because this function is for repository.
	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		return null;
	}

//	CMIS: not use because this function is for repository.
	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId,
			String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		return null;
	}

//	CMIS: not use because this function is for repository.
	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId,
			ExtensionsData extension) {
		return null;
	}

	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId,
			String filter, String orderBy, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems,
			BigInteger skipCount, ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, true, false);
		ObjectInFolderList res = null;

		if (folderId.equals(CmisConstants.tagThread)) {
			res = getThreadList(actor);
		} else if (folderId.startsWith(CmisConstants.tagThreadChildren)) {
			res = getEntriesInThread(folderId, actor);
		} else 
			throw new CmisObjectNotFoundException();
		return res;
	}

	private ObjectInFolderList getEntriesInThread(String folderId, Account actor) {
		List<ObjectInFolderData> objDlist = new LinkedList<ObjectInFolderData>();
		ObjectInFolderListImpl objList = new ObjectInFolderListImpl();
		String threadId = helpers.getObjectUuid(folderId);
		Thread thread = threadService.find(actor, actor, threadId);
		List<WorkGroupNode> entries;
		try {
			entries = workGroupNodeService.findAll(actor, (User) actor, thread);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
		for (WorkGroupNode entry : entries) {
			ObjectInFolderDataImpl objData = new ObjectInFolderDataImpl(
					getObject(actor.getLsUuid(),
							CmisConstants.tagThreadEntry + entry.getUuid(), null, null, null,
							null, null, null, null));
			objDlist.add(objData);
		}
		objList.setObjects(objDlist);
		return objList;
	}

	private ObjectInFolderList getThreadList(Account actor) {
		List<ObjectInFolderData> objDlist = new LinkedList<ObjectInFolderData>();
		ObjectInFolderListImpl objList = new ObjectInFolderListImpl();
		List<Thread> threads = threadService.findAllWhereAdmin((User) actor);
		for (Thread thread : threads) {
			ObjectInFolderDataImpl objData = new ObjectInFolderDataImpl(
					getObject(actor.getLsUuid(),
							CmisConstants.tagThreadChildren + thread.getLsUuid(), null, null, null,
							null, null, null, null));
			objDlist.add(objData);
		}
		objList.setObjects(objDlist);
		return objList;
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId,
			String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, true, false);
		WorkGroupNode entry = null;
		try {
			String workGroupNodeUuid = helpers.getObjectUuid(objectId);
			String workGroupUuid = workGroupNodeService.findWorkGroupUuid(actor, (User) actor, workGroupNodeUuid);
			Thread thread = threadService.find(actor, actor, workGroupUuid);
			entry = workGroupNodeService.find(actor, (User) actor,
					thread, workGroupNodeUuid, false);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
		ObjectParentDataImpl opd = new ObjectParentDataImpl(getObject(
				repositoryId, CmisConstants.tagThreadChildren + entry.getWorkGroup(),
				filter, includeAllowableActions, includeRelationships,
				renditionFilter, false, false, extension));
		opd.setRelativePathSegment(entry.getName());
		List<ObjectParentData> list = new LinkedList<ObjectParentData>();
		list.add(opd);
		return list;
	}

	@Override
	public ObjectData getObject(String repositoryId, String objectId,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, true, false);
		ObjectDataImpl res = new ObjectDataImpl();
		if (objectId.equals(CmisConstants.tagThread)) {
			String name = cmisStrings
					.getName(CmisDirectory.MY_THREADS, SupportedLanguage
							.fromTapestryLocale(actor.getCmisLocale()));
			String path = "/" + name;
			PropertiesImpl properties = helpers.setEntryProperty(
					objectId, path, name);
			res.setProperties(properties);
		} else if (objectId.startsWith(CmisConstants.tagThreadChildren)) {
			Thread thread = threadService
					.find(actor, actor, helpers.getObjectUuid(objectId));
			String name = computeThreadName(actor, thread);
			String path = "/"
					+ cmisStrings.getName(CmisDirectory.MY_THREADS,
							SupportedLanguage.fromTapestryLocale(actor
									.getCmisLocale())) + "/" + name;
			res.setProperties(helpers.setEntryProperty(objectId, path, name));
		} else {
			WorkGroupNode node = null;
			try {
				String workGroupNodeUuid = helpers.getObjectUuid(objectId);
				String workGroupUuid = workGroupNodeService.findWorkGroupUuid(actor, (User) actor, workGroupNodeUuid);
				Thread thread = threadService.find(actor, actor, workGroupUuid);
				node = workGroupNodeService.find(actor, (User) actor,
						thread, workGroupNodeUuid, false);
			} catch (BusinessException e) {
				throw cmisExceptionMappingService.map(e);
			}
			ThreadEntry threadEntry = new ThreadEntry((WorkGroupDocument)node);
			PropertiesImpl properties = helpers.setAllPropertyToEntry(actor,
					objectId, threadEntry, threadEntry.getType(),
					threadEntry.getSize());
			res.setProperties(properties);
		}
		res.setAllowableActions(helpers.setEntryAllowableActions(
				actor, objectId));
		return res;
	}

	@Override
	public ContentStream getContentStream(String repositoryId, String objectId,
			String streamId, BigInteger offset, BigInteger length,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, true, false);
		FileAndMetaData f;
		try {
			String workGroupNodeUuid = helpers.getObjectUuid(objectId);
			String workGroupUuid = workGroupNodeService.findWorkGroupUuid(actor, (User) actor, workGroupNodeUuid);
			Thread thread = threadService.find(actor, actor, workGroupUuid);
			f = workGroupNodeService.download(actor, (User)actor, thread, workGroupNodeUuid);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
		ContentStream res = new ContentStreamImpl(f.getName(),
				BigInteger.valueOf(f.getSize()),
				f.getMimeType(), f.getStream());
		return res;
	}

	@Override
	public ObjectInfo getObjectInfo(String repositoryId, String objectId) {

		Account actor = helpers.prepare(repositoryId, true, false);
		ObjectInfoImpl res;
		if (objectId.equals(CmisConstants.tagThread) || objectId.startsWith(CmisConstants.tagThreadChildren)) {
			res = new ObjectInfoImpl(objectId, BaseTypeId.CMIS_FOLDER);
			res.setObject(getObject(repositoryId, objectId, null, null, null,
					null, null, null, null));
		} else {
			if (!objectId.startsWith(CmisConstants.tagThreadEntry)) {
				throw new CmisObjectNotFoundException();
			}
			WorkGroupNode threadEntry = null;
			try {
				String workGroupNodeUuid = helpers.getObjectUuid(objectId);
				String workGroupUuid = workGroupNodeService.findWorkGroupUuid(actor, (User) actor, workGroupNodeUuid);
				Thread thread = threadService.find(actor, actor, workGroupUuid);
				threadEntry = workGroupNodeService.find(actor, (User) actor,
						thread, workGroupNodeUuid, false);
			} catch (BusinessException e) {
				throw cmisExceptionMappingService.map(e);
			}
			res = new ObjectInfoImpl(objectId, BaseTypeId.CMIS_DOCUMENT);
			res.setContentType(((WorkGroupDocument)threadEntry).getMimeType());
			User user = (User) actor;
			res.setCreatedBy(user.getFirstName() + " " + user.getLastName());
			res.setFileName(threadEntry.getName());

			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.setTime(threadEntry.getCreationDate());
			res.setCreationDate((GregorianCalendar) cal1);

			Calendar cal2 = GregorianCalendar.getInstance();
			cal2.setTime(threadEntry.getModificationDate());
			res.setLastModificationDate((GregorianCalendar)cal2);

			res.setObject(getObject(repositoryId, objectId, null, null, null, null,
					null, null, null));
		}
		return res;
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, true, false);
		try {
			String workGroupNodeUuid = helpers.getObjectUuid(objectId);
			String workGroupUuid = workGroupNodeService.findWorkGroupUuid(actor, (User) actor, workGroupNodeUuid);
			Thread thread = threadService.find(actor, actor, workGroupUuid);
			workGroupNodeService.delete(actor, (User) actor, thread, workGroupNodeUuid);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
	}

	@Override
	public String create(String repositoryId, Properties properties,
			String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, true, false);
		String res = folderId;
		if (contentStream.getFileName() != null) {
			File tempFile = null;
			Thread thread = threadService.find(actor, actor, helpers.getObjectUuid(folderId));
			try {
				tempFile = getTempFile(contentStream.getStream(), contentStream.getFileName());
				WorkGroupNode node = workGroupNodeService.create(actor, (User)actor, thread, tempFile, contentStream.getFileName(), null, true);
				return CmisConstants.tagThreadEntry + node.getUuid();
			} catch (BusinessException e) {
				throw cmisExceptionMappingService.map(e);
			} finally {
				deleteTempFile(tempFile);
			}
		}
		return res;
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId,
			Holder<String> changeToken, Properties properties,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, true, false);
		String uuid = helpers.getObjectUuid(objectId.getValue());
		String description = (String) properties.getProperties()
				.get(PropertyIds.DESCRIPTION).getFirstValue();
		String name = (String) properties.getProperties().get(PropertyIds.NAME)
				.getFirstValue();
		try {
			String workGroupUuid = workGroupNodeService.findWorkGroupUuid(actor, (User) actor, uuid);
			Thread thread = threadService.find(actor, actor, workGroupUuid);
			WorkGroupDocument node = new WorkGroupDocument();
			node.setUuid(uuid);
			node.setDescription(description);
			node.setName(name);
			workGroupNodeService.update(actor, (User)actor, thread, node);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
	}

//	CMIS -> This method is never used.
	@Override
	public ObjectData getObjectByPath(String repositoryId, String path,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		return null;
	}
}
