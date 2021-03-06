/*
 * Copyright (c) 2011 Red Hat, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see 
 * <http://www.gnu.org/licenses>.
 */

package com.redhat.rcm.version.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.mae.project.key.VersionlessProjectKey;
import org.junit.Test;

import com.redhat.rcm.version.mgr.AbstractVersionManagerTest;
import com.redhat.rcm.version.mgr.session.CoordinateRelocations;
import com.redhat.rcm.version.mgr.session.VersionManagerSession;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RelocationsTest
    extends AbstractVersionManagerTest
{

    @Test
    public void checkAddRelocations_HandlingOfSpaceNewlineComma()
    {
        final VersionManagerSession session = createVersionManagerSession();

        final File f = new File( "." );

        final String inGA1 = "org.foo:foo";
        final String outGA1 = "org.bar:foo:1.0";

        final String inGA2 = "my.proj:core";
        final String outGA2 = "org.proj:core:1.0";

        final Map<String, String> relos = new HashMap<String, String>();
        relos.put( inGA1, outGA1 );
        relos.put( inGA2, outGA2 );

        final CoordinateRelocations relocations = new CoordinateRelocations( new HashMap<String, String>(), session );
        relocations.addBomRelocations( f, relos );

        final Map<VersionlessProjectKey, FullProjectKey> r = relocations.getRelocationsByFile().get( f );

        assertThat( r.get( new VersionlessProjectKey( "org.foo", "foo" ) ), equalTo( new FullProjectKey( "org.bar",
                                                                                                         "foo",
                                                                                                         "1.0" ) ) );

        assertThat( r.get( new VersionlessProjectKey( "my.proj", "core" ) ), equalTo( new FullProjectKey( "org.proj",
                                                                                                          "core",
                                                                                                          "1.0" ) ) );
    }

    @Test
    public void checkAddRelocations_HandlingOfSpaceCommaNewlineTab()
    {
        final File f = new File( "." );
        final String inGA1 = "org.foo:foo";
        final String outGA1 = "org.bar:foo:1.0";

        final String inGA2 = "my.proj:core";
        final String outGA2 = "org.proj:core:1.0";

        final Map<String, String> relos = new HashMap<String, String>();
        relos.put( inGA1, outGA1 );
        relos.put( inGA2, outGA2 );

        final VersionManagerSession session = createVersionManagerSession();
        final CoordinateRelocations relocations = new CoordinateRelocations( new HashMap<String, String>(), session );
        relocations.addBomRelocations( f, relos );

        final Map<VersionlessProjectKey, FullProjectKey> r = relocations.getRelocationsByFile().get( f );

        assertThat( r.get( new VersionlessProjectKey( "org.foo", "foo" ) ), equalTo( new FullProjectKey( "org.bar",
                                                                                                         "foo",
                                                                                                         "1.0" ) ) );

        assertThat( r.get( new VersionlessProjectKey( "my.proj", "core" ) ), equalTo( new FullProjectKey( "org.proj",
                                                                                                          "core",
                                                                                                          "1.0" ) ) );
    }

    @Test
    public void checkAddRelocations_InvalidLineDoesNotStopOtherAdditions()
    {
        final File f = new File( "." );

        final String inGA1 = "org.foo:foo";
        final String outGA1 = "org.bar:foo:1.0";

        final String inGA2 = "my.proj:core";
        final String outGA2 = "org.proj:core:1.0";

        final String inGA3 = "com.foo:project:1";
        final String outGA3 = "com.myco.foo:project1";

        final Map<String, String> relos = new HashMap<String, String>();
        relos.put( inGA1, outGA1 );
        relos.put( inGA2, outGA2 );
        relos.put( inGA3, outGA3 );

        final VersionManagerSession session = createVersionManagerSession();
        final CoordinateRelocations relocations = new CoordinateRelocations( new HashMap<String, String>(), session );
        relocations.addBomRelocations( f, relos );

        final Map<VersionlessProjectKey, FullProjectKey> r = relocations.getRelocationsByFile().get( f );

        assertThat( r.get( new VersionlessProjectKey( "org.foo", "foo" ) ), equalTo( new FullProjectKey( "org.bar",
                                                                                                         "foo",
                                                                                                         "1.0" ) ) );

        assertThat( r.get( new VersionlessProjectKey( "my.proj", "core" ) ), equalTo( new FullProjectKey( "org.proj",
                                                                                                          "core",
                                                                                                          "1.0" ) ) );

        assertThat( session.getErrors(), notNullValue() );
        assertThat( session.getErrors().size(), equalTo( 1 ) );
    }

}
