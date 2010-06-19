/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aeminiumruntime.statistics;

public class Statistics {
    public long startTime,readyTime, runningTime,outTime,waitingTime;

    Statistics(long startTime,long readyTime,long runningTime, long outTime){
        this.startTime=startTime;
        this.readyTime=readyTime;
        this.runningTime=runningTime;
        this.outTime=outTime;
    }

    public Statistics(){}

    public void calcTime(){
        long runningTimeAux=runningTime;
        
        waitingTime=readyTime-startTime;
        readyTime=runningTime-readyTime;
        runningTime=outTime-runningTimeAux;
    }

    public void setReadyTime(long readyTime) {
        this.readyTime = readyTime;
    }

    public void setRunningTime(long runningTime) {
        this.runningTime = runningTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public void setStartTime(long startTime){
        this.startTime=startTime;
    }
    
    @Override
    public String toString(){
        return "new task:"
                +"\n\tTime wainting for dependencies: " + waitingTime
                +"\n\tTime in ready queue: "+readyTime
                +"\n\tTime in running queue: "+runningTime
                +"\n\tTotal time: "+(outTime-startTime);
    }

}
