package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.AbstractDomain;

public interface DomainBusinessService {
    public AbstractDomain findById(String identifier);
}
