package org.linagora.linshare.core.domain.vo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.EntryTagAssociation;
import org.linagora.linshare.core.domain.entities.ThreadEntry;

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

}
