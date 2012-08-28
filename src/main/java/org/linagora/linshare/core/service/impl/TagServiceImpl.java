package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.TagBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.TagFilter;
import org.linagora.linshare.core.domain.entities.TagFilterRule;
import org.linagora.linshare.core.domain.entities.TagFilterRuleTagAssociation;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagServiceImpl implements TagService {

	private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);
	
	private final TagBusinessService tagBusinessService;

	public TagServiceImpl(TagBusinessService tagBusinessService) {
		super();
		this.tagBusinessService = tagBusinessService;
	}

	
	@Override
	public Tag findByOwnerAndName(Account actor, Thread owner, String name) throws BusinessException {
		// TODO : check if the actor is a thread member
		return tagBusinessService.findByOwnerAndName(owner, name);
	}
	

	@Override
	public Tag findByOwnerAndName(User owner, String name) throws BusinessException {
		Tag tag = tagBusinessService.findByOwnerAndName(owner, name);
		if(!tag.getOwner().equals(owner)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get this tag.");
		}
		return tag;
	}

	
	@Override
	public void setTagToThreadEntries(Account actor, Thread owner, List<ThreadEntry> threadEntries, String tagName, String value) throws BusinessException {
		// TODO : check if the actor is a thread member
		Tag tag = findByOwnerAndName(actor, owner, tagName);
		if(tag != null) {
			for (ThreadEntry threadEntry : threadEntries) {
				tagBusinessService.setTagToThreadEntry(owner, threadEntry, tag, value);
			}
		} else {
			logger.error("tag was noty found : "  + tagName);
		}
	}

}
