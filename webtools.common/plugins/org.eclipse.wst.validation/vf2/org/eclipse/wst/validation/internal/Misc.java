package org.eclipse.wst.validation.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

/**
 * Some miscellaneous helper methods. 
 * @author karasiuk
 *
 */
public class Misc {
	
	public static void close(InputStream in){
		if (in == null)return;
		try {
			in.close();
		}
		catch (IOException e){
			// eat it
		}
	}

	public static void close(OutputStream out) {
		if (out == null)return;
		try {
			out.close();
		}
		catch (IOException e){
			// eat it
		}		
	}
	
	public static boolean debugOptionAsBoolean(String option){
		String opt = Platform.getDebugOption(option);
		if (opt == null)return false;
		opt = opt.toLowerCase();
		if ("true".equals(opt))return true; //$NON-NLS-1$
		if ("yes".equals(opt))return true; //$NON-NLS-1$
		return false;
	}
	
	
	/**
	 * Answer a units appropriate string for the time.
	 * @param time time in nano seconds
	 */
	public static String getTimeNano(long time){
		if (time <= 1000)return NLS.bind(ValMessages.TimeNano, time);
		if (time <= 1000000)return NLS.bind(ValMessages.TimeMicro, time/1000);
		return getTimeMS(time/1000000);
	}
	
	/**
	 * Answer the CPU time consumed by this thread in nano seconds.
	 * @return -1 if the time can not be determined.
	 */
	public static long getCPUTime(){
		long cpuTime = -1;
		ThreadMXBean tb = ManagementFactory.getThreadMXBean();
		if (tb.isCurrentThreadCpuTimeSupported()){
			cpuTime = tb.getCurrentThreadCpuTime();
		}
		return cpuTime;
	}
	
	/**
	 * Answer a units appropriate string for the time.
	 * @param time time in milliseconds
	 */
	public static String getTimeMS(long time) {
		if (time <= 1000)return ValMessages.TimeUnder;
		if (time <= 60000)return NLS.bind(ValMessages.TimeSec, time/1000);
		return NLS.bind(ValMessages.TimeMin, time/60000);
	}

	
	public static void niy(String msg){
		if (msg == null)msg = "Sorry, this function is not implemented yet"; //$NON-NLS-1$
		throw new RuntimeException(msg);
	}
	
	/**
	 * Answer the type as a human readable string. This is only used for debugging.
	 * @param type
	 * @return
	 */
	public static String resourceChangeEventType(int type){
		StringBuffer b = new StringBuffer(200);
		if ((type & IResourceChangeEvent.POST_BUILD) != 0)b.append("post_build "); //$NON-NLS-1$
		if ((type & IResourceChangeEvent.POST_CHANGE) != 0)b.append("post_change "); //$NON-NLS-1$
		if ((type & IResourceChangeEvent.PRE_BUILD) != 0)b.append("pre_build "); //$NON-NLS-1$
		if ((type & IResourceChangeEvent.PRE_CLOSE) != 0)b.append("pre_close "); //$NON-NLS-1$
		if ((type & IResourceChangeEvent.PRE_DELETE) != 0)b.append("pre_delete "); //$NON-NLS-1$
		return b.toString();
	}
	
}
