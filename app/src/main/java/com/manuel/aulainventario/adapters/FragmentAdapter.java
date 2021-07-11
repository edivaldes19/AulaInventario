package com.manuel.aulainventario.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.manuel.aulainventario.fragments.ActiveFragment;
import com.manuel.aulainventario.fragments.ConsumptionFragment;
import com.manuel.aulainventario.fragments.DidacticFragment;
import com.manuel.aulainventario.fragments.PedagogicalFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new DidacticFragment();
            case 2:
                return new ConsumptionFragment();
            case 3:
                return new ActiveFragment();
        }
        return new PedagogicalFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}