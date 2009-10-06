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
package org.linagora.linShare.view.tapestry.services.impl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.linagora.linShare.core.domain.vo.FileLogEntryVo;
import org.linagora.linShare.core.domain.vo.LogEntryVo;
import org.linagora.linShare.core.domain.vo.ShareLogEntryVo;
import org.linagora.linShare.core.domain.vo.UserLogEntryVo;
import org.linagora.linShare.view.tapestry.services.TimelineConverter;
import org.linagora.linkit.timeline.data.TimelineEvent;

public class TimelineConverterImpl implements TimelineConverter {

	private final String FileEntryDaoClassName = "fileEntry";
	private final String ShareEntryDaoClassName = "shareEntry";
	private final String UserEntryDaoClassName = "userEntry";
	/*private final Messages messages;
	*/	
	DateFormat df;
	public TimelineConverterImpl() {
		df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, Locale.US);
	}

	public TimelineEvent convert(LogEntryVo logEntry) {
		TimelineEvent theEvent = new TimelineEvent(df.format(logEntry.getActionDate().getTime()), logEntry.getLogAction().toString());
		theEvent.setCaption(logEntry.getDescription());
		// complete it
		
		if (logEntry instanceof UserLogEntryVo) {
			theEvent.setClassname(UserEntryDaoClassName);
			
			theEvent.setDescription("Target : <br/>" + 
					"Name : " + ((UserLogEntryVo)logEntry).getTargetFirstname() +" " +  ((UserLogEntryVo)logEntry).getTargetLastname() +"<br/>"+
					"Mail : " + ((UserLogEntryVo)logEntry).getTargetMail());
			
		} else {
			if (logEntry instanceof ShareLogEntryVo) {
				theEvent.setClassname(ShareEntryDaoClassName);
				theEvent.setDescription("Target : " + ((ShareLogEntryVo)logEntry).getTargetMail());
			} else {
				if (logEntry instanceof FileLogEntryVo) {
					theEvent.setClassname(FileEntryDaoClassName);
					theEvent.setDescription("file name : " + ((FileLogEntryVo)logEntry).getFileName());
				} 
			}
		}
		
		
		return theEvent;
	}

	public List<TimelineEvent> convert(List<LogEntryVo> listLogEntry) {
		List<TimelineEvent> list = new ArrayList<TimelineEvent>();
		if (listLogEntry==null) 
			return list;
		
		for (LogEntryVo logEntryVo : listLogEntry) {
			list.add(convert(logEntryVo));
		}
		return list;
	}

}
