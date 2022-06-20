package io.klib.process.command;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.service.command.Descriptor;
import org.hyperic.sigar.ProcExe;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcUtil;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarPermissionDeniedException;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.klib.api.ProcessGuard;

@Component(immediate = true, service = ProcessGuardCommand.class, property = { "osgi.command.scope=os",
		"osgi.command.function=listProc", "osgi.command.function=killProcRecursive", "osgi.command.function=killProc" })
@ConsumerType
public class ProcessGuardCommand {

	private ProcessGuard pg;

	@Reference
	void bindProcessGuard(final ProcessGuard procguard) {
		this.pg = procguard;
	}

	@Descriptor("list all processes")
	public String listProc() {
		StringBuffer sb = new StringBuffer();
		List<Long> procIDs = pg.getProcessList();
		for (Long procID : procIDs) {
			sb.append(collectProcDetails(procID, " -> "));
		}
		return sb.toString();
	}

	@Descriptor("show details for the process")
	public String listProc(@Descriptor("ProcessID") final long pid) {
		return collectProcDetails(pid, "\n");
	}

	private String collectProcDetails(final Long pid, final String separator) {

		String textFormat = "%s";
		if (separator == "\n") {
			textFormat = "%12s";
		}
		StringBuffer sb = new StringBuffer();
		Formatter fmt = new Formatter(null, Locale.getDefault());
		try {
			Sigar sigar = new Sigar();
			ProcState procState = sigar.getProcState(pid);
			ProcExe procExe = sigar.getProcExe(pid);
			fmt.format(textFormat + " %5d %s", "pid:", pid, separator);
			fmt.format(textFormat + " %5d %s", "parent pid:", procState.getPpid(), separator);
			fmt.format(textFormat + " %-25s %s", "name:", procState.getName(), separator);
			fmt.format(textFormat + " %-55s %s", "wrk dir:", procExe.getCwd(), separator);
			fmt.format(textFormat + " %-50s %s", "description:", ProcUtil.getDescription(sigar, pid), separator);
			String javaMainClass = ProcUtil.getJavaMainClass(sigar, pid);
			if (javaMainClass != null) {
				fmt.format(textFormat + " %s %s", "javaMain:", javaMainClass, separator);
			}
			sb.append(fmt.out());
			fmt.close();
			sb.append("\n");
		} catch (SigarException e) {
			if (e instanceof SigarPermissionDeniedException) {
				// System.err.println("no permission for details of pid " +
				// pid);
			} else {
				// System.err.println(e.getMessage());
			}
		}
		return sb.toString();
	}

	@Descriptor("send SIGKILL (9) to process")
	public String killProc(@Descriptor("ProcessID") final Long pid) {
		pg.killProcess(pid);
		return "send SIGKILL to process with pid " + pid;
	}

	@Descriptor("send SIGKILL (9) to process and all its children")
	public String killProcRecursive(@Descriptor("ProcessID") final Long pid) {
		pg.killProcessRecursive(pid);
		return "send SIGKILL to process with pid " + pid + "and all child processes";
	}

}
