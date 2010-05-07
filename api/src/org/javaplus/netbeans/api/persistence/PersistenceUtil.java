package org.javaplus.netbeans.api.persistence;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: consider to make PeristenceUnit class immutable
 * @author Roger Suen
 */
public class PersistenceUtil {

    /**
     * Returns the classpath entry of the specified persistence unit
     * as a list of URL.
     * @param unit the persistence unit to query, cannot be <tt>null</tt>.
     * @return a list of URL.
     * @throws NullPointerException if <tt>unit</tt> is <tt>null</tt>.
     * @throws IllegalStateException if any <tt>UrlSpec</tt> of this
     *         <tt>PersistenceUnit</tt> returns <tt>null</tt> URL.
     */
    public static List<URL> getUrls(PersistenceUnit unit) {
        if (unit == null) {
            throw new NullPointerException("null unit");
        }

        List<UrlSpec> urlSpecs = unit.getUrlSpecs();
        List<URL> result = new ArrayList<URL>(urlSpecs.size());
        for (UrlSpec spec : urlSpecs) {
            URL url = spec.getUrl();
            if (url == null) {
                throw new IllegalStateException(
                        "A null URL returned from one of UrlSpecs of the "
                        + "PeristenceUnit object: PersistenceUnit = " + unit
                        + ", UrlSpec = " + spec);
            }
            result.add(url);
        }
        return result;
    }
}
