package com.aiitec.openapi.net;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.aiitec.openapi.constant.AIIConstant;
import com.aiitec.openapi.constant.AIIStatus;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.PacketUtil;
import com.aiitec.openapi.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AIIResponse<T> implements AIIResponseListener<T> {

    private Class<T> entityClass;
    private Context context;
    // Dialog只需设置一次，所以用静态；
    private Dialog dialog;
    private T t;
    // 默认显示dialog, 可用通过isShowDilog(false)设置不显示dialog;
    private boolean isShowDilog = true;

    public void setShowDilog(boolean isShowDilog) {
        this.isShowDilog = isShowDilog;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public AIIResponse(Context context) {
        this(context, true);
    }

    public AIIResponse(Context context, boolean isShowDilog) {
    	this(context, null, isShowDilog);
    }
    
    public AIIResponse(Context context, Dialog dialog) {
        this(context, dialog, true);
    }
    
    public AIIResponse(Context context, Dialog dialog, boolean isShowDilog) {
        this.isShowDilog = isShowDilog;
        this.dialog = dialog;
        init(context);
    }

   

    @SuppressWarnings("unchecked")
    private void init(Context context) {
        this.context = context;
        Type genType = getClass().getGenericSuperclass();
        if (genType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (params != null && params.length > 0) {
                entityClass = (Class<T>) params[0];
                if(entityClass != null){
            		try {
                        t = entityClass.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
            	} else {
            		LogUtil.e("entityClass:"+entityClass);
            	}
            }
        }
        if (isShowDilog && dialog == null) {
            dialog = new ProgressDialog(context);
            ((ProgressDialog) dialog).setMessage("正在加载......");
          
        }
    }

    @Override
    public T get() {
        if (t == null) {
        	if(entityClass != null){
        		try {
                    return entityClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        	} else {
        		LogUtil.e("entityClass>>>"+entityClass);
        	}
            
        }

        return t;
    }

    @Override
    public void onFailure(String content, int index) {
        if (context != null) {
            ToastUtil.show(context, "当前网络无法连接服务器");
        }
    }

    @Override
    public void onSuccess(T response, int index) {

    }

    @Override
    public void onStart(int index) {
        showDialog();
    }

    public void showDialog() {

        if (isShowDilog && dialog != null && !dialog.isShowing()) {
        	try {
        		 if(context!= null && context instanceof Activity){
                     if(!((Activity)context).isFinishing()){
                         dialog.show();
                     }
                 } else {
                     dialog.show();
                 }
			} catch (Exception e) {
				e.printStackTrace();
			}
           
            
        }
    }

    public void dismissDialog() {
        if(isShowDilog && dialog != null && dialog.isShowing()){
            try {
                if(context!=null && context instanceof Activity){
                    if(!((Activity)context).isFinishing()){
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          
//            dialog = null;  //本来要设置为空的，但是假设我用Activity里的dialog，这里写成空了，其他协议，就没有dialog了。
        }
        
       
    }

    @Override
    public void onFinish(int index) {
        dismissDialog();
    }

    @Override
    public void onLoginOut(int status) {
        if (context != null) {
            //这个服务端设计有问题，有原来的session永远都登录不了，那么如果已经下线了，就把session去掉，重新请求
            PacketUtil.session_id = null;
            Intent intent = new Intent(AIIConstant.FILTER_ACTION_LOGIN);
            if(context instanceof Activity){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.putExtra(AIIConstant.EXTRA_STATUS, status);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }

    @Override
    public void onCache(T content, int index) {

    }

    @Override
    public void onOptionFast(int type, int index) {
    	LogUtil.w("操作太快");
    }

    @Override
    public void onServiceError(String content, int status, int index) {
        if (context != null) {
            // && status != AIIStatus.LOGIN_AT_OTHER
            // && status != AIIStatus.UNLOGIN
            // && status != AIIStatus.USER_INEXISTENCE
            // && status != AIIStatus.USER_BELOCKED
            // 没有Session , Session会话过期， 缓存可用， 操作太快 这几项不给用户提示， 其他错误提示

            if (status != AIIStatus.NO_SESSION && status != AIIStatus.SESSION_EXPIRATION
                    && status != AIIStatus.CACHE_AVAILABLE && status != AIIStatus.OPTION_FAST) {

                ToastUtil.show(context, content);
            }
        }
    }

}
