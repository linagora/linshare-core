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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.ParameterVo;

public class DomainTransformer {
	
	private final ParameterTransformer parameterTransformer;

	public DomainTransformer(ParameterTransformer parameterTransformer) {
		super();
		this.parameterTransformer = parameterTransformer;
	}

	public DomainVo disassemble(Domain entityObject) {
		if (entityObject == null) {
			return null;
		}
		DomainVo domainVo = new DomainVo(entityObject);
		ParameterVo paramVo = parameterTransformer.disassemble(entityObject.getParameter());
		domainVo.setParameterVo(paramVo);
		return domainVo;
	}

	public List<DomainVo> disassembleList(List<Domain> entityObjectList) {
		List<DomainVo> list = new ArrayList<DomainVo>();
		for (Domain domain : entityObjectList) {
			DomainVo domainVo = disassemble(domain);
			if (domainVo!=null) list.add(domainVo);
		}
		return list;
	}

}
