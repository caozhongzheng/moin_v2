package com.moinapp.wuliao.commons.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.regularupdate.RegularUpdateManager;
import com.moinapp.wuliao.utils.PackageUtils;

public class LiveReceiver extends BroadcastReceiver {
    private static ILogger MyLog = LoggerFactory.getLogger("LiveReceiver");
    public static final String ACTION_REGULAR_UPDATE = ClientInfo.getPackageName() + ".regular_update";
    public static final String ACTION_SILENT_INSTALL = ClientInfo.getPackageName() + ".silent_install";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            onReceiveInternal(context, intent);
        } catch (Exception e){
            MyLog.e(e);
        }
    }

    private void onReceiveInternal(Context context, Intent intent) {
        String action = intent.getAction();
        if(ACTION_REGULAR_UPDATE.equals(action)){
            RegularUpdateManager.getInstance().regularUpdate(true);
        } else if (ACTION_SILENT_INSTALL.equals(action)){
            final String apkPath = intent.getStringExtra("apkPath");
            if (!TextUtils.isEmpty(apkPath)) {
                new Thread() {
                    public void run() {
                        try {
                            String result = PackageUtils.silentInstallApk(apkPath);
                            // 静默安装成功
                            if (result.contains("Success")) {
                                MyLog.i("silentInstallApk Success: " + apkPath);
                            }
                        } catch (Exception e) {
                            MyLog.e(e);
                        }
                    };
                }.start();
            }
        }
    }
}
