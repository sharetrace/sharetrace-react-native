package com.sharetrace.react;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;

import cn.net.shoot.sharetracesdk.AppData;
import cn.net.shoot.sharetracesdk.ShareTrace;
import cn.net.shoot.sharetracesdk.ShareTraceInstallListener;

public class SharetraceModuleModule extends ReactContextBaseJavaModule {

    private static final String KEY_CODE = "code";
    private static final String KEY_MSG = "msg";
    private static final String KEY_PARAMSDATA = "paramsData";
    private static final String KEY_RESUMEPAGE = "resumePage";

    private final ReactApplicationContext reactContext;

    public SharetraceModuleModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        ShareTrace.init((Application) reactContext.getApplicationContext());
    }

    @Override
    public String getName() {
        return "SharetraceModule";
    }

    private WritableMap parseToResult(int code, String msg, String paramsData, String resumePage) {
        WritableMap result = Arguments.createMap();
        result.putInt(KEY_CODE, code);
        result.putString(KEY_MSG, msg);
        result.putString(KEY_PARAMSDATA, paramsData);
        result.putString(KEY_RESUMEPAGE, resumePage);
        return result;
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

                String paramsData = (appData.paramsData == null) ? "" : appData.paramsData;
                String resumePage = (appData.resumePage == null) ? "" : appData.resumePage;

                WritableMap ret = parseToResult(200, "Success", paramsData, resumePage);
                callback.invoke(ret);
            }

            @Override
            public void onError(int code, String message) {
                System.out.println("getInstallTrace onError start... " + code + ", " + message);
                WritableMap ret = parseToResult(code, message, "", "");
                callback.invoke(ret);
            }
        });
    }
}
