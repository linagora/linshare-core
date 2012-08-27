package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.TagBusinessService;
import org.linagora.linshare.core.domain.constants.TagType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.EntryTagAssociation;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.TagEnum;
import org.linagora.linshare.core.domain.entities.TagEnumValue;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.EntryTagAssociationRepository;
import org.linagora.linshare.core.repository.TagRepository;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagBusinessServiceImpl implements TagBusinessService {
	
	private static final Logger logger = LoggerFactory.getLogger(TagBusinessServiceImpl.class);
	
	private final TagRepository tagRepository;
	private final ThreadEntryRepository threadEntryRepository;
	private final EntryTagAssociationRepository entryTagAssociationRepository;
	

	public TagBusinessServiceImpl(TagRepository tagRepository, ThreadEntryRepository threadEntryRepository, EntryTagAssociationRepository entryTagAssociationRepository) {
		super();
		this.tagRepository = tagRepository;
		this.threadEntryRepository = threadEntryRepository;
		this.entryTagAssociationRepository = entryTagAssociationRepository;
	}

	@Override
	public Tag findByOwnerAndName(Account owner, String name) throws BusinessException {
		Tag tag = tagRepository.findByOwnerAndName(owner, name);
		if(tag == null) {
			logger.error("Can't find tag with name " + name);
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT, "Can't find tag with name " + name);
		}
		return tag;
	}

	@Override
	public void setTagToThreadEntry(Thread owner, ThreadEntry threadEntry, Tag tag, String optionalValue) throws BusinessException {

		logger.debug("association between tag '" + tag.getName()+ "' and '" + threadEntry.getName() + "' with value : " + optionalValue);
		EntryTagAssociation tagAssociation = new EntryTagAssociation(threadEntry,tag);
		
		if(tag.getTagType().equals(TagType.ENUM)) {
			
			if(((TagEnum)tag).getNotNull()) {
				if(optionalValue != null) {
					tagAssociation.setTagEnumValue(new TagEnumValue(optionalValue));
				} else {
					logger.error("Can't make the previous association !");
				}
			} else {
				if(optionalValue != null) {
					tagAssociation.setTagEnumValue(new TagEnumValue(optionalValue));
				}
			}
		}
		
		entryTagAssociationRepository.create(tagAssociation);		
		threadEntry.getTagAssociations().add(tagAssociation);
		threadEntryRepository.update(threadEntry);
	}
	
	
}
