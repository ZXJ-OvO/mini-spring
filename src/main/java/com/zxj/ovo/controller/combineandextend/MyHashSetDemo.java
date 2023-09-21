package com.zxj.ovo.controller.combineandextend;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/***
 * 每次往集合中添加数据的时候，都做一次记录，统计总共添加了多少次
 * @param <E>
 */
public class MyHashSetDemo<E> {

    //统计
    int count;

    public HashSet<E> getHset() {
        return hset;
    }

    public void setHset(HashSet<E> hset) {
        this.hset = hset;
    }

    private HashSet<E> hset;


    public boolean add(E e) {
        count++;
        return hset.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        count += c.size();
        return hset.addAll(c);
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) {
        MyHashSetDemo<Integer> set = new MyHashSetDemo<>();
        set.setHset(new HashSet<>());
        set.add(1);
        set.add(2);
        set.add(3);
        System.out.println(set.getCount());   //输出 = 3

        set.addAll(List.of(4, 5, 6));   //正确输出 = 6
        System.out.println(set.getCount());
    }
}
