package com.henry.criminalintent;

import android.content.Intent;

import java.util.UUID;
import java.util.function.IntToDoubleFunction;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks,CrimeListFragment.DeleteCrimeCallbacks{
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container)== null){
            Intent intent = CrimePagerActivity.newIntent(this,crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }




    @Override
    public void onCrimeDeleted(UUID crimeId) {
        Crime crime = CrimeLab.get(this).getCrime(crimeId);
        CrimeLab.get(this).removeCrime(crime);
    }
}
