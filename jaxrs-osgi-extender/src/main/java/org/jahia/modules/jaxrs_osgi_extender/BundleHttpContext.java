/*******************************************************************************
 * Copyright (c) 2010 Neil Bartlett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial API and implementation
 ******************************************************************************/
package org.jahia.modules.jaxrs_osgi_extender;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class BundleHttpContext implements HttpContext {

    private final Bundle bundle;

    public BundleHttpContext(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public URL getResource(String name) {
        return bundle.getEntry(name);
    }

    @Override
    public String getMimeType(String name) {
        return null;
    }

    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return true;
    }

}
