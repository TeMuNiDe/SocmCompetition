package com.vitech.socmcompetition;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by varma on 16-05-2017.
 */

public class MainPageAdapter extends FragmentStatePagerAdapter {

public Map<Integer,Fragment> fragmentMap;
    public MainPageAdapter(FragmentManager fm) {
        super(fm);
        fragmentMap = new HashMap<>();

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
       switch (position){
           case 0:fragment = new LogoFragment();fragmentMap.put(position,fragment);break;
           case 1:fragment = new UserDetailsFragment();fragmentMap.put(position,fragment);break;
           case 2:fragment = new TermsAndConditions();fragmentMap.put(position,fragment);break;
           default:fragment  = new RulesAndRegulations();fragmentMap.put(position,fragment);break;
       }

       return  fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
