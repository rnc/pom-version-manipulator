/*
 *  Copyright (C) 2010 John Casey.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.redhat.rcm.version;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ManipulationSession
{

    private final Map<String, Set<File>> missingVersions = new HashMap<String, Set<File>>();

    private final Map<File, Throwable> errors = new HashMap<File, Throwable>();

    private Map<String, String> depMap;

    private final File backups;

    private final boolean preserveDirs;

    public ManipulationSession( final File backups, final boolean preserveDirs )
    {
        this.backups = backups;
        this.preserveDirs = preserveDirs;
    }

    public ManipulationSession addMissingVersion( final File pom, final String key )
    {
        Set<File> poms = missingVersions.get( key );
        if ( poms == null )
        {
            poms = new HashSet<File>();
            missingVersions.put( key, poms );
        }

        poms.add( pom );

        return this;
    }

    public ManipulationSession addError( final File pom, final Throwable error )
    {
        errors.put( pom, error );
        return this;
    }

    public ManipulationSession setDependencyMap( final Map<String, String> depMap )
    {
        this.depMap = depMap;
        return this;
    }

    public Map<String, String> getDependencyMap()
    {
        return depMap;
    }

    public File getBackups()
    {
        return backups;
    }

    public boolean isPreserveDirs()
    {
        return preserveDirs;
    }

}
