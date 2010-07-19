package aeminiumruntime.tests;

import java.security.Permission;

import org.junit.Before;
import org.junit.After;

public class NoExitTest extends BaseTest
{

    protected static class ExitException extends SecurityException 
    {
        private static final long serialVersionUID = -8176668954455559274L;
		
		public final int status;
        public ExitException(int status) 
        {
                super("There is no escape!");
                this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager 
    {
        @Override
        public void checkPermission(Permission perm) 
        {
                // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context) 
        {
                // allow anything.
        }
        @Override
        public void checkExit(int status) 
        {
                super.checkExit(status);
                throw new ExitException(status);
        }
    }

    @Before
    public void setUp() throws Exception 
    {
        System.setSecurityManager(new NoExitSecurityManager());
        super.setUp();
    }
    
    
    @After
    public void tearDown() throws Exception 
    {
        System.setSecurityManager(null); // or save and restore original
    }

}