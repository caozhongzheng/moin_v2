package com.moinapp.wuliao.modules.login;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.UiUtils;
import com.moinapp.wuliao.commons.ui.a2zletter.LetterBaseListAdapter;
import com.moinapp.wuliao.commons.ui.a2zletter.LetterComparator;
import com.moinapp.wuliao.commons.ui.a2zletter.LetterListView;
import com.moinapp.wuliao.utils.LocateUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by moying on 15/6/28.
 */
public class SelectCityActivity extends BaseActivity implements AMapLocationListener {
    protected static final String KEY_PROVINCE = "province";
    protected static final String KEY_CITY = "city";
    protected static final String KEY_DISTRICT = "district";
    private ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);

    private LayoutInflater mInflater;
    private View mHeaderView;
    private LinearLayout mHotCityLayout;
    private EditText autoLocate;
    private LetterListView cityList;
    private ListView hotcityList;
    private String[] cityArr;
    private String[] hotcityArr;
    private String currentCity;
    private LocationManagerProxy mAMapLocationManager;
    private AMapLocation mMapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city_layout);

        mInflater = LayoutInflater.from(SelectCityActivity.this);

        initTopBar(getString(R.string.select_city));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(false, "");

        currentCity = getIntent().getStringExtra(KEY_CITY);
        initView();
    }

    private void initView() {
        mHeaderView = mInflater.inflate(R.layout.select_city_header, null, false);
        autoLocate = (EditText) mHeaderView.findViewById(R.id.auto_locate);
        hotcityList = (ListView) mHeaderView.findViewById(R.id.hotcitylist);
        mHotCityLayout = (LinearLayout) mHeaderView.findViewById(R.id.hotcity_ly);

        hotcityArr = getResources().getStringArray(R.array.hot_city);
        if(hotcityArr.length > 0) {

            for (int i = 0; i < hotcityArr.length; i++) {
                View parent = mInflater.inflate(R.layout.city_list_item, null, false);
                TextView textView = (TextView) parent.findViewById(R.id.city);
                textView.setText(hotcityArr[i]);
//                MyLog.i("currentCity= [" + currentCity + "], hotcityArr[i]= [" + hotcityArr[i] + "], isEqual=" + (currentCity.replaceAll(" ","").equals(hotcityArr[i])));
                if(currentCity.replaceAll(" ", "").equals(hotcityArr[i])) {
                    textView.setTextColor(getResources().getColor(R.color.common_text_main));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.common_title_grey));
                }
                final int position = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCity(hotcityArr[position]);
                    }
                });


                mHotCityLayout.addView(parent);
            }

        }
        hotcityList.setAdapter(new HotCityAdapter(SelectCityActivity.this, hotcityArr));
        UiUtils.fixListViewHeight(hotcityList);

        cityList = (LetterListView) findViewById(R.id.citylist);

        cityArr = getResources().getStringArray(R.array.city);
        cityList.setHeader(mHeaderView);
        cityList.setAdapter(new CityAdapter(SelectCityActivity.this, cityArr));


        autoLocate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MyLog.i("autoLocate OnTouchListener ");
                setLocation();
                return true;
            }
        });
        LocateUtil locateUtil = new LocateUtil(SelectCityActivity.this, new LocateUtil.LocateListener() {
            @Override
            public void onLocateFinish(final AMapLocation aMapLocation) {
                MyLog.i("onLocateFinish " + aMapLocation);

                mMapLocation = aMapLocation;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        autoLocate.setText(aMapLocation.getCity());
                    }
                });

                destory();
            }

            @Override
            public void onLocateFailed() {
                MyLog.i("onLocateFailed ");
                destory();
            }
        });
        locateUtil.startLocation();
    }

    private void setLocation() {
        Intent mIntent = new Intent();

        if(mMapLocation == null) {
            this.setResult(0, mIntent);
        } else {
            mIntent.putExtra(SelectCityActivity.KEY_PROVINCE, mMapLocation.getProvince());
            mIntent.putExtra(SelectCityActivity.KEY_CITY, mMapLocation.getCity());
            mIntent.putExtra(SelectCityActivity.KEY_DISTRICT, mMapLocation.getDistrict());
            this.setResult(0, mIntent);
        }
        finish();
    }


    private void setCity(String city) {
        destory();
        Intent mIntent = new Intent();
        mIntent.putExtra(SelectCityActivity.KEY_CITY, city);
        this.setResult(0, mIntent);
        finish();
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        MyLog.i("onLocationChanged " + aMapLocation);
        if (aMapLocation == null){
            aMapLocation = new AMapLocation("AMapLocation");
            aMapLocation.setLatitude(0);
            aMapLocation.setLongitude(0);
        }
        destory();
    }

    @Override
    public void onLocationChanged(Location location) {
        MyLog.i("onLocationChanged " + location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        MyLog.i("onStatusChanged " + bundle);
    }

    @Override
    public void onProviderEnabled(String s) {
        MyLog.i("onProviderEnabled " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        MyLog.i("onProviderDisabled " + s);
    }

    /**
     * 停止定位
     */
    private void destory(){
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    public class HotCityAdapter extends BaseAdapter {
        Context mContext;
        private LayoutInflater mLayoutInflater;
        String[] mCityArr;

        public HotCityAdapter(Context context, String[] cityArr) {
            mContext = context;
            if (mLayoutInflater == null) {
                mLayoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            mCityArr = cityArr;
        }

        @Override
        public int getCount() {
            return mCityArr.length;
        }

        @Override
        public Object getItem(int i) {
            return mCityArr[i % mCityArr.length];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.city_list_item, null);
                viewHolder = getListViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setContentView(viewHolder, position);

            return convertView;
        }

        class ViewHolder {
            public TextView mCity;
        }
        private ViewHolder getListViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();
            holder.mCity = (TextView) convertView.findViewById(R.id.city);
            return holder;
        }

        private void setContentView(ViewHolder viewHolder, final int position) {
            viewHolder.mCity.setText(mCityArr[position]);
            if(currentCity.equals(mCityArr[position])) {
                viewHolder.mCity.setTextColor(getResources().getColor(R.color.common_text_main));
            } else {
                viewHolder.mCity.setTextColor(getResources().getColor(R.color.common_title_grey));
            }
            viewHolder.mCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCity(mCityArr[position]);
                }
            });
        }

    }

    private class CityAdapter extends LetterBaseListAdapter<NameValuePair> {
        Context mContext;
        private LayoutInflater mLayoutInflater;
        List<NameValuePair> dataList;

        public CityAdapter(Context context, String[] cityArr) {
            super();

            mContext = context;
            if (mLayoutInflater == null) {
                mLayoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            ArrayList<String> citylist = new ArrayList(cityArr.length);
            for (int i = 0; i < cityArr.length; i++) {
                citylist.add(cityArr[i]);
            }
            LetterComparator pinyinComparator = new LetterComparator();
            Collections.sort(citylist, pinyinComparator);
            dataList = new ArrayList<NameValuePair>();
            for(int i=0; i<cityArr.length; i++)
            {
                NameValuePair pair = new BasicNameValuePair(String.valueOf(i), citylist.get(i));
                dataList.add(pair);
            }
            setContainerList(dataList);
        }
        /** 字母对应的key,因为字母是要插入到列表中的,为了区别,所有字母的item都使用同一的key. **/
        private static final String LETTER_KEY = "letter";

        @Override
        public Object getItem(int position)
        {
            return list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public String getItemString(NameValuePair t)
        {
            return t.getValue();
        }

        @Override
        public NameValuePair create(char letter)
        {
            return new BasicNameValuePair(LETTER_KEY, String.valueOf(letter));
        }

        @Override
        public boolean isLetter(NameValuePair t)
        {
            //判断是不是字母行,通过key比较,这里是NameValuePair对象,其他对象,就由你自己决定怎么判断了.
            return t.getName().equals(LETTER_KEY);
        }

        @Override
        public View getLetterView(int position, View convertView, ViewGroup parent)
        {
            //这里是字母的item界面设置.
            if(convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.city_list_item, null);
                TextView textView = (TextView) convertView.findViewById(R.id.city);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                convertView.setBackgroundColor(getResources().getColor(R.color.common_grey));
            }
            ((TextView)convertView.findViewById(R.id.city)).setText(list.get(position).getValue());

            return convertView;
        }

        @Override
        public View getContainerView(int position, View convertView, ViewGroup parent)
        {
            TextView textView;
            //这里是其他正常数据的item界面设置.
            if(convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.city_list_item, null);
            }
            textView = (TextView) convertView.findViewById(R.id.city);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(list.get(position).getValue());
            if(currentCity.replaceAll(" ", "").equals(list.get(position).getValue())) {
                textView.setTextColor(getResources().getColor(R.color.common_text_main));
            } else {
                textView.setTextColor(getResources().getColor(R.color.common_title_grey));
            }
            final int index = position;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCity(list.get(index).getValue());
                }
            });
            MyLog.i(position + " : " + list.get(position).getValue());

            return convertView;
        }
    }
}
