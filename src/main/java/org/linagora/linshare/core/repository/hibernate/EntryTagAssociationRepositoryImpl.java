package org.linagora.linshare.core.repository.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.EntryTagAssociation;
import org.linagora.linshare.core.repository.EntryTagAssociationRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class EntryTagAssociationRepositoryImpl extends AbstractRepositoryImpl<EntryTagAssociation> implements EntryTagAssociationRepository {

	public EntryTagAssociationRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(EntryTagAssociation entity) {
		DetachedCriteria det = DetachedCriteria.forClass(EntryTagAssociation.class)
				.add(Restrictions.eq( "entry", entity.getEntry())) 
				.add(Restrictions.eq( "tag", entity.getTag()));
		return det;
	}

}
