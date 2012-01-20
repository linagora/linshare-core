package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Functionality;
import org.springframework.orm.hibernate3.HibernateTemplate;

public interface FunctionalityRepository extends AbstractRepository<Functionality> {
	
	public Functionality findById(long id);
	
	public Functionality findById(AbstractDomain domain, String identifier);
	
	public HibernateTemplate getHibernateTemplate();
	
}
