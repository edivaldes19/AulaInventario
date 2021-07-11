package com.manuel.aulainventario.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.manuel.aulainventario.fragments.ActivoFragment;
import com.manuel.aulainventario.fragments.ConsumoFragment;
import com.manuel.aulainventario.fragments.DidacticoFragment;
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
                return new DidacticoFragment();
            case 2:
                return new ConsumoFragment();
            case 3:
                return new ActivoFragment();
        }
        return new PedagogicalFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}