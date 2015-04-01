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
package org.linagora.linshare.core.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class MySSLSocketFactory extends SSLSocketFactory {

	SSLSocketFactory sf;

	public MySSLSocketFactory() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
			sf = ctx.getSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	  public static SocketFactory getDefault(){
	    return new MySSLSocketFactory();
	  }

	@Override
	public Socket createSocket(Socket s, String host, int port,
			boolean autoClose) throws IOException {
		return sf.createSocket(s, host, port, autoClose);
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return sf.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
        return sf.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
        return sf.createSocket(arg0, arg1);
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        return sf.createSocket(arg0, arg1);
	}

	@Override
	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
        return sf.createSocket(arg0, arg1, arg2, arg3);
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
        return sf.createSocket(arg0, arg1, arg2, arg3);
	}

}
