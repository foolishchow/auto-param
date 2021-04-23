package me.foolishchow.androidplugins.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import me.foolishchow.android.annotation.FragmentParam;
import me.foolishchow.android.annotation.Navigation;
import me.foolishchow.android.annotation.NavigationAction;
import me.foolishchow.androidplugins.R;

/**
 * Description:
 * Author: foolishchow
 * Date: 13/3/2021 12:06 PM
 */
@Navigation(actions = {
        @NavigationAction(
                name = "fromMain",
                actionId = R.id.action_main_to_child,
                description = "从xx跳转"
        )
})
public class MainFragment extends BaseFragment {

    @FragmentParam(cacheToTag = true)
    int in;

    @FragmentParam
    boolean bool;

    @FragmentParam
    double doub;

    @FragmentParam
    Double doubl;


    @FragmentParam
    float floa;

    @FragmentParam
    Float mFloat;

    @FragmentParam
    char cha;

    @FragmentParam
    byte byt;

    @FragmentParam
    long mLong;

    @FragmentParam
    Integer integer;


    @FragmentParam
    String string;

    @FragmentParam
    List<String> list;

    @FragmentParam
    List<Integer> ints;

    @FragmentParam
    boolean[] bools;
    @FragmentParam
    Text text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        setArguments(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
