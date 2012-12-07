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
package org.linagora.linshare.core.domain.transformers.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.domain.transformers.Transformer;
import org.linagora.linshare.core.domain.vo.AllowedMimeTypeVO;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public class AllowedMimeTypeTransformer implements Transformer<AllowedMimeType, AllowedMimeTypeVO>{

	public AllowedMimeType assemble(AllowedMimeTypeVO valueObject) {
		
		
		AllowedMimeType allowedMimeType=new AllowedMimeType();
		
		if(null!=valueObject){
			
			try {
				BeanUtils.copyProperties(allowedMimeType,valueObject);
			} catch (IllegalAccessException e) {
				throw new TechnicalException(TechnicalErrorCode.BEAN_ERROR,"error with allowedMimeTypeVo to allowedMimeType transformation");
			} catch (InvocationTargetException e) {
				throw new TechnicalException(TechnicalErrorCode.BEAN_ERROR,"error with allowedMimeTypeVo to allowedMimeType transformation");
			}
			
			return allowedMimeType;
		}else{	
			return null;
		}
	}

	public List<AllowedMimeType> assembleList(List<AllowedMimeTypeVO> valueObjectList) {
		ArrayList<AllowedMimeType> allowedMimeTypes=new ArrayList<AllowedMimeType>();
		for(AllowedMimeTypeVO allowedMimeTypeVo :valueObjectList){
			allowedMimeTypes.add(assemble(allowedMimeTypeVo));
		}
		return allowedMimeTypes;
	}

	public AllowedMimeTypeVO disassemble(AllowedMimeType entityObject) {
		
		
		if(null!=entityObject){
			
			return new AllowedMimeTypeVO(entityObject.getId(),entityObject.getMimetype() ,entityObject.getExtensions(),entityObject.getStatus());
		}else{	
			return null;
		}
	}

	public List<AllowedMimeTypeVO> disassembleList(List<AllowedMimeType> entityObjectList) {
		ArrayList<AllowedMimeTypeVO> allowedMimeTypes=new ArrayList<AllowedMimeTypeVO>();
		for(AllowedMimeType allowedMimeType :entityObjectList){
			allowedMimeTypes.add(disassemble(allowedMimeType));
		}
		return allowedMimeTypes;
	}

}
