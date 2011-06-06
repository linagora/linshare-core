package org.linagora.linShare.view.tapestry.beans;

import org.linagora.linShare.core.domain.entities.Role;

public enum SelectableRole {
	
	SIMPLE, ADMIN;
	
	public static Role fromSelectableRole(SelectableRole value) {
        switch(value) {
            case SIMPLE : return Role.SIMPLE;
            case ADMIN : return Role.ADMIN;
            default : throw new IllegalArgumentException("Doesn't match an existing SelectableRole");
        }
    }

	
	public static SelectableRole fromRole(Role value) {
        switch(value) {
            case SIMPLE : return SelectableRole.SIMPLE;
            case ADMIN : return SelectableRole.ADMIN;
            default : throw new IllegalArgumentException("Doesn't match an existing SelectableRole");
        }
    }
}
