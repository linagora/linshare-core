/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.repository.hibernate;


import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DocumentEntryRepositoryImpl extends AbstractRepositoryImpl<DocumentEntry> implements DocumentEntryRepository {
	
	public DocumentEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DocumentEntry aDoc) {
		DetachedCriteria det = DetachedCriteria.forClass(DocumentEntry.class).add(Restrictions.eq( "uuid", aDoc.getUuid()) );
		return det;
	}
	
	 /** Find a document using its id.
     * @param id
     * @return found document (null if no document found).
     */
	@Override
    public DocumentEntry findById(String uuid) {
        List<DocumentEntry> entries = findByCriteria(Restrictions.eq("uuid", uuid));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }

	@Override
	public List<DocumentEntry> findAllMyDocumentEntries(Account owner) {
		List<DocumentEntry> entries = findByCriteria(Restrictions.eq("entryOwner", owner));
        if (entries == null) {
            return null;
        }
        return entries;
	}

	@Override
	public DocumentEntry create(DocumentEntry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public DocumentEntry update(DocumentEntry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}
}
