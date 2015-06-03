package com.teamrouteme.routeme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    public static final String TEXT_DONATE = "Mi stai offrendo %d euro";

    public static final String INCREASE_CREDIT = "INCREASE_CREDIT";

    public static final String SELLER = "routeme@gmail.com";

    public static final String KEY_BUNDLE = "KEY_BUNBLE";

    public static final String KEY_DONATE = "KEY_DONATE";

    private static final String TO_BUY = "Acquisto di crediti da %s (%s) ";

    private static final String DONATE = "Offerta di %s euro da %s (%s) :-)";

    private static final String MESSAGE_PAYPAL = "Per %s.\n Da %s (%s)";


    private static final String CLIENT_ID_PAYPAL = "AUs9mMZKDuWPwR4bqpQPiW91ybKKubyuk_YMVpmH8K6YvK_2qn0H7oDu8csEHXFkd_gaBeb-lTydtMtJ";

    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId(CLIENT_ID_PAYPAL);

    private int creditToBuy;




    private TextView tvCreditToBuy;

    private Button btnAcquista100;
    private Button btnAcquista250;
    private Button btnAcquista500;

    private Intent intentForCallBack;

    public PayPalActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal);

        btnAcquista100 = (Button) findViewById(R.id.button100);
        btnAcquista250 = (Button) findViewById(R.id.button250);
        btnAcquista500 = (Button) findViewById(R.id.button500);

        Bundle b = getIntent().getExtras();

        //numero di crediti da acquistare
        creditToBuy=b.getInt(KEY_BUNDLE);


        tvCreditToBuy = (TextView)findViewById(R.id.tv_credit_to_buy);

        //sto comprando crediti
        if(b.getString(KEY_DONATE,"").length()==0)
            tvCreditToBuy.setText(String.format(TO_BUY, ParseUser.getCurrentUser().get("name"), ParseUser.getCurrentUser().get("username")));
        else{
            //mi stai offrendo una birra
            //tvCreditToBuy.setText( String.format(DONATE, creditToBuy, ParseUser.getCurrentUser().get("name"), ParseUser.getCurrentUser().get("username")));
            btnAcquista100.setText(String.format(TEXT_DONATE,1));
            btnAcquista250.setText(String.format(TEXT_DONATE, 2));
            btnAcquista500.setText(String.format(TEXT_DONATE, 3));


        }


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

        BigDecimal bdCreditToBuy = null;

        intentForCallBack = new Intent();

        switch (pressed.getId()){

            case R.id.button100:
                bdCreditToBuy = new BigDecimal(1);
                intentForCallBack.putExtra(INCREASE_CREDIT,100);
                break;

            case R.id.button250:
                bdCreditToBuy = new BigDecimal(2);
                intentForCallBack.putExtra(INCREASE_CREDIT,250);
                break;

            case R.id.button500:
                bdCreditToBuy = new BigDecimal(3);
                intentForCallBack.putExtra(INCREASE_CREDIT,500);
                break;
        }



        PayPalPayment payment = new PayPalPayment(bdCreditToBuy, "EUR", String.format(MESSAGE_PAYPAL,SELLER, ParseUser.getCurrentUser().get("name"),ParseUser.getCurrentUser().get("username")),
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
                    Toast.makeText(getBaseContext(), "Pagamento effettuato correttamente", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK, intentForCallBack);
                    finish();

                    // TODO: send 'confirm' to your server for verification. (TODO: inviare 'conferma' sul server per la verifica.)
                            // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/ (Vedi https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/)
                            // for more details. (per maggiori dettagli.)

                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), "Errore", Toast.LENGTH_SHORT).show();

                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);

                    setResult(RESULT_CANCELED, intentForCallBack);
                    finish();

                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
            Toast.makeText(getBaseContext(), "Errore", Toast.LENGTH_SHORT).show();

            setResult(RESULT_CANCELED, intentForCallBack);
            finish();




        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            Toast.makeText(getBaseContext(), "Errore", Toast.LENGTH_SHORT).show();

            setResult(RESULT_CANCELED, intentForCallBack);
            finish();
        }



    }




}
