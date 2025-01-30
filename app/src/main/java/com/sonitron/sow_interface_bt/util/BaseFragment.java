package com.sonitron.sow_interface_bt.util;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        SystemUiUtils.enableImmersiveMode(requireActivity());

    }
}
