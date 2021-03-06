package com.dema.versatile.mediation.core.im;

import android.text.TextUtils;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsEncrypt;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IAdPlatformMgr;
import com.dema.versatile.mediation.core.in.IAdPlatformMgrListener;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.utils.UtilsAd;
import com.dema.versatile.mediation.utils.UtilsAdmob;

public class AdmobPlatformMgr implements IAdPlatformMgr {
    public AdmobPlatformMgr() {
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean requestBannerAdAsync(String strAdKey, String strAdID, String strAdBannerSize, Size size, IAdPlatformMgrListener iAdPlatformMgrListener) {

        UtilsAd.logE("rick-superlog-requestBannerAdAsync", "strAdID:"+strAdID+" , strAdBannerSize:"+strAdBannerSize);

        if (TextUtils.isEmpty(strAdID) || TextUtils.isEmpty(strAdBannerSize))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            AdView adView = new AdView(CMMediationFactory.getApplication());
            adView.setAdUnitId(strAdID);
            adView.setAdSize(UtilsAdmob.getBannerSize(CMMediationFactory.getApplication(), strAdBannerSize, size));
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "loaded");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(adView, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onAdFailedToLoad(int var1) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsJson.JsonSerialization(jsonObject, "code", var1);
                    UtilsJson.JsonSerialization(jsonObject, "message", UtilsAdmob.getErrorMessageByErrorCode(var1));
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(var1);
                    }
                }

                @Override
                public void onAdImpression() {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "impression");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }

                @Override
                public void onAdOpened() {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "clicked");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }
            });
            adView.loadAd(new AdRequest.Builder().build());
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "request");
            UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error error) {

        }

        return false;
    }

    @Override
    public boolean requestNativeAdAsync(String strAdKey, String strAdID, int[] arrayResLayoutID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            AdLoader adLoader = new AdLoader.Builder(CMMediationFactory.getApplication(), strAdID)
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "loaded");
                            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                            if (null != iAdPlatformMgrListener) {
                                iAdPlatformMgrListener.onAdLoaded(unifiedNativeAd, strAdKey, strAdRequestID);
                            }
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "failed");
                            UtilsJson.JsonSerialization(jsonObject, "code", errorCode);
                            UtilsJson.JsonSerialization(jsonObject, "message", UtilsAdmob.getErrorMessageByErrorCode(errorCode));
                            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                            if (null != iAdPlatformMgrListener) {
                                iAdPlatformMgrListener.onAdFailed(errorCode);
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "impression");
                            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                            if (null != iAdPlatformMgrListener) {
                                iAdPlatformMgrListener.onAdImpression();
                            }
                        }

                        @Override
                        public void onAdClicked() {
                            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "clicked");
                            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                            if (null != iAdPlatformMgrListener) {
                                iAdPlatformMgrListener.onAdClicked();
                            }
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "request");
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {

        }

        return false;
    }

    @Override
    public boolean requestInterstitialAdAsync(String strAdKey, String strAdID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            InterstitialAd interstitialAd = new InterstitialAd(CMMediationFactory.getApplication());
            interstitialAd.setAdUnitId(strAdID);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "loaded");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(interstitialAd, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "code", errorCode);
                    UtilsJson.JsonSerialization(jsonObject, "message", UtilsAdmob.getErrorMessageByErrorCode(errorCode));
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(errorCode);
                    }
                }

                @Override
                public void onAdOpened() {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "impression");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }

                @Override
                public void onAdLeftApplication() {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "clicked");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }
            });

            interstitialAd.loadAd(new AdRequest.Builder().build());
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "request");
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error error) {

        }

        return false;
    }

    @Override
    public boolean showBannerAd(AdBean adBean, ViewGroup VGContainer) {
        if (null == adBean || null == adBean.mObjectAd || null == adBean.mIAdItem)
            return false;

        JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(adBean.mAdKey, adBean.mIAdItem.getAdID(), adBean.mAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "impression");
        UtilsJson.JsonSerialization(jsonObject, "size", adBean.mIAdItem.getAdBannerSize());
        UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, jsonObject);

        return UtilsAd.showAdView(CMMediationFactory.getApplication(), (View) adBean.mObjectAd, VGContainer, adBean.mIAdItem.isNeedMask());
    }

    @Override
    public boolean showNativeAd(AdBean adBean, ViewGroup VGContainer, int[] arrayResLayoutID) {
        if (null == adBean || null == adBean.mObjectAd || null == adBean.mIAdItem)
            return false;

        return UtilsAd.showAdView(CMMediationFactory.getApplication(),
                UtilsAdmob.getDefaultNativeAdView(CMMediationFactory.getApplication(), (UnifiedNativeAd) adBean.mObjectAd, arrayResLayoutID),
                VGContainer, adBean.mIAdItem.isNeedMask());
    }

    @Override
    public boolean showInterstitialAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        ((InterstitialAd) adBean.mObjectAd).show();
        return true;
    }

    @Override
    public boolean releaseAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        if (adBean.mObjectAd instanceof com.google.android.gms.ads.AdView) {
            ((com.google.android.gms.ads.AdView) adBean.mObjectAd).destroy();
        } else if (adBean.mObjectAd instanceof com.google.android.gms.ads.formats.UnifiedNativeAd) {
            ((com.google.android.gms.ads.formats.UnifiedNativeAd) adBean.mObjectAd).destroy();
        } else {
            return false;
        }

        return true;
    }
}
