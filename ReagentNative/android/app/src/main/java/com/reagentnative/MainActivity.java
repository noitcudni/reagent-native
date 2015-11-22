package com.reagentnative;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;

//import com.facebook.react.BuildConfig;
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  public static final String TAG = "MainActivity";

  Handler handler = new Handler();

  private ReactInstanceManager mReactInstanceManager;
  private ReactRootView mReactRootView;
  private JmDNS jmdns;
  private MulticastLock lock;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

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

    handler.postDelayed(new Runnable() {
      public void run() {
        setupmDNS();
      }
    }, 1000);

  }

  private InetAddress getDeviceIpAddress(WifiManager wifi) {
    InetAddress result = null;
    try {
      // default to Android localhost
      result = InetAddress.getByName("127.0.0.1");

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

  protected void setupmDNS() {
    try {
      WifiManager wifi = (WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
      final InetAddress deviceIPAddress = getDeviceIpAddress(wifi);
      //InetAddress deviceIPAddress = InetAddress.getLocalHost();

      Log.d(TAG, ">> deviceIPAddress: " + deviceIPAddress.toString());
      lock = wifi.createMulticastLock(getClass().getName());
      lock.setReferenceCounted(true);
      lock.acquire();

      jmdns = JmDNS.create(deviceIPAddress, "android.simulator.device");
      ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.",
          "Ambly Reagent on Nexus", 8888,
          "Lih Android Test");
      jmdns.registerService(serviceInfo);
    } catch (IOException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  @Override
  public void onStart() {
    super.onStart();
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
