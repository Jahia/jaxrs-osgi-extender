jaxrs-osgi-extender
===================
Based on https://github.com/njbartlett/jaxrs-osgi-extender but updated to use JAX-RS 2 and Jersey 2.4.1.

Bundles that need to be deployed to attempt to make this bundle work:

- org.glassfish.jersey.bundles:jaxrs-ri:2.4.1
- asm-all-repackaged-2.2.0-b21.jar
- guava-14.0.1.jar
- hk2-api-2.2.0-b21.jar
- hk2-locator-2.2.0-b21.jar
- hk2-utils-2.2.0-b21.jar
- osgi-resource-locator-1.0.1.jar
- org.jboss.spec.javax.annotation:jboss-annotations-api_1.2_spec:1.0.0.Final