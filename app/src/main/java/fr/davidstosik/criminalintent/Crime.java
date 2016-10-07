package fr.davidstosik.criminalintent;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by sto on 10/2/16.
 */
public class Crime extends BaseObservable {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    @Bindable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
        notifyPropertyChanged(BR.title);
    }

    public UUID getId() {
        return mId;
    }

    @Bindable
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
        notifyPropertyChanged(BR.date);
    }

    @Bindable
    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
        notifyPropertyChanged(BR.solved);
    }
}
