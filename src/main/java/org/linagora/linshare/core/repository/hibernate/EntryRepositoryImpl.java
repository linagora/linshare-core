package org.linagora.linshare.core.repository.hibernate;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.EntryRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class EntryRepositoryImpl extends AbstractRepositoryImpl<Entry> implements EntryRepository {

	public EntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Entry aDoc) {
		DetachedCriteria det = DetachedCriteria.forClass(Entry.class).add(Restrictions.eq( "uuid", aDoc.getUuid()) );
		return det;
	}
	
	 /** Find a document using its id.
     * @param id
     * @return found document (null if no document found).
     */
	@Override
    public Entry findById(String uuid) {
        List<Entry> entries = findByCriteria(Restrictions.eq("uuid", uuid));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }

	
	@Override
	public Entry create(Entry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public Entry update(Entry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}
	
	@Override
	public List<Entry> getOutdatedEntry() {
		return findByCriteria(Restrictions.lt("expirationDate", Calendar.getInstance()));
	}
}
