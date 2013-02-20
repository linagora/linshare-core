package org.linagora.linshare.core.dao.tika;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is designed to detect mime type an extension from a file.
 * @author fma
 *
 */
public class MimeTypeMagicNumberTikaImpl implements MimeTypeMagicNumberDao {

	private static final Logger logger = LoggerFactory.getLogger(MimeTypeMagicNumberTikaImpl.class);
	
	@Override
	public List<AllowedMimeType> getAllSupportedMimeType() {

		List<AllowedMimeType> mimetypesList = new ArrayList<AllowedMimeType>();

		MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
		SortedSet<MediaType> types = defaultMimeTypes.getMediaTypeRegistry().getTypes();
		int i = 0;
		for (MediaType mediaType : types) {
			i++;
			AllowedMimeType oneAllowedMimeType = null;
			
			String strMimeType = mediaType.toString();
			String extension;
			try {
				extension = defaultMimeTypes.forName(strMimeType).getExtension();
				oneAllowedMimeType = new AllowedMimeType(i, strMimeType, extension, MimeTypeStatus.AUTHORISED);
				mimetypesList.add(oneAllowedMimeType);
			} catch (MimeTypeException e) {
				logger.error("Can not find extension(s) for mime type : " + strMimeType);
				logger.debug(e.getMessage());
			}
		}
		return mimetypesList;
	}

}
