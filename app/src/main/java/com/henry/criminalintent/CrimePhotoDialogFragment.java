package com.henry.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CrimePhotoDialogFragment extends DialogFragment {

    private static final String ARG_PHOTO_FILE = "photo_file";
    private ImageView mPhoto;

    public static CrimePhotoDialogFragment newInstance(File path){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_FILE,path);

        CrimePhotoDialogFragment fragment = new CrimePhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_photo,null);
        File photoFile = (File)getArguments().getSerializable(ARG_PHOTO_FILE);
        mPhoto = (ImageView) view.findViewById(R.id.dialog_photo);
        Bitmap bitmap = PictureUtils.getScaleBitmap(photoFile.getPath(),getActivity());
        mPhoto.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getContext())
                            .setView(view)
                            .create();


    }
}
