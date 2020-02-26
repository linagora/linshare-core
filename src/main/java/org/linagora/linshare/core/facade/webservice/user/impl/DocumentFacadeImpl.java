/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.commons.lang3.Validate;
import org.apache.cxf.helpers.IOUtils;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.core.facade.webservice.common.dto.DocumentAttachement;
import org.linagora.linshare.core.facade.webservice.common.dto.MimeTypeDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.MimePolicyService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.SignatureService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DocumentFacadeImpl extends UserGenericFacadeImp implements DocumentFacade {

	private final DocumentEntryService documentEntryService;

	private final MimePolicyService mimePolicyService;

	private final ShareService shareService;

	private final SignatureService signatureService;

	private final EntryBusinessService entryBusinessService;

	protected final AuditLogEntryService auditLogEntryService;

	protected final ThreadService threadService;

	protected final WorkGroupNodeService workGroupNodeService;

	protected final WorkGroupDocumentRevisionService revisionService;

	public DocumentFacadeImpl(final DocumentEntryService documentEntryService,
			final AccountService accountService,
			final MimePolicyService mimePolicyService,
			final ShareService shareService,
			final EntryBusinessService entryBusinessService,
			final AuditLogEntryService auditLogEntryService,
			final ThreadService threadService,
			final WorkGroupNodeService workGroupNodeService,
			final SignatureService signatureService,
			final WorkGroupDocumentRevisionService revisionService) {
		super(accountService);
		this.documentEntryService = documentEntryService;
		this.mimePolicyService = mimePolicyService;
		this.shareService = shareService;
		this.signatureService = signatureService;
		this.entryBusinessService = entryBusinessService;
		this.auditLogEntryService = auditLogEntryService;
		this.threadService = threadService;
		this.workGroupNodeService = workGroupNodeService;
		this.revisionService = revisionService;
	}

	@Override
	public List<DocumentDto> findAll() throws BusinessException {
		User authUser = checkAuthentication();
		List<DocumentEntry> docs = documentEntryService.findAll(authUser, authUser);
		return ImmutableList.copyOf(Lists.transform(docs, DocumentDto.toDto()));
	}

	@Override
	public DocumentDto find(String uuid, boolean withShares) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required document uuid");
		User authUser = checkAuthentication();
		DocumentEntry entry = documentEntryService.find(authUser, authUser, uuid);
		DocumentDto documentDto = new DocumentDto(entry);
		List<ShareDto> shares = Lists.newArrayList();
		if (withShares) {
			for (AnonymousShareEntry share : entryBusinessService.findAllMyAnonymousShareEntries(authUser, entry)) {
				shares.add(ShareDto.getSentShare(share, false));
			}
			for (ShareEntry share : entryBusinessService.findAllMyShareEntries(authUser, entry)) {
				shares.add(ShareDto.getSentShare(share, false));
			}
		}
		Collections.sort(shares);
		documentDto.setShares(shares);
		return documentDto;
	}
	
	@Override
	public DocumentDto create(File tempFile, String fileName, String description, String metadata)
			throws BusinessException {
		Validate.notNull(tempFile, "Missing required file (check parameter named file)");
		User authUser = checkAuthentication();
		if ((authUser.isGuest() && !authUser.getCanUpload()))
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		DocumentEntry res = documentEntryService.create(authUser, authUser, tempFile, fileName, description, false, metadata);
		return new DocumentDto(res);
	}

	@Override
	public DocumentDto addDocumentXop(DocumentAttachement doca) throws BusinessException {
		File tempFile = null;
		try {
			User authUser = checkAuthentication();
			DataHandler dh = doca.getDocument();
			InputStream in = dh.getInputStream();
			String fileName = doca.getFilename();
			String comment = (doca.getComment() == null) ? "" : doca.getComment();
			tempFile = File.createTempFile("linshare-xop-", ".tmp");
			IOUtils.transferTo(in, tempFile);
			DocumentEntry res = documentEntryService.create(authUser, authUser, tempFile, fileName, comment, false, null);
			return new DocumentDto(res);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			deleteTempFile(tempFile);
			throw new BusinessException(BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
					"Can not generate temp file from input stream.");
		}
	}

	protected void deleteTempFile(File tempFile) {
		if (tempFile != null) {
			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception e) {
				logger.warn("Can not delete temp file : " + tempFile.getAbsolutePath());
				logger.debug(e.getMessage(), e);
			}
		}
	}

	@Override
	public InputStream getDocumentStream(String docEntryUuid) throws BusinessException {
		Validate.notEmpty(docEntryUuid, "Missing required document uuid");
		logger.debug("downloading for document : " + docEntryUuid);
		User authUser = checkAuthentication();
		return documentEntryService.getDocumentStream(authUser, authUser, docEntryUuid);
	}

	@Override
	public InputStream getThumbnailStream(String docEntryUuid, ThumbnailType kind)
			throws BusinessException {
		Validate.notEmpty(docEntryUuid, "Missing required document uuid");
		logger.debug("downloading thumbnail for document : " + docEntryUuid);
		User authUser = checkAuthentication();
		if (kind == null) {
			kind = ThumbnailType.MEDIUM;
		}
		return documentEntryService.getDocumentThumbnailStream(authUser, authUser, docEntryUuid, kind);
	}

	@Override
	public DocumentDto delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required document uuid");
		logger.debug("deleting for document : " + uuid);
		User authUser = checkAuthentication();
		DocumentEntry documentEntry = shareService.deleteAllShareEntries(authUser, authUser, uuid, LogActionCause.UNDEFINED);
		documentEntryService.delete(authUser, authUser, uuid);
		return new DocumentDto(documentEntry);
	}

	@Override
	public List<MimeTypeDto> getMimeTypes() throws BusinessException {
		User authUser = checkAuthentication();
		List<MimeTypeDto> res = Lists.newArrayList();
		Set<MimeType> mimeTypes = mimePolicyService.findAllMyMimeTypes(authUser);
		for (MimeType mimeType : mimeTypes) {
			if (mimeType.getEnable()) {
				res.add(new MimeTypeDto(mimeType, true));
			}
		}
		return res;
	}

	@Override
	public Boolean isEnableMimeTypes() throws BusinessException {
		User authUser = checkAuthentication();
		return documentEntryService.mimeTypeFilteringStatus(authUser);
	}

	@Override
	public DocumentDto update(String documentUuid, DocumentDto documentDto) throws BusinessException {

		Validate.notEmpty(documentUuid, "Missing required document uuid");
		Validate.notNull(documentDto, "Missing required DocumentDto");
		Validate.notEmpty(documentDto.getName(), "Missing required fileName");

		User authUser = checkAuthentication();
		return new DocumentDto(documentEntryService.updateFileProperties(authUser, authUser, documentUuid,
				documentDto.getName(), documentDto.getDescription(), documentDto.getMetaData()));
	}

	@Override
	public DocumentDto updateFile(File file, String givenFileName, String documentUuid) throws BusinessException {
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		Validate.notNull(file, "Missing required File stream");

		User authUser = checkAuthentication();
		return new DocumentDto(documentEntryService.update(authUser, authUser, documentUuid, file, givenFileName));
	}

	@Override
	public DocumentDto createWithSignature(File tempFile, String fileName, String description,
			InputStream signatureFile, String signatureFileName, InputStream x509) throws BusinessException {
		Validate.notNull(tempFile, "Missing required file (check parameter named file)");
		User authUser = checkAuthentication();
		if ((authUser.isGuest() && !authUser.getCanUpload()))
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		DocumentEntry res = documentEntryService.create(authUser, authUser, tempFile, fileName, description, false, null);
		if (signatureFile != null) {
			X509Certificate x509certificate = null;
			try {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				x509certificate = (X509Certificate) cf.generateCertificate(x509);
			} catch (CertificateException e) {
				throw new BusinessException(BusinessErrorCode.INVALID_INPUT_FOR_X509_CERTIFICATE,
						"unable to generate a X509 certificate", e);
			}
			signatureService.createSignature(authUser, res.getDocument(), signatureFile, signatureFileName,
					x509certificate);
		}

		documentEntryService.updateFileProperties(authUser, authUser, res.getUuid(), res.getName(), description, null);
		return new DocumentDto(res);
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String actorUuid, String uuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate) {
		Account authUser = checkAuthentication();
		User actor = (User) getActor(authUser, actorUuid);
		DocumentEntry entry = documentEntryService.find(authUser, actor, uuid);
		return auditLogEntryService.findAll(authUser, actor, entry.getUuid(), actions, types, beginDate, endDate);
	}

	@Override
	public List<DocumentDto> copy(String actorUuid, CopyDto copy, boolean deleteShare) throws BusinessException {
		Account authUser = checkAuthentication();
		User actor = (User) getActor(authUser, actorUuid);
		Validate.notNull(copy);
		TargetKind resourceKind = copy.getKind();
		Validate.notNull(resourceKind);
		String resourceUuid = copy.getUuid();
		Validate.notEmpty(resourceUuid);
		if (TargetKind.RECEIVED_SHARE.equals(resourceKind)) {
			// FIXME:if the current user do have enough space, there is side effect on audit.
			// Some audit traces will be created before quota checks ! :s
			ShareEntry share = shareService.findForDownloadOrCopyRight(authUser, actor, resourceUuid);
			CopyResource cr = new CopyResource(resourceKind, share);
			DocumentEntry newDocumentEntry = documentEntryService.copy(authUser, actor, cr);
			shareService.markAsCopied(authUser, actor, resourceUuid, new CopyMto(newDocumentEntry));
			if (deleteShare) {
				shareService.delete(authUser, actor, share, LogActionCause.COPY);
			}
			return Lists.newArrayList(new DocumentDto(newDocumentEntry));
		} else if (TargetKind.SHARED_SPACE.equals(resourceKind)) {
			String workgroupUuid = copy.getContextUuid();
			Validate.notEmpty(workgroupUuid, "Missing workgroup uuid");
			WorkGroup workGroup = threadService.find(authUser, actor, workgroupUuid);
			WorkGroupNode node = workGroupNodeService.findForDownloadOrCopyRight(authUser, actor, workGroup,
					resourceUuid);
			if (WorkGroupNodeType.DOCUMENT.equals(node.getNodeType())) {
				WorkGroupDocument wgDocument = (WorkGroupDocument) node;
				WorkGroupDocumentRevision mostRecent = (WorkGroupDocumentRevision) revisionService
						.findMostRecent(workGroup, node.getUuid());
				CopyResource cr = new CopyResource(resourceKind, workGroup, mostRecent);
				cr.setName(wgDocument.getName());
				DocumentEntry newDocumentEntry = documentEntryService.copy(authUser, actor, cr);
				workGroupNodeService.markAsCopied(authUser, actor, workGroup, wgDocument,
						new CopyMto(newDocumentEntry));
				return Lists.newArrayList(new DocumentDto(newDocumentEntry));
			}
			if (WorkGroupNodeType.DOCUMENT_REVISION.equals(node.getNodeType())) {
				WorkGroupDocument wgDocument = (WorkGroupDocument) node;
				CopyResource cr = new CopyResource(resourceKind, workGroup, wgDocument);
				DocumentEntry newDocumentEntry = documentEntryService.copy(authUser, actor, cr);
				workGroupNodeService.markAsCopied(authUser, actor, workGroup, wgDocument,
						new CopyMto(newDocumentEntry));
				return Lists.newArrayList(new DocumentDto(newDocumentEntry));
			}
		} else if (TargetKind.PERSONAL_SPACE.equals(resourceKind)) {
			DocumentEntry documentEntry = documentEntryService.findForDownloadOrCopyRight(authUser, actor, resourceUuid);
			CopyResource cr = new CopyResource(resourceKind, documentEntry);
			DocumentEntry newDocumentEntry = documentEntryService.copy(authUser, actor, cr);
			documentEntryService.markAsCopied(authUser, actor, documentEntry, new CopyMto(newDocumentEntry));
			return Lists.newArrayList(new DocumentDto(newDocumentEntry));
		}
		throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN, "This action is not supported.");
	}
}
