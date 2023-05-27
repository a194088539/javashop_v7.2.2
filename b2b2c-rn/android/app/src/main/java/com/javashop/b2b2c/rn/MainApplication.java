package com.javashop.b2b2c.rn;

import android.app.Application;

import com.facebook.react.ReactApplication;
import org.devio.rn.splashscreen.SplashScreenReactPackage;
import com.reactnativecommunity.webview.RNCWebViewPackage;
// import cn.reactnative.modules.weibo.WeiboPackage;
import com.theweflex.react.WeChatPackage;
import com.apsl.versionnumber.RNVersionNumberPackage;
import com.oblador.vectoricons.VectorIconsPackage;
import com.imagepicker.ImagePickerPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
import iyegoroff.RNColorMatrixImageFilters.ColorMatrixImageFiltersPackage;
import com.example.qiepeipei.react_native_clear_cache.ClearCachePackage;
import org.reactnative.camera.RNCameraPackage;
import com.enation.javashop.reactnative.android.addressselector.AddressSelectorPackage;
import com.reactlibrary.AlipayPackage;
import com.facebook.react.PackageList;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.brentvatne.react.ReactVideoPackage;

import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      @SuppressWarnings("UnnecessaryLocalVariable")
      List<ReactPackage> packages = new PackageList(this).getPackages();
      // packages.add(new MainReactPackage());
      // packages.add(new SplashScreenReactPackage());
      // packages.add(new RNCWebViewPackage());
      // packages.add(new WeiboPackage());
      // packages.add(new WeChatPackage());
      // packages.add(new RNVersionNumberPackage());
      // packages.add(new VectorIconsPackage());
      // packages.add(new QQPackage());
      // packages.add(new ImagePickerPackage());
      // packages.add(new RNGestureHandlerPackage());
      // packages.add(new ColorMatrixImageFiltersPackage());
      // packages.add(new ClearCachePackage());
      // packages.add(new RNCameraPackage());
      // packages.add(new AddressSelectorPackage());
      // packages.add(new AlipayPackage());
      return packages;
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
  }

}
