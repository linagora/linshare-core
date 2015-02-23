package org.linagora.linshare.core.domain.entities;

import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.CustomisationType;
import org.linagora.linshare.core.domain.constants.Language;

public class Customisation {

	private long id;

	private String uuid;

	private String name;

	private String description;

	private java.util.Date creationDate;

	private java.util.Date modificationDate;

	private CustomisationType customType;

	private Set<AbstractDomain> abstractDomain;

	private Map<Language, CustomisationEntry> customisationEntries;

	public Customisation() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public java.util.Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(java.util.Date creationDate) {
		this.creationDate = creationDate;
	}

	public java.util.Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(java.util.Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public CustomisationType getCustomType() {
		return customType;
	}

	public void setCustomType(CustomisationType customType) {
		this.customType = customType;
	}

	public Set<AbstractDomain> getAbstractDomain() {
		return abstractDomain;
	}

	public void setAbstractDomain(Set<AbstractDomain> abstractDomain) {
		this.abstractDomain = abstractDomain;
	}

	public Map<Language, CustomisationEntry> getCustomisationEntries() {
		return customisationEntries;
	}

	public void setCustomisationEntries(
			Map<Language, CustomisationEntry> customisationEntries) {
		this.customisationEntries = customisationEntries;
	}
}
