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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.domain.transformers.Transformer;
import org.linagora.linshare.core.domain.vo.FileLogEntryVo;
import org.linagora.linshare.core.domain.vo.LogEntryVo;
import org.linagora.linshare.core.domain.vo.ShareLogEntryVo;
import org.linagora.linshare.core.domain.vo.UserLogEntryVo;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public class LogEntryTransformer implements Transformer<LogEntry, LogEntryVo> {

	public LogEntryVo disassemble(LogEntry entityObject) {
		if (entityObject==null)
			return null;
		
		if (entityObject instanceof ShareLogEntry) {
			return new ShareLogEntryVo(entityObject.getActionDate(), entityObject.getActorMail(),
					entityObject.getActorFirstname(), entityObject.getActorLastname(),
					entityObject.getActorDomain(),
					entityObject.getLogAction(), entityObject.getDescription(),
					((ShareLogEntry)entityObject).getFileName(), ((ShareLogEntry)entityObject).getFileSize(),
					((ShareLogEntry)entityObject).getFileType(),
					((ShareLogEntry)entityObject).getTargetMail(), ((ShareLogEntry)entityObject).getTargetFirstname(),
					((ShareLogEntry)entityObject).getTargetLastname(),((ShareLogEntry)entityObject).getExpirationDate());
		}
		if (entityObject instanceof FileLogEntry) {
			return new FileLogEntryVo(entityObject.getActionDate(), entityObject.getActorMail(),
					entityObject.getActorFirstname(), entityObject.getActorLastname(),
					entityObject.getActorDomain(),
					entityObject.getLogAction(), entityObject.getDescription(),
					((FileLogEntry)entityObject).getFileName(), ((FileLogEntry)entityObject).getFileSize(),
					((FileLogEntry)entityObject).getFileType());
		}
		if (entityObject instanceof UserLogEntry) {
			return new UserLogEntryVo(entityObject.getActionDate(), entityObject.getActorMail(),
					entityObject.getActorFirstname(), entityObject.getActorLastname(),
					entityObject.getActorDomain(),
					entityObject.getLogAction(), entityObject.getDescription(),
					((UserLogEntry)entityObject).getTargetMail(), ((UserLogEntry)entityObject).getTargetFirstname(),
					((UserLogEntry)entityObject).getTargetLastname(), ((UserLogEntry)entityObject).getExpirationDate());
		}
		
		throw new TechnicalException(TechnicalErrorCode.BEAN_ERROR, "Wrong instance of object " + entityObject.getClass() );
	}
	
	public LogEntry assemble(LogEntryVo valueObject) {
		throw new TechnicalException(TechnicalErrorCode.GENERIC, "This method should not be used");
	}

	public List<LogEntry> assembleList(List<LogEntryVo> valueObjectList) {
		throw new TechnicalException(TechnicalErrorCode.GENERIC, "This method should not be used");
	}

	public List<LogEntryVo> disassembleList(List<LogEntry> entityObjectList) {
		if (entityObjectList == null)
			return null;
		List<LogEntryVo> returnList = new ArrayList<LogEntryVo>();
		
		for (LogEntry logEntry : entityObjectList) {
			returnList.add(disassemble(logEntry));
		}
		return returnList;
	}

}
