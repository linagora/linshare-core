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
package org.linagora.linShare.view.tapestry.beans;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;


/**
 * Store the documents that are to be shared to some users, plus the message with the sharing
 */
public class ShareSessionObjects {

    private List<UserVo> users;

    private List<DocumentVo> documents;
    
    private List<String> messages;
    
    private List<String> errors;
    
    private List<String> warnings;
    
    /**
     * multiple or unique share
     * if set to true you will have a multiple share of files
     */
    private boolean multipleSharing;
    
    private boolean reloadGroupsNeeded;
    
    
    public ShareSessionObjects() {
    	users = new ArrayList<UserVo>();
    	documents = new ArrayList<DocumentVo>();
    	messages=new ArrayList<String>();
    	errors=new ArrayList<String>();
    	warnings=new ArrayList<String>();
    	multipleSharing = true; //default: we use this object to remember multiple files selection 
    	reloadGroupsNeeded = false;
    }

    public List<DocumentVo> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentVo> documents) {
        this.documents = documents;
    }

    public List<UserVo> getUsers() {
        return users;
    }

    public void setUsers(List<UserVo> users) {
        this.users = users;
    }
    
    public void addUser(UserVo U) {
    	if (!users.contains(U)) {
    		users.add(U);
    	}
    }
    
    public void addDocument(DocumentVo D) {
    	if (!documents.contains(D)) {
    		documents.add(D);
    	}
    }
    
    public void removeUser(UserVo U) {
    	users.remove(U);
    }
    
    public void removeDocument(DocumentVo D) {
    	documents.remove(D);
    }
    
    public boolean getNotEmpty() {
    	return multipleSharing & (documents.size()>0 || users.size()>0);
    }


	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public List<String> getMessages() {
		return messages;
	}
	
	public void addMessage(String message) {
		this.messages.add(message);
	}

	public void removeMessage(String message) {
		this.messages.remove(message);
	}
	
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}
	
	public void addError(String error) {
		this.errors.add(error);
	}

	public void removeError(String error) {
		this.errors.remove(error);
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public List<String> getWarnings() {
		return warnings;
	}
	
	public void addWarning(String warning) {
		this.warnings.add(warning);
	}

	public void removeWarning(String warning) {
		this.warnings.remove(warning);
	}
	
	public boolean isMultipleSharing() {
		return multipleSharing;
	}

	public void setMultipleSharing(boolean multipleSharing) {
		this.multipleSharing = multipleSharing;
	}

	public void setReloadGroupsNeeded(boolean reloadGroupsNeeded) {
		this.reloadGroupsNeeded = reloadGroupsNeeded;
	}

	public boolean isReloadGroupsNeeded() {
		return reloadGroupsNeeded;
	}
	
	
	
}
