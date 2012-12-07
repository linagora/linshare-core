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
