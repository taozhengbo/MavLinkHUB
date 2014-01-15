package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.HUBGlobals;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class HUBFragment extends Fragment {

	protected HUBGlobals globalVars;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true); // keep HUBFragments in the memory

		globalVars = (HUBGlobals) getActivity().getApplication();

	}

}
