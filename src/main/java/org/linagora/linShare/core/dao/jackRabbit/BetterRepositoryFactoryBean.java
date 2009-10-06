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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jcr.Repository;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springmodules.jcr.RepositoryFactoryBean;
import org.xml.sax.InputSource;

/**
 * FactoryBean for creating a JackRabbit (JCR-170) repository through Spring
 * configuration files. Use this factory bean when you have to manually
 * configure the repository; for retrieving the repository from JNDI use the
 * JndiObjectFactoryBean {@link org.springframework.jndi.JndiObjectFactoryBean}.
 * Sample configuration :
 * <code>
 *&lt;bean id="repository" class="BetterRepositoryFactoryBean"&gt;
 * &lt;!-- normal factory beans params --&gt;
 *   &lt;property name="configuration" value="classpath:repository.xml" /&gt;
 *   &lt;property name="homeDir" value="file:///c:/tmp/jackrabbit" /&gt;
 *   &lt;property name="configurationProperties"&gt;
 *     &lt;list&gt;
 *     &lt;value&gt;classpath:/first.properties&lt;/value&gt;
 *     &lt;value&gt;classpath:/second.properties&lt;/value&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 *</code>
 *
 * @see org.springframework.jndi.JndiObjectFactoryBean
 *
 */
public class BetterRepositoryFactoryBean extends RepositoryFactoryBean {

  /**
   * Default repository configuration file.
   */
  private static final String DEFAULT_CONF_FILE = "repository.xml";

  /**
   * Default repository directory.
   */
  private static final String DEFAULT_REP_DIR = ".";

  /**
   * Properties configuration the repository.
   */
  private List configurationProperties;
  /**
   * Home directory for the repository.
   */
  private Resource homeDir;

  /**
   * Repository configuration created through Spring.
   */
  private RepositoryConfig repositoryConfig;

  /**
   * @see org.springmodules.jcr.RepositoryFactoryBean#createRepository()
   */
  protected Repository createRepository() throws Exception {
    // return JackRabbit repository.
    return RepositoryImpl.create(repositoryConfig);
  }

  /**
   * @see org.springmodules.jcr.RepositoryFactoryBean#resolveConfigurationResource()
   */
  protected void resolveConfigurationResource() throws Exception {
    // read the configuration object
    if (repositoryConfig != null)
      return;

    if (this.configuration == null) {
      log.debug("no configuration resource specified, using the default one:"
          + DEFAULT_CONF_FILE);
      configuration = new ClassPathResource(DEFAULT_CONF_FILE);
    }

    if (homeDir == null) {
      if (log.isDebugEnabled())
        log.debug("no repository home dir specified, using the default one:"
            + DEFAULT_REP_DIR);
      homeDir = new FileSystemResource(DEFAULT_REP_DIR);
    }
    if (getConfigurationProperties() != null) {
      String goodConfig = replaceVariables(loadConfigurationKeys(),
          getConfiguration(configuration), true);
      repositoryConfig = RepositoryConfig.create(new InputSource(
          new StringReader(goodConfig)), homeDir.getFile().getAbsolutePath());
    } else {
      repositoryConfig = RepositoryConfig.create(new InputSource(configuration
          .getInputStream()), homeDir.getFile().getAbsolutePath());
    }
  }

  /**
   * Performs variable replacement on the given string value. Each
   * <code>${...}</code> sequence within the given value is replaced with the
   * value of the named parser variable. If a variable is not found in the
   * properties an IllegalArgumentException is thrown unless
   * <code>ignoreMissing</code> is <code>true</code>. In the later case,
   * the missing variable is not replaced.
   *
   * @param value
   *          the original value
   * @param ignoreMissing
   *          if <code>true</code>, missing variables are not replaced.
   * @return value after variable replacements
   * @throws IllegalArgumentException
   *           if the replacement of a referenced variable is not found
   */
  public static String replaceVariables(Properties variables, String value,
      boolean ignoreMissing) throws IllegalArgumentException {
    StringBuffer result = new StringBuffer();

    // Value:
    // +--+-+--------+-+-----------------+
    // | |p|--> |q|--> |
    // +--+-+--------+-+-----------------+
    int p = 0, q = value.indexOf("${"); // Find first ${
    while (q != -1) {
      result.append(value.substring(p, q)); // Text before ${
      p = q;
      q = value.indexOf("}", q + 2); // Find }
      if (q != -1) {
        String variable = value.substring(p + 2, q);
        String replacement = variables.getProperty(variable);
        if (replacement == null) {
          if (ignoreMissing) {
            replacement = "${" + variable + '}';
          } else {
            throw new IllegalArgumentException("Replacement not found for ${"
                + variable + "}.");
          }
        }
        result.append(replacement);
        p = q + 1;
        q = value.indexOf("${", p); // Find next ${
      }
    }
    result.append(value.substring(p, value.length())); // Trailing text
    return result.toString();
  }

  /**
   * Shutdown method.
   *
   */
  public void destroy() throws Exception {
    // force cast (but use only the interface)
    if (repository instanceof JackrabbitRepository)
      ((JackrabbitRepository) repository).shutdown();
  }

  /**
   * @return Returns the defaultRepDir.
   */
  public Resource getHomeDir() {
    return this.homeDir;
  }

  /**
   * @param defaultRepDir
   *          The defaultRepDir to set.
   */
  public void setHomeDir(Resource defaultRepDir) {
    this.homeDir = defaultRepDir;
  }

  /**
   * @return Returns the repositoryConfig.
   */
  public RepositoryConfig getRepositoryConfig() {
    return this.repositoryConfig;
  }

  /**
   * @param repositoryConfig
   *          The repositoryConfig to set.
   */
  public void setRepositoryConfig(RepositoryConfig repositoryConfig) {
    this.repositoryConfig = repositoryConfig;
  }

  /**
   * @return Returns the configuration properties.
   */
  public List getConfigurationProperties() {
    return configurationProperties;
  }

  /**
   * @param configurationProperties
   *          The configuration properties to set for the repository.
   */
  public void setConfigurationProperties(List resources) {
    this.configurationProperties = resources;
  }

  /**
   * Load all the configuration properties
   *
   * @return
   */
  protected Properties loadConfigurationKeys() {
    Iterator iter = configurationProperties.iterator();
    Properties props = new Properties();
    ResourceLoader loader = new DefaultResourceLoader(this.getClass()
        .getClassLoader());
    String location = "";
    while (iter.hasNext()) {
      try {
        location = (String) iter.next();
        if (loader.getResource(location).exists()) {
        	props.load(loader.getResource(location).getInputStream());
        } else {
        	log.info("Error loading resource " + location);
        }
      } catch (IOException e) {
        log.info("Error loading resource " + location, e);
      }
    }
    return props;
  }

  /**
   * Load a Resource as a String.
   * @param config the resource
   * @return the String filled with the content of the Resource
   * @throws IOException
   */
  protected String getConfiguration(Resource config) throws IOException {
    StringWriter out = new StringWriter();
    Reader reader = null;
    try {
      reader = new InputStreamReader(config.getInputStream());
      char[] buffer = new char[8];
      int c = 0;
      while ((c = reader.read(buffer)) > 0) {
        out.write(buffer, 0, c);
      }
      return out.toString();
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }
} 
