package org.linagora.linshare.cmis.utils;

import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AllowableActionsImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;

public interface CmisHelpers {

	public Map<String, ObjectData> getMemoizer();

	PropertiesImpl setEntryProperty(String objectId, String path, String name);

	AllowableActionsImpl setEntryAllowableActions(Account actor, String objectId);

	PropertiesImpl setAllPropertyToEntry(Account actor, String objectId, Entry entry, String type, Long size);

	String getObjectUuid(String objectId);

	Account prepare(String repositoryId, boolean thread, boolean document);
}
