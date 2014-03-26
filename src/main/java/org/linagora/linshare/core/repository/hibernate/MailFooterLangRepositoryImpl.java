package org.linagora.linshare.core.repository.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.repository.MailFooterLangRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MailFooterLangRepositoryImpl extends
		AbstractRepositoryImpl<MailFooterLang> implements
		MailFooterLangRepository {

	public MailFooterLangRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailFooterLang entity) {
		return DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("id", entity.getId()));
	}

	@Override
	public boolean isMailFooterReferenced(MailFooter footer) {
		return !findByCriteria(Restrictions.eq("mailFooter", footer)).isEmpty();
	}
}
