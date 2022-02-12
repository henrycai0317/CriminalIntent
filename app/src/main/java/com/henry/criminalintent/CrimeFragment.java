package com.henry.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class CrimeFragment extends Fragment {
    private static final int REQUEST_CONTACT_PERMISSION = 80;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Button mSuspectButton;
    private Button mBtRemoveCrime;
    private Button mCallSuspect;
    private int mHasPhoneNumber =0;
    private String mSuspectId;

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK,null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){   // 多新增getArguments()判斷
        UUID cromeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(cromeId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);

        //Title
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //This space intentionally left blank
            }
        });

        //Date
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
            }
        });
        //Soved Check Box
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mBtRemoveCrime = (Button) v.findViewById(R.id.remove_crime);
        mBtRemoveCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                getActivity().finish();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain"); //普通文本的MIME Type
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);*/
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setChooserTitle(getString(R.string.send_report))
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .createChooserIntent();
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
//        pickContact.addCategory(Intent.CATEGORY_HOME);// 測試過濾器驗證
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

   /*     PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }*/


        mCallSuspect = (Button) v.findViewById(R.id.call_suspect);
        mCallSuspect.setOnClickListener( view -> {
            Intent phoneCall = new Intent(Intent.ACTION_DIAL);
            if(mCrime.getPhone() != null){
            Uri number = Uri.parse("tel:"+mCrime.getPhone());//"the "tel:" is needed to start Activity"
            phoneCall.setData(number);
            }
            startActivity(phoneCall);
        } );

        if(mCrime.getPhone() != null) {
            mCallSuspect.setText(mCrime.getPhone());
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.ETRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null){

           String suspectName =  getSuspectName(data);


            if(hasContractPermission()){
                updateSuspectNumber();
            }else {
                ActivityCompat.requestPermissions(
                        getActivity(),new String[]{Manifest.permission.READ_CONTACTS},REQUEST_CONTACT_PERMISSION);
            }


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CONTACT_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateSuspectNumber();
            }
        }
    }

    private void updateSuspectNumber() {
        String suspectNumber = getSuspectNumber(mSuspectId);
        mCallSuspect.setText(suspectNumber);

    }

    private String getSuspectNumber(String suspectId) {
        String suspectPhoneNumber = null;

        // The content URI of the CommonDataKinds.Phone
        Uri phoneContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // The columns to return for each row
        String[] queryFields = new String[] {
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,   // which is the default phone number.
                ContactsContract.CommonDataKinds.Phone.TYPE,
        };

        // Selection criteria
        String mSelectionClause = ContactsContract.Data.CONTACT_ID + " = ?";

        // Selection criteria
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = suspectId;

        // Does a query against the table and returns a Cursor object
        Cursor c = getActivity().getContentResolver()
                .query(phoneContactUri,queryFields, mSelectionClause, mSelectionArgs, null );

        try {
            // Double-check that you actually got results.
            if (c.getCount() == 0) {
                return null;
            }

            while (c.moveToNext()) {
                    suspectPhoneNumber = c.getString(1);
                    mCrime.setPhone(suspectPhoneNumber);
            }
        } finally {
            c.close();
        }

        return suspectPhoneNumber;
    }

    private boolean hasContractPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private String getSuspectName(Intent data) {
        Uri contactUri = data.getData();
        //Specify which fields you want your query to return
        //values for.
        String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID
        };

        //Perform your query - the contactUri is like a "where"
        //clause here
        Cursor c = getActivity().getContentResolver()
                .query(contactUri,queryFields,null,null,null);

        try {
            //Double-check that you actually got results
            if(c.getCount() == 0){
                return null;
            }

            //pull out the first column of the first row of data -
            //that is your suspect's name
            c.moveToFirst();
            String suspect = c.getString(0);
            mSuspectId = c.getString(1);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            return suspect;


        } finally {
            c.close();
        }

    }


    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect,suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(),dateString,solvedString,suspect);

        return report;
    }
}
