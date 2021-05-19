package me.foolishchow.androidplugins.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.foolishchow.android.annotation.FragmentParam;
import me.foolishchow.android.annotation.Navigation;
import me.foolishchow.android.annotation.NavigationAction;
import me.foolishchow.autoparamdemo.R;

/**
 * Description:
 * Author: foolishchow
 * Date: 13/3/2021 12:06 PM
 */
@Navigation(actions = {
        @NavigationAction(
                name = "fromMain",
                actionId = R.id.action_main_to_child,
                description = "jump from main to child"
        )
})
public class MyFragment extends Fragment {

    @FragmentParam
    int param;

    @FragmentParam
    String param1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child,container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
