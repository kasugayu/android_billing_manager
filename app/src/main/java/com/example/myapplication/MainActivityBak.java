//package com.example.myapplication;
//
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.billingclient.api.AcknowledgePurchaseParams;
//import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.BillingClientStateListener;
//import com.android.billingclient.api.BillingFlowParams;
//import com.android.billingclient.api.BillingResult;
//import com.android.billingclient.api.Purchase;
//import com.android.billingclient.api.PurchaseHistoryRecord;
//import com.android.billingclient.api.PurchaseHistoryResponseListener;
//import com.android.billingclient.api.PurchasesUpdatedListener;
//import com.android.billingclient.api.SkuDetails;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivityBak extends AppCompatActivity
//        implements View.OnClickListener, PurchasesUpdatedListener, AcknowledgePurchaseResponseListener {
//
//    TextView textView1;
//    private BillingClient billingClient;
//    List<SkuDetails> mySkuDetailsList;
//    final String TAG = "Billing Test";
//
//    // アプリ開始時に呼ばれる
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // 操作ボタンと結果出力欄を準備する
//        textView1 = findViewById(R.id.text_view1);
//        findViewById(R.id.button_get_skus).setOnClickListener(this);
//        findViewById(R.id.button_query_owned).setOnClickListener(this);
//        findViewById(R.id.button_purchase).setOnClickListener(this);
//        findViewById(R.id.button_purchase_history).setOnClickListener(this);
//
//        // BillingClientを準備する
//        billingClient = BillingClient.newBuilder(this)
//                .setListener(this).enablePendingPurchases().build();
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(BillingResult billingResult) {
//                int responseCode = billingResult.getResponseCode();
//                if (responseCode == BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//                    textView1.setText("Billing Setup OK");
//                } else {
//                    showResponseCode(responseCode);
//                }
//            }
//
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//                textView1.setText("Billing Servise Disconnected. Retry");
//            }
//        });
//    }
//
//    // ボタンクリック時に呼ばれる
//    @Override
//    public void onClick(View v) {
//        if (v != null) {
//            switch (v.getId()) {
//                case R.id.button_get_skus:
//                    querySkuList();
//                    break;
//
//                case R.id.button_query_owned:
////                    queryOwned();
//                    List<String> skuListToQuery = new ArrayList();
//
//                    skuListToQuery.add("purchase_test");
//                    billingManager.queryInAppProducts(skuListToQuery);
//                    break;
//
//                case R.id.button_purchase:
//                    startPurchase("android.test.purchased");
//                    break;
//
//                case R.id.button_purchase_history:
//                    List<String> skuListToQuery2 = new ArrayList();
//
//                    skuListToQuery2.add("purchase_test_sub");
//                    billingManager.querySubsProducts(skuListToQuery2);
////                    queryPurchaseHistory();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    // アプリ終了時に呼ばれる
//    @Override
//    protected void onDestroy() {
//        billingClient.endConnection();
//        super.onDestroy();
//    }
//
//    BillingManager billingManager;
//
//    // 購入したいアイテムを問い合わせる
//    void querySkuList() {
//        Log.d(TAG, "Begin querySkuList");
//
//        billingManager = new BillingManager(this);
//        return;
////        List skuList = new ArrayList<>();
////        skuList.add("android.test.purchased");  // prepared by Google
////        skuList.add("android.test.canceled");
////        skuList.add("android.test.refunded");
////        skuList.add("android.test.item_unavailable");
////
////        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
////        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
////        billingClient.querySkuDetailsAsync(params.build(),
////                new SkuDetailsResponseListener() {
////                    @Override
////                    public void onSkuDetailsResponse(BillingResult billingResult,
////                                                     List<SkuDetails> skuDetailsList) {
////                        // Process the result.
////                        StringBuffer resultStr = new StringBuffer("");
////                        int responseCode = billingResult.getResponseCode();
////                        if (responseCode == BillingClient.BillingResponseCode.OK) {
////                            // 後の購入手続きのためにSkuの詳細を保持
////                            mySkuDetailsList = skuDetailsList;
////                            // リストを表示
////                            if (skuDetailsList != null) {
////                                for (Object item : skuDetailsList) {
////                                    SkuDetails skuDetails = (SkuDetails) item;
////                                    String sku = skuDetails.getSku();
////                                    String price = skuDetails.getPrice();
////                                    resultStr.append("Sku=" + sku + " Price=" + price + "\n");
////                                }
////                            } else {
////                                resultStr.append("No Sku");
////                            }
////                            textView1.setText(resultStr);
////                        } else {
////                            showResponseCode(responseCode);
////                        }
////                    }
////                });
//    }
//
//    // 購入処理を開始する
//    void startPurchase(String sku) {
//        SkuDetails skuDetails = getSkuDetails(sku);
//        if (skuDetails != null) {
//            BillingFlowParams params = BillingFlowParams.newBuilder()
//                    .setSkuDetails(skuDetails)
//                    .build();
//            BillingResult billingResult = billingClient.launchBillingFlow(this, params);
//            showResponseCode(billingResult.getResponseCode());
//        }
//    }
//
//    // 指定したSKUの詳細をリスト内から得る
//    SkuDetails getSkuDetails(String sku) {
//        SkuDetails skuDetails = null;
//        if(mySkuDetailsList==null){
//            textView1.setText("Exec [Get Skus] first");
//        }else {
//            for (SkuDetails sd : mySkuDetailsList) {
//                if (sd.getSku().equals(sku)) skuDetails = sd;
//            }
//            if (skuDetails == null) {
//                textView1.setText(sku + " is not found");
//            }
//        }
//        return skuDetails;
//    }
//
//    // 購入結果の更新時に呼ばれる
//    @Override
//    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
//        StringBuffer resultStr = new StringBuffer("");
//        int billingResultCode = billingResult.getResponseCode();
//        if (billingResultCode == BillingClient.BillingResponseCode.OK
//                && purchases != null) {
//            for (Purchase purchase : purchases) {
//                //購入を承認する
//                String state = handlePurchase(purchase);
//                //購入したSkuの文字列と承認結果を表示する
//                String sku = purchase.getSku();
//                resultStr.append(sku).append("\n");
//                resultStr.append(" State=").append(state).append("\n");
//            }
//            textView1.setText(resultStr);
//        } else {
//            // Handle error codes.
//            showResponseCode(billingResultCode);
//        }
//    }
//
//    // 購入を承認する
//    String handlePurchase(Purchase purchase) {
//        String stateStr = "error";
//        int purchaseState = purchase.getPurchaseState();
//        if (purchaseState == Purchase.PurchaseState.PURCHASED) {
//            // Grant entitlement to the user.
//            stateStr = "purchased";
//            // Acknowledge the purchase if it hasn't already been acknowledged.
//            if (!purchase.isAcknowledged()) {
//                AcknowledgePurchaseParams acknowledgePurchaseParams =
//                        AcknowledgePurchaseParams.newBuilder()
//                                .setPurchaseToken(purchase.getPurchaseToken())
//                                .build();
//                billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);
//            }
//        }else if(purchaseState == Purchase.PurchaseState.PENDING){
//            stateStr = "pending";
//        }else if(purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE){
//            stateStr = "unspecified state";
//        }
//        return stateStr;
//    }
//
//    // 購入承認の結果が戻る
//    @Override
//    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
//        int responseCode = billingResult.getResponseCode();
//        if(responseCode != BillingClient.BillingResponseCode.OK) {
//            showResponseCode(responseCode);
//        }
//    }
//
//    // 購入済みアイテムを問い合わせる（キャッシュ処理）
//    void queryOwned(){
//        StringBuffer resultStr = new StringBuffer("");
//        Purchase.PurchasesResult purchasesResult
//                = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
//        int responseCode = purchasesResult.getResponseCode ();
//        if(responseCode== BillingClient.BillingResponseCode.OK){
//            resultStr.append("Query Success\n");
//            List<Purchase> purchases = purchasesResult.getPurchasesList();
//            if(purchases.isEmpty()){
//                resultStr.append("Owned Nothing");
//            } else {
//                for (Purchase purchase : purchases) {
//                    resultStr.append(purchase.getSku()).append("\n");
//                }
//            }
//            textView1.setText(resultStr);
//        }else{
//            showResponseCode(responseCode);
//        }
//    }
//
//    // 購入履歴を問い合わせる（ネットワークアクセス処理）
//    void queryPurchaseHistory() {
//        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
//                new PurchaseHistoryResponseListener() {
//                    @Override
//                    public void onPurchaseHistoryResponse(BillingResult billingResult,
//                                                          List<PurchaseHistoryRecord> purchasesList) {
//                        int responseCode = billingResult.getResponseCode();
//                        if (responseCode == BillingClient.BillingResponseCode.OK) {
//                            if (purchasesList == null || purchasesList.size() == 0) {
//                                textView1.setText("No History");
//                            } else {
//                                for (PurchaseHistoryRecord purchase : purchasesList) {
//                                    // Process the result.
//                                    textView1.setText("Purchase History="
//                                            + purchase.toString() + "\n");
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "Debug Message :" + billingResult.getDebugMessage());
//                            showResponseCode(responseCode);
//                        }
//                    }
//                });
//    }
//    // サーバの応答を表示する
//    void showResponseCode(int responseCode){
//        Log.d(TAG, "response Code  " + String.valueOf(responseCode));
//        switch(responseCode){
//            case BillingClient.BillingResponseCode.OK:
//                textView1.setText("OK");break;
//            case BillingClient.BillingResponseCode.USER_CANCELED:
//                textView1.setText("USER_CANCELED");break;
//            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
//                textView1.setText("SERVICE_UNAVAILABLE");break;
//            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
//                textView1.setText("BILLING_UNAVAILABLE");break;
//            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
//                textView1.setText("ITEM_UNAVAILABLE");break;
//            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
//                textView1.setText("DEVELOPER_ERROR");break;
//            case BillingClient.BillingResponseCode.ERROR:
//                textView1.setText("ERROR");break;
//            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
//                textView1.setText("ITEM_ALREADY_OWNED");break;
//            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
//                textView1.setText("ITEM_NOT_OWNED");break;
//            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
//                textView1.setText("SERVICE_DISCONNECTED");break;
//            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
//                textView1.setText("FEATURE_NOT_SUPPORTED");break;
//        }
//    }
//}