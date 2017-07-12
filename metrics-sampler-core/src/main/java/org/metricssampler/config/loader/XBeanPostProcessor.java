package org.metricssampler.config.loader;

import org.metricssampler.config.loader.xbeans.XBean;

import java.lang.reflect.InvocationTargetException;

public interface XBeanPostProcessor extends Comparable<XBeanPostProcessor> {
    /**
     * @return the order of the post-processor - instances with lower order will be executed before those with higher order value.
     */
    int getOrder();

    /**
     * do any post-processing on the xbean potentially modifying its value
     * @param xbean the xbean to process
     */
    void postProcessAfterLoad(XBean xbean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;


default int compareTo(XBeanPostProcessor that) {
        return this.getOrder() - that.getOrder();
    }
}
