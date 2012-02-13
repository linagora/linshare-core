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
package org.linagora.linShare.core.dao.apperture;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.linagora.linShare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linShare.core.domain.entities.AllowedMimeType;
import org.linagora.linShare.core.domain.entities.MimeTypeStatus;
import org.linagora.linShare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MimeTypeMagicNumberImpl implements MimeTypeMagicNumberDao {
	
	
	private Logger log = LoggerFactory.getLogger(MimeTypeMagicNumberImpl.class);
	private static final String MAGIC_MIME_FILE ="/org/semanticdesktop/aperture/mime/identifier/magic/mimetypes.xml";
	
	public MimeTypeMagicNumberImpl() {
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
				if (log.isTraceEnabled()) log.trace("mimeType: "+ mimeTypeStr);

				NodeList extensions = descriptionElement
						.getElementsByTagName("extensions");
				if (extensions.getLength() > 0) {
					line = (Element) extensions.item(0);
					extensionsStr = getCharacterDataFromElement(line);
					
					if (log.isTraceEnabled()) log.trace("extensions: "+ extensionsStr);
				}
				
				oneAllowedMimeType = new AllowedMimeType(i,mimeTypeStr,extensionsStr,MimeTypeStatus.AUTHORISED);
				mimetypesList.add(oneAllowedMimeType);
			}

		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException", e);
		} catch (SAXException e) {
			log.error("SAXException",e);
		} catch (IOException e) {
			log.error("IOException",e);
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

}
