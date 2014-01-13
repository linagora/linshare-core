package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.repository.InternalRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class InternalRepositoryImpl extends GenericUserRepositoryImpl<Internal> implements InternalRepository {

	public InternalRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Internal findByLogin(String login) {
		Internal u = null;
		try {
			u = super.findByMail(login);
		} catch (IllegalStateException e) {
			logger.error("you are looking for an account using mail as login : '"
					+ login
					+ "' but your login is not unique, same account logins in different domains.");;
			logger.debug("error: " + e.getMessage());
			throw e;
		}

		if (u == null) {
			try {
				u = findByLdapUid(login);
			} catch (IllegalStateException e) {
				logger.error("you are looking for an account using LDAP uid as login : '"
						+ login
						+ "' but your login is not unique, same account logins in different domains.");;
						throw e;
			}
		}
		return u;
	}

	private Internal findByLdapUid(String ldapUid) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("ldapUid", ldapUid).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", false));
		List<Internal> users = findByCriteria(criteria);

		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new IllegalStateException("Ldap uid must be unique");
		}
	}

	@Override
	public Internal findByLoginAndDomain(String domain, String login) {
		Internal u = super.findByMailAndDomain(domain, login);
		if (u == null) {
			u = findByDomainAndLdapUid(domain, login);
		}
		return u;
	}

	private Internal findByDomainAndLdapUid(String domain, String ldapUid) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.identifier", domain));
		criteria.add(Restrictions.eq("ldapUid", ldapUid).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", false));
		List<Internal> users = findByCriteria(criteria);

		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new IllegalStateException("Ldap uid must be unique");
		}
	}

}
