package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.ThreadView;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadViewAsso;
import org.linagora.linshare.core.repository.ThreadViewRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ThreadViewRepositoryImpl extends AbstractRepositoryImpl<ThreadView> implements ThreadViewRepository {

	public ThreadViewRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ThreadView entity) {
		return DetachedCriteria.forClass(ThreadView.class).add(Restrictions.eq("id", entity.getId()));
	}

	@Override
	public List<ThreadView> findAllThreadView(Thread thread) {
		return findByCriteria(Restrictions.eq("thread", thread));
	}

	@Override
	public ThreadView findById(String id) {
		List<ThreadView> entries = findByCriteria(Restrictions.eq("id", id));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
	}
	
	@Override
	public List<ThreadViewAsso> findThreadViewAsso(ThreadView threadView) {
		List<ThreadViewAsso> res = null;
		
		// TODO FIXME XXX
		return res;
	}
}
