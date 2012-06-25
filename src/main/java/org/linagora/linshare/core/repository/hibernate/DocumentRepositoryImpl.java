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


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DocumentRepositoryImpl extends AbstractRepositoryImpl<Document> implements
		DocumentRepository {

	
	
	public DocumentRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Document aDoc) {
		DetachedCriteria det = DetachedCriteria.forClass( Document.class )
		.add(Restrictions.eq( "identifier", aDoc.getIdentifier() ) );
		return det;
	}
	
	
	 /** Find a document using its id.
     * @param id
     * @return found document (null if no document found).
     */
    public Document findById(String identifier) {
        List<Document> documents = findByCriteria(Restrictions.eq("identifier", identifier));
        if (documents == null || documents.isEmpty()) {
            return null;
        } else if (documents.size() == 1) {
            return documents.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }



}
