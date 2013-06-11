/* 
 * Copyright (c) Orchestral Developments Ltd (2001 - 2013).
 * 
 * This document is copyright. Except for the purpose of fair reviewing, no part
 * of this publication may be reproduced or transmitted in any form or by any
 * means, electronic or mechanical, including photocopying, recording, or any
 * information storage and retrieval system, without permission in writing from
 * the publisher. Infringers of copyright render themselves liable for
 * prosecution.
 */
package org.js.samples.arquillian.karaf.blueprint;

import java.io.InputStream;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.osgi.spi.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;

@RunWith(Arquillian.class)
public class MyServiceTest {

    @Deployment
    public static JavaArchive createDeployment() {
        final String archiveName = "arquillian.test.jar";
        return ShrinkWrap.create(JavaArchive.class, archiveName)
                .addClasses(MyServiceTest.class)
                .setManifest(new Asset() {

                    public InputStream openStream() {
                        return OSGiManifestBuilder.newInstance()
                                .addBundleActivationPolicy("lazy")
                                .addBundleSymbolicName(archiveName)
                                .addBundleManifestVersion(2)
                                .addBundleVersion("0.0.0")
                                .addExportPackages(MyServiceTest.class)
                                .addDynamicImportPackages("*")
                                .openStream();
                    }
                });
    }

    @Inject
    public BundleContext context;

    @Test
    public void testInitialisation() {
        Assert.assertNotNull(this.context);
    }
}
