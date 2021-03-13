package me.foolishchow.androidplugins.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.foolishchow.androidplugins.R;

/**
 * Description:
 * Author: foolishchow
 * Date: 13/3/2021 12:06 PM
 */
public class ChildFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child,container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
