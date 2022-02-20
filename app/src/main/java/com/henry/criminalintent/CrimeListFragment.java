package com.henry.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;
    private static final String SAVE_SUBTITLE_VISIBLE = "subtitle";
    private static final String TAG = CrimeListFragment.class.getSimpleName();
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mAdapterPosition = -1 ;
    private boolean mSubtitleVisible = false;
    private Callbacks mCallbacks;
    private DeleteCrimeCallbacks mDeleteCrimeCallbacks;

    /*
    Required interface for hosting activities
    */
    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    public interface DeleteCrimeCallbacks{
        void onCrimeDeleted(UUID crimeId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        /*
        為了讓fragment與activity交互，可以在Fragment 類中定義一個接口，並在activity中實現。
        Fragment在他們生命週期的onAttach()方法中獲取接口的實現，然後調用接口的方法來與Activity交互。
        */
        mCallbacks = (Callbacks) context;
        mDeleteCrimeCallbacks = (DeleteCrimeCallbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBLE);
        }

        setCrimeRecyclerViewItemTouchListener();

        updateUI();

        return view;
    }

    private void setCrimeRecyclerViewItemTouchListener() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Crime crime = mAdapter.mCrimes.get(position);
                Log.d(TAG, "onSwiped: "+crime);
                mDeleteCrimeCallbacks.onCrimeDeleted(crime.getId());
            }
        }).attachToRecyclerView(mCrimeRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        mDeleteCrimeCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem sutitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            sutitleItem.setTitle(R.string.hide_subtitle);
        }else {
            sutitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                //更新Menu選項文字
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeSize = crimeLab.getCrimes().size();
//        String subtitle = getString(R.string.subtitle_format,crimeCount);
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural,crimeSize,crimeSize,crimeSize);
        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null){
        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);
        }else {
            if(mAdapterPosition >-1){
            mAdapter.notifyItemChanged(mAdapterPosition);
            mAdapterPosition = -1;
            }else {
                mAdapter.setCrimes(crimes);
                mAdapter.notifyDataSetChanged();
            }
        }

        updateSubtitle();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CRIME){
            // Handle result
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView mTitleTextView;
        private final TextView mDateTextView;
        private Crime mCrime;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            mAdapterPosition = getAdapterPosition();
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            this.mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            return new CrimeHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }

    }

}
