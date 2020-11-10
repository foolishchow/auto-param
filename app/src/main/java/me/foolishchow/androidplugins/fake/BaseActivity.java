package me.foolishchow.androidplugins.fake;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import me.foolishchow.androidplugins.R;
import me.foolishchow.androidplugins.databinding.ActivityBaseBinding;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 10/11/2020 5:39 PM <br/>
 */
public class BaseActivity<
        T extends ViewBinding
        > extends AppCompatActivity {

    protected ActivityBaseBinding mKernalViewBinding;
    protected T mViewBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater from = LayoutInflater.from(this);

        mKernalViewBinding = ActivityBaseBinding.inflate(from);
        setContentView(mKernalViewBinding.getRoot());
        Class<T> genericType = (Class<T>) getGenericType(this, 0);

        if (genericType != null) {
            try {
                Method inflate = genericType.getMethod("inflate", LayoutInflater.class,
                        ViewGroup.class, boolean.class);

                mViewBinding = (T) inflate.invoke(null,from,mKernalViewBinding.mContent,true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }



    @Nullable
    public static <T extends ViewBinding> Class<T> getGenericType(BaseActivity<T> t, int index) {
        Class<?> aClass = t.getClass();
        Type type = aClass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            //noinspection ConstantConditions
            if (actualTypeArguments == null || index >= actualTypeArguments.length) {
                return null;
            }
            return (Class<T>) (actualTypeArguments[index]);
        }
        return null;

        //Class<VH> ViewHolderClass =
        //        (Class<VH>) ((ParameterizedType) t.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }




}
