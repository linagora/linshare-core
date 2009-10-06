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
package org.linagora.linShare.view.tapestry.objects;

import java.util.HashSet;
import java.util.Set;

import org.linagora.linShare.core.domain.vo.HelpVO;



public class HelpsASO {
	
	private Set<HelpVO> set;
	
	public HelpsASO(){
		set=new HashSet<HelpVO>();
	}
	
	public void add(HelpVO helpASO){
		set.add(helpASO);
	}
	
	public HelpVO getHelpVO(String uuid){
		for(HelpVO help:set){
			if(help.getUuid().equals(uuid)){
				return help;
			}
		}
		return null;
	}

}
