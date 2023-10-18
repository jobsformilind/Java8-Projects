package com.test.stock.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MultiValueMap<K, V> {
	private List<Entry<K, V>> entries = new ArrayList<>();

	public int size() {
		return entries.size();
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public V get(Object key) {
		return entries.stream().filter(k -> k.equals(key)).map(Entry::getValue).findFirst().orElse(null);
	}

	public void put(K key, V value) {
		entries.add(new Entry(key, value));
	}

	public List<K> keyList() {
		return entries.stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	public List<V> values() {
		return entries.stream().map(e -> e.getValue()).collect(Collectors.toList());
	}

	public List<Entry<K, V>> entryList() {
		return entries;
	}

	@SuppressWarnings("hiding")
	public class Entry<K, V> {
		K key;
		V value;

		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
	}
}
