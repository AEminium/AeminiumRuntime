package aeminium.runtime.profiler.interceptorTests;

public class InterceptorLauncher
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		int i = 0;
		while (i < 5)
		{
			InterceptorLauncher.methodOne("Method One");
			InterceptorLauncher.methodTwo("Method Two");
			i++;
		}
	}

	public static void methodOne(String name)
	{
		System.out.println("Entered method one!!");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Exited method one...");
	}

	public static void methodTwo(String name)
	{
		System.out.println("Entered method two!!");
		System.out.println("Exited method two...");
	}

}
