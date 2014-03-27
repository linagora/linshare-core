/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
