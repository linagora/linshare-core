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
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AllowableActionsImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectParentDataImpl;
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
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DocumentEntryService;

public class DocumentEntryCmisServiceImpl extends EntryCmisServiceImpl {

	private final DocumentEntryService documentEntryService;
	private final CmisExceptionMappingService cmisExceptionMappingService;
	private final CmisStrings cmisStrings;
	private final CmisHelpers helpers;

	public DocumentEntryCmisServiceImpl(DocumentEntryService documentEntryService,
			CmisExceptionMappingService cmisExceptionMappingService,
			CmisStrings cmisStrings,
			CmisHelpers cmisHelpers) {
		super();
		this.documentEntryService = documentEntryService;
		this.cmisExceptionMappingService = cmisExceptionMappingService;
		this.cmisStrings = cmisStrings;
		this.helpers = cmisHelpers;
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

		Account actor = helpers.prepare(repositoryId, false, true);
		if (!folderId.equals(CmisConstants.tagDocument)) {
			throw new CmisNotSupportedException();
		}
		ObjectInFolderListImpl objList = new ObjectInFolderListImpl();
		List<ObjectInFolderData> objDlist = new LinkedList<ObjectInFolderData>();
		List<DocumentEntry> list;
		try {
			list = documentEntryService.findAllMySyncEntries(actor, actor);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
		for (DocumentEntry docEntry : list) {
			ObjectInFolderDataImpl o = new ObjectInFolderDataImpl();
			o.setObject(getObjectFromEntry(docEntry, actor));
			o.setPathSegment(docEntry.getName());
			objDlist.add(o);
		}
		objList.setObjects(objDlist);
		return objList;
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId,
			String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, false, true);
		ObjectParentDataImpl opd = new ObjectParentDataImpl(getObject(
				repositoryId, CmisConstants.tagDocument, filter, includeAllowableActions,
				includeRelationships, renditionFilter, false, false, extension));
		opd.setRelativePathSegment(cmisStrings.getName(CmisDirectory.MY_FILES,
				SupportedLanguage.fromTapestryLocale(actor.getCmisLocale())));
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

		Account actor = helpers.prepare(repositoryId, false, true);
		ObjectDataImpl res = new ObjectDataImpl();
		Map<String, ObjectData> memoizer = helpers.getMemoizer();
		if (memoizer.get(objectId) != null) {
			res = (ObjectDataImpl) memoizer.get(objectId);
		} else if (objectId.equals(CmisConstants.tagDocument)) {
			String name = cmisStrings.getName(CmisDirectory.MY_FILES,
							SupportedLanguage.fromTapestryLocale(actor.getCmisLocale()));
			res.setProperties(helpers.setEntryProperty(objectId, "/"
					+ name, name));
			res.setAllowableActions(helpers.setEntryAllowableActions(actor, objectId));
			memoizer.put(objectId, res);
		} else {
			DocumentEntry docEntry = null;
			try {
				String docEntryUuid = helpers.getObjectUuid(objectId);
				docEntry = documentEntryService.find(actor, actor, docEntryUuid);
			} catch (BusinessException e) {
				throw cmisExceptionMappingService.map(e);
			}
			res.setProperties(helpers.setAllPropertyToEntry(actor,
					objectId, docEntry, docEntry.getType(), docEntry.getSize()));
			AllowableActionsImpl allowable = new AllowableActionsImpl();
			Set<Action> actions = new HashSet<Action>();
			actions.add(Action.CAN_DELETE_OBJECT);
			actions.add(Action.CAN_REMOVE_OBJECT_FROM_FOLDER);
			actions.add(Action.CAN_SET_CONTENT_STREAM);
			actions.add(Action.CAN_UPDATE_PROPERTIES);
			allowable.setAllowableActions(actions);
			res.setAllowableActions(allowable);
			memoizer.put(objectId, res);
		}
		return res;
	}

