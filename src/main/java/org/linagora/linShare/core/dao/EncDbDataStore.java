/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.linagora.linShare.core.dao;

import java.io.Console;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.core.data.DataStoreException;
import org.apache.jackrabbit.core.data.db.DbDataStore;
import org.apache.jackrabbit.core.persistence.bundle.util.ConnectionRecoveryManager;

/**
 *
 * @author cvargas
 */
public class EncDbDataStore extends DbDataStore{

    

    @Override
    public ConnectionRecoveryManager createNewConnection() throws RepositoryException {
        Console cons = null;
        char[] passwd;
        String password = null;
        for(int i=0;i<20;i++){
            System.out.println();
        }
        if ((cons = System.console()) != null
                && (passwd = cons.readPassword("[%s]", "Password:")) != null) {
            password = new String(passwd);
            java.util.Arrays.fill(passwd, ' ');
        }
        setUrl(getUrl() + ";CIPHER=AES");
        setPassword(password + " "+ getPassword());
        return super.createNewConnection();
    }


}
