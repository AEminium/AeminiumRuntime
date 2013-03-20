package aeminium.runtime.tasktype;


public class TaskTypeExample {
	public static void main(String[] args) {
		System.out.println("hello");
		
		Runnable r = new Runnable() {
			@TaskType(123)
			@Override
			public void run() {
				System.out.println("inside");			
			}			
		};

		//System.out.println("Value: " + TaskTypeAnalyzer.getTaskType(r) );
		
	}

	
}
