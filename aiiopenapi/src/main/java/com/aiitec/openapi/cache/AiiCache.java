package com.aiitec.openapi.cache;


public interface AiiCache {
	public  String get(String key);
	public  void put(String key, String value);
	public  void put(String key, String value,int saveTime);
}
