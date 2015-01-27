package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.EntryRepository;

public class EntryBusinessServiceImpl implements EntryBusinessService {

	private final EntryRepository entryRepository;

	public EntryBusinessServiceImpl(final EntryRepository entryRepository) {
		this.entryRepository = entryRepository;
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
}
