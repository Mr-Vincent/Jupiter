package org.jupiter.example.mytest;


import org.jupiter.rpc.load.balance.WeightArray;
import org.jupiter.rpc.load.balance.WeightSupport;

/**
 * @author dongwei
 * @since 2019/12/03
 * Time: 17:56
 */
public class BinarySearch {


    public static void main(String[] args) {

        int[] a = {3,6,1,9,2,4};

        WeightArray weightArray = new WeightArray(a,a.length);

        int i = WeightSupport.binarySearchIndex(weightArray, a.length, 9);

        System.out.println(i);
    }


}
