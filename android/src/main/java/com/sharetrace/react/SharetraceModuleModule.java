package com.sharetrace.react;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import cn.net.shoot.sharetracesdk.AppData;
import cn.net.shoot.sharetracesdk.ShareTrace;
import cn.net.shoot.sharetracesdk.ShareTraceInstallListener;
import cn.net.shoot.sharetracesdk.ShareTraceWakeUpListener;

public class SharetraceModuleModule extends ReactContextBaseJavaModule {

    private static final String KEY_CODE = "code";
    private static final String KEY_MSG = "msg";
    private static final String KEY_PARAMSDATA = "paramsData";
    private static final String KEY_CHANNEL = "channel";

    private static final String MODULE_NAME = "SharetraceModule";

    private boolean hasInitialized = false;
    private boolean hasRegisterWakeUp = false;
    private WritableMap cacheWakeUpData = null;
    private final ReactApplicationContext reactContext;
    private Intent wakeUpCacheIntent = null;

    public SharetraceModuleModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        reactContext.addActivityEventListener(new ActivityEventListener() {
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

            }

            @Override
            public void onNewIntent(Intent intent) {
                processWakeUp(intent, null);
            }
        });
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    private WritableMap parseToResult(int code, String msg, String paramsData, String channel) {
        WritableMap result = Arguments.createMap();
        result.putInt(KEY_CODE, code);
        result.putString(KEY_MSG, msg);
        result.putString(KEY_PARAMSDATA, paramsData);
        result.putString(KEY_CHANNEL, channel);
        return result;
    }

    private void processWakeUp(Intent intent, final Callback callback) {
        if (!hasInitialized) {
            wakeUpCacheIntent = intent;
            return;
        }
        ShareTrace.getWakeUpTrace(intent, new ShareTraceWakeUpListener() {
            @Override
            public void onWakeUp(AppData appData) {
                if (appData == null) {
                    return;
                }

                WritableMap ret = extractRetMap(appData);
                if (!hasRegisterWakeUp) {
                    cacheWakeUpData = ret;
                    return;
                }

                wakeUpCallback(callback, ret);
            }
        });
    }

    @ReactMethod
    public void startInit() {
        if (reactContext == null) {
            return;
        }
        ShareTrace.init((Application) reactContext.getApplicationContext());
        onModuleInitStart();
    }

    private void onModuleInitStart() {
        hasInitialized = true;
        if (wakeUpCacheIntent == null) {
            return;
        }
        ShareTrace.getWakeUpTrace(wakeUpCacheIntent, new ShareTraceWakeUpListener() {
            @Override
            public void onWakeUp(AppData appData) {
                wakeUpCacheIntent = null;
                if (appData == null) {
                    return;
                }

                WritableMap ret = extractRetMap(appData);
                wakeUpCallback(null, ret);
            }
        });
    }

    @ReactMethod
    public void getWakeUp(final Callback callback) {
        hasRegisterWakeUp = true;
        if (cacheWakeUpData != null) {
            wakeUpCallback(callback, cacheWakeUpData);
            cacheWakeUpData = null;
            return;
        }

        Activity curActivity = getCurrentActivity();
        if (curActivity == null) {
            return;
        }
        Intent intent = curActivity.getIntent();
        processWakeUp(intent, callback);
    }

    private void wakeUpCallback(Callback callback, WritableMap ret) {
        if (callback == null) {
            try {
                getReactApplicationContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("SharetraceWakeupEvent", ret);
            } catch (Throwable e) {
                Log.e("SharetraceModule", "getJSModule error: " + e.getMessage());
            }
        } else {
            callback.invoke(ret);
        }
    }

    private WritableMap extractRetMap(AppData appData) {
        String paramsData = (appData.getParamsData() == null) ? "" : appData.getParamsData();
        String channel = (appData.getChannel() == null) ? "" : appData.getChannel();
        return parseToResult(200, "Success", paramsData, channel);
    }

    @ReactMethod
    public void getInstallTrace(final Callback callback){
        ShareTrace.getInstallTrace(new ShareTraceInstallListener() {
            @Override
            public void onInstall(AppData appData) {
                if (appData == null) {
                    WritableMap ret = parseToResult(-1, "Extract data fail.", "", "");
                    callback.invoke(ret);
                    return;
                }

                WritableMap ret = extractRetMap(appData);
                callback.invoke(ret);
            }

            @Override
            public void onError(int code, String message) {
                WritableMap ret = parseToResult(code, message, "", "");
                callback.invoke(ret);
            }
        });
    }

    @ReactMethod
    public void getInstallTraceWithTimeout(int timeoutSecond, final Callback callback) {
        int defaultTimeout = 10;
        if (timeoutSecond > 0) {
            defaultTimeout = timeoutSecond;
        }
        int timeoutMiles = defaultTimeout * 1000;
        ShareTrace.getInstallTrace(new ShareTraceInstallListener() {
            @Override
            public void onInstall(AppData appData) {
                if (appData == null) {
                    WritableMap ret = parseToResult(-1, "Extract data fail.", "", "");
                    callback.invoke(ret);
                    return;
                }

                WritableMap ret = extractRetMap(appData);
                callback.invoke(ret);
            }

            @Override
            public void onError(int code, String message) {
                WritableMap ret = parseToResult(code, message, "", "");
                callback.invoke(ret);
            }
        }, timeoutMiles);
    }
}
