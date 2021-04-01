/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset;

import javax.sql.rowset.*;
import java.sql.SQLException;

/**
 * This is the implementation specific class for the
 * <code>javax.sql.rowset.spi.RowSetFactory</code>. This is the platform
 * default implementation for the Java SE platform.
 *
 * @author Lance Andersen
 * @version 1.7
 */
public final class RowSetFactoryImpl implements RowSetFactory {

    public CachedRowSet createCachedRowSet() throws SQLException {
        return new CachedRowSetImpl();
    }

    public FilteredRowSet createFilteredRowSet() throws SQLException {
        return new FilteredRowSetImpl();
    }


    public JdbcRowSet createJdbcRowSet() throws SQLException {
        return new JdbcRowSetImpl();
    }

    public JoinRowSet createJoinRowSet() throws SQLException {
        return new JoinRowSetImpl();
    }

    public WebRowSet createWebRowSet() throws SQLException {
        return new WebRowSetImpl();
    }

}
