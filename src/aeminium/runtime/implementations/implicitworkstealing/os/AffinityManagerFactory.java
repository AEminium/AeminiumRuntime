package aeminium.runtime.implementations.implicitworkstealing.os;

import com.sun.jna.Platform;

public class AffinityManagerFactory {
	public static AffinityManager getManager() {
		if ( Platform.isWindows() ) {
			return new WindowsAffinityManager();
		} else if(Platform.isMac()) { 
			return new OSXAffinityManager();
		} else {
			return new PosixAffinityManager();
		}
	}
}
