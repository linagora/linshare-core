package org.linagora.linshare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AbstractDomainRepositoryImpl extends AbstractRepositoryImpl<AbstractDomain> implements AbstractDomainRepository {

	public AbstractDomainRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AbstractDomain entity) {
		DetachedCriteria det = DetachedCriteria.forClass(AbstractDomain.class).add(
				Restrictions.eq("identifier", entity.getIdentifier()));
		return det;
	}

	public AbstractDomain findById(String identifier) {
		List<AbstractDomain> abstractDomain = findByCriteria(Restrictions.eq("identifier", identifier));
		
		if (abstractDomain == null || abstractDomain.isEmpty()) {
			return null;
		} else if (abstractDomain.size() == 1) {
			return abstractDomain.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllDomainIdentifiers() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<String>>() {
					public List<String> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d.identifier from AbstractDomain d order by d.authShowOrder asc");
						return query.setCacheable(true).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d order by d.authShowOrder asc");
						return query.setCacheable(true).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllTopAndSubDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d where TYPE = " + DomainType.TOPDOMAIN.toInt()
								+ " or TYPE = " + DomainType.SUBDOMAIN.toInt());
						return query.setCacheable(true).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllTopDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d where TYPE = " + DomainType.TOPDOMAIN.toInt());
						return query.setCacheable(true).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllSubDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d where TYPE = " + DomainType.SUBDOMAIN.toInt());
						return query.setCacheable(true).list();
					}
				});
	}

	@Override
	public RootDomain getUniqueRootDomain() throws BusinessException {
		RootDomain domain = (RootDomain) this.findById(LinShareConstants.rootDomainIdentifier);
		if(domain == null) {
			throw new BusinessException(BusinessErrorCode.DATABASE_INCOHERENCE_NO_ROOT_DOMAIN,"No root domain found in the database.");
		}
		return domain;
	}

	
}
