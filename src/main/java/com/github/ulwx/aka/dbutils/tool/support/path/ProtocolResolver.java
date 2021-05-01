
package com.github.ulwx.aka.dbutils.tool.support.path;



@FunctionalInterface
 interface ProtocolResolver {

	/**
	 * Resolve the given location against the given resource loader
	 * if this implementation's protocol matches.
	 * @param location the user-specified resource location
	 * @param resourceLoader the associated resource loader
	 * @return a corresponding {@code Resource} handle if the given location
	 * matches this resolver's protocol, or {@code null} otherwise
	 */

	Resource resolve(String location, ResourceLoader resourceLoader);

}
