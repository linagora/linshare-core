package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.linagora.linshare.core.service.ShareService;

import com.google.common.collect.Sets;

public class ShareServiceImpl implements ShareService {

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final ShareExpiryDateService shareExpiryDateService;
	
	private final DocumentEntryService documentEntryService;

	public ShareServiceImpl(
			FunctionalityReadOnlyService functionalityReadOnlyService,
			ShareExpiryDateService shareExpiryDateService,
			DocumentEntryService documentEntryService) {
		super();
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.shareExpiryDateService = shareExpiryDateService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public List<ShareEntry> create(Account actor, User owner,
			ShareContainer shareContainer) throws BusinessException {

		// Check rights.
//		if (!(actor.hasSuperAdminRole() || actor.hasSystemAccountRole())) {
//			
//		}

		// Check documents
		transformDocuments(actor, owner, shareContainer);
		shareContainer.updateEncryptedStatus();

		// Check functionnalities

		// Check recipients

		// TODO Auto-generated method stub
		return null;
	}

	private void transformDocuments(Account actor, User owner, ShareContainer shareContainer) throws BusinessException {
		Set<DocumentEntry> documents = Sets.newHashSet();
		for (DocumentEntry de: shareContainer.getDocuments()) {
			documents.add(documentEntryService.find(actor, owner, de.getUuid()));
		}
		shareContainer.setDocuments(documents);
	}
}
