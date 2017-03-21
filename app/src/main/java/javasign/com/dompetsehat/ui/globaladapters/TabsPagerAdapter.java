package javasign.com.dompetsehat.ui.globaladapters;

/**
 * Created by Spoonart on 8/31/2015.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;


public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    // Tab Titles
    private ArrayList fragments;
    private String tabtitles[];

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public static TabsPagerAdapter newInstance(FragmentManager fm, Context context, int arrayResTitlesId, ArrayList fragments){
        TabsPagerAdapter tpa = new TabsPagerAdapter(fm);
        tpa.init(context, arrayResTitlesId, fragments);
        return tpa;
    }

    public static TabsPagerAdapter newInstance(FragmentManager fm, Context context, ArrayList fragments){
        TabsPagerAdapter tpa = new TabsPagerAdapter(fm);
        tpa.init(context, 0, fragments);
        return tpa;
    }

    private void init(Context context, int arrayResTitlesId, ArrayList fragments){
        this.fragments = fragments;

        if(arrayResTitlesId != 0)
            tabtitles = context.getResources().getStringArray(arrayResTitlesId);
    }

    @Override
    public int getCount() {
        if(fragments == null)
            return 0;
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment)fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(tabtitles == null)
            return "";

        return tabtitles[position];
    }
}
