package aeminium.runtime.tasktype;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;



public class TaskTypeExample {
	public static void main(String[] args) {
		System.out.println("hello");
		
		Body r = new Body() {
			@TaskType(123)
			@Override
			public void execute(Runtime rt, Task current){
				System.out.println("inside");			
			}		
		};
		
		
		

		System.out.println("Value: " + TaskTypeAnalyzer.getTaskType(r) );
		
	}

	
}
