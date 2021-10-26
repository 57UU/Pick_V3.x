package xyz.blockers.pick;

import xyz.blockers.pick.utilities.CountDownTaskManager;

import javax.swing.text.Utilities;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



/**
 * can't pick too frequently!!
 * @param <T>
 */
public class PickOne <T>{
    public PickOne(List<T> list,double interval){
        raw=list;
        ts=new LinkedList<>(raw);
        countDownTime=(int) (interval*60*1000);
        manager=new CountDownTaskManager(countDownTime);
    }
    List<T> raw;
    List<T> ts;
    CountDownTaskManager manager;
    //static int countDownTime=60*1000*90;
    static int countDownTime;//for test
    public static int randint(int length){
        return (int)(Math.random()*length);
    }


    synchronized private void reset(){
        System.out.println("Reset");
        ts=new LinkedList<>(raw);
        manager.clear();
    }
    SecureRandom sr=new SecureRandom();
    public T pick(boolean isRemove){
        if(isRemove){
            if(ts.size()==0){
                reset();
            }
            //int index=randint(ts.size());
            int index=(int )(sr.nextDouble()*ts.size());
            T o = null;
            try {
                o=ts.get(index);
            }catch (Exception e){
                System.out.println(index);
                e.printStackTrace();
            }
            assert o!=null;
            T finalO = o;
            manager.add(()->ts.add(finalO));
            ts.remove(index);
            return o;
        }else {
            return raw.get(randint(raw.size()));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Integer> arrayList=new ArrayList<>();
        for(int i=0;i<100;i++)
            arrayList.add(i);
        var pick= new PickOne<>(arrayList, 10);
        int[] ints=new int[100];
        for(int i=0;i<1000;i++){
            var a=(pick.pick(true));
            ints[a]++;
            Thread.sleep(3);
        }
        System.out.println(Arrays.toString(ints));
    }



}
