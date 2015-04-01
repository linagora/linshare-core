/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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
package org.linagora.linshare.core.dao.jackRabbit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.ISO9075;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JackRabbitFileSystem implements FileSystemDao {


	final private static Logger Logger=LoggerFactory.getLogger(JackRabbitFileSystem.class);

	private JcrTemplate jcrTemplate;

	public JcrTemplate getJcrTemplate() {
		return jcrTemplate;
	}

	public void setJcrTemplate(JcrTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	protected JackRabbitFileSystem(){

	}

	public JackRabbitFileSystem(JcrTemplate jcrTemplate){
		this.jcrTemplate=jcrTemplate;
	}


	public InputStream getContentFile(final String completePath) {


		return (InputStream)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {

				Node root=session.getRootNode();
				Node fileNode=null;
				Logger.debug(completePath);
				if(root.hasNode(completePath)){

					fileNode=root.getNode(completePath);
					if(fileNode.isNodeType(JcrConstants.NT_FILE)){

						return fileNode.getNode("jcr:content").getProperty("jcr:data").getStream();
					}else{
						Logger.error("the path node doesn't contain a file: "+completePath);
					}
				}else{
					Logger.error("the path node is invalid: "+completePath);
				}


				return null;
			}
		});

	}


	private InputStream getContentFileUUID(final String uuid) {


		return (InputStream)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
                Node fileNode = null;
                try {
                    fileNode=session.getNodeByUUID(uuid);
                } catch (RepositoryException e) {
                    return null;
                }

				if(null!=fileNode){


					if(fileNode.getParent().isNodeType(JcrConstants.NT_FILE)){

						return fileNode.getProperty("jcr:data").getStream();
					}else{
						Logger.error("the path node doesn't contain a file: "+uuid);
					}
				}else{
					Logger.error("the path node is invalid: "+uuid);
				}


				return null;
			}
		});

	}

	public String insertFile(final String path, final InputStream stream,final long size,final String fileName, final String mimeType) {


		return (String)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException, FileNotFoundException {
				Node root=session.getRootNode();
				Node file=null;


				if(root.hasNode(path)){
					file=root.getNode(path);
				}else{
					Logger.info("the repository doesn't contain a node at this path: "+path);
					Logger.info("The system will create the path");

					file=root.addNode(path);
				}

				Node transition= file.addNode(ISO9075.encode(fileName),"nt:file");
				file.addMixin("mix:referenceable");
				Node res2 = transition.addNode("jcr:content", "nt:resource");
				res2.setProperty("jcr:data", stream);
				res2.setProperty("jcr:mimeType", mimeType);
				res2.setProperty("jcr:lastModified", new GregorianCalendar());
				session.save();

				return res2.getUUID();

			}
		});







	}

	@SuppressWarnings("unchecked")
	public List<String> getAllPath() {

		return (List<String>)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				session.refresh(true);
				Node root=session.getRootNode();

				ArrayList<String> array=new ArrayList<String>();

				return getPaths(root.getNodes(),array);

			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllSubPath(final String path) {

		return (List<String>)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				session.refresh(true);
				Node root=session.getRootNode();
				Node node=null;
				if(root.hasNode(path)){
					node=root.getNode(path);
				}else{
					Logger.error("error");
				}

				ArrayList<String> array=new ArrayList<String>();

				return getPaths(node.getNodes(),array);

			}
		});
	}

	@SuppressWarnings("unchecked")
	private List<String> getPaths(Iterator<Node> nodes,ArrayList<String> array){

		while(nodes.hasNext()){
			Node currentNode=nodes.next();
			try {
				array.add(currentNode.getPath());
				if(currentNode.getNodes()!=null){

					getPaths(currentNode.getNodes(),array);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return array;
	}
	@SuppressWarnings("unchecked")
	private List<FileInfo> getFiles(Iterator<Node> nodes,ArrayList<FileInfo> array){

		while(nodes.hasNext()){
			Node currentNode=nodes.next();
			try {
				if(currentNode.isNodeType(JcrConstants.NT_FILE)){

					array.add(getFileInfoFromNode(currentNode));
				}

				if(currentNode.hasNodes()){
					getFiles(currentNode.getNodes(),array);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return array;
	}


	@SuppressWarnings("unchecked")
	public InputStream getFileContentByUUID(final String uuid){
		return (InputStream)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {


				return getContentFileUUID(uuid);

			}
		});


	}







	@SuppressWarnings("unchecked")
	public List<FileInfo> getAllFilePathInSubPath(final String path) {
		return (List<FileInfo>)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {

				Node root=session.getRootNode();
				Node node=root.getNode(path);

				ArrayList<FileInfo> array=new ArrayList<FileInfo>();

				return getFiles(node.getNodes(),array);

			}
		});
	}


	@SuppressWarnings("unchecked")
	public Map<String,NodeIterator> executeSqlQuery(List<String> statements) {

		return jcrTemplate.query(statements, Query.SQL, false);
	}

	@SuppressWarnings("unchecked")
	public Map<String,NodeIterator> executeXPathQuery(List<String> statements) {

		return jcrTemplate.query(statements, Query.XPATH, false);
	}

	public void removeFileByUUID(final String uuid) {
		jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {

				Node node=session.getNodeByUUID(uuid).getParent();
				node.remove();
				session.save();
				return null;
			}
		});

	}

	public FileInfo getFileInfoByUUID(final String uuid) {
		return (FileInfo)jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {

				Node node=session.getNodeByUUID(uuid);

				return getFileInfoFromNode(node.getParent());
			}
		});

	}


	private FileInfo getFileInfoFromNode(Node currentNode) throws RepositoryException{
		FileInfo fileInfo=new FileInfo();
		fileInfo.setMimeType(currentNode.getNode("jcr:content").getProperty("jcr:mimeType").getString());
		fileInfo.setPath(currentNode.getPath());
		fileInfo.setUuid(currentNode.getNode("jcr:content").getUUID());
		fileInfo.setLastModified(currentNode.getNode("jcr:content").getProperty("jcr:lastModified").getDate());
		fileInfo.setName(currentNode.getName());
		return fileInfo;
	}

    public void renameFile(final String uuid, final String newName) {
		jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
				Node node=session.getNodeByUUID(uuid);
				FileInfo fileInfo = getFileInfoFromNode(node.getParent());
                fileInfo.setName(newName);
                session.save();
                return null;
            }
        });
    }

}
