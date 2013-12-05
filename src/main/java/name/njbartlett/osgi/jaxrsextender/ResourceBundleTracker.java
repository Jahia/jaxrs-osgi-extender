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
package name.njbartlett.osgi.jaxrsextender;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.BundleTracker;

import javax.servlet.ServletException;
import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

public class ResourceBundleTracker extends BundleTracker {
    public static final String PROP_JAXRS_ALIAS = "JAX-RS-Alias";
    public static final String PROP_JAXRS_CLASSES = "JAX-RS-Classes";

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
        String jaxrsResourceNames = headers.get(PROP_JAXRS_CLASSES);

        if (alias == null || jaxrsResourceNames == null)
            return null; // ignore this bundle

        ServletContainer servlet = processBundle(bundle, jaxrsResourceNames);

        try {
            log.log(LogService.LOG_INFO, MessageFormat.format("Registering HTTP servlet under alias \"{0}\" for JAX-RS resources in bundle {1}", alias, bundle.getLocation()));
            httpService.registerServlet(alias, servlet, null, new BundleHttpContext(bundle));
            return alias;
        } catch (ServletException e) {
            log.log(LogService.LOG_ERROR, "Error registering servlet.", e);
            return null;
        } catch (NamespaceException e) {
            log.log(LogService.LOG_ERROR, "Error registering servlet.", e);
            return null;
        }
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        String alias = (String) object;
        log.log(LogService.LOG_INFO, MessageFormat.format("Unregistering HTTP servlet under alias \"{0}\" for JAX-RS resources in bundle {1}", alias, bundle.getLocation()));
        httpService.unregister(alias);
    }

    private ServletContainer processBundle(Bundle bundle, String concatenatedResourceNames) {
        final String[] resourceNames = concatenatedResourceNames.split(",");
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        for (String resourceName : resourceNames) {
            resourceName = resourceName.trim();
            try {
                classes.add(bundle.loadClass(resourceName));
            } catch (Exception e) {
                log.log(LogService.LOG_ERROR, MessageFormat.format("Error loading class \"{0}\" from bundle \"{1}\".",
                        resourceName, bundle.getLocation()), e);
            }
        }

        if (classes.isEmpty()) return null;

        return new ServletContainer(new ResourceConfig(classes));
    }

}
