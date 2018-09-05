package com.aiitec.openapi.cache;

import com.aiitec.openapi.db.annotation.Unique;
import com.aiitec.openapi.model.Entity;

@SuppressWarnings("serial")
public class CacheModel extends Entity{

	protected long id = -1;
	private long userId = -1;
	private int count;
	private int countOfRequest;
	private String subFolder;
	@Unique
	private String md5RemoveTimestempLasted;
	private String md5;
	private String jsonString;
	private String cacheJsonPacketPath;
	private String cacheNamespace;
	private String timestempLasted;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCountOfRequest() {
		return countOfRequest;
	}
	public void setCountOfRequest(int countOfRequest) {
		this.countOfRequest = countOfRequest;
	}
	public String getSubFolder() {
		return subFolder;
	}
	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}
	public String getMd5RemoveTimestempLasted() {
		return md5RemoveTimestempLasted;
	}
	public void setMd5RemoveTimestempLasted(String md5RemoveTimestempLasted) {
		this.md5RemoveTimestempLasted = md5RemoveTimestempLasted;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public String getCacheJsonPacketPath() {
		return cacheJsonPacketPath;
	}
	public void setCacheJsonPacketPath(String cacheJsonPacketPath) {
		this.cacheJsonPacketPath = cacheJsonPacketPath;
	}
	public String getCacheNamespace() {
		return cacheNamespace;
	}
	public void setCacheNamespace(String cacheNamespace) {
		this.cacheNamespace = cacheNamespace;
	}
	public String getTimestempLasted() {
		return timestempLasted;
	}
	public void setTimestempLasted(String timestempLasted) {
		this.timestempLasted = timestempLasted;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
}
