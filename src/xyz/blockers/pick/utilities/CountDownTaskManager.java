package xyz.blockers.pick.utilities;

import java.util.LinkedList;
import java.util.Queue;

public class CountDownTaskManager {
    private final int countDownTime;
    BackgroundThread thread;
    public CountDownTaskManager(int countDownTime){
        thread=new BackgroundThread(countDownTime);
        System.out.println("CountDown:"+countDownTime);
        this.countDownTime=countDownTime;
    }
    public void clear(){
        lastTask=0;
        thread.clear();
    }
    long lastTask=0;
    public void add(Runnable runnable){
        long timeNow=System.currentTimeMillis();
        int len= (int) (timeNow-lastTask);
        lastTask=timeNow;
        CountDownTask task;
        if(len>=countDownTime){
            task=new CountDownTask(countDownTime,runnable);
        }else {
            task=new CountDownTask(countDownTime-len,runnable);
        }
        thread.add(task);
    }
    private static final class CountDownTask{
        int sleepTime;
        Runnable runnable;
        public CountDownTask(int sleepTime,Runnable runnable){
            this.runnable=runnable;
            this.sleepTime=sleepTime;
        }
    }
    private static final class BackgroundThread{
        Queue<CountDownTask> countDownTaskQueue=new LinkedList<>();
        int sleepTime;
        Runnable runnable=()->{

            while (true){
                int n=countDownTaskQueue.size();
                for(int i=0;i<n;i++){
                    CountDownTask task=countDownTaskQueue.poll();
                    if(task==null)
                        break;
                    //System.out.println("Wait: "+task.sleepTime);
                    try {
                        Thread.sleep(task.sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                    task.runnable.run();
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }

        };
        public void clear(){
            thread.interrupt();
            countDownTaskQueue.clear();
        }
        Thread thread;
        public BackgroundThread(int sleepTime){
            this.sleepTime=sleepTime;
            thread=new Thread(runnable);
            thread.start();
        }
        public void add(CountDownTask task){
            countDownTaskQueue.offer(task);
        }
    }
    //----------------
    public static void main(String[] args)throws Exception {
        CountDownTaskManager manager=new CountDownTaskManager(3000);
        manager.add(()-> System.out.println("123"));
        Thread.sleep(1000);
        manager.add(()-> System.out.println("456"));
        Thread.sleep(20000);
    }
}
