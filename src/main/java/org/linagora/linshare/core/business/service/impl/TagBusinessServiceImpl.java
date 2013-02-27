/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.business.service.impl;

import java.util.Set;

import org.linagora.linshare.core.business.service.TagBusinessService;
import org.linagora.linshare.core.domain.constants.TagType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.EntryTagAssociation;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.TagEnum;
import org.linagora.linshare.core.domain.entities.TagEnumValue;
import org.linagora.linshare.core.domain.entities.TagFilter;
import org.linagora.linshare.core.domain.entities.TagFilterRule;
import org.linagora.linshare.core.domain.entities.TagFilterRuleTagAssociation;
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
	

	@Override
	public void deleteAllTagAssociationsFromThreadEntry(ThreadEntry threadEntry) throws BusinessException {
		logger.debug("Deleting all tags from Thread Entry : " + threadEntry.getName() + " (" + threadEntry.getId() + ")");
		Set<EntryTagAssociation> tagAssociations = threadEntry.getTagAssociations();
		for (EntryTagAssociation tagAssociation : tagAssociations) {
			tagAssociation.setTagEnumValue(null);
			entryTagAssociationRepository.update(tagAssociation);
			entryTagAssociationRepository.delete(tagAssociation);
		}
	}
	

	@Override
	public void runTagFiltersOnThreadEntry(Account actor, Thread owner, ThreadEntry threadEntry) throws BusinessException {
		logger.debug("running tags filters on thread entry : " + threadEntry.getName());
		
		Set<TagFilter> tagFilters = owner.getTagFilters();
		for (TagFilter tagFilter : tagFilters) {
			logger.debug("tag filter name :" + tagFilter.getName());
			
			Set<TagFilterRule> rules = tagFilter.getRules();
			for (TagFilterRule tagFilterRule : rules) {
				
				if(tagFilterRule.isTrue(actor)) {
					logger.debug("tagFilterRule ok : " + tagFilterRule.getId());
					
					Set<TagFilterRuleTagAssociation> tagFilterRuleTagAssociations = tagFilterRule.getTagFilterRuleTagAssociation();
					for (TagFilterRuleTagAssociation tagFilterRuleTagAssociation : tagFilterRuleTagAssociations) {

						Tag tag = tagFilterRuleTagAssociation.getTag();
						logger.debug("current tag is : " + tag.getName());
						String value = null;
						if(tag.getTagType().equals(TagType.ENUM)) {
							if(tagFilterRuleTagAssociation.getTagEnumValue()== null) {
								logger.error("tagFilterRuleTagAssociation.getTagEnumValue() is null");
							}
							value = tagFilterRuleTagAssociation.getTagEnumValue().getValue();
						}
						
						setTagToThreadEntry(owner, threadEntry, tag, value);	
					}
				}
			}
		}
	}
}
