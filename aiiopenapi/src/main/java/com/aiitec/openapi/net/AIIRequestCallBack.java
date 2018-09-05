package com.aiitec.openapi.net;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.aiitec.openapi.cache.AIIPacketCacheManager;
import com.aiitec.openapi.cache.AiiFileCache;
import com.aiitec.openapi.constant.AIIConstant;
import com.aiitec.openapi.constant.AIIStatus;
import com.aiitec.openapi.constant.CommonKey;
import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.JSON;
import com.aiitec.openapi.json.enums.CombinationType;
import com.aiitec.openapi.model.ResponseQuery;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.PacketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 协议回调类
 * 
 * @author Anthony
 * @version 1.0
 * @param <T>
 * @createTime 2016-4-25
 */
public class AIIRequestCallBack<T> implements Callback {

	private AIIResponse<T> aiiResponse;
	private int index;
	private int sparseKey;
	private String cacheKey;
	private Context context;
	private CacheMode cacheMode;
	private AIIPacketCacheManager aiiPacketCacheManager;
	private CombinationType combinationType = JSON.combinationType;
	
	public AIIRequestCallBack(Context context, int index, int sparseKey) {
		this(context, null, index, sparseKey);
	}

	public AIIRequestCallBack(Context context, NoSessionListener noSessionListener, int index, int sparseKey) {
		this.context = context;
		this.index = index;
		this.sparseKey = sparseKey;
		this.noSessionListener = noSessionListener;
		aiiPacketCacheManager = new AIIPacketCacheManager(context);

	}

