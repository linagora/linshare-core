/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DomainAccessPolicy {

	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	private Date creationDate;

	private Date modificationDate;

	private List<DomainAccessRule> rules;

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public DomainAccessPolicy() {
		super();
	}

	public DomainAccessPolicy(long persistenceId) {
		super();
		this.persistenceId = persistenceId;
		this.rules = new ArrayList<DomainAccessRule>();
	}

	public DomainAccessPolicy(DomainAccessPolicy policy) {
		super();
		this.persistenceId = policy.getPersistenceId();
		this.rules = policy.getRules();
	}

	public List<DomainAccessRule> getRules() {
		return rules;
	}

	public void setRules(List<DomainAccessRule> rules) {
		this.rules = rules;
	}

	public void addRule(DomainAccessRule rule) {
		if (this.rules == null) {
			this.rules = new ArrayList<DomainAccessRule>();
		}
		this.rules.add(rule);
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

}
