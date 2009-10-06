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
package org.linagora.linShare.core.Facade.impl;

import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.domain.transformers.impl.ParameterTransformer;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.ParameterService;

/** Facade entry for parameter application management.
 */
public class ParameterFacadeImpl implements ParameterFacade {

    /** parameter repository. */
    private ParameterService parameterservice;
    
	private ParameterTransformer parameterTransformer;
    

    public ParameterFacadeImpl(ParameterService parameterservice) {
        this.parameterservice = parameterservice;
        this.parameterTransformer =  new ParameterTransformer();
    }

	public void createConfig(ParameterVo parameterVo) throws BusinessException {
		parameterservice.createConfig(parameterVo.getParameter());
	}

	public ParameterVo loadConfig() throws BusinessException {
		return new ParameterVo(parameterservice.loadConfig());
	}
}
