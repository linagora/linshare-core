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
package org.linagora.linShare.core.service.impl;

import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.ParameterRepository;
import org.linagora.linShare.core.service.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Services for admin general parameters
 */
public class ParameterServiceImpl implements ParameterService {

    Logger logger = LoggerFactory.getLogger(ParameterServiceImpl.class);
    /** parameter repository. */
    private final ParameterRepository parameterRepository;

    
    public ParameterServiceImpl(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }


	public void createConfig(Parameter params) throws BusinessException {
		parameterRepository.createConfig(params);
	}


	public Parameter loadConfig()  {
		return parameterRepository.loadConfig();
	}
}
