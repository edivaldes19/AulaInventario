package com.manuel.aulainventario.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.activities.PedagogicalFormActivity;
import com.manuel.aulainventario.adapters.PedagogicalAdapter;
import com.manuel.aulainventario.models.Pedagogical;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.PedagogicalProvider;

public class PedagogicalFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    View mView;
    FloatingActionButton mFab;
    MaterialSearchBar mSearchBar;
    AuthProvider mAuthProvider;
    PedagogicalProvider mPedagogicalProvider;
    PedagogicalAdapter mPedagogicalAdapter, mPedagogicalSearch;
    RecyclerView mRecyclerView;

    public PedagogicalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pedagogical, container, false);
        mFab = mView.findViewById(R.id.fabPedagogical);
        mRecyclerView = mView.findViewById(R.id.recyclerViewPedagogical);
        mSearchBar = mView.findViewById(R.id.searchBarPedagogical);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAuthProvider = new AuthProvider();
        mPedagogicalProvider = new PedagogicalProvider();
        mSearchBar.setOnSearchActionListener(this);
        mFab.setOnClickListener(v -> goToForm());
        showTooltip();
        return mView;
    }

    private void showTooltip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFab.setTooltipText("Crear un nuevo registro");
        }
    }

    private void searchByDescription(String description) {
        Query query = mPedagogicalProvider.getPedagogicalByDescription(description);
        FirestoreRecyclerOptions<Pedagogical> options = new FirestoreRecyclerOptions.Builder<Pedagogical>().setQuery(query, Pedagogical.class).build();
        mPedagogicalSearch = new PedagogicalAdapter(options, getContext());
        mPedagogicalSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPedagogicalSearch);
        mPedagogicalSearch.startListening();
    }

    private void getPedagogicalByCurrentTeacher() {
        Query query = mPedagogicalProvider.getPedagogicalByTeacher(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Pedagogical> options = new FirestoreRecyclerOptions.Builder<Pedagogical>().setQuery(query, Pedagogical.class).build();
        mPedagogicalAdapter = new PedagogicalAdapter(options, getContext());
        mPedagogicalAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPedagogicalAdapter);
        mPedagogicalAdapter.startListening();
    }

    private void goToForm() {
        startActivity(new Intent(getContext(), PedagogicalFormActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        getPedagogicalByCurrentTeacher();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPedagogicalAdapter.stopListening();
        if (mPedagogicalSearch != null) {
            mPedagogicalSearch.stopListening();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getPedagogicalByCurrentTeacher();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByDescription(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {
    }
}