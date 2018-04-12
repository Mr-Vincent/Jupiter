package org.jupiter.example.mytest;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * Description: 演示一下通过unsafe改变内存值
 *
 * @author dongwei
 * @date 2018/04/08
 * Time: 15:51
 */
public class UnsafeTest {

    private int i;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    private static  Unsafe unsafe;

    private static Long offset = 0L;
    static {
        Unsafe _unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            _unsafe = (Unsafe) unsafeField.get(null);
        } catch (Throwable t) {

            _unsafe = null;
        }

        unsafe = _unsafe;
        try {
            // i的地址值
            offset = unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("i"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UnsafeTest unsafeTest = new UnsafeTest();
        unsafeTest.setI(10);
        System.out.println("before unsafe op :" + unsafeTest);

        unsafe.putInt(unsafeTest,offset,100);

        System.out.println("after unsafe op :" + unsafeTest);


    }

    @Override
    public String toString() {
        return "UnsafeTest{" +
                "i=" + i +
                '}';
    }
}
