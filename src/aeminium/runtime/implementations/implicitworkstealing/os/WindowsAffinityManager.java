package aeminium.runtime.implementations.implicitworkstealing.os;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinDef;

public class WindowsAffinityManager implements AffinityManager {

	private interface CLibrary extends Library {
		public static final CLibrary INSTANCE = (CLibrary) Native.loadLibrary("kernel32", CLibrary.class);

		public int GetProcessAffinityMask(final int pid, final PointerType lpProcessAffinityMask, final PointerType lpSystemAffinityMask) throws LastErrorException;

		public void SetThreadAffinityMask(final int pid, final WinDef.DWORD lpProcessAffinityMask) throws LastErrorException;

		public int GetCurrentThread() throws LastErrorException;
	}

	@Override
	public void setAffinity(final long affinity) {
		final CLibrary lib = CLibrary.INSTANCE;

		WinDef.DWORD aff = new WinDef.DWORD(affinity);
		try {
			lib.SetThreadAffinityMask(lib.GetCurrentThread(), aff);

		} catch (LastErrorException e) {
			throw new IllegalStateException("sched_getaffinity((" + Long.SIZE / 8 + ") , &(" + affinity + ") ) errorNo=" + e.getErrorCode(), e);
		}
	}
}
