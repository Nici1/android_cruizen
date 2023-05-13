// Generated by view binder compiler. Do not edit!
package org.pytorch.demo.objectdetection.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.pytorch.demo.objectdetection.R;

public final class FragmentFirstBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonFirst;

  @NonNull
  public final TextView textviewFirst;

  private FragmentFirstBinding(@NonNull ConstraintLayout rootView, @NonNull Button buttonFirst,
      @NonNull TextView textviewFirst) {
    this.rootView = rootView;
    this.buttonFirst = buttonFirst;
    this.textviewFirst = textviewFirst;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentFirstBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentFirstBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_first, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentFirstBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_first;
      Button buttonFirst = rootView.findViewById(id);
      if (buttonFirst == null) {
        break missingId;
      }

      id = R.id.textview_first;
      TextView textviewFirst = rootView.findViewById(id);
      if (textviewFirst == null) {
        break missingId;
      }

      return new FragmentFirstBinding((ConstraintLayout) rootView, buttonFirst, textviewFirst);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
