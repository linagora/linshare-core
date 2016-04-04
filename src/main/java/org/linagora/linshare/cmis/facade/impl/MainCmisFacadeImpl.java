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
package org.linagora.linshare.cmis.facade.impl;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityChanges;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.FailedToDeleteDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectParentDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryCapabilitiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryInfoImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeDefinitionListImpl;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.server.support.TypeDefinitionFactory;
import org.linagora.linshare.cmis.constants.CmisConstants;
import org.linagora.linshare.cmis.constants.CmisDirectory;
import org.linagora.linshare.cmis.constants.CmisStrings;
import org.linagora.linshare.cmis.facade.MainCmisFacade;
import org.linagora.linshare.cmis.utils.CmisHelpers;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class MainCmisFacadeImpl extends AbstractCmisService implements MainCmisFacade {

	private final CmisVersion cmisVersion = CmisVersion.CMIS_1_0;
	private final AccountService accountService;
	private TypeDefinitionFactory typeDefinitionFactory = TypeDefinitionFactory
			.newInstance();
	private TypeDefinition folderTypeDefinition = typeDefinitionFactory
			.createBaseFolderTypeDefinition(cmisVersion);
	private final Logger log = LoggerFactory
			.getLogger(MainCmisFacadeImpl.class);
	private TypeDefinition documentTypeDefinition = typeDefinitionFactory
			.createBaseDocumentTypeDefinition(cmisVersion);

	private final CmisService documentEntryCmisService;
	private final CmisService threadEntryCmisService;
	private final CmisHelpers helpers;
	private final CmisStrings cmisStrings;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public MainCmisFacadeImpl(AccountService accountService,
			CmisService documentEntryCmisService,
			CmisService threadEntryCmisService, CmisStrings cmisStrings,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			CmisHelpers cmisHelpers) {
		this.accountService = accountService;
		this.documentEntryCmisService = documentEntryCmisService;
		this.threadEntryCmisService = threadEntryCmisService;
		this.cmisStrings = cmisStrings;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.helpers = cmisHelpers;
	}

	@Override
	public ContentStream getContentStream(String repositoryId, String objectId,
			String streamId, BigInteger offset, BigInteger length,
			ExtensionsData extension) {
		if (objectId.startsWith(CmisConstants.tagDocumentEntry))
			return documentEntryCmisService.getContentStream(repositoryId,
					objectId, streamId, offset, length, extension);
		if (objectId.startsWith(CmisConstants.tagThreadEntry))
			return threadEntryCmisService.getContentStream(repositoryId,
					objectId, streamId, offset, length, extension);
		throw new CmisObjectNotFoundException();

	}

	private Account authenticate() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth == null) {
			log.debug("Not authenticated access");
			throw new CmisPermissionDeniedException();
		}
		log.debug("Authenticated Principal :" + auth.getName());
		Account currentReq = (Account) RequestContextHolder
				.getRequestAttributes().getAttribute(
						"org.linagora.linshare.cmis.currentUser",
						RequestAttributes.SCOPE_REQUEST);
		if (currentReq != null)
			return currentReq;
		Account account = accountService.findByLsUuid(auth.getName());
		Functionality cmisFunc = functionalityReadOnlyService
				.getCmisFunctionality(account.getDomain());
		if (!cmisFunc.getActivationPolicy().getStatus())
			throw new CmisPermissionDeniedException(
					"Not Authorized to use CMIS !");
		return account;
	}

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {

		RepositoryInfoImpl r = new RepositoryInfoImpl();
		DateFormat dateFormat = new SimpleDateFormat(
				"MM-dd-yyyy_HH'h'mm'm'ss's'");

		Account account = authenticate();
		r.setId(account.getLsUuid());
		r.setName(account.getFullName() + "_" + dateFormat.format(account.getCreationDate()));
		r.setRootFolder(CmisConstants.tagRoot);
		r.setCmisVersion(cmisVersion);
		RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
		capabilities.setCapabilityChanges(CapabilityChanges.NONE);
		capabilities.setSupportsGetDescendants(false);
		capabilities.setSupportsGetFolderTree(false);
		r.setCapabilities(capabilities);
		List<RepositoryInfo> res = new LinkedList<RepositoryInfo>();
		res.add(r);
		return res;
	}

	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId,
			String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		TypeDefinitionList res = new TypeDefinitionListImpl(
				new ArrayList<TypeDefinition>());
		return res;
	}

	@Override
	public List<ObjectInFolderContainer> getDescendants(String repositoryId,
			String folderId, BigInteger depth, String filter,
			Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, ExtensionsData extension) {
		return super.getDescendants(repositoryId, folderId, depth, filter,
				includeAllowableActions, includeRelationships, renditionFilter,
				includePathSegment, extension);
	}

	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId,
			ExtensionsData extension) {
		authenticate();
		TypeDefinition res = null;
		if (typeId.equals(BaseTypeId.CMIS_FOLDER.value())) {
			res = folderTypeDefinition;
		} if (typeId.equals(BaseTypeId.CMIS_DOCUMENT.value())) {
			res = documentTypeDefinition;
		}
		return res;
	}

	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId,
			String filter, String orderBy, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems,
			BigInteger skipCount, ExtensionsData extension) {
		Account account = authenticate();
		ObjectInFolderListImpl res = new ObjectInFolderListImpl();
		if (folderId.equals(CmisConstants.tagRoot)) {
			List<ObjectInFolderData> objDlist = new LinkedList<ObjectInFolderData>();
			ObjectInFolderDataImpl objectInFolderDataImpl = null;

			boolean threadFunc = functionalityReadOnlyService
					.getCmisThreadsFunctionality(account.getDomain())
					.getActivationPolicy().getStatus();

			boolean docFunc = functionalityReadOnlyService
					.getCmisDocumentsFunctionality(account.getDomain())
					.getActivationPolicy().getStatus();

			if (threadFunc) {
				objectInFolderDataImpl = new ObjectInFolderDataImpl();
				objectInFolderDataImpl.setObject(getObject(repositoryId,
						CmisConstants.tagThread, filter,
						includeAllowableActions, includeRelationships,
						renditionFilter, false, false, extension));
				objDlist.add(objectInFolderDataImpl);
			}
			if (docFunc) {
				objectInFolderDataImpl = new ObjectInFolderDataImpl();
				objectInFolderDataImpl.setObject(getObject(repositoryId,
						CmisConstants.tagDocument, filter,
						includeAllowableActions, includeRelationships,
						renditionFilter, false, false, extension));
				objDlist.add(objectInFolderDataImpl);
			}
			res.setObjects(objDlist);
		} else if (folderId.equals(CmisConstants.tagDocument)) {
			res = (ObjectInFolderListImpl) documentEntryCmisService
					.getChildren(repositoryId, folderId, filter, orderBy,
							includeAllowableActions, includeRelationships,
							renditionFilter, includePathSegment, maxItems,
							skipCount, extension);
		} else if (folderId.startsWith(CmisConstants.tagThreadChildren)
				|| folderId.equals(CmisConstants.tagThread)) {
			res = (ObjectInFolderListImpl) threadEntryCmisService.getChildren(
					repositoryId, folderId, filter, orderBy,
					includeAllowableActions, includeRelationships,
					renditionFilter, includePathSegment, maxItems, skipCount,
					extension);
		} else
			throw new CmisNotSupportedException();
		return res;
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId,
			String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		authenticate();
		String parentId = null;
		List<ObjectParentData> list = new LinkedList<ObjectParentData>();
		if (objectId.equals(CmisConstants.tagRoot) || objectId.equals(CmisConstants.tagDocument)
				|| objectId.equals(CmisConstants.tagThread)) {
			parentId = CmisConstants.tagRoot;
		} if (objectId.startsWith(CmisConstants.tagDocumentEntry)) {
			parentId = CmisConstants.tagDocument;
		}
		if (objectId.startsWith(CmisConstants.tagThreadChildren)) {
			parentId = CmisConstants.tagThread;
		}
		if (objectId.startsWith(CmisConstants.tagThreadEntry)) {
			list = threadEntryCmisService.getObjectParents(repositoryId,
					objectId, filter, includeAllowableActions,
					includeRelationships, renditionFilter,
					includeRelativePathSegment, extension);
		} else {
		ObjectParentDataImpl opd = new ObjectParentDataImpl(getObject(
				repositoryId, parentId, filter, includeAllowableActions,
				includeRelationships, renditionFilter, false, false, extension));
		opd.setRelativePathSegment(getObjectInfo(repositoryId, objectId)
				.getObject().getProperties().getPropertyList().get(4)
				.getFirstValue().toString());
		list.add(opd);
		}
		return list;
	}

	@Override
	public ObjectData getObject(String repositoryId, String objectId,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		authenticate();
		ObjectDataImpl res = new ObjectDataImpl();
		Map<String, ObjectData> memoizer = helpers.getMemoizer();
		if (memoizer.get(objectId) != null) {
			res = (ObjectDataImpl) memoizer.get(objectId);
		}
		if (objectId.equals(CmisConstants.tagRoot)) {
			res.setProperties(helpers.setEntryProperty(objectId, "/", ""));
			res.setAllowableActions(helpers.setEntryAllowableActions(null,
					objectId));
			memoizer.put(objectId, res);
		} else if (objectId.equals(CmisConstants.tagDocument)
				|| objectId.startsWith(CmisConstants.tagDocumentEntry)) {
			res = (ObjectDataImpl) documentEntryCmisService.getObject(
					repositoryId, objectId, filter, includeAllowableActions,
					includeRelationships, renditionFilter, includePolicyIds,
					includeAcl, extension);
			memoizer.put(objectId, res);
		} else if (objectId.equals(CmisConstants.tagThread)
				|| objectId.startsWith(CmisConstants.tagThreadChildren)
				|| objectId.startsWith(CmisConstants.tagThreadEntry)) {
			res = (ObjectDataImpl) threadEntryCmisService.getObject(
					repositoryId, objectId, filter, includeAllowableActions,
					includeRelationships, renditionFilter, includePolicyIds,
					includeAcl, extension);
			memoizer.put(objectId, res);
		} else
			throw new CmisObjectNotFoundException();
		return res;
	}

	@Override
	public ObjectData getObjectByPath(String repositoryId, String path,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		Account actor = authenticate();
		ObjectData res = null;
		if (!path.startsWith("/")) {
			throw new CmisNotSupportedException();
		}
		String[] splitPath = path.substring(1).split("/");
		if (splitPath.length == 1) {
			if (splitPath[0].equals("")) {
				res = getObject(repositoryId, CmisConstants.tagRoot, filter,
						includeAllowableActions, includeRelationships,
						renditionFilter, includePolicyIds, includeAcl,
						extension);
			} else if (splitPath[0].equals(cmisStrings.getName(CmisDirectory.MY_FILES,
					SupportedLanguage.fromTapestryLocale(actor.getCmisLocale())))) {
				res = getObject(repositoryId, CmisConstants.tagDocument, filter,
						includeAllowableActions, includeRelationships,
						renditionFilter, includePolicyIds, includeAcl,
						extension);
			} else
				throw new CmisObjectNotFoundException();
		} else if (splitPath.length == 2) {
			if (splitPath[0].equals(cmisStrings.getName(CmisDirectory.MY_FILES,
					SupportedLanguage.fromTapestryLocale(actor.getCmisLocale())))) {
				res = documentEntryCmisService.getObjectByPath(repositoryId,
						path, filter, includeAllowableActions,
						includeRelationships, renditionFilter,
						includePolicyIds, includeAcl, extension);
			} else if (splitPath[0].equals(cmisStrings
					.getName(CmisDirectory.MY_THREADS, SupportedLanguage.fromTapestryLocale(actor.getCmisLocale())))) {
				res = threadEntryCmisService.getObjectByPath(repositoryId,
						path, filter, includeAllowableActions,
						includeRelationships, renditionFilter,
						includePolicyIds, includeAcl, extension);
			} else throw new CmisObjectNotFoundException();
		} else
			throw new CmisObjectNotFoundException();
		return res;
	}

	public ObjectInfo getObjectInfo(String repositoryId, String objectId) {
		ObjectInfoImpl res;
		if (objectId.equals(CmisConstants.tagRoot)) {
			res = new ObjectInfoImpl(objectId, BaseTypeId.CMIS_FOLDER);
			res.setObject(getObject(repositoryId, objectId, null, null, null,
					null, null, null, null));
			return res;
		} else if (objectId.equals(CmisConstants.tagDocument)
				|| objectId.startsWith(CmisConstants.tagDocumentEntry)) {
			res = (ObjectInfoImpl) documentEntryCmisService.getObjectInfo(repositoryId,
					objectId);
		} else if (objectId.equals(CmisConstants.tagThread) || objectId.startsWith(CmisConstants.tagThreadChildren)
				|| objectId.startsWith(CmisConstants.tagThreadEntry)) {
			return threadEntryCmisService.getObjectInfo(repositoryId, objectId);
		} else
			throw new CmisObjectNotFoundException();
		return res;
	}

	public RepositoryInfo getRepositoryInfo(String repositoryId,
			ExtensionsData extension) {
		RepositoryInfo repo = null;
		RepositoryInfo defau = null;
		List<RepositoryInfo> list = getRepositoryInfos(extension);
		for (RepositoryInfo r : list) {
			defau = r;
			if (r.getId().equals(repositoryId))
				repo = r;
		}
		if (repo == null) {
			if (defau == null) {
				throw new CmisObjectNotFoundException();
			}
			return defau;
		}
		return repo;
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId,
			String objectId, Boolean allVersions, ExtensionsData extension) {
		authenticate();
		if (objectId.startsWith(CmisConstants.tagDocumentEntry)) {
			documentEntryCmisService.deleteObjectOrCancelCheckOut(repositoryId,
					objectId, allVersions, extension);
		} if (objectId.startsWith(CmisConstants.tagThreadEntry)) {
			threadEntryCmisService.deleteObjectOrCancelCheckOut(repositoryId,
					objectId, allVersions, extension);
		}
	}

	@Override
	public String create(String repositoryId, Properties properties,
			String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies,
			ExtensionsData extension) {
		authenticate();
		String res;
		if (contentStream == null) {
			log.error("Tried to sync a folder !");
			res = CmisConstants.tagRoot;
		} else if (folderId.equals(CmisConstants.tagDocument)) {
			res = documentEntryCmisService.create(repositoryId, properties,
					folderId, contentStream, versioningState, policies,
					extension);
		} else if (folderId.startsWith(CmisConstants.tagThreadChildren)) {
			res = threadEntryCmisService.create(repositoryId, properties,
					folderId, contentStream, versioningState, policies,
					extension);
		} else
			throw new CmisNotSupportedException();
		return res;
	}

	@Override
	public void setContentStream(String repositoryId, Holder<String> objectId,
			Boolean overwriteFlag, Holder<String> changeToken,
			ContentStream contentStream, ExtensionsData extension) {
		authenticate();
		if (objectId.getValue().startsWith(CmisConstants.tagDocumentEntry)) {
			documentEntryCmisService.setContentStream(repositoryId, objectId,
					overwriteFlag, changeToken, contentStream, extension);
		}
		else
			throw new CmisPermissionDeniedException();
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId,
			Holder<String> changeToken, Properties properties,
			ExtensionsData extension) {
		authenticate();
		if (objectId.getValue().startsWith(CmisConstants.tagDocumentEntry)) {
			documentEntryCmisService.updateProperties(repositoryId, objectId,
					changeToken, properties, extension);
		} else if (objectId.getValue().startsWith(CmisConstants.tagThreadEntry)) {
			threadEntryCmisService.updateProperties(repositoryId, objectId,
					changeToken, properties, extension);
		} else
			throw new CmisPermissionDeniedException();
	}

	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId,
			Boolean allVersions, UnfileObject unfileObjects,
			Boolean continueOnFailure, ExtensionsData extension) {
		authenticate();
		FailedToDeleteData failedToDeleteData = new FailedToDeleteDataImpl();
		return failedToDeleteData;
	}
}
