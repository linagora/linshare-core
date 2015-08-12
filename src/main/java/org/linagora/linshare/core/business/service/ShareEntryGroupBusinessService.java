package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;

public interface ShareEntryGroupBusinessService {

	public ShareEntryGroup create(ShareEntryGroup entity) throws BusinessException;

	public void delete(ShareEntryGroup shareEntryGroup)
			throws BusinessException;

	public ShareEntryGroup findById(long id) throws BusinessException;

	public ShareEntryGroup update(ShareEntryGroup shareEntryGroup)
			throws BusinessException;

	public List<ShareEntryGroup> findUndownloadedSharedDocToAlert();
}
