package com.zjl.ad.index;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/7/26 18:58
 */
public interface IndexAware<K, V> {

    V get(K key);

    void add(K key, V value);

    void update(K key, V value);

    void delete(K key, V value);
}
