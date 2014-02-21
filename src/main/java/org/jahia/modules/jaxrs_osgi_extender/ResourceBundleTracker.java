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

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.BundleTracker;

import javax.ws.rs.core.Application;
import java.util.Dictionary;

public class ResourceBundleTracker extends BundleTracker {
    public static final String PROP_JAXRS_ALIAS = "JAX-RS-Alias";
    public static final String PROP_JAXRS_APPLICATION = "JAX-RS-Application";

    private final LogService log;
    private final HttpService httpService;

    public ResourceBundleTracker(BundleContext context,
                                 HttpService httpService, LogService log) {
        super(context, Bundle.ACTIVE, null);
        this.httpService = httpService;
        this.log = log;
    }

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent event) {
        @SuppressWarnings("unchecked")
        Dictionary<String, String> headers = bundle.getHeaders();

        String alias = headers.get(PROP_JAXRS_ALIAS);
        String jaxrsApplication = headers.get(PROP_JAXRS_APPLICATION);

        if (alias == null || jaxrsApplication == null) {
            return null; // ignore this bundle
        }

        // modify alias so that we can properly detect in ServletHandler that we are requesting a REST call and
        // therefore the request should not be wrapped
        String initialAlias = alias; // record the initial alias since that's what is recorded internally and return that
        alias = alias + ".jaxrs";

        ServletContainer servlet = processBundle(bundle, jaxrsApplication);

        BundleHttpContext bundleContext = new BundleHttpContext(bundle);

        try {
            log.log(LogService.LOG_INFO, "Registering HTTP servlet under alias " + alias +
                    " for JAX-RS resources in bundle " + bundle.getLocation());

            httpService.registerServlet(alias, servlet, null, bundleContext);

            return initialAlias;
        } catch (Exception e) {
            log.log(LogService.LOG_ERROR, "Error registering servlet.", e);
            return null;
        }
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
        removedBundle(bundle, event, object);
        addingBundle(bundle, event);
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        String alias = (String) object;

        log.log(LogService.LOG_INFO, "Unregistering HTTP servlet under alias " + alias + " for JAX-RS resources in bundle "
                + bundle.getLocation());

        httpService.unregister(alias);
    }

    private ServletContainer processBundle(Bundle bundle, String applicationName) {
        Application application = null;
        try {
            Class<?> applicationClass = bundle.loadClass(applicationName);
            application = (Application) applicationClass.newInstance();
        } catch (Exception e) {
            log.log(LogService.LOG_ERROR, "Error loading application class " + applicationName + " from bundle "
                    + bundle.getLocation(), e);
        }

        return new ServletContainer(ResourceConfig.forApplication(application));
    }

}
