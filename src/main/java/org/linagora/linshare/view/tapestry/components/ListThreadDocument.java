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
package org.linagora.linshare.view.tapestry.components;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Response;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.models.SorterModel;
import org.linagora.linshare.view.tapestry.models.impl.ThreadEntrySorterModel;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.impl.MailContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Import(library= { "ListThreadDocument.js"})
@SupportsInformalParameters
public class ListThreadDocument {

    private static final Logger logger = LoggerFactory.getLogger(ListThreadDocument.class);

    /***********************************
     * Parameters
     ***********************************/

    /**
     * The user owner for the document list.
     */
    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    private UserVo user;

    /**
     * The list of documents.
     */
    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    @Property
    private List<ThreadEntryVo> listThreadEntries;

    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    @Property
    private String title;
    
    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    @Property
    private List<ThreadEntryVo> listSelected;


    /***********************************
     * Properties
     ***********************************/

    @Property
    private ThreadEntryVo threadEntry;
    
    @Property
    private String login;
    
	@Property
	private boolean valueCheck;
	
	@Property
	private String checkBoxGroupUuid;
    
	
    @SuppressWarnings("unused")
	private boolean filesSelected;
    

    /***********************************
     * Service injection
     ***********************************/

    @Inject
    private ThreadEntryFacade threadEntryFacade;

    @Inject
	private Messages messages;
    
	@Inject
	private Response response;

    @Inject
    private MailContainerBuilder mailContainerBuilder;

    @Inject
	private BeanModelSource beanModelSource;
    
    @Property
	@Persist
	private BeanModel<ThreadEntryVo> model;
    
    @Property
	@Persist
	private SorterModel<ThreadEntryVo> sorterModel;
    
    @Inject
	private ComponentResources componentResources;
    
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
    
    
    /***********************************
	 * Flags
	 ***********************************/
    
	@Persist
	private boolean refreshFlag;
	
	@Persist
	private List<ThreadEntryVo> entries;
	
	@Property
	private String deleteConfirmed; // this is nasty, but i didn't find a proper
									// workaround


	

	public ListThreadDocument() {
		super();
		checkBoxGroupUuid = "filesSelected_" + UUID.randomUUID().toString();
	}
	
	
    /*********************************
     * Phase render
     *********************************/

    /**
     * Initialization of the selected list and set the userLogin from the user ASO.
     * @throws BusinessException 
     */
    @SuppressWarnings("unchecked")
	@SetupRender
    public void init() throws BusinessException {
    	if (listThreadEntries == null)
    		return;
    	Collections.sort(listThreadEntries);
    	login = user.getLogin();
    	initModel();
    }
    
	/***************************************************************************
	 * ActionLink methods
	 **************************************************************************/

	/**
	 * The action triggered when the user click on the download link on the name
	 * of the file.
	 */

	public StreamResponse onActionFromDownload(String uuid)	throws BusinessException {
		// when user has been logged out
		if (listThreadEntries == null) {
			return null;
		}
		
		ThreadEntryVo current = null;
		for (ThreadEntryVo vo : listThreadEntries) {
			if (vo.getIdentifier().equals(uuid)) {
				current = vo;
				break;
			}
		}
		
		if (current == null) {
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,	"invalid uuid for this user");
		} else {
			InputStream stream = threadEntryFacade.retrieveFileStream(current, user);
			return new FileStreamResponse(current, stream);
		}
	}
	
	
	/***************************************************************************
	 * Events
	 **************************************************************************/
	
	@SuppressWarnings("unchecked")
	@OnEvent(value="eventReorderList")
	public void reorderList(Object[] o1){
		if (o1 != null && o1.length > 0){
			this.entries = (List<ThreadEntryVo>)Arrays.copyOf(o1,1)[0];
			this.sorterModel = new ThreadEntrySorterModel(this.entries);
			refreshFlag = true;
		}
	}

	public Object onSuccessFromSearch() {
		if (listSelected.size() < 1) {
			businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.NOFILE_SELECTED, MessageSeverity.WARNING));
			return null;
		}
		if ("true".equals(deleteConfirmed)) {
			componentResources.getContainer().getComponentResources().getContainer().getComponentResources().triggerEvent("eventDeleteFromListDocument", listSelected.toArray(), null);
		}
		return null;
	}


	/***************************************************************************
	 * Other methods
	 **************************************************************************/
	
	/**
	 * model for the datagrid we need it to switch off the signature and the
	 * encrypted column dynamically administration can desactivate the signature
	 * and encryption function
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public BeanModel<ThreadEntryVo> initModel() throws BusinessException {
		//Initialize the sorter model for sorter component.
		if (refreshFlag == true){
			listThreadEntries = entries;
			refreshFlag = false;
		}
		sorterModel = new ThreadEntrySorterModel(listThreadEntries);
		model = beanModelSource.createDisplayModel(ThreadEntryVo.class, componentResources.getMessages());
		
		model.add("fileProperties", null);
		model.add("actions", null);
		model.add("selectedValue", null);
		
		List<String> reorderlist = new ArrayList<String>();
		reorderlist.add("fileProperties");
		reorderlist.add("actions");
		model.reorder(reorderlist.toArray(new String[reorderlist.size()]));

		return model;
	}
	
    /**
     * Generate the css class in order to display the icon corresponding to the file mime type
     * 
     * @return the css class
     */
    public String getTypeCSSClass() {
		String ret = threadEntry.getType();
		ret = ret.replace("/", "_");
		ret = ret.replace("+", "__");
		ret = ret.replace(".", "_-_");
		return ret;
	}

    /**
     * Format the file size for good readability
     * 
     * @return size in human readable format
     */
    public String getFriendlySize() {
		return FileUtils.getFriendlySize(threadEntry.getSize(), messages);
	}
    
    /**
	 * Format the creation date for good displaying using DateFormatUtils of
	 * apache commons lib.
	 * 
	 * @return creation date the date in localized format.
	 */
	public String getCreationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(threadEntry.getCreationDate().getTime());
	}
	
	/**
	 * Remove all carriage return for chenillekit tool tip
	 * 
	 * @return the formated comment
	 */
	public String getFormattedComment() {
		String result = threadEntry.getFileComment().replaceAll("\r","");
		result = result.replaceAll("\n", " ");
		return result;
	}
	
	/**
	 * 
	 * @return false (the document is never filesSelected by default)
	 */
	public boolean isFilesSelected() {
		return false;
	}
	
	/**
	 * This method is called when the form is submitted.
	 * 
	 * @param filesSelected
	 *            filesSelected or not in the form.
	 */
	public void setFilesSelected(boolean selected) {
		if (selected) {
			listSelected.add(threadEntry);
		}
	}
	
	public Link getThumbnailPath() {
        return componentResources.createEventLink("thumbnail", threadEntry.getIdentifier());
	}
	
	public boolean getThumbnailExists() {
		return threadEntryFacade.documentHasThumbnail(login, threadEntry.getIdentifier());
	}
	
	public void onThumbnail(String docId) {
		InputStream stream = null;
		ThreadEntryVo current = null;
		for (ThreadEntryVo vo : listThreadEntries) {
			if (vo.getIdentifier().equals(docId)) {
				current = vo;
				break;
			}
		}
		stream = threadEntryFacade.getDocumentThumbnail(user.getLsUid(), current.getIdentifier());
		if (stream == null)
			return;
		OutputStream os = null;
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		try {
			os = response.getOutputStream("image/png");
			BufferedImage bufferedImage=ImageIO.read(stream);
			if (bufferedImage != null)
				ImageIO.write(bufferedImage, Constants.THMB_DEFAULT_FORMAT, os);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
