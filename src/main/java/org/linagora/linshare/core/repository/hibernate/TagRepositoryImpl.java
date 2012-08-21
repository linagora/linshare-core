package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.repository.TagRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class TagRepositoryImpl extends AbstractRepositoryImpl<Tag>  implements TagRepository {

	public TagRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}


	@Override
	public Tag findById(Long id) {
		List<Tag> tags = findByCriteria(Restrictions.eq("id", id).ignoreCase());
        if (tags == null || tags.isEmpty()) {
            return null;
        } else if (tags.size() == 1) {
            return tags.get(0);
        } else {
            throw new IllegalStateException("Mail must be unique");
        }
	}
	

	@Override
	public Tag findByOwnerAndName(Account owner, String name) {
		List<Tag> results = findByCriteria(Restrictions.eq("owner", owner),Restrictions.eq("name", name));
		if (results == null || results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new IllegalStateException("Tag must be unique");
        }
	}

	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Tag entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Tag.class).add(Restrictions.eq("name", entity.getName())).add(Restrictions.eq("owner", entity.getOwner()));
		return det;
	}

}
