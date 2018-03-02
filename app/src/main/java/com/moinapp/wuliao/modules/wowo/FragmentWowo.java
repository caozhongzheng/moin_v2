package com.moinapp.wuliao.modules.wowo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.utils.EmoticonsUtils;
import com.moinapp.wuliao.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by moying on 15/5/13.
 */
public class FragmentWowo extends Fragment {

    private static final ILogger MyLog = LoggerFactory.getLogger(WowoModule.MODULE_NAME);
    private Context context;
    private ViewPager mViewPager;
    private List<String> mTitleList = new ArrayList<String>();
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private PagerTabStrip pagerTabStrip;
    private static boolean isNeedCheckEmiticonStatus = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        if(ApplicationContext.getContext() == null) {
            ApplicationContext.setContext(context);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wowo_layout, container, false);

        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_list);
        mFragmentList = new ArrayList<Fragment>();
        Fragment fragmentWo= new FragmentWoList();
        Fragment fragmentPost = new FragmentSuggestPost();
        mFragmentList.add(fragmentWo);
        mFragmentList.add(fragmentPost);
        mTitleList.add("窝窝");
        mTitleList.add("推荐帖子");

        pagerTabStrip=(PagerTabStrip) rootView.findViewById(R.id.pagertab);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.common_text_main));
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

        FragmentManager fm = this.getActivity().getSupportFragmentManager();
        mViewPager.setAdapter(new MyFragmentPagerAdapter(fm));
        mViewPager.setCurrentItem(0);
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            checkEmoticonsStatus();
        } else {
            isNeedCheckEmiticonStatus = true;
        }

        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkEmoticonsStatus();
    }

    private void checkEmoticonsStatus() {
        if(isNeedCheckEmiticonStatus && NetworkUtils.isWifi(getActivity())) {
            EmoticonsUtils.checkEmojiStatus(getActivity());
            isNeedCheckEmiticonStatus = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
