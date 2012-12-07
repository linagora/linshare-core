package org.linagora.linshare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainPatternRepositoryImpl extends
		AbstractRepositoryImpl<DomainPattern> implements
		DomainPatternRepository {

	public DomainPatternRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainPattern entity) {
		DetachedCriteria det = DetachedCriteria.forClass( DomainPattern.class )
		.add(Restrictions.eq( "identifier", entity.getIdentifier() ) );
		return det;
	}

	@Override
	public DomainPattern findById(String identifier) {
		List<DomainPattern> patterns = findByCriteria(Restrictions.eq("identifier", identifier));

		if (patterns == null || patterns.isEmpty()) {
			return null;
		} else if (patterns.size() == 1) {
			return patterns.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DomainPattern> findAllSystemDomainPattern() {
		return (List<DomainPattern>) getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from DomainPattern d where d.system = 'true'");
						return query.setCacheable(true).list();

					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DomainPattern> findAllUserDomainPattern() {
		return (List<DomainPattern>) getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from DomainPattern d where d.system = 'false'");
						return query.setCacheable(true).list();

					}
				});
	}

}
