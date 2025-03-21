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

import com.jmathanim.jmathanim.JMathAnimScene;

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
     * resource.loadResource(&quot;c:/config/config.xml&quot;) will return an
     * URL pointing to external file
     * loadResource(&quot;#config/config.xml&quot;) will return an URL pointing
     * to internal file located at src/resources/config/config.xml
     *
     *
     * @param resource String with the path of the resource. If this string
     * begins with &quot;#&quot; it denotes an internal file located at resources
     * directory
     * @param folder Folder where to look at (config, arrows,...)
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

    /**
     * Parses a relative external resource file path and converts it into a URL object.
     * The resource is resolved within the specified subfolder of the configured resources directory.
     * If the resource cannot be loaded due to IO errors or a malformed URL, the method logs the error
     * and returns null.
     *
     * @param resource The name of the resource file to be loaded. This is expected to be
     *                 a relative file name within the folder.
     * @param folder The folder within the resources directory where the resource file is expected
     *               to reside.
     * @return A URL object representing the location of the resource, or null if the resource
     *         could not be loaded or resolved.
     */
    private URL parseExternalRelativeResource(String resource, String folder) {
        URL externalResource = null;
        String baseFileName = "";
        try {
            File resourcesDir = JMathAnimConfig.getConfig().getResourcesDir();
            baseFileName = resourcesDir.getCanonicalPath() + File.separator + folder + File.separator + resource;
            File file = new File(baseFileName);
            externalResource = file.toURI().toURL();
        } catch (MalformedURLException ex) {
            JMathAnimScene.logger.error("Couldn't load resource " + baseFileName);
        } catch (IOException ex) {
            JMathAnimScene.logger.error("An unknown I/O error. Maybe you don't have permissions"
                    + "to access files on your working directory or simply this file doesn't exists at all!");
            JMathAnimScene.logger.error("Couldn't load resource " + baseFileName);
        }
        return externalResource;
    }

    /**
     * Resolves and retrieves a URL pointing to an internal resource located within the
     * specified internal folder of the project's resources directory.
     *
     * @param resource The name of the internal resource to locate. This should not include
     *                 the starting character "#" as it is already assumed to be an internal file.
     * @param folder The folder in the resources directory where the resource is expected to reside.
     * @return A URL object pointing to the resolved internal resource, or null if the resource
     *         could not be located.
     */
    private URL parseInternalResource(String resource, String folder) {
        String urlStr = folder + "/" + resource;

        return this.getClass().getClassLoader().getResource(urlStr);
    }

    /**
     * Parses the provided absolute file path and converts it into a URL.
     * This method is specifically used for handling external resources located
     * outside of the application.
     *
     * @param resource A string representing the absolute file path of the resource.
     *                 The path must point to an existing file on the system.
     * @return A {@code URL} object representing the parsed resource location,
     *         or {@code null} if the file path is malformed or cannot be converted.
     */
    private URL parseExternalAbsoluteResource(String resource) {
        URL externalResource = null;
        try {
            File file = new File(resource);
            externalResource = file.toURI().toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return externalResource;
    }
}
