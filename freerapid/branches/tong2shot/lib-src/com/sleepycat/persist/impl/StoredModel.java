/*-
 *
 *  This file is part of Oracle Berkeley DB Java Edition
 *  Copyright (C) 2002, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle Berkeley DB Java Edition is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle Berkeley DB Java Edition is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License in
 *  the LICENSE file along with Oracle Berkeley DB Java Edition.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package com.sleepycat.persist.impl;

import java.util.Set;

import com.sleepycat.persist.model.ClassMetadata;
import com.sleepycat.persist.model.EntityMetadata;
import com.sleepycat.persist.model.EntityModel;

/**
 * The EntityModel used when a RawStore is opened.  The metadata and raw type
 * information comes from the catalog directly, without using the current
 * class definitions.
 *
 * @author Mark Hayes
 */
class StoredModel extends EntityModel {

    private volatile PersistCatalog catalog;
    private volatile Set<String> knownClasses;

    StoredModel(final PersistCatalog catalog) {
        this.catalog = catalog;
    }

    /**
     * This method is used to initialize the model when catalog creation is
     * complete, and reinitialize it when a Replica refresh occurs.
     */
    @Override
    protected void setCatalog(final PersistCatalog newCatalog) {
        super.setCatalog(newCatalog);
        this.catalog = newCatalog;
        knownClasses = newCatalog.getModelClasses();
    }

    @Override
    public ClassMetadata getClassMetadata(String className) {
        ClassMetadata metadata = null;
        Format format = catalog.getFormat(className);
        if (format != null && format.isCurrentVersion()) {
            metadata = format.getClassMetadata();
        }
        return metadata;
    }

    @Override
    public EntityMetadata getEntityMetadata(String className) {
        EntityMetadata metadata = null;
        Format format = catalog.getFormat(className);
        if (format != null && format.isCurrentVersion()) {
            metadata = format.getEntityMetadata();
        }
        return metadata;
    }

    @Override
    public Set<String> getKnownClasses() {
        return knownClasses;
    }
}
