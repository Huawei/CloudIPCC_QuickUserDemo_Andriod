package com.huawei.esdk;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.huawei.esdk.fragment.LazyFragment;
import java.util.List;

/**
 * Created on 2017/12/28.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter
{
    private List<Fragment> fragmentList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList)
    {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String s = "";
        if(fragmentList.get(position) instanceof LazyFragment)
        {
            s = ((LazyFragment)fragmentList.get(position)).getPagerTitle();
        }
        return s;
    }
}