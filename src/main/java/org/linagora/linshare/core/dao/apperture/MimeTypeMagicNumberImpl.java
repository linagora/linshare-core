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
package org.linagora.linshare.core.dao.apperture;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MimeTypeMagicNumberImpl implements MimeTypeMagicNumberDao {
	
	private Logger logger = LoggerFactory.getLogger(MimeTypeMagicNumberImpl.class);
	
	private static final String MAGIC_MIME_FILE ="/org/semanticdesktop/aperture/mime/identifier/magic/mimetypes.xml";
	
	private final MimeTypeIdentifier mimeTypeIdentifier;
	
	public MimeTypeMagicNumberImpl() {
		this.mimeTypeIdentifier = new MagicMimeTypeIdentifier();
	}
	
	public List<AllowedMimeType> getAllSupportedMimeType() {

		List<AllowedMimeType> mimetypesList = new ArrayList<AllowedMimeType>();
		
		try {
			 
			 AllowedMimeType oneAllowedMimeType = null;
			
			InputStream in = MimeTypeMagicNumberImpl.class.getResourceAsStream(MAGIC_MIME_FILE);

			if (in==null) throw new TechnicalException("unable to find apperture configuration file: "+MAGIC_MIME_FILE);
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(in);

			NodeList descriptionNodes = doc.getElementsByTagName("description");
			for (int i = 0; i < descriptionNodes.getLength(); i++) {
				String mimeTypeStr = null;
				String extensionsStr = null;
				
				Element descriptionElement = (Element) descriptionNodes.item(i);

				NodeList mimetype = descriptionElement.getElementsByTagName("mimeType");
				Element line = (Element) mimetype.item(0);
				
				mimeTypeStr = getCharacterDataFromElement(line);
				if (logger.isTraceEnabled()) logger.trace("mimeType: "+ mimeTypeStr);

				NodeList extensions = descriptionElement
						.getElementsByTagName("extensions");
				if (extensions.getLength() > 0) {
					line = (Element) extensions.item(0);
					extensionsStr = getCharacterDataFromElement(line);
					
					if (logger.isTraceEnabled()) logger.trace("extensions: "+ extensionsStr);
				}
				
				oneAllowedMimeType = new AllowedMimeType(i,mimeTypeStr,extensionsStr,MimeTypeStatus.AUTHORISED);
				mimetypesList.add(oneAllowedMimeType);
			}

		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException", e);
		} catch (SAXException e) {
			logger.error("SAXException",e);
		} catch (IOException e) {
			logger.error("IOException",e);
		}
		
		return mimetypesList;
		
	}

	private static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

	@Override
	public String getMimeType(InputStream theFileInputStream) throws BusinessException {
		byte[] bytes;
		try {
			if(theFileInputStream.markSupported()) {
				theFileInputStream.mark(mimeTypeIdentifier.getMinArrayLength()+1);
			}
			bytes = IOUtil.readBytes(theFileInputStream, mimeTypeIdentifier.getMinArrayLength());
			if(theFileInputStream.markSupported()) {
				theFileInputStream.reset();
			}
		} catch (IOException e) {
			logger.error("Could not read the uploaded file !", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND, "Could not read the uploaded file.");
		}

		// let the MimeTypeIdentifier determine the MIME type of this file
		String mimeType = mimeTypeIdentifier.identify(bytes, null, null);
		if(mimeType == null) {
			mimeType = "data";
		}
		logger.debug("Mime type found : " + mimeType);
		return mimeType;
		
	}

	@Override
	public String getMimeType(File file) throws BusinessException {
		BufferedInputStream bufStream = null;
		FileInputStream f = null;
		String mimeType = null;
		try {
			f = new FileInputStream(file);
			bufStream = new BufferedInputStream(f);
			mimeType = this.getMimeType(bufStream);
		} catch (FileNotFoundException e) {
			logger.error("Could not read the uploaded file !", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND, "Could not read the uploaded file.");
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bufStream != null) {
				try {
					bufStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(mimeType == null) {
			mimeType = "data";
		}
		logger.debug("Mime type found : " + mimeType);
		return mimeType;
	}

	
	
}
