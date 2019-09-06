package io.klib.process.sigar;

import java.util.LinkedList;
import java.util.List;

import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarPermissionDeniedException;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.klib.api.ProcessGuard;

@ProviderType
@Component(service = ProcessGuard.class)
public class ProcessGuardSigar implements ProcessGuard {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final int SIGKILL = 9;

	Sigar sigar = new Sigar();

	@Override
	public List<Long> getProcessList() {
		List<Long> procList = new LinkedList<>();
		try {
			long[] procs = sigar.getProcList();
			for (long proc : procs) {
				procList.add(proc);
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return procList;
	}

	@Override
	public void killProcess(final long pid) {
		try {
			sigar.kill(pid, SIGKILL);
		} catch (SigarException e) {
			if (e instanceof SigarPermissionDeniedException) {
				logger.debug("no perm for process with pid " + pid);
			} else {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void killProcessRecursive(final long pid) {
		try {
			List<Long> childPIDs = getChildPIDs(pid);
			for (Long childPID : childPIDs) {
				try {
					sigar.kill(childPID, SIGKILL);
					logger.debug("killing child pid: " + childPID);
				} catch (SigarException e) {
					logger.error(e.getMessage(), e);
				}
			}
			logger.debug("killing pid: " + pid);
			/* TODO Check if parent process still exists (maybe already quit)*/
			// ProcState procState = sigar.getProcState(pid);
			// procState. 
			sigar.kill(pid, SIGKILL);
		} catch (SigarException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Long> getChildPIDs(final long pid) {
		long[] procList = new long[] {};
		try {
			procList = sigar.getProcList();
		} catch (SigarException e1) {
			logger.error(e1.getMessage(), e1);
		}

		List<Long> childPIDs = new LinkedList<>();
		for (long childCandidatePID : procList) {
			ProcState procState = null;
			try {
				procState = sigar.getProcState(childCandidatePID);
				if (procState.getPpid() == pid) {
					childPIDs.add(childCandidatePID);
				}
			} catch (SigarException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return childPIDs;
	}

	@Override
	public long getPID() {
		return sigar.getPid();
	}

}