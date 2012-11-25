package fr.bellev.stdatmosphere;

import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

	private final List<ResultFragment> fragments;

	public MyPagerAdapter(FragmentManager fm, List<ResultFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public ResultFragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
	
	@Override
	public String getPageTitle(int pos) {
		ResultFragment frag=this.getItem(pos);
		String Title=frag.GetTitle();
		return Title;
	}
	
	public void update() {
		for (int i=0;i<fragments.size();i++) {
			fragments.get(i).update();
		}
		this.notifyDataSetChanged();
	}
}