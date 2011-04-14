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
package org.linagora.linShare.core.domain.transformers.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.transformers.Transformer;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;

public class ParameterTransformer implements Transformer<Parameter, ParameterVo>{

	public Parameter assemble(ParameterVo valueObject) {
		
		
		Parameter parameter=new Parameter();
		
		if(null!=valueObject){
			
			try {
				
				BeanUtils.copyProperties(parameter,valueObject);
			} catch (IllegalAccessException e) {
				throw new TechnicalException(TechnicalErrorCode.BEAN_ERROR,"error with ParameterVo to Parameter transformation");
			} catch (InvocationTargetException e) {
				throw new TechnicalException(TechnicalErrorCode.BEAN_ERROR,"error with ParameterVo to Parameter transformation");
			}
			
			return parameter;
		}else{	
			return null;
		}
	}

	public List<Parameter> assembleList(List<ParameterVo> valueObjectList) {
		ArrayList<Parameter> parameters=new ArrayList<Parameter>();
		for(ParameterVo parameterVo :valueObjectList){
			parameters.add(assemble(parameterVo));
		}
		return parameters;
	}

	public ParameterVo disassemble(Parameter entityObject) {
		
		if(null!=entityObject){
			return new ParameterVo(entityObject.getFileSizeMax(), entityObject.getUserAvailableSize(), 
				entityObject.getGlobalQuota(), entityObject.getUsedQuota(), entityObject.getGlobalQuotaActive(),
                entityObject.getActiveMimeType(), entityObject.getActiveSignature() , entityObject.getActiveEncipherment(),entityObject.getActiveDocTimeStamp(),entityObject.getGuestAccountExpiryTime(),
                entityObject.getGuestAccountExpiryUnit(), null, entityObject.getDefaultShareExpiryUnit(), entityObject.getDefaultShareExpiryTime(), entityObject.getDefaultFileExpiryUnit(), 
                entityObject.getDefaultFileExpiryTime(), null,entityObject.getDeleteDocWithShareExpiryTime() ,null, null, null);
		}else{	
			return null;
		}
	}

	public List<ParameterVo> disassembleList(List<Parameter> entityObjectList) {
		ArrayList<ParameterVo> parameters=new ArrayList<ParameterVo>();
		for(Parameter parameter :entityObjectList){
			parameters.add(disassemble(parameter));
		}
		return parameters;
	}

}
