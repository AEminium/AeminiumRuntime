package aeminium.runtime.implementations.implicitworkstealing.os;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.LongByReference;

public class PosixAffinityManager implements AffinityManager {
	
	private static final String LIBRARY_NAME = Platform.isWindows() ? "msvcrt" : "c";

	private interface CLibrary extends Library {
		public static final CLibrary INSTANCE = (CLibrary) Native.loadLibrary(
				LIBRARY_NAME, CLibrary.class);

		public int sched_setaffinity(final int pid, final int cpusetsize,
				final PointerType cpuset) throws LastErrorException;

		public int sched_getaffinity(final int pid, final int cpusetsize,
				final PointerType cpuset) throws LastErrorException;
	}

	@Override
	public void setAffinity(final long affinity) {
		final CLibrary lib = CLibrary.INSTANCE;
		try {
			// fixme: where are systems with more then 64 cores...
			final int ret = lib.sched_setaffinity(0, Long.SIZE / 8,
					new LongByReference(affinity));
			if (ret < 0) {
				throw new IllegalStateException("sched_setaffinity(("
						+ Long.SIZE / 8 + ") , &(" + affinity + ") ) return "
						+ ret);
			}
		} catch (LastErrorException e) {
			throw new IllegalStateException("sched_getaffinity((" + Long.SIZE
					/ 8 + ") , &(" + affinity + ") ) errorNo="
					+ e.getErrorCode(), e);
		}
	}

}
