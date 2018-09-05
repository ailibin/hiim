package com.aiitec.openapi.enums;
/**
 * 缓存模式
 * @author Anthony
 * @createTime 2016-3-24
 */
public enum CacheMode {
    /**不缓存*/
	NONE,
	/**经常更新模式，一般每次都读缓存，常用于详情协议，但是网络数据返回后有被更新掉*/
	PRIORITY_OFTEN,
	/**正常列表模式，返回1020就读缓存*/
	PRIORITY_COMMON,
//	/**检查更新列表模式，如参照项列表， 分类列表等返回1020就读缓存*/
//	PRIORITY_SOMETIME,
//	/**偶尔更新列表模式，启动APP就请求一次，其它时间也不检查更新，返回1020就读缓存*/
//	PRIORITY_OCCASIONALLY;  这个感觉不适用
	
}
