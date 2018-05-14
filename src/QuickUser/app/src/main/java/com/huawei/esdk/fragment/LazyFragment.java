package com.huawei.esdk.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huawei.esdk.R;

/**
 * Created on 2017/12/28.
 */
public class LazyFragment extends BaseFragment
{
    //真正要显示的View是否已经被初始化（正常加载）
    private boolean isInit = false;
    private Bundle savedInstanceState;
    public static final String INTENT_BOOLEAN_LAZYLOAD = "intent_boolean_lazyLoad";
    private boolean isLazyLoad = true;
    private FrameLayout layout;
    //是否处于可见状态，in the screen
    private boolean isStart = false;
    protected String pagerTitle = "";

    @Override
    protected final void onCreateView(Bundle savedInstanceState)
    {
        Log.d("TAG", "onCreateView() : " + "getUserVisibleHint():" + getUserVisibleHint());
        super.onCreateView(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle)
        {
            isLazyLoad = bundle.getBoolean(INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        }
        //判断是否懒加载
        if (!isLazyLoad)
        {
            //不需要懒加载，调用onCreateViewLazy正常加载显示内容即可
            onCreateViewLazy(savedInstanceState);
            isInit = true;
            return;
        }

        //一旦isVisibleToUser==true即可对真正需要的显示内容进行加载
        if (getUserVisibleHint() && !isInit)
        {
            this.savedInstanceState = savedInstanceState;
            onCreateViewLazy(savedInstanceState);
            isInit = true;
        }
        else
        {
            //进行懒加载
            layout = new FrameLayout(getApplicationContext());
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_lazy_loading, null);
            layout.addView(view);
            super.setContentView(layout);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("TAG", "setUserVisibleHint() called with: " + "isVisibleToUser = [" + isVisibleToUser + "]");
        //一旦isVisibleToUser==true即可进行对真正需要的显示内容的加载
        //可见，但还没被初始化
        if (isVisibleToUser && !isInit && getContentView() != null)
        {
            onCreateViewLazy(savedInstanceState);
            isInit = true;
            onResumeLazy();
        }

        if (!isInit || null == getContentView())
        {
            return;
        }
        //已经被初始化（正常加载）过了
        if (isVisibleToUser)
        {
            isStart = true;
            onFragmentStartLazy();
        }
        else
        {
            isStart = false;
            onFragmentStopLazy();
        }
    }

    @Override
    public void setContentView(int layoutResID)
    {
        //判断若isLazyLoad==true,移除所有lazy view，加载真正要显示的view
        if (isLazyLoad && null != layout && null != getContentView() && null != getContentView().getParent() && null != inflater)
        {
            layout.removeAllViews();
            View view = inflater.inflate(layoutResID, layout, false);
            layout.addView(view);
        }
        //直接加载
        else
        {
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view)
    {
        //判断若isLazyLoad==true,移除所有lazy view，加载真正要显示的view
        if (isLazyLoad && null != layout && getContentView() != null && getContentView().getParent() != null)
        {
            layout.removeAllViews();
            layout.addView(view);
        }
        //直接加载
        else
        {
            super.setContentView(view);
        }
    }

    @Deprecated
    @Override
    public final void onStart()
    {
        Log.d("TAG", "onStart() : " + "getUserVisibleHint():" + getUserVisibleHint());
        super.onStart();
        if (isInit && !isStart && getUserVisibleHint())
        {
            isStart = true;
            onFragmentStartLazy();
        }
    }

    @Deprecated
    @Override
    public final void onStop()
    {
        super.onStop();
        Log.d("TAG", "onStop() called: " + "getUserVisibleHint():" + getUserVisibleHint());
        if (isInit && isStart && getUserVisibleHint())
        {
            isStart = false;
            onFragmentStopLazy();
        }
    }

    //当Fragment被滑到可见的位置时，调用
    protected void onFragmentStartLazy()
    {
        Log.d("TAG", "onFragmentStartLazy() called with: " + "");
    }

    //当Fragment被滑到不可见的位置，offScreen时，调用
    protected void onFragmentStopLazy()
    {
        Log.d("TAG", "onFragmentStopLazy() called with: " + "");
    }

    protected void onCreateViewLazy(Bundle savedInstanceState)
    {
        Log.d("TAG", "onCreateViewLazy() called with: " + "savedInstanceState = [" + savedInstanceState + "]");
    }

    protected void onResumeLazy()
    {
        Log.d("TAG", "onResumeLazy() called with: " + "");
    }

    protected void onPauseLazy()
    {
        Log.d("TAG", "onPauseLazy() called with: " + "");
    }

    protected void onDestroyViewLazy()
    {

    }

    @Override
    @Deprecated
    public final void onResume()
    {
        Log.d("TAG", "onResume() : " + "getUserVisibleHint():" + getUserVisibleHint());
        super.onResume();
        if (isInit)
        {
            onResumeLazy();
        }
    }

    @Override
    @Deprecated
    public final void onPause()
    {
        Log.d("TAG", "onPause() : " + "getUserVisibleHint():" + getUserVisibleHint());
        super.onPause();
        if (isInit)
        {
            onPauseLazy();
        }
    }

    @Override
    @Deprecated
    public final void onDestroyView()
    {
        Log.d("TAG", "onDestroyView() : " + "getUserVisibleHint():" + getUserVisibleHint());
        super.onDestroyView();
        if (isInit)
        {
            onDestroyViewLazy();
        }
        isInit = false;
    }

    public String getPagerTitle() {
        return pagerTitle;
    }

    public void setPagerTitle(String pagerTitle) {
        this.pagerTitle = pagerTitle;
    }
}
