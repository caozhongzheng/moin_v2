package com.moinapp.wuliao.commons.https;

import android.content.Context;
import android.util.Log;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.info.CommonDefine;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * 自签名证书的sockget工厂
 * @author liujiancheng
 *
 */
public class SSLCustomSocketFactory extends SSLSocketFactory {
    private static ILogger MyLog = LoggerFactory.getLogger("SSLCustomSocketFactory");
    private static final String KEY_PASS = "123456";

    public SSLCustomSocketFactory(KeyStore trustStore) throws Throwable {
        super(trustStore);
    }

    public static SSLSocketFactory getSocketFactory(Context context) {
        try {
            InputStream ins;
            switch(CommonDefine.getBuildType()) {
                case CommonDefine.VERSION_STYLE_RELEASE:	// Release
                    ins = context.getResources().openRawResource(R.raw.clientstore_prd);
                    break;
                case CommonDefine.VERSION_STYLE_TEST:	// 测试
                    ins = context.getResources().openRawResource(R.raw.clientstore);
                    break;
                default:
                    ins =  context.getResources().openRawResource(R.raw.clientstore_prd);
                    break;
            }

            KeyStore trustStore = KeyStore.getInstance("BKS");
            try {
                trustStore.load(ins, KEY_PASS.toCharArray());
            }
            finally {
                ins.close();
            }
            SSLSocketFactory factory = new SSLCustomSocketFactory(trustStore);
            return factory;
        } catch (Throwable e) {
            MyLog.d(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
