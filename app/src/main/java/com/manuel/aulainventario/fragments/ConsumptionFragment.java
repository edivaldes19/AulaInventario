package com.manuel.aulainventario.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.activities.ConsumptionFormActivity;
import com.manuel.aulainventario.adapters.ConsumptionAdapter;
import com.manuel.aulainventario.models.Consumption;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.ConsumptionProvider;

public class ConsumptionFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    View mView;
    FloatingActionButton mFab;
    MaterialSearchBar mSearchBar;
    AuthProvider mAuthProvider;
    ConsumptionProvider mConsumptionProvider;
    ConsumptionAdapter mConsumptionAdapter, mConsumptionSearch;
    RecyclerView mRecyclerView;

    public ConsumptionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_consumption, container, false);
        mFab = mView.findViewById(R.id.fabConsumption);
        mRecyclerView = mView.findViewById(R.id.recyclerViewConsumption);
        mSearchBar = mView.findViewById(R.id.searchBarConsumption);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAuthProvider = new AuthProvider();
        mConsumptionProvider = new ConsumptionProvider();
        mSearchBar.setOnSearchActionListener(this);
        mFab.setOnClickListener(v -> goToForm());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    mFab.show();
                } else if (dy > 0) {
                    mFab.hide();
                }
            }
        });
        return mView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchByDescription(String description) {
        Query query = mConsumptionProvider.getConsumptionByDescription(description);
        FirestoreRecyclerOptions<Consumption> options = new FirestoreRecyclerOptions.Builder<Consumption>().setQuery(query, Consumption.class).build();
        mConsumptionSearch = new ConsumptionAdapter(options, getContext());
        mConsumptionSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mConsumptionSearch);
        mConsumptionSearch.startListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getConsumptionByCurrentTeacher() {
        Query query = mConsumptionProvider.getConsumptionByTeacher(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Consumption> options = new FirestoreRecyclerOptions.Builder<Consumption>().setQuery(query, Consumption.class).build();
        mConsumptionAdapter = new ConsumptionAdapter(options, getContext());
        mConsumptionAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mConsumptionAdapter);
        mConsumptionAdapter.startListening();
    }

    private void goToForm() {
        startActivity(new Intent(getContext(), ConsumptionFormActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        getConsumptionByCurrentTeacher();
    }

    @Override
    public void onStop() {
        super.onStop();
        mConsumptionAdapter.stopListening();
        if (mConsumptionSearch != null) {
            mConsumptionSearch.stopListening();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getConsumptionByCurrentTeacher();
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