	public void setAiiResponse(AIIResponse<T> aiiResponse) {
		this.aiiResponse = aiiResponse;
		if (aiiResponse != null) {
			aiiResponse.onStart(index);
		}
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setKey(int key) {
		this.sparseKey = key;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public void setCacheModel(CacheMode cacheMode) {
		this.cacheMode = cacheMode;
	}

	public void setCombinationType(CombinationType combinationType) {
		this.combinationType = combinationType;
	}
	
	private NoSessionListener noSessionListener;

	public void setNoSessionListener(NoSessionListener noSessionListener) {
		this.noSessionListener = noSessionListener;
	}

	interface NoSessionListener {
		void onNosession(int index);
	}

	/** 网络异常，请求失败，但是这些回调居然在子线程中，气死我了 */
	@Override
	public void onFailure(Call arg0, IOException arg1) {
		arg1.printStackTrace();
		if (sparseKey >= 0) {
			AIIRequest.requestSparse.remove(sparseKey);// 队列在请求失败后删除
		}
		
		handler.sendEmptyMessage(2);

	}

	/** 回调成功，但是这些回调居然在子线程中，气死我了 */
	@Override
	public void onResponse(Call arg0, Response arg1) throws IOException {
		if (sparseKey >= 0) {
			AIIRequest.requestSparse.remove(sparseKey);// 队列在请求后删除
		}

		// for (String name: arg1.headers().names()) {
		// LogUtil.i("Header:  "+name+ "   "+arg1.headers().get(name));
		// }
		String responseContent = "";
		String contentEncoding = arg1.header("Content-Encoding");

		boolean isGzip = false;
		if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {

			InputStream inStream = arg1.body().byteStream();
			// long count = arg1.body().contentLength();
			// int count = 0;
			// while (count == 0) {
			// count = inStream.available();
			// }
			// LogUtil.i("gzip length:"+count);
			GZIPInputStream gzin = new GZIPInputStream(inStream);
			BufferedReader in = new BufferedReader(new InputStreamReader(gzin,
					"utf-8"));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			responseContent = sb.toString();
			isGzip=true;
//			LogUtil.i("isGzip=true;  unZip length:" + responseContent.length());

		} else {
			isGzip=false;
			responseContent = arg1.body().string();
//			LogUtil.i("isGzip=false; length:" + responseContent.length());
		}

		LogUtil.d(responseContent+"  \nisGzip:"+isGzip);
		int code = arg1.code();
		if (code == 200) {
			Message msg = new Message();
			msg.what = 1;
			msg.obj = responseContent;
			handler.sendMessage(msg);
		} else {

			handler.sendEmptyMessage(2);
		}

	}

	private void onFailure() {
		if (aiiResponse != null) {
			if(context != null && context instanceof FragmentActivity){
				if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
					return;//页面已销毁的话，就不需要调用回调接口了
				}
			}
			aiiResponse.onFailure("当前网络无法连接服务器", index);
			aiiResponse.onFinish(index);
		}
		readCache();
		
	}

	@SuppressWarnings("unchecked")
	private void onSuccess(String responseContent) {
		String cacheTimestamp = "";
		String namespace = "";
		String session = "";
		int status = -1;
		ResponseQuery query = null;

		if (aiiResponse != null) {

			T t = aiiResponse.get();
			
			if (t != null) {
				if (String.class != t.getClass()){//服务器返回json 前面多了一些错误的东西，稍微做些容错处理， 如果本身是String则忽略
					if(!responseContent.trim().startsWith("{")){
						int firstBraces = responseContent.indexOf("{");
						if(firstBraces > 0){//裁剪删除掉第一个大括号前面的东西
							responseContent = responseContent.substring(firstBraces, responseContent.length());
						}
					}
				}
				if (ResponseQuery.class.isAssignableFrom(t.getClass())) {
					JSONObject obj = null;
					try {
						obj = new JSONObject(responseContent);
					} catch (JSONException e) {
						if (aiiResponse != null) {
							if(context != null && context instanceof FragmentActivity){
								if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
									return;//页面已销毁的话，就不需要调用回调接口了
								}
							}
							aiiResponse.onServiceError("服务器数据异常", -1, index);
						}
						LogUtil.e("服务器数据异常" + e.getMessage());
						e.printStackTrace();
					}
					if (obj != null) {
						namespace = obj.optString("n");
						String queryString = obj.optString("q");
						session = obj.optString("s");
						query = (ResponseQuery) JSON.parseObject(queryString,
								aiiResponse.get().getClass(), combinationType);
						if (query != null) {
							status = query.getStatus();
						}

					}

				} else if (com.aiitec.openapi.packet.Response.class
						.isAssignableFrom(t.getClass())) {
					com.aiitec.openapi.packet.Response response = (com.aiitec.openapi.packet.Response) JSON
							.parseObject(responseContent, aiiResponse.get().getClass());
					if (response != null) {
						query = response.getQuery();
						status = query.getStatus();
						cacheTimestamp = query.getTimestamp();
						namespace = response.getNamespace();
						session = response.getSession();
					} else {
						if (aiiResponse != null) {
							if(context != null && context instanceof FragmentActivity){
								if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
									return;//页面已销毁的话，就不需要调用回调接口了
								}
							}
							aiiResponse.onServiceError("服务器数据异常", -1, index);
						}
						LogUtil.e("服务器数据异常" + responseContent);
					}

				} else if (String.class == t.getClass()) {
					t = (T) responseContent;
					if(context != null && context instanceof FragmentActivity){
						if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
							return;//页面已销毁的话，就不需要调用回调接口了
						}
					}
					aiiResponse.onSuccess(t, index);
				} else {// sendOther时可能用到这个, 未经严格测试，慎用!
					t = (T) JSON.parseObject(responseContent, t.getClass(), combinationType);
					if(context != null && context instanceof FragmentActivity){
						if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
							return;//页面已销毁的话，就不需要调用回调接口了
						}
					}
					if (t != null) {
						aiiResponse.onSuccess(t, index);
					} else {
						if (aiiResponse != null) {
							aiiResponse.onServiceError("服务器数据异常", -1, index);
						}
						LogUtil.e("服务器数据异常" + responseContent);
					}
				}
			} else {
				LogUtil.e("泛型获取异常");
			}

		}
		if(context != null && context instanceof FragmentActivity){
			if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
				return;//页面已销毁的话，就不需要调用回调接口了
			}
		}
		if (query != null && aiiResponse != null) {
			if (status == AIIStatus.SUCCESS) {
				// 成功
				if (cacheMode != null && cacheMode != CacheMode.NONE
						&& context != null && cacheKey != null) {
					// 如果是需要缓存的，就存储返回的数据
					AiiUtil.putString(context, cacheKey + "timestamp", cacheTimestamp);
					AiiUtil.putString(context, cacheKey + "namespace", namespace);
					aiiPacketCacheManager.put(cacheKey, responseContent);
				}

				if (namespace.equals("Session")) {
					PacketUtil.session_id = session;
					if (index != -1) {
						if (context != null) {
							AiiUtil.putString(context, CommonKey.KEY_SESSION, session);
						}
					}
				} else if (namespace.equals("UserDetails")) {
					// 如果是用户详情协议， 读取用户id存起来，缓存需要用
					try {
						JSONObject obj = new JSONObject(responseContent);
						JSONObject objUser = obj.optJSONObject("q")
								.optJSONObject("user");
						AIIConstant.USER_ID = objUser.optLong("id");
						AiiFileCache.changeDir(PacketUtil.getCacheDir(context));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				aiiResponse.onSuccess((T) query, index);
			} else {
				aiiResponse.onServiceError(query.getDesc(), status, index);
				switch (status) {
				case AIIStatus.NO_SESSION:// sessionId为空或不存在
					if (noSessionListener != null) {
						noSessionListener.onNosession(index);
					}
					break;
				case AIIStatus.LOGIN_AT_OTHER:// 用户在别处登录
					if (context != null) {
						try {
							context.sendBroadcast(new Intent(
									AIIConstant.FILTER_ACTION_LOGIN_ON_OTHER));
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					//
				case AIIStatus.UNLOGIN:// 未登录
				case AIIStatus.USER_INEXISTENCE:// 用户不存在
					if (aiiResponse != null) {
						aiiResponse.onLoginOut(status);
					}
					break;
				case AIIStatus.SESSION_EXPIRATION:// session会话过期
				case AIIStatus.USER_BELOCKED:// 用户被锁定
					if (noSessionListener != null) {
						noSessionListener.onNosession(index);
					}
					if (aiiResponse != null) {
						aiiResponse.onLoginOut(index);
					}
					break;
				case AIIStatus.CACHE_AVAILABLE:// 缓存可用
					
					readCache();
					break;
				case AIIStatus.OPTION_FAST:// 操作太快
					if (aiiResponse != null) {
						aiiResponse.onOptionFast(1, index);
					}
					break;

				default:

					break;
				}
			}
		} else {

		}
		if(context != null && context instanceof FragmentActivity){
			if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
				return;//页面已销毁的话，就不需要调用回调接口了
			}
		}
		if (aiiResponse != null) {
			aiiResponse.onFinish(index);
		}

	}

	@SuppressWarnings("unchecked")
	private void readCache() {
		if (aiiResponse != null && context != null && cacheKey != null) {
			// 如果缓存可用，那么就读取缓存
			String content = aiiPacketCacheManager.get(cacheKey);
			if (!TextUtils.isEmpty(content)) {
				Class<?> clazz = aiiResponse.get().getClass();
				if(context != null && context instanceof FragmentActivity){
					if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
						return;//页面已销毁的话，就不需要调用回调接口了
					}
				}
				if(clazz.equals(String.class)){
					aiiResponse.onCache((T) content, index);
				} else {
					if (ResponseQuery.class.isAssignableFrom(clazz)) {
						try {
							JSONObject obj = new JSONObject(content);
							content = obj.optString("q");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					Object cache = JSON.parseObject(content, clazz, combinationType);
					if(cache != null){
						if(context != null && context instanceof FragmentActivity){
							if(((FragmentActivity)context).getSupportFragmentManager().isDestroyed()){
								return;//页面已销毁的话，就不需要调用回调接口了
							}
						}
						aiiResponse.onCache((T) cache, index);
					}
				}
			}
		}
	}

	Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1) {
				onSuccess(msg.obj.toString());
			} else if (msg.what == 2) {
				onFailure();
			}
			return false;
		}
	});
}
