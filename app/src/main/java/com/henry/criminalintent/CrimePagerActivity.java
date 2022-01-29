package com.henry.criminalintent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private static final String EXTRA_CRIME_ID =
            "com.henry.android.criminalintent.crime_id";
    private Button mFirst;
    private Button mLast;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        mFirst = (Button) findViewById(R.id.btFirst);
        mLast = (Button) findViewById(R.id.btLast);


        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }


        });
        // mViewPager.setOnPageChangeListener  已Deprecated，要改成addOnPageChangeListener
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                toggleButton(position);
            }

            @Override
            public void onPageSelected(int position) {
                //Do nothing
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Do nothing
            }
        });

        for(int i=0;i<mCrimes.size();i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }





    public void onFirst(View view) {
        mViewPager.setCurrentItem(0);
    }

    public void onLast(View view) {
        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount()-1);
    }

    private void toggleButton(int position) {
        if(position == 0){
            mFirst.setEnabled(false);
        }
        else{
            mFirst.setEnabled(true);
        }
        if(position == mViewPager.getAdapter().getCount()-1){
            mLast.setEnabled(false);
        }else{
            mLast.setEnabled(true);
        }
    }



}