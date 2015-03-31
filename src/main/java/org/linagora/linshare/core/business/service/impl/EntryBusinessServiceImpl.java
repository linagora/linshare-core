package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.EntryRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;

public class EntryBusinessServiceImpl implements EntryBusinessService {

	private final EntryRepository entryRepository;

	private final ShareEntryRepository shareEntryRepository;

	private final AnonymousShareEntryRepository anonymousShareEntryRepository;

	public EntryBusinessServiceImpl(EntryRepository entryRepository,
			ShareEntryRepository shareEntryRepository,
			AnonymousShareEntryRepository anonymousShareEntryRepository) {
		super();
		this.entryRepository = entryRepository;
		this.shareEntryRepository = shareEntryRepository;
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
	}

	@Override
	public Entry find(String entryUuid) throws BusinessException {
		Validate.notEmpty(entryUuid, "The entry uiid is required.");
		return entryRepository.findById(entryUuid);
	}

	@Override
	public List<Entry> findAllMyShareEntries(User owner) {
		Validate.notNull(owner, "The owner is required.");
		return entryRepository.findAllMyShareEntries(owner);
	}

	@Override
	public List<ShareEntry> findAllMyShareEntries(User owner,
			DocumentEntry entry) {
		return shareEntryRepository.findAllMyShareEntries(owner, entry);
	}

	@Override
	public List<AnonymousShareEntry> findAllMyAnonymousShareEntries(User owner,
			DocumentEntry entry) {
		return anonymousShareEntryRepository.findAllMyAnonymousShareEntries(owner, entry);
	}
}
