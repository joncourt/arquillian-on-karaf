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
package org.jc.samples.arquillian.karaf.blueprint.acceptance.test;

import java.io.InputStream;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.osgi.spi.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jc.samples.arquillian.karaf.blueprint.MessageFormatter;
import org.jc.samples.arquillian.karaf.blueprint.MyService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@RunWith(Arquillian.class)
public class MyServiceTest {

    @Deployment
    public static JavaArchive createDeployment() {
        final String archiveName = "arquillian.test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, archiveName)
                .addClasses(MyServiceTest.class)
                .setManifest(new Asset() {

                    public InputStream openStream() {
                        return OSGiManifestBuilder.newInstance()
                                .addBundleSymbolicName(archiveName)
                                .addBundleManifestVersion(2)
                                .addBundleVersion("0.0.0")
                                .addExportPackages(MyServiceTest.class)
                                .addDynamicImportPackages("*")
                                .openStream();
                    }
                });

        final JavaArchive[] resolvedArchives = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.mockito:mockito-all:1.9.5")
                .withTransitivity()
                .as(JavaArchive.class);

        for (final JavaArchive resolvedArchive : resolvedArchives) {
            archive.merge(resolvedArchive);
        }

        return archive;
    }

    @Inject
    public BundleContext context;
    private ServiceReference<MyService> myServiceRef;
    private MyService myService;

    @Before
    public void before() {
        this.myServiceRef = this.context.getServiceReference(MyService.class);
        this.myService = this.context.getService(this.myServiceRef);
    }

    @After
    public void after() {
        this.context.ungetService(this.myServiceRef);
    }

    @Test
    public void testMyService() {
        final MessageFormatter formatter = Mockito.mock(MessageFormatter.class);
        Mockito.when(formatter.format(Matchers.anyString())).then(new Answer<String>() {

            public String answer(final InvocationOnMock invocation) throws Throwable {
                return "Echo processed: " + invocation.getArguments()[0];
            }
        });

        Assert.assertEquals("Echo processed: Hello World!!", this.myService.echo("Hello World!!", formatter));
    }
}
