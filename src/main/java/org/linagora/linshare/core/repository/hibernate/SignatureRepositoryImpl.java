package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.repository.SignatureRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class SignatureRepositoryImpl  extends AbstractRepositoryImpl<Signature> implements SignatureRepository {


	public SignatureRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Signature sig) {
		DetachedCriteria det = DetachedCriteria.forClass(Signature.class).add(Restrictions.eq( "uuid", sig.getUuid() ) );
		return det;
	}
	
	
	@Override
    public Signature findByUuid(String uuid) {
        List<Signature> sigs = findByCriteria(Restrictions.eq("uuid", uuid));
        if (sigs == null || sigs.isEmpty()) {
            return null;
        } else if (sigs.size() == 1) {
            return sigs.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }
}
