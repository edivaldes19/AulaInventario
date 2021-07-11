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
import com.manuel.aulainventario.activities.DidacticFormActivity;
import com.manuel.aulainventario.adapters.DidacticAdapter;
import com.manuel.aulainventario.models.Didactic;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.DidacticProvider;

public class DidacticFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    View mView;
    FloatingActionButton mFab;
    MaterialSearchBar mSearchBar;
    AuthProvider mAuthProvider;
    DidacticProvider mDidacticProvider;
    DidacticAdapter mDidacticAdapter, mDidacticSearch;
    RecyclerView mRecyclerView;

    public DidacticFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_didactic, container, false);
        mFab = mView.findViewById(R.id.fabDidactic);
        mRecyclerView = mView.findViewById(R.id.recyclerViewDidactic);
        mSearchBar = mView.findViewById(R.id.searchBarDidactic);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAuthProvider = new AuthProvider();
        mDidacticProvider = new DidacticProvider();
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
        Query query = mDidacticProvider.getDidacticByDescription(description);
        FirestoreRecyclerOptions<Didactic> options = new FirestoreRecyclerOptions.Builder<Didactic>().setQuery(query, Didactic.class).build();
        mDidacticSearch = new DidacticAdapter(options, getContext());
        mDidacticSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mDidacticSearch);
        mDidacticSearch.startListening();
    }

    private void getDidacticByCurrentTeacher() {
        Query query = mDidacticProvider.getDidacticByTeacher(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Didactic> options = new FirestoreRecyclerOptions.Builder<Didactic>().setQuery(query, Didactic.class).build();
        mDidacticAdapter = new DidacticAdapter(options, getContext());
        mDidacticAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mDidacticAdapter);
        mDidacticAdapter.startListening();
    }

    private void goToForm() {
        startActivity(new Intent(getContext(), DidacticFormActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        getDidacticByCurrentTeacher();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDidacticAdapter.stopListening();
        if (mDidacticSearch != null) {
            mDidacticSearch.stopListening();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getDidacticByCurrentTeacher();
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