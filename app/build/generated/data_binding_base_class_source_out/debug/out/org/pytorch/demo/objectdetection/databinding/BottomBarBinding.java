// Generated by view binder compiler. Do not edit!
package org.pytorch.demo.objectdetection.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.pytorch.demo.objectdetection.R;

public final class BottomBarBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button cameraButton;

  @NonNull
  public final Button loginButton;

  @NonNull
  public final Button mapButton;

  private BottomBarBinding(@NonNull LinearLayout rootView, @NonNull Button cameraButton,
      @NonNull Button loginButton, @NonNull Button mapButton) {
    this.rootView = rootView;
    this.cameraButton = cameraButton;
    this.loginButton = loginButton;
    this.mapButton = mapButton;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static BottomBarBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static BottomBarBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.bottom_bar, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static BottomBarBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.camera_button;
      Button cameraButton = rootView.findViewById(id);
      if (cameraButton == null) {
        break missingId;
      }

      id = R.id.login_button;
      Button loginButton = rootView.findViewById(id);
      if (loginButton == null) {
        break missingId;
      }

      id = R.id.map_button;
      Button mapButton = rootView.findViewById(id);
      if (mapButton == null) {
        break missingId;
      }

      return new BottomBarBinding((LinearLayout) rootView, cameraButton, loginButton, mapButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
