package com.reagentnative;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import android.app.Activity;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

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

    setUpmDNS();
    setContentView(mReactRootView);
  }


  private void setUpmDNS() {
    try {
      WifiManager wifi = (WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
      final InetAddress deviceIPAddress = getDeviceIpAddress(wifi);

      Log.d(TAG, ">> deviceIPAddress: " + deviceIPAddress.toString());
      //lock = wifi.createMulticastLock(getClass().getName());
      //lock.setReferenceCounted(true);
      //lock.acquire();
      Log.d(TAG, "Starting ZeroConf probe...");
      jmdns = JmDNS.create(deviceIPAddress, "android.simulator.device");

      jmdns = JmDNS.create();
      ServiceInfo serviceInfo = ServiceInfo.create("_test._tcp.local.",
          "AndroidTest", 0,
          "test from android");
      jmdns.registerService(serviceInfo);
    } catch (IOException e) {

    }
  }

  private InetAddress getDeviceIpAddress(WifiManager wifi) {
    InetAddress result = null;
    try {
      // default to Android localhost
      result = InetAddress.getByName("10.0.0.2");

      // figure out our wifi address, otherwise bail
      WifiInfo wifiinfo = wifi.getConnectionInfo();
      int intaddr = wifiinfo.getIpAddress();
      byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff) };
      result = InetAddress.getByAddress(byteaddr);
    } catch (UnknownHostException ex) {
      Log.w(TAG, String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
    }

    return result;
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
