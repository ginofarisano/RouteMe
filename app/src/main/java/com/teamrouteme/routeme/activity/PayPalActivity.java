package com.teamrouteme.routeme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.teamrouteme.routeme.R;

import org.json.JSONException;

import java.math.BigDecimal;

/**
 * Chiamare questa classse con startActivity e un Bundle.
 * Nel bundle inviare un oggetto intero con chiave KEY_BUNBLE e come valore un
 * intero indicante il numero di crediti necessari per acquistare l'itinerario
 *
 */
public class PayPalActivity extends Activity {


    public static final String SELLER = "routeme@gmail.com";

    public static final String KEY_BUNDLE = "KEY_BUNBLE";

    public static final String KEY_DONATE = "KEY_DONATE";

    private static final String TO_BUY = "Per Acquisto di %s crediti da %s (%s) per %s";

    private static final String DONATE = "Offerta di %s euro da %s (%s) per %s :-)";

    private static final String MESSAGE_PAYPAL = "Per %s.\n Pagamento di %s euro da %s (%s)";


    private static final String CLIENT_ID_PAYPALL = "AUs9mMZKDuWPwR4bqpQPiW91ybKKubyuk_YMVpmH8K6YvK_2qn0H7oDu8csEHXFkd_gaBeb-lTydtMtJ";

    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId(CLIENT_ID_PAYPALL);

    private int creditToBuy;




    private TextView tvCreditToBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pall);

        Bundle b = getIntent().getExtras();

        //numero di crediti da acquistare
        creditToBuy=b.getInt(KEY_BUNDLE);


        tvCreditToBuy = (TextView)findViewById(R.id.tv_credit_to_buy);

        //sto comprando crediti
        if(b.getString(KEY_DONATE,"").length()==0)
            tvCreditToBuy.setText(String.format(TO_BUY, creditToBuy, ParseUser.getCurrentUser().get("name"), ParseUser.getCurrentUser().get("username"),creditToBuy));
        else
            //mi stai offrendo una birra
            tvCreditToBuy.setText( String.format(DONATE, creditToBuy, ParseUser.getCurrentUser().get("name"), ParseUser.getCurrentUser().get("username"),creditToBuy));

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);

    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    /**
     *  invio pagamento per l'acquisto dei crediti
     * @param pressed
     */
    public void onBuyPressed(View pressed) {

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.

        BigDecimal bdCreditToBuy = new BigDecimal(creditToBuy);


        PayPalPayment payment = new PayPalPayment(bdCreditToBuy, "EUR", String.format(MESSAGE_PAYPAL,SELLER,creditToBuy, ParseUser.getCurrentUser().get("name"),ParseUser.getCurrentUser().get("username")),
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }

    /*
     * Enable retrieval of shipping addresses from buyer's PayPal account
     */
    private void enableShippingAddressRetrieval(PayPalPayment paypalPayment, boolean enable) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification. (TODO: inviare 'conferma' sul server per la verifica.)
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/ (Vedi https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/)
                    // for more details. (per maggiori dettagli.)

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }

        finish();

    }




}
