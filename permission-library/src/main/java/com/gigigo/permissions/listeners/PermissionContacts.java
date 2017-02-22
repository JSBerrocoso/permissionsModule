package com.gigigo.permissions.listeners;

import android.Manifest;
import android.content.Context;
import com.gigigo.permissions.R;
import com.gigigo.permissions.interfaces.Permission;

public class PermissionContacts implements Permission {

  private final Context context;

  public PermissionContacts(Context context) {
    this.context = context;
  }

  @Override
  public String getAndroidPermissionStringType() {
    return Manifest.permission.READ_CONTACTS;
  }

  @Override
  public int getPermissionSettingsDeniedFeedback() {
    return R.string.ggg_permission_settings;
  }

  @Override
  public int getPermissionDeniedFeedback() {
    return R.string.ggg_permission_denied_contacts;
  }

  @Override
  public int getPermissionRationaleTitle() {
    return R.string.ggg_permission_rationale_title_contacts;
  }

  @Override
  public int getPermissionRationaleMessage() {
    return R.string.ggg_permission_rationale_message_contacts;
  }

  @Override
  public int getNumRetry() {
    return context.getResources().getInteger(R.integer.ggg_permission_retries_contacts);
  }
}