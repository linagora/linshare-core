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
    
    /**
     * multiple or unique share
     * if set to true you will have a multiple share of files
     */
    private boolean multipleSharing;
    
    
    public ShareSessionObjects() {
    	users = new ArrayList<UserVo>();
    	documents = new ArrayList<DocumentVo>();
    	messages=new ArrayList<String>();
    	multipleSharing = true; //default: we use this object to remember multiple files selection 
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

	public boolean isMultipleSharing() {
		return multipleSharing;
	}

	public void setMultipleSharing(boolean multipleSharing) {
		this.multipleSharing = multipleSharing;
	}
	
	
	
}
