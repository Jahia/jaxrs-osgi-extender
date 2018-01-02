/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2018 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.jaxrs_osgi_extender;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class HttpServiceTracker extends ServiceTracker {

    private final LogService log;

    public HttpServiceTracker(BundleContext context, LogService log) {
        super(context, HttpService.class.getName(), null);
        this.log = log;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        HttpService httpService = (HttpService) context.getService(reference);

        log.log(LogService.LOG_INFO, "Starting to track JAX-RS bundles");

        ResourceBundleTracker bundleTracker = new ResourceBundleTracker(context, httpService, log);
        bundleTracker.open();

        return bundleTracker;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        log.log(LogService.LOG_INFO, "Stopping tracking JAX-RS bundles");

        ResourceBundleTracker bundleTracker = (ResourceBundleTracker) service;
        bundleTracker.close();

        context.ungetService(reference);
    }
}
