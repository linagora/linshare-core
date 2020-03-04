package org.linagora.linshare.mongo.entities.light;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

/**
 * Object used to contains minimal required information about
 * {@link SharedSpaceRole}
 */
public class LightSharedSpaceRole extends GenericLightEntity {

	protected NodeType type;

	public LightSharedSpaceRole() {
		super();
	}

	public LightSharedSpaceRole(SharedSpaceRole role) {
		super(role);
		this.type = role.getType();
	}

	public LightSharedSpaceRole(String uuid, String name, NodeType type) {
		super(uuid, name);
		this.type = type;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "LightSharedSpaceRole [type=" + type + ", uuid=" + uuid + ", name=" + name + "]";
	}

}
