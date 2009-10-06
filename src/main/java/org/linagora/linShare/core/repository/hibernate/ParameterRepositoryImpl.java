/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.ParameterRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ParameterRepositoryImpl extends AbstractRepositoryImpl<Parameter> implements ParameterRepository {

    public ParameterRepositoryImpl(HibernateTemplate hibernateTemplate) {
        super(hibernateTemplate);
    }

    @Override
    protected DetachedCriteria getNaturalKeyCriteria(Parameter param) {
        DetachedCriteria det = DetachedCriteria.forClass(Parameter.class).add(Restrictions.eq("id", param.getId()));
        return det;
    }

	public Parameter loadConfig() {
		
		Parameter p = null;
		
		List<Parameter> paramsinDb = findByCriteria(Restrictions.eq("id", Parameter.CONSTANT_KEY_ID));
		
		if(paramsinDb!=null && paramsinDb.size()>0) 
		p = paramsinDb.get(0);
		else p = new Parameter();
		
		return p;
	}
	
	public void createConfig(Parameter params) throws  BusinessException {
		getHibernateTemplate().saveOrUpdate(params);
	}

} 
