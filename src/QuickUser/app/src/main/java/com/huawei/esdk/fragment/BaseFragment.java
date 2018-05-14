package com.huawei.esdk.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 2017/12/28.
 */
public class BaseFragment extends Fragment
{
    protected LayoutInflater inflater;
    private View contentView;
    private Context context;
    private ViewGroup container;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    //子类通过重写onCreateView，调用setOnContentView进行布局设置，否则null == contentView，返回null
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.inflater = inflater;
        this.container = container;
        onCreateView(savedInstanceState);
        if (null == contentView)
        {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        return contentView;
    }

    protected void onCreateView(Bundle savedInstanceState)
    {
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        contentView = null;
        container = null;
        inflater = null;
    }

    public Context getApplicationContext()
    {
        return context;
    }

    public void setContentView(int layoutResID)
    {
        if (inflater != null)
        {
            setContentView((ViewGroup) inflater.inflate(layoutResID, container, false));
        }
    }

    public void setContentView(View view)
    {
        contentView = view;
    }

    public View getContentView()
    {
        return contentView;
    }

    public View findViewById(int id)
    {
        if (contentView != null)
            return contentView.findViewById(id);
        return null;
    }

    @Override
    public void onDetach()
    {
        Log.d("TAG", "onDetach() : ");
        super.onDetach();
    }

    @Override
    public void onDestroy()
    {
        Log.d("TAG", "onDestroy() : ");
        super.onDestroy();
    }
}
