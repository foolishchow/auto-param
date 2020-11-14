package me.foolishchow.android.utils.binding;

import android.view.View;

import androidx.annotation.NonNull;

public interface ViewBinding<T extends View> {
    /**
     * Returns the outermost {@link View} in the associated layout file. If this binding is for a
     * {@code <merge>} layout, this will return the first view inside of the merge tag.
     */
    @NonNull
    T getRoot();
}



/*
* public final class ActivityBaseBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final FrameLayout mContent;

  @NonNull
  public final Toolbar mNav;

  @NonNull
  public final TextView mNavTitle;

  private ActivityBaseBinding(@NonNull RelativeLayout rootView, @NonNull FrameLayout mContent,
      @NonNull Toolbar mNav, @NonNull TextView mNavTitle) {
    this.rootView = rootView;
    this.mContent = mContent;
    this.mNav = mNav;
    this.mNavTitle = mNavTitle;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityBaseBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityBaseBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_base, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityBaseBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.m_content;
      FrameLayout mContent = rootView.findViewById(id);
      if (mContent == null) {
        break missingId;
      }

      id = R.id.m_nav;
      Toolbar mNav = rootView.findViewById(id);
      if (mNav == null) {
        break missingId;
      }

      id = R.id.m_nav_title;
      TextView mNavTitle = rootView.findViewById(id);
      if (mNavTitle == null) {
        break missingId;
      }

      return new ActivityBaseBinding((RelativeLayout) rootView, mContent, mNav, mNavTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
* */