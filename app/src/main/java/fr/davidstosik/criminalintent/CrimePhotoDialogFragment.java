package fr.davidstosik.criminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by sto on 2016/11/20.
 */

public class CrimePhotoDialogFragment extends DialogFragment {
    private static final String ARG_FILE_PATH = "file_path";
    private File mPhotoFile;
    private boolean mPhotoNeedsUpdate;
    private ImageView mPhotoView;

    static CrimePhotoDialogFragment newInstance(File file) {
        CrimePhotoDialogFragment f = new CrimePhotoDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, file.getAbsolutePath());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoFile = new File(getArguments().getString(ARG_FILE_PATH));
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_photo_dialog, container, false);
        mPhotoView = (ImageView)v.findViewById(R.id.view_photo);

        mPhotoNeedsUpdate = true;
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mPhotoNeedsUpdate) {
                    updatePhoto();
                    mPhotoNeedsUpdate = false;
                }
            }
        });
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimePhotoDialogFragment.this.dismiss();
            }
        });
        return v;
    }

    private void updatePhoto() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            int height = mPhotoView.getHeight();
            int width = mPhotoView.getWidth();
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), width, height);
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}