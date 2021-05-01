
package com.github.ulwx.aka.dbutils.tool.support.path;

import com.github.ulwx.aka.dbutils.tool.support.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class DefaultResourceLoader implements ResourceLoader {

   private ClassLoader classLoader;

   private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);

   private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);


   /**
    * Create a new DefaultResourceLoader.
    * <p>ClassLoader access will happen using the thread context class loader
    * at the time of actual resource access (since 5.3). For more control, pass
    * a specific ClassLoader to {@link #DefaultResourceLoader(ClassLoader)}.
    * @see Thread#getContextClassLoader()
    */
   public DefaultResourceLoader() {
   }

   /**
    * Create a new DefaultResourceLoader.
    * @param classLoader the ClassLoader to load class path resources with, or {@code null}
    * for using the thread context class loader at the time of actual resource access
    */
   public DefaultResourceLoader( ClassLoader classLoader) {
       this.classLoader = classLoader;
   }


   /**
    * Specify the ClassLoader to load class path resources with, or {@code null}
    * for using the thread context class loader at the time of actual resource access.
    * <p>The default is that ClassLoader access will happen using the thread context
    * class loader at the time of actual resource access (since 5.3).
    */
   public void setClassLoader( ClassLoader classLoader) {
       this.classLoader = classLoader;
   }


   @Override

   public ClassLoader getClassLoader() {
       return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
   }

   /**
    * Register the given resolver with this resource loader, allowing for
    * additional protocols to be handled.
    * <p>Any such resolver will be invoked ahead of this loader's standard
    * resolution rules. It may therefore also override any default rules.
    * @since 4.3
    * @see #getProtocolResolvers()
    */
   public void addProtocolResolver(ProtocolResolver resolver) {
       Assert.notNull(resolver, "ProtocolResolver must not be null");
       this.protocolResolvers.add(resolver);
   }

   /**
    * Return the collection of currently registered protocol resolvers,
    * allowing for introspection as well as modification.
    * @since 4.3
    */
   public Collection<ProtocolResolver> getProtocolResolvers() {
       return this.protocolResolvers;
   }

   /**
    * Obtain a cache for the given value type, keyed by {@link Resource}.
    * @param valueType the value type, e.g. an ASM {@code MetadataReader}
    * @return the cache {@link Map}, shared at the {@code ResourceLoader} level
    * @since 5.0
    */
   @SuppressWarnings("unchecked")
   public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
       return (Map<Resource, T>) this.resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
   }

   /**
    * Clear all resource caches in this resource loader.
    * @since 5.0
    * @see #getResourceCache
    */
   public void clearResourceCaches() {
       this.resourceCaches.clear();
   }


   @Override
   public Resource getResource(String location) {
       Assert.notNull(location, "Location must not be null");

       for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
           Resource resource = protocolResolver.resolve(location, this);
           if (resource != null) {
               return resource;
           }
       }

       if (location.startsWith("/")) {
           return getResourceByPath(location);
       }
       else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
           return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
       }
       else {
           try {
               // Try to parse the location as a URL...
               URL url = new URL(location);
               return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
           }
           catch (MalformedURLException ex) {
               // No URL -> resolve as resource path.
               return getResourceByPath(location);
           }
       }
   }


   protected Resource getResourceByPath(String path) {
       return new ClassPathContextResource(path, getClassLoader());
   }


   /**
    * ClassPathResource that explicitly expresses a context-relative path
    * through implementing the ContextResource interface.
    */
   protected static class ClassPathContextResource extends ClassPathResource implements ContextResource {

       public ClassPathContextResource(String path,  ClassLoader classLoader) {
           super(path, classLoader);
       }

       @Override
       public String getPathWithinContext() {
           return getPath();
       }

       @Override
       public Resource createRelative(String relativePath) {
           String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
           return new ClassPathContextResource(pathToUse, getClassLoader());
       }
   }

}
