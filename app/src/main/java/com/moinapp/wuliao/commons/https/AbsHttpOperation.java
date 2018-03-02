package com.moinapp.wuliao.commons.https;

import android.annotation.SuppressLint;
import android.content.Context;

import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.info.MobileInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.utils.CommonMethod;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 实现http接口调用的类，支持Get/Post/Put/Delete四种操作
 * 具体的业务protocal类可以实现HttpObsever接口得到response回调
 * @author liujiancheng
 *
 */

public class AbsHttpOperation implements IHttpOperation {
    private static ILogger MyLog = LoggerFactory.getLogger("AbsHttpOperation");
	private Context mContext;
	private HttpObserver mObserver;
	public final static int ERROR_NONE = 0;

	public AbsHttpOperation(Context context, HttpObserver ob) {
		mContext = context;
		mObserver = ob;
	}
	
	@Override
	public void doGet(String url) {
		HttpGet request;
        try {
            MyLog.i("url="+url);
            request = new HttpGet(url);
            addHeadInfo(request);
        } catch (Throwable e) {
            MyLog.e(e);
            return;
        }
        processRequest(request);
	}

	@SuppressLint("NewApi")
	@Override
	public void doPut(String url, Map data) {
		HttpPut request;
        try {
            MyLog.i("url="+url);
            request = new HttpPut(url);
            addHeadInfo(request);
        } catch (Throwable e) {
            MyLog.e(e);
            return;
        }
        processRequest(request, data);
	}

    @SuppressLint("NewApi")
    public void doPost(String url, Map data) {
        HttpPost request;
        try {
            MyLog.i("url="+url);
            request = new HttpPost(url);
            addHeadInfo(request);
        } catch (Throwable e) {
            MyLog.e(e);
            return;
        }
        processRequest(request, data);
    }

	@Override
	public void doDelete(String url) {
		HttpGet request;
        try {
            request = new HttpGet(url);
            addHeadInfo(request);
        } catch (Throwable e) {
            MyLog.e(e);
            return;
        }

        processRequest(request);
	}

    @Override
    public void uploadFile(String url, String key, String filePath) {
        HttpPost request;
        try {
            request = new HttpPost(url);
            addHeadInfo(request);
        } catch (Throwable e) {
            MyLog.e(e);
            return;
        }

        uploadToServer(request, key, filePath);
    }

    private HttpClient getHttpClient() {
		return HttpClientFactory.getCustomClient(mContext);
	}


