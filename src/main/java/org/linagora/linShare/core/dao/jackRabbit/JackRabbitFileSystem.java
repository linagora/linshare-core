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
package org.linagora.linShare.core.dao.jackRabbit;

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
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JackRabbitFileSystem implements FileSystemDao {


	final private static Logger log=LoggerFactory.getLogger(JackRabbitFileSystem.class);

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
				System.out.println(completePath);
				if(root.hasNode(completePath)){

					fileNode=root.getNode(completePath);
					if(fileNode.isNodeType(JcrConstants.NT_FILE)){

						return fileNode.getNode("jcr:content").getProperty("jcr:data").getStream();
					}else{
						log.error("the path node doesn't contain a file: "+completePath);
					}
				}else{
					log.error("the path node is invalid: "+completePath);
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
						log.error("the path node doesn't contain a file: "+uuid);
					}
				}else{
					log.error("the path node is invalid: "+uuid);
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
					log.info("the repository doesn't contain a node at this path: "+path);
					log.info("The system will create the path");

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
					log.error("error");
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
