package org.gpsanonymity.data;

import java.util.Collection;
import java.util.HashMap;

public class BiMap<E,D> {
	protected HashMap<E,D> keyValue;
	protected HashMap<D,E> valueKey;
	
	public BiMap() {
		keyValue = new HashMap<E, D>();
		valueKey = new HashMap<D, E>();
	}
	
	public E getKey(D value){
		return valueKey.get(value);
	}
	public D getValue(E key){
		return keyValue.get(key);
	}
	public void putKeyValue(E key, D value){
		putValueKey(value,key);
	}
	public void putValueKey(D value, E key){
		keyValue.put(key, value);
		valueKey.put(value,key);
	}
	public Collection<D> values(){
		return keyValue.values();
	}
	public Collection<E> keys(){
		return valueKey.values();
	}

	public boolean containsKey(E key) {
		return keyValue.containsKey(key);
	}
	public boolean containsValue(D value) {
		return valueKey.containsKey(value);
	}
	public void removeKey(E key){
		valueKey.remove(keyValue.get(key));
		keyValue.remove(key);
	}
	public void removeValue(D value){
		keyValue.remove(valueKey.get(value));
		valueKey.remove(value);
	}
	
}
