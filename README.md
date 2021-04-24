# Android Auto Param
-----

generate param jumper for Activity and Fragment

[![Release](https://jitpack.io/v/foolishchow/auto-param.svg)]
(https://jitpack.io/#foolishchow/auto-param)

## usage

- dependence
  ```gradle
  implementation 'com.github.foolishchow.auto-param:utils:0.0.22'
  annotationProcessor 'com.github.foolishchow.auto-param:processor:0.0.22'
  ```
- activity   
  ```java
  public MyActivity extends XXXActivity{
    @IntentParam
    int param;

  }
  ```
  this will generate `MyActivityJumper`
  ```java
  public class MyActivityJumper extends IntentBuilder {
    private static final String PARAM = "eca07335a33c5aeb5e1bc7c98b4b9d80";

    public static MyActivityJumper with(Context context) {
      MainActivityJumper var = new MyActivityJumper();
      var.setContext(context);
      var.setIntent(new Intent(context,MyActivityJumper.class));
      return var;
    }

    public MyActivityJumper Param(int param) {
      mIntent.putExtra(PARAM,param);
      return this;
    }

    public static void parse(MyActivity activity) {
      if(activity == null || activity.isFinishing() || activity.isDestroyed()) {
        return;
      }
      Intent intent = activity.getIntent();
      if (intent == null) {
        return;
      }
      if (intent.hasExtra(PARAM)) {
        activity.param = intent.getIntExtra(PARAM,Integer.MIN_VALUE);
      }
    }
  }

  ```
  then you can use `MyActivityJumper` to start `MyActivity`
  ```java
  MyActivityJumper.with(context).Param(1).start();
  ```

- fragment
  ```java
  public class MyFragment extends Fragment {

    @FragmentParam
    int param;

    @FragmentParam
    String param1;
  }
  ```
  this will generate `MyFragmentJumper`
  ```java
  public class MyFragmentJumper {
    private static final String PARAM = "eca07335a33c5aeb5e1bc7c98b4b9d80";

    private static final String PARAM1 = "a2cbb63ab0f80334d9a100be6c372d35";

    private final Bundle mBundle = new Bundle();

    public Bundle getBundle() {
      return mBundle;
    }

    public static MyFragmentJumper with() {
      return new ChildFragmentJumper();
    }

    public MyFragmentJumper Param(int param) {
      mBundle.putInt(PARAM,param);
      return this;
    }

    public MyFragmentJumper Param1(String param) {
      mBundle.putString(PARAM1,param);
      return this;
    }

    public MyFragment build() {
      MyFragment fragment = null;
      try {
        fragment = MyFragment.class.newInstance();
        fragment.setArguments(mBundle);
      } catch (Throwable e) {
        e.printStackTrace();
      }
      assert fragment != null;
      return fragment;
    }

    public static void parse(MyFragment fragment) {
      if (fragment == null) {
        return;
      }
      Bundle bundle = fragment.getArguments();
      if (bundle == null) {
        return;
      }
      if (bundle.containsKey(PARAM)) {
        fragment.param = bundle.getInt(PARAM,Integer.MIN_VALUE);
      }
      if (bundle.containsKey(PARAM1)) {
        fragment.param1 = bundle.getString(PARAM1);
      }
    }
  }
  ```
  then you can use
  ```java
  MyFragment fragment = MyFragmentJumper.with().Param(1).Param1("").build();
  ```

- jetpack navigation
  ```java
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

  }
  ```
  this will generate 
  ```java
  public class MyFragmentJumper {
    private static final String PARAM = "eca07335a33c5aeb5e1bc7c98b4b9d80";

    private static final String PARAM1 = "a2cbb63ab0f80334d9a100be6c372d35";

    private final Bundle mBundle = new Bundle();

    /**
    * jump from main to child
    */
    public void fromMain(View view) {
      Navigation.findNavController(view).navigate(2131230775,mBundle) ;
    }

    public Bundle getBundle() {
      return mBundle;
    }

    public static MyFragmentJumper with() {
      return new MyFragmentJumper();
    }

    public MyFragmentJumper Param(int param) {
      mBundle.putInt(PARAM,param);
      return this;
    }

    public MyFragmentJumper Param1(String param) {
      mBundle.putString(PARAM1,param);
      return this;
    }

    public MyFragment build() {
      MyFragment fragment = null;
      try {
        fragment = MyFragment.class.newInstance();
        fragment.setArguments(mBundle);
      } catch (Throwable e) {
        e.printStackTrace();
      }
      assert fragment != null;
      return fragment;
    }

    public static void parse(MyFragment fragment) {
      if (fragment == null) {
        return;
      }
      Bundle bundle = fragment.getArguments();
      if (bundle == null) {
        return;
      }
      if (bundle.containsKey(PARAM)) {
        fragment.param = bundle.getInt(PARAM,Integer.MIN_VALUE);
      }
      if (bundle.containsKey(PARAM1)) {
        fragment.param1 = bundle.getString(PARAM1);
      }
    }
  }
  ```
