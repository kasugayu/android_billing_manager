package com.example.myapplication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

// PurchaseUpdatedListener
interface BillingManagerSetupListener {
    void onSetupFinished (@NonNull BillingResult billingResult);
    void onPurchaseUpdated (@NonNull BillingResult billingResult, @Nullable List<Purchase> list);
    void onDisconnected ();
    void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult);
}

// Porduct一覧取得完了Listener
interface BillingQueryProductsListerner {
    void onResult (BillingResult billingResult, List<SkuDetails> skuDetailsList);
}

// 起動プロセス終了Listener
interface LaunchPurchaseFlowListerner {
    void onResult (BillingResult billingResult);
}

// 課金管理Manager
public class BillingManager implements PurchasesUpdatedListener, AcknowledgePurchaseResponseListener {

    private final BillingClient mBillingClient;
    private final Activity mActivity;

    private final String TAG = "HytBillingManager";
    private BillingManagerSetupListener listener = null;

    // @Brief : コンストラクタ
    public BillingManager(Activity activity, BillingManagerSetupListener listener) {
        this(activity);
        this.listener = listener;
    }

    // @Brief : コンストラクタ
    public BillingManager(Activity activity) {
        mActivity = activity;
        mBillingClient = BillingClient
                .newBuilder(mActivity)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                // Callback実行
                if (listener != null) listener.onSetupFinished(billingResult);
            }

            @Override
            public void onBillingServiceDisconnected() {

                // Callback実行
                if (listener != null) listener.onDisconnected();
            }
        });
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        // Callback実行
        if (listener != null) listener.onPurchaseUpdated(billingResult, list);
    }

    // @Brief: ユーザーの購入アイテムを取得
    public Purchase.PurchasesResult QueryPurchases (String skuType)
    {
        if (!mBillingClient.isReady()) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready");
        }

        // Query for existing in app products that have been purchased. This does NOT include subscriptions.
        Purchase.PurchasesResult result = mBillingClient.queryPurchases(skuType);
        if (result.getPurchasesList() == null) {
            Log.i(TAG, "No existing in app purchases found.");
        } else {
            Log.i(TAG, "Existing purchases: ${result.purchasesList}");
        }

        return result;
    }

    // 通常商品一覧を取得する
    public void queryInAppProducts(List<String> skuList, BillingQueryProductsListerner listerner) {
        queryProducts(skuList, BillingClient.SkuType.INAPP, listerner);
    }

    // 定期購入商品一覧を取得する
    public void querySubsProducts(List<String> skuList, BillingQueryProductsListerner listerner) {
        queryProducts(skuList, BillingClient.SkuType.SUBS, listerner);
    }

    // skuTypeを指定して商品一覧を取得する
    // -2 : BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED
    // -1 : BillingClient.BillingResponseCode.SERVICE_DISCONNECTED
    //  0 : BillingClient.BillingResponseCode.OK
    //  4 : BillingClient.BillingResponseCode.ITEM_UNAVAILABLE
    //  5 : BillingClient.BillingResponseCode.DEVELOPER_ERROR
    //  6 : BillingClient.BillingResponseCode.ERROR
    //  7 : BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
    //  8 : BillingClient.BillingResponseCode.ITEM_NOT_OWNED
    public void queryProducts(List<String> skuList, String skuType, final BillingQueryProductsListerner listerner) {

        // パラメータ設定
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList)
                .setType(skuType);

        // 問い合わせ
        mBillingClient.querySkuDetailsAsync(
                params.build(),
                new SkuDetailsResponseListener()
                {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
                        // skuが取得できているか
                        if (skuDetailsList != null) {
                            Log.d(TAG, String.valueOf(skuDetailsList.size()));
                            // fire callback
                            listerner.onResult(billingResult, skuDetailsList);
                        } else {
                            Log.i(TAG, "No sku found from query");
                        }
                        Log.d(TAG, "querySkuDetailsAsync" + billingResult.getDebugMessage() + " " + billingResult.getResponseCode());
                    }
                });
    }

    // @Brief : 購入フローを始める
    public void launchPurchaseFlow(SkuDetails skuDetails, LaunchPurchaseFlowListerner listerner) {
         BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        BillingResult billingResult = mBillingClient.launchBillingFlow(mActivity, flowParams);
        listerner.onResult(billingResult);
        Log.i(TAG, "launchPurchaseFlow result ${responseCode}");
    }

    private void handlePurchase(Purchase purchase) {
        // If your app has a server component, first verify the purchase by checking that the
        // purchaseToken hasn't already been used.

        // If purchase was a consumable product (a product you want the user to be able to buy again)
        handleConsumableProduct(purchase);

        // If purchase was non-consumable product
        handleNonConsumableProduct(purchase);
    }

    private void handleConsumableProduct(final Purchase purchase) {

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        // 消費操作が終了
        mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                int responseCode = billingResult.getResponseCode();

                // 購入トークンが一致する1つのPurchaseを取得する
                Purchase p = null;
//                for (Purchase item : purchase) {
//                for (int i = 0; purchase.) {
                    if (purchase.getPurchaseToken().equals(purchaseToken)){
//                        p = item;
                    }
//                }

                if (responseCode == BillingClient.BillingResponseCode.OK) {

                    Log.d(TAG, "Billing OK!!");
                    // ココで消費完了なので、お好きな処理を行う
                    // p.getSku()
                    // p.getOrderId() などを使う

                }
            }
        });
    }

    private void handleNonConsumableProduct( Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams.Builder acknowledgePurchaseParams =
                        AcknowledgePurchaseParams
                                .newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken());

                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams.build(), (AcknowledgePurchaseResponseListener) mActivity);
            }
        }
    }

    // 購入承認の結果が来たとき呼ばれる
    @Override
    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
        if(listener != null) listener.onAcknowledgePurchaseResponse(billingResult);
    }

    // コネクションを終了する
    public void endConnection ()
    {
        mBillingClient.endConnection();
    }

    // 購入を確認する
    public void acknowledgePurchase (Purchase purchase)
    {
        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, this);
    }

    // 購入履歴を取得する
    public void queryPurchaseHistoryAsync(String skuType, PurchaseHistoryResponseListener listener)
    {
        mBillingClient.queryPurchaseHistoryAsync(skuType, listener);
    }

    public String responseCode2Text(int responseCode){
        switch(responseCode){
            case BillingClient.BillingResponseCode.OK:
                return "OK";
            case BillingClient.BillingResponseCode.USER_CANCELED:
                return "USER_CANCELED";
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                return "SERVICE_UNAVAILABLE";
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                return "BILLING_UNAVAILABLE";
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                return "ITEM_UNAVAILABLE";
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                return "DEVELOPER_ERROR";
            case BillingClient.BillingResponseCode.ERROR:
                return "ERROR";
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                return "ITEM_ALREADY_OWNED";
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                return "ITEM_NOT_OWNED";
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "SERVICE_DISCONNECTED";
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                return "FEATURE_NOT_SUPPORTED";
            default:
                return "ERROR";
        }
    }
}
