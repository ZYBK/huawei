package com.tanwan.sslmly.lianyun.ulit;


import java.util.HashMap;

/**
 * 节点数据
 */
class Node{
    String key;
    byte[] value;
    Node pre;
    Node next;

    public Node(String key, byte[] value){
        this.key = key;
        this.value = value;
    }
}

/**
 * LRU缓存算法
 */
public class LRUCache {
    int capacity;
    HashMap<String, Node> map = new HashMap<String, Node>();
    Node head=null;
    Node end=null;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    public synchronized byte[] get(String key) {
        if(map.containsKey(key)){
            Node n = map.get(key);
            remove(n);
            setHead(n);
            return n.value;
        }

        return null;
    }

    private void remove(Node n){
        if(n.pre!=null){
            n.pre.next = n.next;
        }else{
            head = n.next;
        }

        if(n.next!=null){
            n.next.pre = n.pre;
        }else{
            end = n.pre;
        }

    }

    private void setHead(Node n){
        n.next = head;
        n.pre = null;

        if(head!=null)
            head.pre = n;

        head = n;

        if(end ==null)
            end = head;
    }

    public synchronized void set(String key, byte[] value) {
        if(map.containsKey(key)){
            Node old = map.get(key);
            old.value = value;
            remove(old);
            setHead(old);
        }else{
            Node created = new Node(key, value);
            if(map.size()>=capacity){
                map.remove(end.key);
                remove(end);
                setHead(created);

            }else{
                setHead(created);
            }

            map.put(key, created);
        }
    }
}
