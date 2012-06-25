package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class FunctionalityRepositoryImpl extends AbstractRepositoryImpl<Functionality> implements FunctionalityRepository {

	public FunctionalityRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Functionality findById(long id) {
		List<Functionality> fonc = findByCriteria(Restrictions.eq("id", id));
		if (fonc == null || fonc.isEmpty()) {
			return null;
		} else if (fonc.size() == 1) {
			return fonc.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}
	
	@Override
	public Functionality findById(AbstractDomain domain, String identifier) {
		
		List<Functionality> fonc = findByCriteria(Restrictions.and(Restrictions.eq("domain", domain), Restrictions.eq("identifier", identifier)));
		
		if (fonc == null || fonc.isEmpty()) {
			return null;
		} else if (fonc.size() == 1) {
			return fonc.get(0);
		} else {
			throw new IllegalStateException("the Identifier and domain couple must be unique");
		}
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Functionality entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Functionality.class).add(
				Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public HibernateTemplate getHibernateTemplate() {
		return super.getHibernateTemplate();
	}
	
}
