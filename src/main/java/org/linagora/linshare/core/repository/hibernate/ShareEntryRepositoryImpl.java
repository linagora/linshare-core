package org.linagora.linshare.core.repository.hibernate;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ShareEntryRepositoryImpl extends AbstractRepositoryImpl<ShareEntry> implements ShareEntryRepository  {

	public ShareEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ShareEntry share) {
		DetachedCriteria det = DetachedCriteria.forClass(ShareEntry.class).add(Restrictions.eq( "uuid", share.getUuid()) );
		return det;
	}
	
	 /** Find a document using its id.
     * @param id
     * @return found document (null if no document found).
     */
	@Override
    public ShareEntry findById(String uuid) {
        List<ShareEntry> entries = findByCriteria(Restrictions.eq("uuid", uuid));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }

	
	@Override
	public ShareEntry create(ShareEntry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}
	

	@Override
	public ShareEntry update(ShareEntry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}
	
	
	@Override
	public ShareEntry getShareEntry(DocumentEntry documentEntry, User sender, User recipient) {
		
		List<ShareEntry> results = findByCriteria(Restrictions.eq("documentEntry", documentEntry),Restrictions.eq("entryOwner", sender),Restrictions.eq("recipient", recipient));
		if (results == null || results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new IllegalStateException("Sharing must be unique");
        }
	}
	
	
	@Override
	public List<ShareEntry> findAllMyShareEntries(User owner) {
		List<ShareEntry> entries = findByCriteria(Restrictions.eq("recipient", owner));
        if (entries == null) {
            return null;
        }
        return entries;
	}

}
