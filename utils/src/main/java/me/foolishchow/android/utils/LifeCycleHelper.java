package me.foolishchow.android.utils;

import android.os.Bundle;
import android.os.PersistableBundle;

import java.util.HashMap;
import java.util.Map;

import me.foolishchow.android.utils.helpers.IInstanceStateHelper;

import static me.foolishchow.android.annotation.Constant.INSTANT_STATE_SUFFIX;

public class LifeCycleHelper {
	private static Map<String, IInstanceStateHelper<?>> helperCache = new HashMap<>();

    //private static Map<String,ISaveViewStateHelper> viewHelperCache = new HashMap<>();

    private final static int VIEW_STATE_KEY = -999;

	/**
	 * equate to{@link LifeCycleHelper#restoreInstanceState}, it's just renamed to make it easier to understand
	 */
	@Deprecated
	public static <T> void bind(T recover, Bundle savedInstanceState){
        restoreInstanceState(recover, savedInstanceState);
	}

	/**
	 * added while need to recover data, used in {android.app.Activity#onCreate(Bundle)}
	 *
	 * @param recover current Activity or Fragment
	 * @param savedInstanceState Bundle
	 */
	public static <T> void restoreInstanceState(T recover, Bundle savedInstanceState){
        restoreInstanceState(recover, savedInstanceState, null);
	}

    /**
     * equate to{@link LifeCycleHelper#restoreInstanceState(Object, Bundle)}, used in {android.app.Activity#onCreate(Bundle, PersistableBundle)}
     * added in 2.1.0
     *
     * @param recover current Activity or Fragment
     * @param savedInstanceState Bundle
     */
    public static <T> void restoreInstanceState(T recover, Bundle savedInstanceState, PersistableBundle persistentState){
        if(savedInstanceState != null || persistentState != null){
            IInstanceStateHelper<T> persistableSaveHelper = findSaveHelper(recover);
            if(persistableSaveHelper != null){
                persistableSaveHelper.restoreInstanceState(savedInstanceState, persistentState, recover);
            }
        }
    }

    /**
     * added while need to save data, used in {android.app.Activity#onSaveInstanceState(Bundle)}
     *
     * @param save current Activity or Fragment
     * @param outState Bundle
     */
	public static <T> void saveInstanceState(T save, Bundle outState){
        saveInstanceState(save, outState, null);
	}

    /**
     * equate to{@link LifeCycleHelper#saveInstanceState(Object, Bundle)}, used in {android.app.Activity#onSaveInstanceState(Bundle, PersistableBundle)}
     * added in 2.1.0
     *
     * @param save current Activity or Fragment
     * @param outState Bundle
     */
    public static <T> void saveInstanceState(T save, Bundle outState, PersistableBundle persistentState){
        if(outState != null || persistentState != null){
            IInstanceStateHelper<T> persistableSaveHelper = findSaveHelper(save);
            if(persistableSaveHelper != null){
                persistableSaveHelper.saveInstanceState(outState, persistentState, save);
            }
        }
    }


	///**
	// * added while need to save data, used in custom view
	// *
	// * @param save current custom view
	// * @param container SparseArray<Parcelable>
	// */
	//public static <T> void save(T save, SparseArray<Parcelable> container){
    //    if(container != null){
    //        ISaveInstanceStateHelper<T> viewStateHelper = findSaveHelper(save);
    //        if(viewStateHelper != null){
    //            Bundle bundle = (Bundle) container.get(VIEW_STATE_KEY);
    //            if(bundle == null){
    //                bundle = new Bundle();
    //            }
    //            viewStateHelper.save(bundle, null, save);
    //            container.put(VIEW_STATE_KEY, bundle);
    //        }
    //    }
	//}
    //
    ///**
    // * recover for custom view
    // *
    // * @param save current custom view
    // * @param container SparseArray<Parcelable>
    // */
    //public static <T> void recovery(T save, SparseArray<Parcelable> container){
    //    if(container != null){
    //        ISaveInstanceStateHelper<T> viewStateHelper = findSaveHelper(save);
    //        if(viewStateHelper != null){
    //            Bundle bundle = (Bundle) container.get(VIEW_STATE_KEY);
    //            if(bundle != null){
    //                viewStateHelper.recover(bundle, null, save);
    //            }
    //        }
    //    }
    //}

	private static <T> IInstanceStateHelper<T> findSaveHelper(T cl) {
		String clazz = cl.getClass().getName();
        IInstanceStateHelper<T> saveInstanceStateHelper = (IInstanceStateHelper<T>) helperCache.get(clazz);
		if(saveInstanceStateHelper == null){
			try {
				Class<?> findClass = Class.forName(clazz + INSTANT_STATE_SUFFIX);
				saveInstanceStateHelper = (IInstanceStateHelper<T>)findClass.newInstance();
				helperCache.put(clazz,saveInstanceStateHelper);
			} catch (ClassNotFoundException e) {
				// ignore
//				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return saveInstanceStateHelper;
	}

    //private static <T> ISaveViewStateHelper<T> findViewSaveHelper(T cl) {
    //    String clazz = cl.getClass().getName();
    //    ISaveViewStateHelper<T> saveViewStateHelper = viewHelperCache.get(clazz);
    //    if(saveViewStateHelper == null){
    //        try {
    //            Class<?> findClass = Class.forName(clazz + HELPER_END);
    //            saveViewStateHelper = (ISaveViewStateHelper<T>)findClass.newInstance();
    //            viewHelperCache.put(clazz,saveViewStateHelper);
    //        } catch (ClassNotFoundException e) {
    //            // ignore
    //        } catch (InstantiationException e) {
    //            e.printStackTrace();
    //        } catch (IllegalAccessException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //    return saveViewStateHelper;
    //}

}
