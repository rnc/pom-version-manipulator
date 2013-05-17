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

package com.redhat.rcm.version.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;

import com.redhat.rcm.version.VManException;
import com.redhat.rcm.version.mgr.session.VersionManagerSession;
import com.redhat.rcm.version.model.Project;
import com.redhat.rcm.version.util.DepMapToString;

@Component( role = Report.class, hint = ModifiedDependenciesReport.ID )
public class ModifiedDependenciesReport
    implements Report
{
    public static final String ID = "modified-dependencies";

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public void generate( final File reportsDir, final VersionManagerSession session )
        throws VManException
    {
        final File report = new File( reportsDir, ID + ".md" );

        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter( report ) );

            writer.write( "# Dependency Modifications by Project\n\n\n" );

            final LinkedHashSet<Project> projects = session.getCurrentProjects();
            for ( final Project project : projects )
            {
                final Map<Dependency, Dependency> mods =
                    session.getDependencyModifications( project.getVersionlessKey() );

                /* @formatter:off */
                // NOTE: Using Markdown format...
                final String out = String.format( 
                    "## %s\n" +
                    "\n" +
                    "  - POM: %s\n" +
                    "  - %d modified dependencies\n" +
                    "\n" +
                    "### Modifications:\n" +
                    "\n" +
                    "%s\n" +
                    "\n" +
                    "\n", 
                    project.getKey(), project.getPom(), mods.size(), new DepMapToString( mods ) 
                );
                /* @formatter:on */

                writer.write( out );
            }
        }
        catch ( final IOException e )
        {
            throw new VManException( "Failed to write to: %s. Reason: %s", e, report, e.getMessage() );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }

}