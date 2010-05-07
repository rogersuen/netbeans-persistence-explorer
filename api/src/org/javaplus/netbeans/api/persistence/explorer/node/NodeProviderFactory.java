package org.javaplus.netbeans.api.persistence.explorer.node;

import org.openide.util.Lookup;

/**
 * This interface defines the factory used to create <tt>NodeProvider</tt>
 * instances.
 * 
 * @author Roger Suen
 */
public interface NodeProviderFactory {

    /**
     * Creates and returns an instance of <tt>NodeProvider</tt> with
     * the specified lookup as the context. The <tt>lookup</tt> object
     * is provided by the parent node, and contains the context information
     * of that node.
     * 
     * @param lookup the lookup object as the context, cannot be <tt>null</tt>.
     * @return an instance of <tt>NodeProvider</tt>.
     */
    NodeProvider createNodeProvider(Lookup lookup);
}
