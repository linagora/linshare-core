package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.repository.AbstractDomainRepository;

public class DomainBusinessServiceImpl implements DomainBusinessService {

    private AbstractDomainRepository repository;

    public DomainBusinessServiceImpl(AbstractDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public AbstractDomain findById(String identifier) {
        return repository.findById(identifier);
    }
}
