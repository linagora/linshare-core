package org.linagora.linshare.core.business.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.linagora.linshare.core.business.service.ShareEntryGroupBusinessService;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareEntryGroupBusinessServiceImpl
		implements ShareEntryGroupBusinessService {

	private final ShareEntryGroupRepository shareEntryGroupRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(ShareEntryGroupBusinessServiceImpl.class);

	public ShareEntryGroupBusinessServiceImpl(
			ShareEntryGroupRepository shareEntryGroupRepository) {
		super();
		this.shareEntryGroupRepository = shareEntryGroupRepository;
	}

	@Override
	public ShareEntryGroup create(ShareEntryGroup entity)
			throws BusinessException {
		return shareEntryGroupRepository.create(entity);
	}

	@Override
	public void delete(ShareEntryGroup shareEntryGroup)
			throws BusinessException {
		shareEntryGroupRepository.delete(shareEntryGroup);
	}

	@Override
	public ShareEntryGroup findById(long id) throws BusinessException {
		return shareEntryGroupRepository.findById(id);
	}

	@Override
	public ShareEntryGroup update(ShareEntryGroup shareEntryGroup)
			throws BusinessException {
		return shareEntryGroupRepository.update(shareEntryGroup);
	}

	@Override
	public List<ShareEntryGroup> findUndownloadedSharedDocToAlert() {
		List<ShareEntryGroup> all = shareEntryGroupRepository.findAllToNotify();
		List<ShareEntryGroup> undownloadedShareEntryGroup = new ArrayList<ShareEntryGroup>();
		for (ShareEntryGroup shareEntryGroup : all) {
			Iterator<ShareEntry> shareEntriesIterator = shareEntryGroup
					.getShareEntries().iterator();
			while (shareEntriesIterator.hasNext()) {
				if (shareEntriesIterator.next().getDownloaded() == 0) {
					undownloadedShareEntryGroup.add(shareEntryGroup);
					break;
				}
			}
			Iterator<AnonymousShareEntry> anonymousShareEntriesIterator = shareEntryGroup
					.getAnonymousShareEntries().iterator();
			while (anonymousShareEntriesIterator.hasNext()) {
				if (anonymousShareEntriesIterator.next().getDownloaded() == 0) {
					undownloadedShareEntryGroup.add(shareEntryGroup);
					break;
				}
			}
		}
		return undownloadedShareEntryGroup;
	}

}
