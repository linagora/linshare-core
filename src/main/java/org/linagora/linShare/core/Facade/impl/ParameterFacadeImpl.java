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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.ParameterRepository;
import org.linagora.linShare.core.service.ParameterService;
import org.linagora.linShare.core.utils.AESCrypt;

/** Facade entry for parameter application management.
 */
public class ParameterFacadeImpl implements ParameterFacade {

    /** parameter repository. */
    private ParameterService parameterservice;
    
    
	private ParameterRepository parameterRepository;
    

    public ParameterFacadeImpl(ParameterService parameterservice,
    		ParameterRepository parameterRepository) {
        this.parameterservice = parameterservice;
        this.parameterRepository =  parameterRepository;
    }

	public ParameterVo saveOrUpdate(ParameterVo parameterVo) throws BusinessException {
//		Parameter param = parameterservice.loadConfig(parameterVo.getIdentifier());
//		if (param == null) {
//			parameterRepository.create(parameterVo.getParameter());
//		} else {
//			param.updateFrom(parameterVo);
//			parameterRepository.saveOrUpdate(param);
//		}
//		return new ParameterVo(parameterservice.loadConfig(parameterVo.getIdentifier()));
		return new ParameterVo(parameterservice.saveOrUpdate(parameterVo.getParameter()));
	}

	public ParameterVo loadConfig(String identifier) throws BusinessException {
		return new ParameterVo(parameterservice.loadConfig(identifier));
	}

	public boolean checkPlatformEncryptSupportedAlgo() {
		
			//test encrypt aes 256
		
			boolean res = true;
			AESCrypt aes;

			try {
				aes = new AESCrypt(false, "password");
				aes.encrypt(2, new ByteArrayInputStream("test".getBytes()),	new ByteArrayOutputStream());
				
			} catch (UnsupportedEncodingException e) {
				res =  false;
			} catch (GeneralSecurityException e) {
				res =  false;
			} catch (IOException e) {
				res =  false;
			} catch (Error err) {
				res = false;
			}

			return res;
		}
}