	private ObjectData getObjectFromEntry(DocumentEntry docEntry, Account actor) {
		ObjectDataImpl res = new ObjectDataImpl();
		Map<String, ObjectData> memoizer = helpers.getMemoizer();
		String objectId = CmisConstants.tagDocumentEntry + docEntry.getUuid();
		if (memoizer.get(objectId) != null) {
			return memoizer.get(objectId);
		}
		res.setProperties(helpers.setAllPropertyToEntry(actor,
				objectId, docEntry, docEntry.getType(), docEntry.getSize()));
		res.setAllowableActions(helpers.setEntryAllowableActions(actor, objectId));
		memoizer.put(objectId, res);
		return res;
	}

	@Override
	public ContentStream getContentStream(String repositoryId, String objectId,
			String streamId, BigInteger offset, BigInteger length,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, false, true);
		DocumentEntry docEntry;
		InputStream in;
		try {
			String docEntryUuid = helpers.getObjectUuid(objectId);
			docEntry = documentEntryService.find(actor, actor, docEntryUuid);
			in = documentEntryService.getDocumentStream(actor, actor,
					docEntry.getUuid());
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
		ContentStream res = new ContentStreamImpl(docEntry.getName(),
				BigInteger.valueOf(docEntry.getSize()), docEntry.getType(), in);
		return res;
	}

	@Override
	public ObjectInfo getObjectInfo(String repositoryId, String objectId) {
		ObjectInfoImpl res;
		if (objectId.equals(CmisConstants.tagDocument)) {
			res = new ObjectInfoImpl(objectId, BaseTypeId.CMIS_FOLDER);
			res.setObject(getObject(repositoryId, objectId, null, null, null,
					null, null, true, null));
		} else {
			res = new ObjectInfoImpl(objectId, BaseTypeId.CMIS_DOCUMENT);
			res.setObject(getObject(repositoryId, objectId, null, null, null, null,
					null, true, null));
		}
		return res;
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId,
			String objectId, Boolean allVersions, ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, false, true);
		try {
			String docEntryUuid = helpers.getObjectUuid(objectId);
			DocumentEntry docEntry = documentEntryService.find(actor, actor,
					docEntryUuid);
			documentEntryService.updateFileProperties(actor,
					docEntry.getUuid(), docEntry.getName(),
					docEntry.getComment(), false);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
	}

	@Override
	public String create(String repositoryId, Properties properties,
			String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, false, true);
		String res;
		File tempFile = null;
		if (contentStream.getFileName() == null)
			res = CmisConstants.tagRoot;
		try {
			tempFile = getTempFile(contentStream.getStream(), contentStream.getFileName());
			DocumentEntry documentEntry = documentEntryService
					.create(actor, actor, tempFile,
							contentStream.getFileName(), "", true, null);
			res = CmisConstants.tagDocumentEntry + documentEntry.getUuid();
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		} finally {
			deleteTempFile(tempFile);
		}
		return res;
	}

	@Override
	public void setContentStream(String repositoryId, Holder<String> objectId,
			Boolean overwriteFlag, Holder<String> changeToken,
			ContentStream contentStream, ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, false, true);
		File tempFile = null;
		try {
			tempFile = getTempFile(contentStream.getStream(), contentStream.getFileName());
			documentEntryService.update(actor, actor, helpers.getObjectUuid(objectId.getValue()),
					tempFile, contentStream.getFileName());
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		} finally {
			deleteTempFile(tempFile);
		}
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId,
			Holder<String> changeToken, Properties properties,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, false, true);
		try {
			documentEntryService.updateFileProperties(actor, helpers.getObjectUuid(objectId.getValue()),
					(String) properties.getProperties().get(PropertyIds.NAME)
							.getFirstValue(), "", true);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
	}

	@Override
	public ObjectData getObjectByPath(String repositoryId, String path,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		Account actor = helpers.prepare(repositoryId, false, true);
		String[] splitPath = path.substring(1).split("/");
		String filename = splitPath[1];
		try {
			DocumentEntry documentEntry = documentEntryService
					.findMoreRecentByName(actor, actor, filename);
			return getObject(repositoryId,
					CmisConstants.tagDocumentEntry + documentEntry.getUuid(), filter,
					includeAllowableActions, includeRelationships,
					renditionFilter, includePolicyIds, includeAcl, extension);
		} catch (BusinessException e) {
			throw cmisExceptionMappingService.map(e);
		}
	}
}
