/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jUpdate.
 *
 * jUpdate is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jUpdate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jUpdate; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.update;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import net.nikr.update.update.LocalError;
import net.nikr.update.update.OnlineError;
import net.nikr.update.update.Updaters;

public class Program {

	public static final String PROGRAM_VERSION = "2.2.0";
	private final List<LocalError> localErrors = new ArrayList<>();
	private final List<OnlineError> onlineErrors = new ArrayList<>();

	protected Program() {}

	public Program(final String link, final String jarFile) {
		setProxy();
		update(link, jarFile);
		SplashUpdater.hide();
		System.exit(0);
	}

	protected final boolean update(String link, String jarFile) {
		boolean updated = false;
		if (Updaters.FILE_LIST.use(link, jarFile)) {
			try {
				Updaters.FILE_LIST.update(link, jarFile);
				updated = true; //Update okay
			} catch (LocalError ex) {
				localErrors.add(ex);
			} catch (OnlineError ex) {
				onlineErrors.add(ex);
			}
		}
		if (!updated && Updaters.INSTALLER.use(link, jarFile)) {
			try {
				Updaters.INSTALLER.update(link, jarFile);
				updated = true; //Update okay
			} catch (LocalError ex) {
				localErrors.add(ex);
			} catch (OnlineError ex) {
				onlineErrors.add(ex);
			}
		}
		if (!updated && localErrors.isEmpty() && onlineErrors.isEmpty()) {
			//Only show mirror error when no other errors happened
			JOptionPane.showMessageDialog(null, "No download mirrors online\r\nPlease download the update from the official homepage", "jUpdate: Error", JOptionPane.ERROR_MESSAGE);
		}
		if (!updated) { //Only show errors when everything failed
			for (LocalError localError : localErrors) {
				JOptionPane.showMessageDialog(null, localError.getMessage(), "jUpdate: Local Error", JOptionPane.ERROR_MESSAGE);
			}
			for (OnlineError onlineError : onlineErrors) {
				JOptionPane.showMessageDialog(null, onlineError.getMessage(), "jUpdate: Online Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return updated;
	}

	private void setProxy() {
		//XXX - Workaround: Allow basic proxy authorization
		System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");

		//Set http proxy from https settings
		if (System.getProperty("https.proxyHost") != null) {
			System.setProperty("http.proxyHost", System.getProperty("https.proxyHost"));
		}
		if (System.getProperty("https.proxyPort") != null) {
			System.setProperty("http.proxyPort", System.getProperty("https.proxyPort"));
		}
		if (System.getProperty("https.proxyUser") != null) {
			System.setProperty("http.proxyUser", System.getProperty("https.proxyUser"));
		}
		if (System.getProperty("https.proxyPassword") != null) {
			System.setProperty("http.proxyPassword", System.getProperty("https.proxyPassword"));
		}

		//Set socks username and password
		if (System.getProperty("java.net.socks.username") != null && System.getProperty("java.net.socks.password") != null) {
			Authenticator.setDefault(new ProxyAuth(System.getProperty("java.net.socks.username"), System.getProperty("java.net.socks.password")));
		}
		//Set https username and password
		if (System.getProperty("https.proxyUser") != null && System.getProperty("https.proxyPassword") != null) {
			Authenticator.setDefault(new ProxyAuth(System.getProperty("https.proxyUser"), System.getProperty("https.proxyPassword")));
		}
		//Set socks host and port
		if (System.getProperty("socksProxyHost") != null && System.getProperty("socksProxyPort") != null) {
			ProxySelector.setDefault(new ProxyHost(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(System.getProperty("socksProxyHost"), Integer.parseInt(System.getProperty("socksProxyPort"))))));
		}
		//Set https host and port
		if (System.getProperty("https.proxyHost") != null && System.getProperty("https.proxyPort") != null) {
			ProxySelector.setDefault(new ProxyHost(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(System.getProperty("https.proxyHost"), Integer.parseInt(System.getProperty("https.proxyPort"))))));
		}
	}

	public static class ProxyAuth extends Authenticator {

		private final PasswordAuthentication auth;

		private ProxyAuth(String user, String password) {
			auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return auth;
		}
	}

	public static class ProxyHost extends ProxySelector {

		private final Proxy proxy;

		public ProxyHost(Proxy proxy) {
			this.proxy = proxy;
		}

		@Override
		public List<Proxy> select(URI uri) {
			return Collections.singletonList(proxy);
		}

		@Override
		public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

		}

	}
}
