package com.jeduan.crop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.Math;

public class CropPlugin extends CordovaPlugin {
    private CallbackContext callbackContext;
    private Uri inputUri;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
      if (action.equals("cropImage")) {
          String imagePath = args.getString(0);
          JSONObject options = args.getJSONObject(1);
          int imgWidth = options.getInt("imgWidth") != -1 ? options.getInt("imgWidth") : 196;

          this.inputUri = Uri.parse(imagePath);

          PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
          pr.setKeepCallback(true);
          callbackContext.sendPluginResult(pr);
          this.callbackContext = callbackContext;

          cordova.setActivityResultCallback(this);
          CropImage.activity(this.inputUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(imgWidth, imgWidth)
                        .setFixAspectRatio(true)
                        .start(cordova.getActivity());
          return true;
      }
      return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                Uri imageUri = result.getUri();
                this.callbackContext.success("file://" + imageUri.getPath());
                this.callbackContext = null;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                try {
                    JSONObject err = new JSONObject();
                    err.put("message", "Error on cropping");
                    err.put("code", String.valueOf(resultCode));
                    this.callbackContext.error(err);
                    this.callbackContext = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                try {
                    JSONObject err = new JSONObject();
                    err.put("message", "User cancelled");
                    err.put("code", "userCancelled");
                    this.callbackContext.error(err);
                    this.callbackContext = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private String getTempDirectoryPath() {
        File cache = null;

        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + cordova.getActivity().getPackageName() + "/cache/");
        }
        // Use internal storage
        else {
            cache = cordova.getActivity().getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        cache.mkdirs();
        return cache.getAbsolutePath();
    }

    public Bundle onSaveInstanceState() {
        Bundle state = new Bundle();

        if (this.inputUri != null) {
            state.putString("inputUri", this.inputUri.toString());
        }

        return state;
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {

        if (state.containsKey("inputUri")) {
            this.inputUri = Uri.parse(state.getString("inputUri"));
        }

        this.callbackContext = callbackContext;
    }
}
