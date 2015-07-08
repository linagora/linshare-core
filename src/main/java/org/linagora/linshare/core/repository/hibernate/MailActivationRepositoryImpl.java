package org.linagora.linshare.core.repository.hibernate;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.repository.MailActivationRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.google.common.collect.Sets;

public class MailActivationRepositoryImpl extends AbstractRepositoryImpl<MailActivation> implements MailActivationRepository {

	public MailActivationRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailActivation entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public MailActivation findById(long id) {
		return DataAccessUtils.singleResult(findByCriteria(
				Restrictions.eq("id", id)));
	}

	@Override
	public MailActivation findByDomain(AbstractDomain domain, String identifier) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", domain));
		det.add(Restrictions.eq("identifier", identifier));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public Set<MailActivation> findAll(AbstractDomain domain) {
		List<MailActivation> ma = findByCriteria(Restrictions.eq("domain", domain));
		Set<MailActivation> ret = Sets.newHashSet();
		ret.addAll(ma);
		return ret;
	}
}
