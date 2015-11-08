package com.reagentnative;

import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import android.app.Activity;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.view.KeyEvent;

//import com.facebook.react.BuildConfig;
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  public static final String TAG = "MainActivity";


  private ReactInstanceManager mReactInstanceManager;
  private ReactRootView mReactRootView;
  private JmDNS jmdns;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    //Log.d(TAG, "BuildConfig.Debug: " + BuildConfig.DEBUG);
    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "ReagentNative", null);

    setContentView(mReactRootView);
  }

  private void setUpmDNS() {
    try {
    jmdns = JmDNS.create();
    ServiceInfo serviceInfo = ServiceInfo.create("_test._tcp.local.",
        "AndroidTest", 0,
        "test from android");
    jmdns.registerService(serviceInfo);
    } catch (IOException e) {

    }
  }

  @Override
  public void onStart() {
    super.onStart();
    registerService();
  }

  private void registerService() {
    // Create the NsdServiceInfo object, and populate it.
    NsdServiceInfo serviceInfo  = new NsdServiceInfo();

    // The name is subject to change based on conflicts
    // with other services advertised on the same network.
    serviceInfo.setServiceName("reagent_android_native");
    serviceInfo.setServiceType("_http._tcp.");
    serviceInfo.setPort(9091);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
      mReactInstanceManager.showDevOptionsDialog();
      return true;
    }
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (mReactInstanceManager != null) {
      mReactInstanceManager.onPause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (mReactInstanceManager != null) {
      mReactInstanceManager.onResume(this);
    }
  }
}
