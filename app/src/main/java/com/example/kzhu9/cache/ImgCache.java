package com.example.kzhu9.cache;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by slgu1 on 12/22/15.
 */
import java.util.*;

public class ImgCache {

    static class Datanode{
        Datanode prev = null;
        Datanode next = null;
        Bitmap value;
        String key;
        Datanode(String key, Bitmap value){
            this.key = key;
            this.value = value;
        }
    }
    static class Mylinkedlist{
        Datanode head = null;
        Datanode last = null;
        void addLast(Datanode datanode){
            if(head == null){
                head = datanode;
                last = datanode;
                return;
            }
            last.next = datanode;
            datanode.prev = last;
            datanode.next = null;
            last = datanode;
        }
        void debug(){
            Datanode tmp = head;
            while(tmp != null){
                System.out.print(tmp.value);
                System.out.print(' ');
                tmp = tmp.next;
            }
            System.out.println("");
        }
        void addFirst(Datanode datanode){
            if(head == null){
                head = datanode;
                last = datanode;
                return;
            }
            datanode.prev = null;
            datanode.next = head;
            head.prev = datanode;
            head = datanode;
        }
        void remove(Datanode datanode){
            if(datanode.prev != null){
                datanode.prev.next = datanode.next;
            }
            else{
                //IMPORTANT TO SET
                head = datanode.next;
            }
            if(datanode.next != null){
                datanode.next.prev = datanode.prev;
            }
            else {
                //IMPORTANT TO SET
                last = datanode.prev;
            }
        }
        Datanode popLast(){
            Datanode result = last;
            if(last != null){
                if(head == last) {
                    head = null;
                    last = null;
                }
                else{
                    last = last.prev;
                    last.next = null;
                }
            }
            return result;
        }
        Datanode popFirst(){
            Datanode result = head;
            if(head != null){
                if(head == last){
                    head = null;
                    last = null;
                }
                else{
                    head = head.next;
                    head.prev = null;
                }
            }
            return result;
        }
    }
    int capacity;
    int usage = 0;
    Mylinkedlist lru_list = new Mylinkedlist();
    HashMap <String, Datanode> dict = new HashMap<String, Datanode>();
    public ImgCache(int capacity) {
        this.capacity = capacity;
    }

    public Bitmap get(String key) {
        if(dict.containsKey(key)){
            Datanode data = dict.get(key);
            lru_list.remove(data);
            lru_list.addLast(data);
            return data.value;
        }
        return null;
    }

    public void put(String key, Bitmap value) {
        if(dict.containsKey(key)){
            //reorder lru_list
            Datanode datanode = dict.get(key);
            lru_list.remove(datanode);
            //set new value
            datanode.value = value;
            lru_list.addLast(datanode);
        }
        else{
            if(usage == capacity){
                Datanode node_delete = lru_list.popFirst();
                dict.remove(node_delete.key);
                --usage;
            }
            Datanode new_data = new Datanode(key, value);
            dict.put(key, new_data);
            lru_list.addLast(new_data);
            ++usage;
        }
    }
    private static ImgCache cache;
    public static synchronized ImgCache single() {
        if (cache == null) {
            cache = new ImgCache(50);
            return cache;
        }
        return cache;
    }
}
