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

import org.linagora.linShare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.LogEntry;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.UserLogEntry;
import org.linagora.linShare.core.domain.transformers.Transformer;
import org.linagora.linShare.core.domain.vo.DisplayableLogEntryVo;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;

public class DisplayableLogEntryTransformer implements Transformer<LogEntry, DisplayableLogEntryVo> {

	public DisplayableLogEntryVo disassemble(LogEntry entityObject) {
		if (entityObject==null)
			return null;
		
		if (entityObject instanceof ShareLogEntry) {
			return new DisplayableLogEntryVo(entityObject.getActionDate().getTime(), entityObject.getActorMail(),
					entityObject.getActorFirstname(), entityObject.getActorLastname(),
					entityObject.getLogAction(), entityObject.getDescription(),
					((ShareLogEntry)entityObject).getTargetMail(), ((ShareLogEntry)entityObject).getTargetFirstname(),
					((ShareLogEntry)entityObject).getTargetLastname(),
					((ShareLogEntry)entityObject).getFileName(), ((ShareLogEntry)entityObject).getFileSize(),
					((ShareLogEntry)entityObject).getFileType(), ((ShareLogEntry)entityObject).getExpirationDate());
		}
		if (entityObject instanceof FileLogEntry) {
			return new DisplayableLogEntryVo(entityObject.getActionDate().getTime(), entityObject.getActorMail(),
					entityObject.getActorFirstname(), entityObject.getActorLastname(),
					entityObject.getLogAction(), entityObject.getDescription(),
					((FileLogEntry)entityObject).getFileName(), ((FileLogEntry)entityObject).getFileSize(),
					((FileLogEntry)entityObject).getFileType());
		}
		if (entityObject instanceof UserLogEntry) {
			return new DisplayableLogEntryVo(entityObject.getActionDate().getTime(), entityObject.getActorMail(),
					entityObject.getActorFirstname(), entityObject.getActorLastname(),
					entityObject.getLogAction(), entityObject.getDescription(),
					((UserLogEntry)entityObject).getTargetMail(), ((UserLogEntry)entityObject).getTargetFirstname(),
					((UserLogEntry)entityObject).getTargetLastname(), ((UserLogEntry)entityObject).getExpirationDate());
		}
		if (entityObject instanceof AntivirusLogEntry) {
			return new DisplayableLogEntryVo(entityObject.getActionDate().getTime(), entityObject.getActorMail(),
					entityObject.getActorFirstname(), entityObject.getActorLastname(),
					entityObject.getLogAction(), entityObject.getDescription(),
					entityObject.getDescription(), null, "");
		}
		
		throw new TechnicalException(TechnicalErrorCode.BEAN_ERROR, "Wrong instance of object " + entityObject.getClass() );
	}
	
	public LogEntry assemble(DisplayableLogEntryVo valueObject) {
		throw new TechnicalException(TechnicalErrorCode.GENERIC, "This method should not be used");
	}

	public List<LogEntry> assembleList(List<DisplayableLogEntryVo> valueObjectList) {
		throw new TechnicalException(TechnicalErrorCode.GENERIC, "This method should not be used");
	}

	public List<DisplayableLogEntryVo> disassembleList(List<LogEntry> entityObjectList) {
		if (entityObjectList == null)
			return null;
		List<DisplayableLogEntryVo> returnList = new ArrayList<DisplayableLogEntryVo>();
		
		for (LogEntry logEntry : entityObjectList) {
			returnList.add(disassemble(logEntry));
		}
		return returnList;
	}

}
