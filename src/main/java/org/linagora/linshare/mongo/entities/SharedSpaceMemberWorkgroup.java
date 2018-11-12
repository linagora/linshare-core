package org.linagora.linshare.mongo.entities;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.NodeType;

@XmlRootElement(name = "SharedSpaceMember")
public class SharedSpaceMemberWorkgroup extends SharedSpaceMember {

	public SharedSpaceMemberWorkgroup() {
		super();
		this.type = NodeType.WORK_GROUP;
	}
}
