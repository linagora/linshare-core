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
package org.linagora.linshare.view.tapestry.enums;

public enum ActionFromBarDocument
{
		NO_ACTION, SHARED_ACTION, GROUP_SHARE_ACTION, MEMBER_ADD_ACTION, DELETE_ACTION, CRYPT_ACTION, DECRYPT_ACTION, SIGNATURE_ACTION, COPY_ACTION;
		
		public static ActionFromBarDocument fromString(String item){
			
			if (item == null) return ActionFromBarDocument.NO_ACTION;
			
			if (item.equalsIgnoreCase("SHARED_ACTION")) {
				return ActionFromBarDocument.SHARED_ACTION;
			}
			else if (item.equalsIgnoreCase("GROUP_SHARE_ACTION")) {
				return ActionFromBarDocument.GROUP_SHARE_ACTION;
			}
			else if (item.equalsIgnoreCase("MEMBER_ADD_ACTION")) {
				return ActionFromBarDocument.MEMBER_ADD_ACTION;
			}
			else if (item.equalsIgnoreCase("DELETE_ACTION")) {
				return ActionFromBarDocument.DELETE_ACTION;
			}
			else if (item.equalsIgnoreCase("CRYPT_ACTION")) {
				return ActionFromBarDocument.CRYPT_ACTION;
			}
			else if (item.equalsIgnoreCase("DECRYPT_ACTION")) {
				return ActionFromBarDocument.DECRYPT_ACTION;
			}
			else if (item.equalsIgnoreCase("SIGNATURE_ACTION")) {
				return ActionFromBarDocument.SIGNATURE_ACTION;
			}
			else if (item.equalsIgnoreCase("COPY_ACTION")) {
				return ActionFromBarDocument.COPY_ACTION;
			}
			else {
				return ActionFromBarDocument.NO_ACTION;
			}
		}
}
