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

package org.linagora.linshare.view.tapestry.pages.lists;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.pages.lists.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Import(library = { "../../components/jquery/jquery-1.7.2.js" })
public class Index {

	private static Logger logger = LoggerFactory.getLogger(Index.class);

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo loginUser;

	@Inject
	private PersistentLocale persistentLocale;

	@Inject
	private Messages messages;

	@SessionState
	@Property
	private List<MailingListVo> lists;

	@Property
	private MailingListVo list;

	@Property
	@Persist(value = "flash")
	private String listToDelete;

	@Inject
	private MailingListFacade mailingListFacade;

	@Persist
	@Property
	private String targetLists;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@InjectComponent
	private Grid grid;

	@Persist
	@Property
	private String criteriaOnSearch;

	@Persist
	private boolean inSearch;

	@Property
	private String selectedRadioButton;

	@Persist
	private boolean alreadyConnect;

	@Persist
	private boolean fromCreate;

	@SetupRender
	public void init() throws BusinessException {
		if (alreadyConnect == false) {
			criteriaOnSearch = "allMyLists";
			targetLists = "*";
		}
		if (inSearch == false && fromCreate == false) {
			List<MailingListVo> finalList = mailingListFacade.getAllMyList(loginUser);
			lists = new ArrayList<MailingListVo>();

			for (MailingListVo current : finalList) {
				if (current.isPublic() == false) {
					lists.add(current);
				}
			}
		}
		if (fromCreate == true) {
			lists = mailingListFacade.getAllMyList(loginUser);
			for (MailingListVo current : lists) {
				if (current.isPublic() == true) {
					targetLists = "";
				}
			}
		}
		if (!lists.isEmpty()) {
			if (grid.getSortModel().getSortConstraints().isEmpty()) {
				grid.getSortModel().updateSort("identifier");
			}
			mailingListFacade.refreshList(lists);
		}
		alreadyConnect = true;
	}

	public boolean getListIsDeletable() throws BusinessException {
		return mailingListFacade.getListIsDeletable(loginUser, list);
	}

	public boolean getUserIsOwner() throws BusinessException {
		return loginUser.equals(list.getOwner());
	}

	public void onActionFromDeleteList(String uuid) {
		this.listToDelete = uuid;
	}

	@OnEvent(value = "listDeleteEvent")
	public void deleteList() throws BusinessException {
		mailingListFacade.deleteList(loginUser, listToDelete);

		for (MailingListVo current : lists) {
			if (current.getUuid() == listToDelete) {
				lists.remove(current);
			}
		}
		list = null;
	}

	public void onSuccessFromForm() throws BusinessException {
		inSearch = true;
		lists = mailingListFacade.setListFromUserSearch(loginUser, targetLists, criteriaOnSearch);
		fromCreate = false;
	}

	public void onSuccessFromResetForm() {
		inSearch = false;
		fromCreate = false;
		criteriaOnSearch = "allMyLists";
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}

	public boolean getIsPublic() {
		return list.isPublic();
	}

	public boolean isEmptyList() {
		return lists.isEmpty();
	}

	public String getPublic() {
		return "public";
	}

	public String getPrivate() {
		return "private";
	}

	public String getAll() {
		return "all";
	}

	public String getAllMyLists() {
		return "allMyLists";
	}

	public boolean isFromCreate() {
		return fromCreate;
	}

	public void setFromCreate(boolean fromCreate) {
		this.fromCreate = fromCreate;
	}
}