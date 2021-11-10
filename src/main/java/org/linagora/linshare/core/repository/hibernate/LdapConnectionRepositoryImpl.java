/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.RemoteServerRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class LdapConnectionRepositoryImpl extends
		AbstractRepositoryImpl<LdapConnection> implements
		RemoteServerRepository<LdapConnection> {

	public LdapConnectionRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public LdapConnection create(LdapConnection entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public LdapConnection update(LdapConnection entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(LdapConnection entity) {
		DetachedCriteria det = DetachedCriteria.forClass(LdapConnection.class)
				.add(Restrictions.eq("uuid", entity.getUuid()));
		return det;
	}

	@Override
	public boolean isUsed(LdapConnection ldapConnection) {
		DetachedCriteria det = DetachedCriteria
				.forClass(LdapUserProvider.class);
		det.add(Restrictions.eq("ldapConnection", ldapConnection));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(det)) > 0;
	}

	@Override
	public LdapConnection findByUuid(String uuid) {
		List<LdapConnection> conns = findByCriteria(Restrictions.eq("uuid",
				uuid));
		if (conns == null || conns.isEmpty()) {
			return null;
		} else if (conns.size() == 1) {
			return conns.get(0);
		} else {
			throw new IllegalStateException("Uuid must be unique");
		}
	}

}
