jaxrs-osgi-extender
===================
Based on https://github.com/njbartlett/jaxrs-osgi-extender but updated to use JAX-RS 2 and Jersey 2.4.1.
Having a bit of an issue with javax.annotation.Priority not being found when the bundle is started, though. :(


Bundles that need to be deployed to attempt to make this bundle work:

- jaxrs-ri-2.4.1.jar (part of Jersey)
- asm-all-repackaged-2.2.0-b21.jar
- guava-14.0.1.jar
- hk2-api-2.2.0-b21.jar
- hk2-locator-2.2.0-b21.jar
- hk2-utils-2.2.0-b21.jar
- osgi-resource-locator-1.0.1.jar
- javax.annotation-api-1.2.jar