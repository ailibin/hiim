package com.aiitec.openapi.cache;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;








import android.content.Context;


/**
 * @author shc
 * @since 2015-05-04
 * 缓存工具类
 */
public class AiiFileCache implements AiiCache{
	protected static final int TIME_HOUR = 60 * 60;
	protected static final int TIME_DAY = TIME_HOUR * 24;
	protected static final int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
	protected static final int MAX_COUNT = Integer.MAX_VALUE; // 不限制存放数据的数量
	private static volatile AiiFileCache instance;
	private ACacheManager mCache;
	private String cacheDir;
	
	public static AiiFileCache getInstance(Context context, String cacheDir) {
	        if (instance == null) {
	            synchronized (AiiFileCache.class) {
	                if (instance == null) {
	                    instance = new AiiFileCache(new File(cacheDir),MAX_SIZE,MAX_COUNT);
	                	instance.cacheDir = cacheDir;
	                }
	            }
	        }
	        return instance;
	    }


	private AiiFileCache(){
		
	}
	public static AiiFileCache changeDir(String cacheDir) {
		if(instance != null && instance.cacheDir != null && instance.cacheDir.equals(cacheDir)){
			//如果路径相同就不用重建
		} else {
			instance = new AiiFileCache(new File(cacheDir),MAX_SIZE,MAX_COUNT);
			instance.cacheDir = cacheDir;
		}
		return instance;
	}

	private AiiFileCache(File cacheDir, long max_size, int max_count) {
		if (!cacheDir.exists() && !cacheDir.mkdirs()) {
//			throw new RuntimeException("can't make dirs in "
//					+ cacheDir.getAbsolutePath());
			return;
		}
		mCache = new ACacheManager(cacheDir, max_size, max_count);
	}

