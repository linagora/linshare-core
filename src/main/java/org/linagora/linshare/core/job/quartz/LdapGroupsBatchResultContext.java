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
package org.linagora.linshare.core.job.quartz;

import java.util.Map;

import org.linagora.linshare.core.domain.constants.LdapBatchMetaDataType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;

import com.google.common.collect.Maps;

public class LdapGroupsBatchResultContext extends DomainBatchResultContext {

	private Map<LdapBatchMetaDataType, Integer> resultStats = Maps.newHashMap();

	public LdapGroupsBatchResultContext(AbstractDomain resource) {
		super(resource);
		this.identifier = resource.getUuid();
		for (LdapBatchMetaDataType metaDataType : LdapBatchMetaDataType.values()) {
			resultStats.put(metaDataType, 0);
		}
	}

	public Map<LdapBatchMetaDataType, Integer> getResultStats() {
		return resultStats;
	}

	public void setResultStats(Map<LdapBatchMetaDataType, Integer> resultStats) {
		this.resultStats = resultStats;
	}

	public void add(LdapBatchMetaDataType metaDataType) {
		// Increment the stats for the given identifier
		this.resultStats.compute(metaDataType, (key, val) -> (val == null) ? 1 : val + 1);
	}
}
