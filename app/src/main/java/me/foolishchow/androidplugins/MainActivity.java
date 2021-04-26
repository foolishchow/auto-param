package me.foolishchow.androidplugins;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import java.util.List;

import me.foolishchow.android.annotation.IntentParam;
import me.foolishchow.autoparamdemo.R;
import me.foolishchow.autoparamdemo.navigation.MainNavigation;


public class MainActivity extends AppCompatActivity {

    @IntentParam
    List<Integer> ints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController controller = getNavController();
        MainNavigation navDestinations = new MainNavigation(controller);

        NavGraph inflate = controller.getNavInflater().inflate(R.navigation.main);
        String displayName = inflate.getNavigatorName();
        //Class<MainActivity$ShadowLifeCycleDelegate> classes =
        //        MainActivity$ShadowLifeCycleDelegate.class;
        //mKernalViewBinding.mNavTitle.setText("sdasdasdasd");
        //setContentView(R.layout.activity_main);
        //MainActivity$ShadowLifeCycleDelegate mainActivity$ShadowLifeCycleDelegate = new MainActivity$ShadowLifeCycleDelegate();
        //mainActivity$ShadowLifeCycleDelegate.restoreInstanceState();
    }

    @Nullable
    private NavController getNavController() {
        try {
            NavHostFragment loginNavHost = (NavHostFragment) getSupportFragmentManager().findFragmentByTag("login_nav_host");
            if (loginNavHost == null) return null;
            return loginNavHost.getNavController();
        } catch (Exception ignored) {

        }
        return null;
    }


}