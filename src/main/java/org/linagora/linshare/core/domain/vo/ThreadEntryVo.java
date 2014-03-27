/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
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
package org.linagora.linshare.core.domain.vo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.EntryTagAssociation;
import org.linagora.linshare.core.domain.entities.ThreadEntry;

import com.google.common.base.Function;

public class ThreadEntryVo extends DocumentVo {

	private static final long serialVersionUID = -8188541199713836570L;
	
	private List<TagVo> tags = new ArrayList<TagVo>();

	public ThreadEntryVo(String identifier, String name, String fileComment, Calendar creationDate, Calendar expirationDate, String type, String ownerLogin, Boolean encrypted, Long size) {
		super(identifier, name, fileComment, creationDate, expirationDate, type, ownerLogin, encrypted, false, size);
	}

	public ThreadEntryVo(ThreadEntry threadEntry) {
		super(threadEntry.getUuid(), threadEntry.getName(), threadEntry.getComment(), threadEntry.getCreationDate(), threadEntry.getExpirationDate(), threadEntry.getType(), threadEntry.getEntryOwner().getLsUuid(), threadEntry.getCiphered(), false, threadEntry.getSize());
		Set<EntryTagAssociation> tagAssociations = threadEntry.getTagAssociations();
		if(tagAssociations != null) {
			for (EntryTagAssociation entryTagAssociation : tagAssociations) {
				this.addTag(new TagVo(entryTagAssociation));
			}
		}
	}

	public List<TagVo> getTags() {
		return tags;
	}

	public void setTags(List<TagVo> tags) {
		this.tags = tags;
	}

	public void addTag(TagVo tag) {
		this.tags.add(tag);
	}

	/*
	 * Transformers
	 */
	public static Function<ThreadEntry, ThreadEntryVo> toVo() {
		return new Function<ThreadEntry, ThreadEntryVo>() {
			@Override
			public ThreadEntryVo apply(ThreadEntry arg0) {
				return new ThreadEntryVo(arg0);
			}
		};
	}
}
