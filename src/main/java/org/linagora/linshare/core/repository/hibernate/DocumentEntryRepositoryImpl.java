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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DocumentEntryRepositoryImpl extends AbstractRepositoryImpl<Entry> implements DocumentEntryRepository {
	
	public DocumentEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Entry aDoc) {
		DetachedCriteria det = DetachedCriteria.forClass(Entry.class)
		.add(Restrictions.eq( "id", aDoc.getId() ) );
		return det;
	}
	
	 /** Find a document using its id.
     * @param id
     * @return found document (null if no document found).
     */
	@Override
    public Entry findById(Long id) {
        List<Entry> entries = findByCriteria(Restrictions.eq("id", id));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }

	
//	
//	@Override
//	public Entry findByOwnerAndUuid(Account account, String uuid) {
//		List<Entry> entries = findByCriteria(Restrictions.eq("entryOwner", account),Restrictions.eq("document.identifier", uuid));
//        if (entries == null || entries.isEmpty()) {
//            return null;
//        } else if (entries.size() == 1) {
//            return entries.get(0);
//        } else {
//            throw new IllegalStateException("Id must be unique");
//        }
//	}

	@Override
	public Entry create(Entry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		return super.create(entity);
	}

	@Override
	public Entry update(Entry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}
	
}
