/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.view.tapestry.beans;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;


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
    
    private boolean comeFromSharePopup;

	private boolean reloadThreadsNeeded;
    
    
    public ShareSessionObjects() {
    	users = new ArrayList<UserVo>();
    	documents = new ArrayList<DocumentVo>();
    	messages=new ArrayList<String>();
    	errors=new ArrayList<String>();
    	warnings=new ArrayList<String>();
    	multipleSharing = true; //default: we use this object to remember multiple files selection 
    	reloadThreadsNeeded = false;
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

	public void setReloadThreadsNeeded(boolean reloadThreadsNeeded) {
		this.reloadThreadsNeeded = reloadThreadsNeeded;
	}

	public boolean isReloadThreadsNeeded() {
		return reloadThreadsNeeded;
	}

	public boolean isComeFromSharePopup() {
		return comeFromSharePopup;
	}

	public void setComeFromSharePopup(boolean comeFromSharePopup) {
		this.comeFromSharePopup = comeFromSharePopup;
	}
	
	
	
}
