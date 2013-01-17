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
package org.linagora.linshare.core.facade.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.domain.transformers.Transformer;
import org.linagora.linshare.core.domain.transformers.impl.AllowedMimeTypeTransformer;
import org.linagora.linshare.core.domain.vo.AllowedMimeTypeVO;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MimeTypeFacade;
import org.linagora.linshare.core.service.MimeTypeService;

public class MimeTypeFacadeImpl implements MimeTypeFacade {

	private MimeTypeService mimeTypeService;
	
	
	private Transformer<AllowedMimeType, AllowedMimeTypeVO> allowedMimeTypeTransformer;

	public MimeTypeFacadeImpl(MimeTypeService mimeTypeService) {
		this.mimeTypeService = mimeTypeService;
		this.allowedMimeTypeTransformer =  new AllowedMimeTypeTransformer();
		
	}

	public List<AllowedMimeTypeVO> getAllSupportedMimeType()
			throws BusinessException {
		return allowedMimeTypeTransformer.disassembleList(mimeTypeService.getAllSupportedMimeType());
	}

	public List<AllowedMimeTypeVO> getAllowedMimeType() throws BusinessException {
		return allowedMimeTypeTransformer.disassembleList(mimeTypeService.getAllowedMimeType());
	}

	public boolean isAllowed(String mimeType) {
		return mimeTypeService.isAllowed(mimeType);
	}

	public void createAllowedMimeType(List<AllowedMimeTypeVO> list)
			throws BusinessException {
		 mimeTypeService.createAllowedMimeType(allowedMimeTypeTransformer.assembleList(list));
	}

	public void saveOrUpdateAllowedMimeType(List<AllowedMimeTypeVO> list)
			throws BusinessException {
		mimeTypeService.saveOrUpdateAllowedMimeType(allowedMimeTypeTransformer.assembleList(list));
	}

}
