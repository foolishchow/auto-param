package me.foolishchow.androidplugins.fake;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.NavHostFragment;

/**
 * Description:
 * Author: foolishchow
 * Date: 26/4/2021 8:49 PM
 */
public class BaseGraph extends NavGraph {

    public BaseGraph(NavController controller) {
        //noinspection unchecked
        super((Navigator<? extends NavGraph>) controller.getNavigatorProvider().getNavigator("navigation"));

        //setStartDestination();
        //new NavDestination(controller.getNavigatorProvider().getNavigator(""))
        //addDestination();
    }

    public void addAction(@IdRes int actionId,@IdRes int fromDestinationId,
                          @IdRes int targetDestinationId) {
    }

    public  <T extends Fragment> void addDestination(Class<T> tClass,int id,String label){

    }



}
