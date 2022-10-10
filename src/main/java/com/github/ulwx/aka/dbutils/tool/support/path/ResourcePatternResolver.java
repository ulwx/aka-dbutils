
package com.github.ulwx.aka.dbutils.tool.support.path;

import java.io.IOException;


interface ResourcePatternResolver extends ResourceLoader {


    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    /**
     * Resolve the given location pattern into {@code Resource} objects.
     * <p>Overlapping resource entries that point to the same physical
     * resource should be avoided, as far as possible. The result should
     * have set semantics.
     *
     * @param locationPattern the location pattern to resolve
     * @return the corresponding {@code Resource} objects
     * @throws IOException in case of I/O errors
     */
    Resource[] getResources(String locationPattern) throws IOException;

}