	// =======================================
	// ============ String数据 读写 ==============
	// =======================================
	/**
	 * 保存 String数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的String数据
	 */
	public void put(String key, String value) {
		if(mCache == null) return;
			
		File file = mCache.newFile(key);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file), 1024);
			out.write(value);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mCache.put(file);
		}
	}

	/**
	 * 保存 String数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的String数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, String value, int saveTime) {
		put(key, Utils.newStringWithDateInfo(saveTime, value));
	}

	/**
	 * 读取 String数据
	 * 
	 * @param key
	 * @return String 数据
	 */
	public String get(String key) {
		if(mCache == null) return null;
		File file = mCache.get(key);
		if (!file.exists())
			return null;
		boolean removeFile = false;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			String readString = "";
			String currentLine;
			while ((currentLine = in.readLine()) != null) {
				readString += currentLine;
			}
			if (!Utils.isDue(readString)) {
				return Utils.clearDateInfo(readString);
			} else {
				removeFile = true;
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (removeFile)
				remove(key);
		}
	}


	/**
	 * 获取缓存文件
	 * 
	 * @param key
	 * @return value 缓存的文件
	
//	public File file(String key) {
//		File f = mCache.newFile(key);
//		if (f.exists())
//			return f;
//		return null;
//	} */

	/**
	 * 移除某个key
	 * 
	 * @param key
	 * @return 是否移除成功
	 */
	public boolean remove(String key) {
		if(mCache == null) return false;
		return mCache.remove(key);
	}

	/**
	 * 清除所有数据
	 */
	public void clear() {
		if(mCache == null) return;
		mCache.clear();
	}

	/**
	 * @title 缓存管理器
	 * @author shc
	 * @version 1.0
	 */
	public class ACacheManager {
		private final AtomicLong cacheSize;
		private final AtomicInteger cacheCount;
		private final long sizeLimit;
		private final int countLimit;
		private final Map<File, Long> lastUsageDates = Collections
				.synchronizedMap(new HashMap<File, Long>());
		protected File cacheDir;

		private ACacheManager(File cacheDir, long sizeLimit, int countLimit) {
			this.cacheDir = cacheDir;
			this.sizeLimit = sizeLimit;
			this.countLimit = countLimit;
			cacheSize = new AtomicLong();
			cacheCount = new AtomicInteger();
			calculateCacheSizeAndCacheCount();
		}

		/**
		 * 计算 cacheSize和cacheCount
		 */
		private void calculateCacheSizeAndCacheCount() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					int size = 0;
					int count = 0;
					File[] cachedFiles = cacheDir.listFiles();
					if (cachedFiles != null) {
						for (File cachedFile : cachedFiles) {
							size += calculateSize(cachedFile);
							count += 1;
							lastUsageDates.put(cachedFile,
									cachedFile.lastModified());
						}
						cacheSize.set(size);
						cacheCount.set(count);
					}
				}
			}).start();
		}

		private void put(File file) {
			int curCacheCount = cacheCount.get();
			while (curCacheCount + 1 > countLimit) {
				long freedSize = removeNext();
				cacheSize.addAndGet(-freedSize);

				curCacheCount = cacheCount.addAndGet(-1);
			}
			cacheCount.addAndGet(1);

			long valueSize = calculateSize(file);
			long curCacheSize = cacheSize.get();
			while (curCacheSize + valueSize > sizeLimit) {
				long freedSize = removeNext();
				curCacheSize = cacheSize.addAndGet(-freedSize);
			}
			cacheSize.addAndGet(valueSize);

			Long currentTime = System.currentTimeMillis();
			file.setLastModified(currentTime);
			lastUsageDates.put(file, currentTime);
		}

		private File get(String key) {
			File file = newFile(key);
			Long currentTime = System.currentTimeMillis();
			file.setLastModified(currentTime);
			lastUsageDates.put(file, currentTime);

			return file;
		}

		private File newFile(String key) {
			return new File(cacheDir, key );
//			return new File(cacheDir, key.hashCode() + "");
		}

		private boolean remove(String key) {
			File image = get(key);
			return image.delete();
		}

		private void clear() {
			lastUsageDates.clear();
			cacheSize.set(0);
			File[] files = cacheDir.listFiles();
			if (files != null) {
				for (File f : files) {
					f.delete();
				}
			}
		}

		/**
		 * 移除旧的文件
		 * 
		 * @return
		 */
		private long removeNext() {
			if (lastUsageDates.isEmpty()) {
				return 0;
			}

			Long oldestUsage = null;
			File mostLongUsedFile = null;
			Set<Entry<File, Long>> entries = lastUsageDates.entrySet();
			synchronized (lastUsageDates) {
				for (Entry<File, Long> entry : entries) {
					if (mostLongUsedFile == null) {
						mostLongUsedFile = entry.getKey();
						oldestUsage = entry.getValue();
					} else {
						Long lastValueUsage = entry.getValue();
						if (lastValueUsage < oldestUsage) {
							oldestUsage = lastValueUsage;
							mostLongUsedFile = entry.getKey();
						}
					}
				}
			}

			long fileSize = calculateSize(mostLongUsedFile);
			if (mostLongUsedFile.delete()) {
				lastUsageDates.remove(mostLongUsedFile);
			}
			return fileSize;
		}

		private long calculateSize(File file) {
			return file.length();
		}
	}

	/**
	 * @title 时间计算工具类
	 * @author shc
	 * @version 1.0
	 */
	private static class Utils {

		/**
		 * 判断缓存的String数据是否到期
		 * 
		 * @param str
		 * @return true：到期了 false：还没有到期
		 */
		private static boolean isDue(String str) {
			return isDue(str.getBytes());
		}

		/**
		 * 判断缓存的byte数据是否到期
		 * 
		 * @param data
		 * @return true：到期了 false：还没有到期
		 */
		private static boolean isDue(byte[] data) {
			String[] strs = getDateInfoFromDate(data);
			if (strs != null && strs.length == 2) {
				String saveTimeStr = strs[0];
				while (saveTimeStr.startsWith("0")) {
					saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
				}
				long saveTime = Long.valueOf(saveTimeStr);
				long deleteAfter = Long.valueOf(strs[1]);
				if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
					return true;
				}
			}
			return false;
		}

		private static String newStringWithDateInfo(int second, String strInfo) {
			return createDateInfo(second) + strInfo;
		}

		private static String clearDateInfo(String strInfo) {
			if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
				strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1,
						strInfo.length());
			}
			return strInfo;
		}

		private static boolean hasDateInfo(byte[] data) {
			return data != null && data.length > 15 && data[13] == '-'
					&& indexOf(data, mSeparator) > 14;
		}

		private static String[] getDateInfoFromDate(byte[] data) {
			if (hasDateInfo(data)) {
				String saveDate = new String(copyOfRange(data, 0, 13));
				String deleteAfter = new String(copyOfRange(data, 14,
						indexOf(data, mSeparator)));
				return new String[] { saveDate, deleteAfter };
			}
			return null;
		}

		private static int indexOf(byte[] data, char c) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] == c) {
					return i;
				}
			}
			return -1;
		}

		private static byte[] copyOfRange(byte[] original, int from, int to) {
			int newLength = to - from;
			if (newLength < 0)
				throw new IllegalArgumentException(from + " > " + to);
			byte[] copy = new byte[newLength];
			System.arraycopy(original, from, copy, 0,
					Math.min(original.length - from, newLength));
			return copy;
		}

		private static final char mSeparator = ' ';

		private static String createDateInfo(int second) {
			String currentTime = System.currentTimeMillis() + "";
			while (currentTime.length() < 13) {
				currentTime = "0" + currentTime;
			}
			return currentTime + "-" + second + mSeparator;
		}

	}


}

