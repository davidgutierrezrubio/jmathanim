/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class loads resources stored in the jar or in external files
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ResourceLoader {

	JMathAnimConfig config;

	public ResourceLoader() {
		config = JMathAnimConfig.getConfig();
	}

	/**
	 * Returns an URL pointing the
	 * resource.loadResource(&quot;c:/config/config.xml&quot;) will return an URL
	 * pointing to external file loadResource(&quot;#config/config.xml&quot;) will
	 * return an URL pointing to internal file located at
	 * src/resources/config/config.xml
	 *
	 *
	 * @param resource String with the path of the resource. If this string begins
	 *                 with &quot;#&quot; it denotes a internal file located at
	 *                 resources directory
	 * @param folder   Folder where to look at (config, arrows,...)
	 * @return URL with the resource location
	 */
	public URL getResource(String resource, String folder) {
		if (resource.startsWith("!")) {
			return parseExternalAbsoluteResource(resource.substring(1));
		}

		if (resource.startsWith("#")) {
			return parseInternalResource(resource.substring(1), folder);
		} else {
			return parseExternalRelativeResource(resource, folder);
		}
	}

	private URL parseExternalRelativeResource(String resource, String folder) {
		URL externalResource = null;
		try {
			File resourcesDir = JMathAnimConfig.getConfig().getResourcesDir();
			String baseFileName = resourcesDir.getCanonicalPath() + File.separator + folder + File.separator + resource;
			File file = new File(baseFileName);
			externalResource = file.toURI().toURL();
		} catch (MalformedURLException ex) {
			Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return externalResource;
	}

	private URL parseInternalResource(String resource, String folder) {
		String urlStr = folder + "/" + resource;

		return this.getClass().getClassLoader().getResource(urlStr);
	}

	private URL parseExternalAbsoluteResource(String resource) {
		URL externalResource = null;
		try {
			File file = new File(resource);
			externalResource = file.toURI().toURL();
		} catch (MalformedURLException ex) {
			Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return externalResource;
	}
}
