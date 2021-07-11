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
import com.manuel.aulainventario.activities.ActiveFormActivity;
import com.manuel.aulainventario.adapters.ActiveAdapter;
import com.manuel.aulainventario.models.Active;
import com.manuel.aulainventario.providers.ActiveProvider;
import com.manuel.aulainventario.providers.AuthProvider;

public class ActiveFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    View mView;
    FloatingActionButton mFab;
    MaterialSearchBar mSearchBar;
    AuthProvider mAuthProvider;
    ActiveProvider mActiveProvider;
    ActiveAdapter mActiveAdapter, mActiveSearch;
    RecyclerView mRecyclerView;

    public ActiveFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_active, container, false);
        mFab = mView.findViewById(R.id.fabActive);
        mRecyclerView = mView.findViewById(R.id.recyclerViewActive);
        mSearchBar = mView.findViewById(R.id.searchBarActive);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAuthProvider = new AuthProvider();
        mActiveProvider = new ActiveProvider();
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
        Query query = mActiveProvider.getActiveByDescription(description);
        FirestoreRecyclerOptions<Active> options = new FirestoreRecyclerOptions.Builder<Active>().setQuery(query, Active.class).build();
        mActiveSearch = new ActiveAdapter(options, getContext());
        mActiveSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mActiveSearch);
        mActiveSearch.startListening();
    }

    private void getActiveByCurrentTeacher() {
        Query query = mActiveProvider.getActiveByTeacher(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Active> options = new FirestoreRecyclerOptions.Builder<Active>().setQuery(query, Active.class).build();
        mActiveAdapter = new ActiveAdapter(options, getContext());
        mActiveAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mActiveAdapter);
        mActiveAdapter.startListening();
    }

    private void goToForm() {
        startActivity(new Intent(getContext(), ActiveFormActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        getActiveByCurrentTeacher();
    }

    @Override
    public void onStop() {
        super.onStop();
        mActiveAdapter.stopListening();
        if (mActiveSearch != null) {
            mActiveSearch.stopListening();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getActiveByCurrentTeacher();
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