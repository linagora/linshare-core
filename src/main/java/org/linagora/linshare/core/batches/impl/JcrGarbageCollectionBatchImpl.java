/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.batches.impl;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.data.GarbageCollector;
import org.apache.jackrabbit.core.state.ItemStateException;
import org.linagora.linshare.core.batches.JcrGarbageCollectionBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springmodules.jcr.JcrTemplate;

/**
 * !!!!! Warning !!!!!
 * We found that this garbage collector was buggy in the
 * Jackrabbit version we use with LinShare (1.4)
 * Do not use this Batch if you do not upgrade the Jackrabbit
 * version
 * https://issues.apache.org/jira/browse/JCR-2492
 * 
 * @author sduprey
 *
 */
public class JcrGarbageCollectionBatchImpl implements JcrGarbageCollectionBatch {

	final private static Logger Logger = LoggerFactory
			.getLogger(JcrGarbageCollectionBatchImpl.class);

	private final JcrTemplate jcrTemplate;

	public JcrGarbageCollectionBatchImpl(JcrTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	public void removeUnusedFiles() {

		Logger.info("JCR Garbage Collection job start.");

		try {
			System.gc();
			SessionImpl session = (SessionImpl) jcrTemplate.getSessionFactory().getSession();
			GarbageCollector gc = session.createDataStoreGarbageCollector();
			gc.scan();
			gc.stopScan();
			gc.deleteUnused();
		} catch (RepositoryException e) {
			Logger.error(e.getMessage());
		} catch (ItemStateException e) {
			Logger.error(e.getMessage());
		} catch (IllegalStateException e) {
			Logger.error(e.getMessage());
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}

		Logger.info("JCR Garbage Collection job end.");
	}
}
