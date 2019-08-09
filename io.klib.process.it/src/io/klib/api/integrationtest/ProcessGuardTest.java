package io.klib.api.integrationtest;

import static org.knowhowlab.osgi.testing.assertions.BundleAssert.assertBundleState;
import static org.knowhowlab.osgi.testing.assertions.OSGiAssert.getBundleContext;
import static org.knowhowlab.osgi.testing.assertions.ServiceAssert.assertServiceAvailable;
import static org.knowhowlab.osgi.testing.assertions.ServiceAssert.assertServiceUnavailable;
import static org.knowhowlab.osgi.testing.utils.BundleUtils.findBundle;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.knowhowlab.osgi.testing.utils.ServiceUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import io.klib.api.ProcessGuard;
import junit.framework.TestCase;

public class ProcessGuardTest extends TestCase {

    private final String        SERVICE_BUNDLE = "io.klib.process.impl.sigar";

    private final BundleContext context        = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String userprofile = System.getenv("USERPROFILE");
        String sysdrive = System.getenv("SYSTEMDrive");
        String sysroot = System.getenv("SYSTEMRoot");
        String temp = System.getenv("TEMP");
        String ComSpec = System.getenv("ComSpec");
        String path = System.getenv("PATH");
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File("C:\\Windows\\system32\\"));
        pb.environment().put("USERPROFILE", userprofile);
        pb.environment().put("SYSTEMDrive", sysdrive);
        pb.environment().put("SYSTEMRoot", sysroot);
        pb.environment().put("TEMP", temp);
        pb.environment().put("ComSpec", ComSpec);
        pb.environment().put("PATH", path);
        String os = System.getProperty("os.name");
        switch (os) {
		case "Windows 10":
	        pb.command("cmd.exe", "/K cmd.exe");
			break;

		default:
	        pb.command("/bin/sh");
			break;
		}
        pb.start();
    }

    public void testLifeCycle() throws Exception {
        assertBundleState(Bundle.ACTIVE, SERVICE_BUNDLE);
        assertServiceAvailable(ProcessGuard.class);
        findBundle(getBundleContext(), SERVICE_BUNDLE).stop();
        assertServiceUnavailable(ProcessGuard.class, 1, TimeUnit.SECONDS);
        findBundle(getBundleContext(), SERVICE_BUNDLE).start();
        ProcessGuard pg = ServiceUtils.getService(context, ProcessGuard.class);
        assertNotNull(pg);
    }

    public void testGetProcessList() {
        ProcessGuard pg = ServiceUtils.getService(context, ProcessGuard.class);
        long pidViaRuntimeMXBean = getPidViaRuntimeMXBean();

        long pid = pg.getPID();
        assertEquals(pidViaRuntimeMXBean, pid);

        assertTrue(pg.getProcessList().contains(pid));
    }

    public void testKillProcess() {
        ProcessGuard pg = ServiceUtils.getService(context, ProcessGuard.class);
        long pid = pg.getPID();

        List<Long> childPIDs = pg.getChildPIDs(pid);
        assertTrue(childPIDs.size() > 0);

        for (Long child: childPIDs) {
            pg.killProcessRecursive(child);
        }

        List<Long> childPIDsAfter = pg.getChildPIDs(pid);
        assertTrue(childPIDsAfter.size() == 0);

    }

    private Long getPidViaRuntimeMXBean() {
        RuntimeMXBean rtb = ManagementFactory.getRuntimeMXBean();
        String processName = rtb.getName();
        Long result = null;
        Pattern pattern = Pattern.compile("^([0-9]+)@.+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(processName);
        if (matcher.matches()) {
            result = new Long(Long.parseLong(matcher.group(1)));
        }
        return result;
    }

}
