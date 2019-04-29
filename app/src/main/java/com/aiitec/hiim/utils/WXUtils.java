//package com.aiitec.hiim.utils;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//
//import com.aiitec.hiim.R;
//import com.aiitec.hiim.im.location.util.ToastUtil;
//import com.aiitec.hiim.im.utils.LogUtil;
//import com.bumptech.glide.Glide;
//import com.tencent.mm.opensdk.constants.Build;
//import com.tencent.mm.opensdk.modelmsg.SendAuth;
//import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.opensdk.modelmsg.WXImageObject;
//import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
//import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
//import com.tencent.mm.opensdk.modelpay.PayReq;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//
//import java.io.ByteArrayOutputStream;
//
///**
// * Created by ailibin on 2018/2/28.
// * 微信登录工具类
// */
//
//public class WXUtils {
//
//    public static IWXAPI wxApi;
//    /**
//     * 微信好友(发送到聊天界面)
//     */
//    public static final int WXFRIEND = 1;
//    /**
//     * 朋友圈
//     */
//    public static final int WXCIRCLE = 2;
//    private static WXMediaMessage wxMessage;
//
//    /**
//     * 是充值爱心还是支付还是充值钱包的类型
//     */
//    public static int mType = 0;
//
//    //开个线程再进行一次压缩
//    @SuppressLint("HandlerLeak")
//    private static Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                Bitmap thumbBmp = (Bitmap) msg.obj;
//                int action = msg.arg1;
//                byte[] imageBytes = BitmapUtil.bmpToByteArray(thumbBmp, true);
//                wxMessage.thumbData = imageBytes;
//
//                //构造一个req
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                //请求的唯一标识
//                req.transaction = mShareInfo.getReqTag();
//                req.message = wxMessage;
//                //分享到微信好友
//                if (action == WXFRIEND) {
//                    req.scene = SendMessageToWX.Req.WXSceneSession;
//                } else {
//                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
//                }
//                //调用api接口发送分享数据到微信
//                wxApi.sendReq(req);
//            }
//        }
//    };
//
//    /**
//     * 创建微信api将app注册到微信
//     *
//     * @param context
//     */
//    public static void initWX(Context context) {
//        //创建微信api
//        wxApi = WXAPIFactory.createWXAPI(context, ContentUtil.wechatAppId);
//        // 将该app注册到微信
//        wxApi.registerApp(ContentUtil.wechatAppId);
//
//    }
//
//    /**
//     * 用户授权
//     */
//    public static void authorizationLogin() {
//        if (!wxApi.isWXAppInstalled()) {
//            BaseUtil.showToast("您还没有安装微信客户端！");
//            return;
//        }
//        //发送授权请求
//        final SendAuth.Req req = new SendAuth.Req();
//        req.scope = "snsapi_userinfo";
//        req.state = "letar_weixin_login_authorization";
//        boolean isOK = wxApi.sendReq(req);
//        LogUtil.d("ailibin", "isOK: " + isOK);
//    }
//
//    /**
//     * 用户授权
//     */
//    public static void authorizationBind() {
//        if (!wxApi.isWXAppInstalled()) {
//            BaseUtil.showToast("您还没有安装微信客户端！");
//            return;
//        }
//        //发送授权请求
//        final SendAuth.Req req = new SendAuth.Req();
//        req.scope = "snsapi_userinfo";
//        req.state = "letar_weixin_bind_authorization";
//        boolean isOK = wxApi.sendReq(req);
//        LogUtil.d("ailibin", "isOK: " + isOK);
//    }
//
//
//    public synchronized static void pay(Context context, Wxpay wxpay) {
////        mType = type;
//        if (wxApi == null) {
//            initWX(context);
//        }
//        if (isWXAppInstalled(context)) {
//            if (isPaySupported(context)) {
//                sendPayReq(context, wxpay);
//            } else {
//                ToastUtil.show(context, "你的微信版本不支持支付功能");
//            }
//        } else {
//            ToastUtil.show(context, "你的手机未安装微信");
//        }
//    }
//
//    private synchronized static void sendPayReq(Context context, Wxpay wxpay) {
//        sendPayReq(context, wxpay, -1);
//    }
//
//
//    /**
//     * @param context
//     * @param wxpay
//     * @param type    根据不同的行为刷新不同的界面
//     */
//    private synchronized static void sendPayReq(Context context, Wxpay wxpay, int type) {
//
//        try {
//            if (wxpay != null) {
//                //if (wxpay.getRetcode().equals("0")) {
//                PayReq request = new PayReq();
//                request.appId = ContentUtil.wechatAppId;
//                request.partnerId = wxpay.getPartnerid() + "";
//                request.prepayId = wxpay.getPrepayid();
//                request.nonceStr = wxpay.getNoncestr();
//                request.timeStamp = wxpay.getTimestamp();
//                request.packageValue = wxpay.getPackager();
//                request.sign = wxpay.getSign();
//                request.extData = wxpay.getAppid();
//                wxApi.sendReq(request);
//                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//                //} else {
//                //ToastUtil.show(context, "返回错误" + wxpay.getRetmsg());
//                //}
//            } else {
//                ToastUtil.show(context, "服务器请求错误");
//            }
//        } catch (Exception e) {
//            ToastUtil.show(context, "异常：" + e.getMessage());
//        }
//    }
//
//
//    /**
//     * 判断是否安装微信
//     */
//    public synchronized static boolean isWXAppInstalled(Context context) {
//        if (wxApi == null) {
//            initWX(context);
//        }
//        return wxApi.isWXAppInstalled();
//
//    }
//
//    /**
//     * 判断微信版本是否支持支付功能
//     */
//    public synchronized static boolean isPaySupported(Context context) {
//        if (wxApi == null) {
//            initWX(context);
//        }
//        return wxApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
//    }
//
//
//    /**
//     * 发起分享
//     *
//     * @param context
//     * @param action    微信好友/朋友圈
//     * @param shareInfo 分享需要的信息对象
//     */
//    public static void toShare(final Context context, final int action, final WxShareInfo shareInfo) {
//
//        LogUtil.d("ailibin", "shareInfo: " + shareInfo.toString());
//        // 初始化一个webpageobject对象，填写URL
//        WXWebpageObject webPage = new WXWebpageObject();
//        webPage.webpageUrl = shareInfo.getUrl();
//        mShareInfo = shareInfo;
//        wxMessage = new WXMediaMessage(webPage);
//        //标题
//        wxMessage.title = shareInfo.getTitle();
//        //描述
//        wxMessage.description = shareInfo.getDescription();
//        //图标(这里有个bug就是图片大于32k就分享不出去),所以要开线程进行处理
//        byte[] imageBytes = null;
//        if (!TextUtils.isEmpty(shareInfo.getImageUrl())) {//有网络路径
//            //将网络图片路径转换为字节数组(应为异步操作)
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Bitmap bitmap = Glide.with(context)
//                                .load(ImagePathUtil.getWholeImagePath(shareInfo.getImageUrl()))
//                                .asBitmap()
//                                .into(32, 48)
//                                .get();
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        int len = baos.toByteArray().length;
//                        Message message = Message.obtain();
//                        message.obj = bitmap;
//                        message.arg1 = action;
//                        message.what = 1;
//                        mHandler.sendMessage(message);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        LogUtil.w("ailibin", "获取网络图片出错了。" + shareInfo.getImageUrl() + "\n" + e.getMessage());
//                    }
//                }
//            }).start();
//            return;
//        } else {//没有网络路径
//            Bitmap bitmap;
//            //有设置本地图片
//            if (shareInfo.getImageRes() != -1) {
//                bitmap = BitmapFactory.decodeResource(context.getResources(), shareInfo.getImageRes());
//            } else {//没有设置本地图片
//                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//            }
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 32, 48, true);
//            bitmap.recycle();
//            imageBytes = BitmapUtil.bmpToByteArray(thumbBmp, true);
//        }
//        wxMessage.thumbData = imageBytes;
//        //构造一个req
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        //请求的唯一标识
//        req.transaction = shareInfo.getReqTag();
//        req.message = wxMessage;
//        //分享到微信好友
//        if (action == WXFRIEND) {
//            req.scene = SendMessageToWX.Req.WXSceneSession;
//        } else {
//            req.scene = SendMessageToWX.Req.WXSceneTimeline;
//        }
//        //调用api接口发送分享数据到微信
//        wxApi.sendReq(req);
//    }
//
//    /**
//     * 分享二维码
//     *
//     * @param context
//     * @param action
//     * @param bmp
//     */
//    public static void toShare(Context context, int action, Bitmap bmp) {
//
////        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.my_btn_add2x);
//        //初始化WXImageObject和WXMediaMessage对象
//        WXImageObject imgObj = new WXImageObject(bmp);
//        WXMediaMessage msg = new WXMediaMessage();
//        //描述
//        msg.description = "扫码加我为好友";
//        msg.mediaObject = imgObj;
//
//        //设置缩略图
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
//        msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
//        //        thumbBmp.recycle();
//
//        //构造一个req
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        //请求的唯一标识
//        req.transaction = "img";
//        req.message = msg;
//        //分享到微信好友
//        if (action == WXFRIEND) {
//            req.scene = SendMessageToWX.Req.WXSceneSession;
//        } else {
//            req.scene = SendMessageToWX.Req.WXSceneTimeline;
//        }
//        //调用api接口发送数据到微信
//        boolean isShareOK = wxApi.sendReq(req);
//        LogUtil.d("ailibin", "isShareOK: " + isShareOK);
//    }
//
//
//}
