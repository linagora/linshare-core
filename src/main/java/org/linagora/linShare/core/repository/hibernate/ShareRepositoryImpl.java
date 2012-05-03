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
package org.linagora.linShare.core.repository.hibernate;

import java.util.Calendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.repository.ShareRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ShareRepositoryImpl extends AbstractRepositoryImpl<Share> implements ShareRepository {

	public ShareRepositoryImpl(HibernateTemplate hibernateTemplate) {
        super(hibernateTemplate);
    }

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Share entity) {
		DetachedCriteria det = DetachedCriteria.forClass( Share.class )
		.add(Restrictions.eq( "sender", entity.getSender() ))
		.add(Restrictions.eq( "receiver", entity.getReceiver() ))
		.add(Restrictions.eq( "document", entity.getDocument() ));
		
		return det;
	}

	public Share getShare(Document shareDocument, User sender, User recipient) {
			
		List<Share> results = findByCriteria(Restrictions.eq("document", shareDocument),Restrictions.eq("sender", sender),Restrictions.eq("receiver", recipient));
		if (results == null || results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new IllegalStateException("Sharing must be unique");
        }
	}

	public List<Share> getSharesLinkedToDocument(Document doc) {
		return findByCriteria(Restrictions.eq("document", doc));
	}

    public List<Share> getOutdatedShares() {
        return findByCriteria(Restrictions.lt("expirationDate", Calendar.getInstance()));
    }
    
    public List<Share> getUpcomingOutdatedShares(Integer date) {
    	Calendar calMin = Calendar.getInstance();
    	calMin.add(Calendar.DAY_OF_MONTH, date);
    	
    	Calendar calMax = Calendar.getInstance();
    	calMax.add(Calendar.DAY_OF_MONTH, date+1);
        
    	return findByCriteria(Restrictions.lt("expirationDate", calMax), Restrictions.gt("expirationDate", calMin));
    }

	@Override
	public Share getShare(long persistenceId) {
		List<Share> results = findByCriteria(Restrictions.eq("persistenceId", persistenceId));
		if (results == null || results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new IllegalStateException("Sharing must be unique");
        }
	}
}
