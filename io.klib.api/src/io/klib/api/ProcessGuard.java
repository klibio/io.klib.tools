package io.klib.api;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * ProcessGuard 
 * TODO enter javadoc
 * <p>
 * This is an example of an interface that is expected to be implemented by Providers of the API. Adding methods to this
 * interface is a minor change, because only Providers will be affected.
 * </p>
 * 
 * @see ProviderType
 * @since 1.0
 */
@ProviderType
public interface ProcessGuard {

    /**
     * 
     * @return OS process id of the current running process
     */
    long getPID();

    /**
     * 
     * @return array of all running process ids
     */
    List<Long> getProcessList();

    /**
     * 
     * @return array of all child process ids
     */
    List<Long> getChildPIDs(long pid);

    /**
     * 
     * @param pid of the process to be send SIGKILL
     */
    void killProcess(long pid);

    /**
     * 
     * @param pid of the process you want to send SIGKILL and also to all of the child processes
     */
    void killProcessRecursive(long pid);

}
