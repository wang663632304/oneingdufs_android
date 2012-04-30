/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.molice.oneingdufs.androidpn;

import java.util.Properties;

import com.molice.oneingdufs.activities.SettingsActivity;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** 
 * This class is to manage the notificatin service and to load the configuration.<br/>
 * 要在这个类的{@link #loadProperties()}方法中设置项目properties文件的文件名
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class ServiceManager {

    private static final String LOGTAG = LogUtil
            .makeLogTag(ServiceManager.class);

    private Context context;

    private SharedPreferencesStorager sharedPrefs;

    private Properties props;

    private String version = "0.5.0";

    private String apiKey;

    private String xmppHost;

    private String xmppPort;

    private String callbackActivityPackageName;

    private String callbackActivityClassName;

    public ServiceManager(Context context) {
        this.context = context;

        if (context instanceof Activity) {
            Log.i(LOGTAG, "Callback Activity...");
            Activity callbackActivity = (Activity) context;
            // 这里设置点击Notification后要调用的Activity
            callbackActivityPackageName = callbackActivity.getPackageName();
            callbackActivityClassName = callbackActivity.getClass().getName();
        }

        //        apiKey = getMetaDataValue("ANDROIDPN_API_KEY");
        //        Log.i(LOGTAG, "apiKey=" + apiKey);
        //        //        if (apiKey == null) {
        //        //            Log.e(LOGTAG, "Please set the androidpn api key in the manifest file.");
        //        //            throw new RuntimeException();
        //        //        }

        props = loadProperties();
        apiKey = props.getProperty("apiKey", "1234567890");
//        xmppHost = props.getProperty("xmppHost", "10.0.2.2");
        xmppHost = SettingsActivity.getApnHostUrl(context);
        xmppPort = props.getProperty("xmppPort", "5222");
        Log.i(LOGTAG, "apiKey=" + apiKey);
        Log.i(LOGTAG, "xmppHost=" + xmppHost);
        Log.i(LOGTAG, "xmppPort=" + xmppPort);

        sharedPrefs = new SharedPreferencesStorager(context);
        sharedPrefs.set(Constants.API_KEY, apiKey)
        	.set(Constants.VERSION, version)
        	.set(Constants.XMPP_HOST, xmppHost)
        	.set(Constants.XMPP_PORT, Integer.parseInt(xmppPort))
        	.set(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, callbackActivityPackageName)
        	.set(Constants.CALLBACK_ACTIVITY_CLASS_NAME, callbackActivityClassName)
        	.save();
    }

    public void startService() {
    	if(!ProjectConstants.isServiceRunning(context, NotificationService.SERVICE_NAME)) {
	        Thread serviceThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                Intent intent = NotificationService.getIntent();
	                context.startService(intent);
	            }
	        });
	        serviceThread.start();
    	} else {
    		Log.d("启动APN服务", "ServiceManger#startService, 服务已存在，TODO:检查是否处于成功连接状态...");
    	}
    }
    
    // TODO 添加一个方法，用于在切换Activity时检查XMPP连接是否正常，如果不正常（如服务器宕机），则中断当前服务并重新启动
    // getConnection.isConnected()
    

    public void stopService() {
        Intent intent = NotificationService.getIntent();
        context.stopService(intent);
    }

    //    private String getMetaDataValue(String name, String def) {
    //        String value = getMetaDataValue(name);
    //        return (value == null) ? def : value;
    //    }
    //
    //    private String getMetaDataValue(String name) {
    //        Object value = null;
    //        PackageManager packageManager = context.getPackageManager();
    //        ApplicationInfo applicationInfo;
    //        try {
    //            applicationInfo = packageManager.getApplicationInfo(context
    //                    .getPackageName(), 128);
    //            if (applicationInfo != null && applicationInfo.metaData != null) {
    //                value = applicationInfo.metaData.get(name);
    //            }
    //        } catch (NameNotFoundException e) {
    //            throw new RuntimeException(
    //                    "Could not read the name in the manifest file.", e);
    //        }
    //        if (value == null) {
    //            throw new RuntimeException("The name '" + name
    //                    + "' is not defined in the manifest file's meta data.");
    //        }
    //        return value.toString();
    //    }

    private Properties loadProperties() {
        //        InputStream in = null;
        //        Properties props = null;
        //        try {
        //            in = getClass().getResourceAsStream(
        //                    "/org/androidpn/client/client.properties");
        //            if (in != null) {
        //                props = new Properties();
        //                props.load(in);
        //            } else {
        //                Log.e(LOGTAG, "Could not find the properties file.");
        //            }
        //        } catch (IOException e) {
        //            Log.e(LOGTAG, "Could not find the properties file.", e);
        //        } finally {
        //            if (in != null)
        //                try {
        //                    in.close();
        //                } catch (Throwable ignore) {
        //                }
        //        }
        //        return props;

        Properties props = new Properties();
        try {
            int id = context.getResources().getIdentifier("oneingdufs", "raw",
                    context.getPackageName());
            props.load(context.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e(LOGTAG, "Could not find the properties file.", e);
            // e.printStackTrace();
        }
        return props;
    }

    //    public String getVersion() {
    //        return version;
    //    }
    //
    //    public String getApiKey() {
    //        return apiKey;
    //    }

    public void setNotificationIcon(int iconId) {
        sharedPrefs.set(Constants.NOTIFICATION_ICON, iconId)
        	.save();
    }

    //    public void viewNotificationSettings() {
    //        Intent intent = new Intent().setClass(context,
    //                NotificationSettingsActivity.class);
    //        context.startActivity(intent);
    //    }
//
//    public static void viewNotificationSettings(Context context) {
//        Intent intent = new Intent().setClass(context,
//                NotificationSettingsActivity.class);
//        context.startActivity(intent);
//    }

}