	private void processResponse(HttpResponse httpResponse){
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        HttpEntity entity = httpResponse.getEntity();
	    String result = null;
	    int returnCode = -1;
	    
        if (responseCode == 200 && entity != null) {
        	returnCode = ERROR_NONE;
			try {
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    MyLog.i("Content-Encoding:GZIP");
                    InputStream is = httpResponse.getEntity().getContent();
//                    is = new GZIPInputStream(new BufferedInputStream(is),4096);
//                    StringBuffer out = new StringBuffer();
//                    byte[] b = new byte[4096];
//                    for (int n;(n = is.read(b)) != -1;)   {
//                        out.append(new String(b, 0, n));
//                    }
//                    result = out.toString();
                    GZIPInputStream gis = new GZIPInputStream(is);
                    int l;
                    ByteArrayBuffer bt= new ByteArrayBuffer(4096);
                    byte[] tmp = new byte[4096];
                    while ((l=gis.read(tmp))!=-1){
                        bt.append(tmp, 0, l);
                    }
                    result = new String(bt.toByteArray(),"utf-8");
                } else {
                    MyLog.i("Content-Encoding:TEXT");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    StringBuilder build = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        build.append(line);
                    }
                    result = build.toString();
                }
            } catch (Throwable e) {
                MyLog.e(e);
			}
        } else {
        	MyLog.e("responseCode=" + responseCode);
        } 
        mObserver.onHttpResult(returnCode, result);
 	}

    //每次请求时，需要把常规连接的信息放在http头里面
    private void addHeadInfo(HttpRequestBase request) {
        String uid = ClientInfo.getUID();
        String passport = ClientInfo.getPassport();
        String imei = MobileInfo.getImei(mContext);
        MyLog.i("add header:imei="+imei + ",uid="+uid + ", moin passport="+passport + ", device width="+ MobileInfo.getDisplayWidth(mContext));

        request.addHeader(LoginConstants.IMEI, imei);
        request.addHeader(LoginConstants.UID, uid);
        request.addHeader(LoginConstants.MOIN_PASSPORT, passport);
        request.addHeader(NetConstants.DEVICE_WIDTH, String.valueOf(MobileInfo.getDisplayWidth(mContext)));

        request.setHeader("Accept-Encoding", "gzip,deflate");
        //测试http host和http referer
//        request.addHeader("Referer", "http://www.moin.com");
//        request.addHeader("Host", "http://www.moin.com");
    }

    private void processRequest(HttpRequestBase request) {
        if (!CommonMethod.hasActiveNetwork(mContext)) {
            mObserver.onHttpResult(NetConstants.NONE_NETWORK, null);
            return;
        }

        int errorCode = 0;
        HttpClient client = getHttpClient();
        HttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(request);
        } catch (ClientProtocolException e) {
            errorCode = NetConstants.NETWORK_ERROR;
            MyLog.e(e);
        } catch (UnsupportedEncodingException e) {
            errorCode = NetConstants.NETWORK_ERROR;
            MyLog.e(e);
        } catch (ConnectTimeoutException cte) {
            errorCode = NetConstants.NETWORK_TIMEOUT;
            MyLog.e(cte);
        } catch (IOException e) {
            errorCode = NetConstants.NETWORK_TIMEOUT;
            MyLog.e(e);
        } catch (Throwable e) {
            MyLog.e(e);
        } finally {
            if (errorCode != 0) {
                mObserver.onHttpResult(errorCode, null);
            } else {
                processResponse(httpResponse);
            }
            client.getConnectionManager().shutdown();
        }
    }

    private void processRequest(HttpRequestBase request, Map para) {
        if (!CommonMethod.hasActiveNetwork(mContext)) {
            mObserver.onHttpResult(NetConstants.NONE_NETWORK, null);
            return;
        }

        int errorCode = 0;
        HttpClient client = getHttpClient();
        HttpResponse httpResponse = null;
        try {
            if (para != null && para.size() > 0) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                Iterator<Map.Entry<String, String>> iter = para.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> i = iter.next();
                    pairs.add(new BasicNameValuePair(i.getKey(), i.getValue()));
                    MyLog.i("processRequest,key=" + i.getKey());
                    MyLog.i("processRequest,value="+i.getValue());
                }

                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);

                if (request instanceof HttpPost) {
                    HttpPost post = (HttpPost)request;
                    post.setEntity(formEntity);
                } else if (request instanceof HttpPut){
                    HttpPut put = (HttpPut)request;
                    put.setEntity(formEntity);
                }
            }
            httpResponse = client.execute(request);
        } catch (ClientProtocolException e) {
            errorCode = NetConstants.NETWORK_ERROR;
            MyLog.e(e);
        } catch (UnsupportedEncodingException e) {
            errorCode = NetConstants.NETWORK_ERROR;
            MyLog.e(e);
        } catch (ConnectTimeoutException cte) {
            errorCode = NetConstants.NETWORK_TIMEOUT;
            MyLog.e(cte);
        } catch (IOException e) {
            errorCode = NetConstants.NETWORK_TIMEOUT;
            MyLog.e(e);
        } catch (Throwable e) {
            MyLog.e(e);
        } finally {
            if (errorCode != 0) {
                mObserver.onHttpResult(errorCode, null);
            } else {
                processResponse(httpResponse);
            }
            client.getConnectionManager().shutdown();
        }
    }

    private void uploadToServer(HttpRequestBase request, String key, String filePath) {

        if (!CommonMethod.hasActiveNetwork(mContext)) {
            mObserver.onHttpResult(NetConstants.NONE_NETWORK, null);
            return;
        }

        int errorCode = 0;
        HttpClient client = getHttpClient();
        HttpResponse response = null;

        File file = new File(filePath);
        if (!file.exists()) {
            MyLog.i("upload file is not exist!");
            mObserver.onHttpResult(-1, null);
            return;
        }

        FileBody fileBody = new FileBody(file, "image/jpeg");
        MultipartEntity entity = new MultipartEntity();
        entity.addPart(key, fileBody);

        ((HttpPost)request).setEntity(entity);
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            errorCode = NetConstants.NETWORK_ERROR;
            MyLog.e(e);
        } catch (UnsupportedEncodingException e) {
            errorCode = NetConstants.NETWORK_ERROR;
            MyLog.e(e);
        } catch (ConnectTimeoutException cte) {
            errorCode = NetConstants.NETWORK_TIMEOUT;
            MyLog.e(cte);
        } catch (IOException e) {
            errorCode = NetConstants.NETWORK_TIMEOUT;
            MyLog.e(e);
        } catch (Throwable e) {
            MyLog.e(e);
        } finally {
            if (errorCode != 0) {
                mObserver.onHttpResult(errorCode, null);
            } else {
                processResponse(response);
            }
            client.getConnectionManager().shutdown();
        }
    }

    /**
     * @param url servlet的地址
     * @param params 要传递的参数
     * @param files 要上传的文件
     * @return true if upload success else false
     * @throws ClientProtocolException
     * @throws IOException
     */
//    private boolean uploadFiles(String url,Map<String, String>params,ArrayList<File>files) throws ClientProtocolException, IOException {
//        HttpClient client=new DefaultHttpClient();// 开启一个客户端 HTTP 请求
//        HttpPost post = new HttpPost(url);//创建 HTTP POST 请求
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
////		builder.setCharset(Charset.forName("uft-8"));//设置请求的编码格式
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
//        int count=0;
//        for (File file:files) {
////			FileBody fileBody = new FileBody(file);//把文件转换成流对象FileBody
////			builder.addPart("file"+count, fileBody);
//            builder.addBinaryBody("file"+count, file);
//            count++;
//        }
//        builder.addTextBody("method", params.get("method"));//设置请求参数
//        builder.addTextBody("fileTypes", params.get("fileTypes"));//设置请求参数
//        HttpEntity entity = builder.build();// 生成 HTTP POST 实体
//        post.setEntity(entity);//设置请求参数
//        HttpResponse response = client.execute(post);// 发起请求 并返回请求的响应
//        if (response.getStatusLine().getStatusCode()==200) {
//            return true;
//        }
//        return false;
//    }
}
