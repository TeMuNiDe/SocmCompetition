package com.vitech.socmcompetition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instamojo.android.Instamojo;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;
import org.json.JSONObject;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_END;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_RULES_AND_REGULATIONS;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_STORY;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_T_AND_C;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_USER_DETAILS;


public class StorySubmitActivity extends AppCompatActivity {

NoTouchPager mainPager;
 MainPageAdapter adapter;
    String uname = "NO_NAME",umail = "NO_EMAIL",uref="NO_REF";
    UserDetailsFragment fragment;
ProgressDialog paymentDialog;
    String clientID,clientSecret,api_key,auth_token;
    FirebaseDatabase database;
    Story underSubmission;
Button next;
    ImageView payStatView;
    View writeStory;
    TextView payStatText;


    public class Story{

String reference;
        String story_id;
        String transaction_id;
        String submission_status;
        String transaction_status;
        String order_id;
        String payment_id;


        public Story(String story_id, String transaction_id, String submission_status, String transaction_status, String order_id, String payment_id,String reference){
            this.story_id =  story_id;
            this.transaction_id = transaction_id;
            this.submission_status = submission_status;
            this.transaction_status = transaction_status;
            this.order_id = order_id;
            this.payment_id = payment_id;
            this.reference = reference;

        }


        public Map<String,String> toMap(){
            Map<String,String> map = new HashMap<>();
            map.put("story_id",story_id);
            map.put("transaction_id",transaction_id);
            map.put("submission_status",submission_status);
            map.put("transaction_status",transaction_status);
            map.put("order_id",order_id);
            map.put("payment_id",payment_id);
            map.put("reference",reference);
            return map;
        }


    }

    public enum NAV_STATE{
       NAV_USER_DETAILS,NAV_T_AND_C,NAV_RULES_AND_REGULATIONS,NAV_STORY,NAV_END;
    }

    NAV_STATE currentNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_submit);
        currentNav = NAV_USER_DETAILS;
        WebView editor = (WebView)findViewById(R.id.story);
        editor.getSettings().setJavaScriptEnabled(true);
        editor.getSettings().setDomStorageEnabled(true);
        editor.getSettings().setLightTouchEnabled(true);

        Instamojo.setBaseUrl("https://test.instamojo.com/");
        editor.loadUrl("file:///android_asset/ckeditor/index.html");
        writeStory = findViewById(R.id.write_story_view);
mainPager = (NoTouchPager)findViewById(R.id.main_pager);
 adapter = new MainPageAdapter(getSupportFragmentManager());
  mainPager.setAdapter(adapter);
        next = (Button)findViewById(R.id.button);

        payStatView = (ImageView)findViewById(R.id.pay_stat);
        payStatText = (TextView)findViewById(R.id.message_state);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainPager.setCurrentItem(1);
                next.setVisibility(View.VISIBLE);
            }
        },1000);


               next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentNav){
                    case NAV_USER_DETAILS:if(verifyUserInput()){
                        mainPager.setCurrentItem(2,true);
                        currentNav = NAV_T_AND_C;
                        next.setText("Next");
                    }break;
                    case NAV_T_AND_C:mainPager.setCurrentItem(3,true);currentNav = NAV_RULES_AND_REGULATIONS;next.setText("Next");break;
                    case NAV_RULES_AND_REGULATIONS:hidePagerAndShowStorySubmit();next.setText("Submit");currentNav = NAV_STORY;break;
                    case NAV_STORY:createStory();next.setVisibility(View.GONE);currentNav=NAV_END;break;
                    case NAV_END:startActivity(new Intent(getApplicationContext(),CertificateActivity.class).putExtra("name",uname));


                }

            }
        });
    }

    boolean verifyUserInput(){

        fragment =(UserDetailsFragment) adapter.fragmentMap.get(1);

        if(fragment.name.getText().toString().equals("")||fragment.email.getText().toString().equals("")){
            return false;
        }
        else {
            uname = fragment.name.getText().toString();
umail = fragment.email.getText().toString();
            uref = fragment.reference.getText().toString();


            return true;
        }
    }
    void hidePagerAndShowStorySubmit(){

        Animation hideUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.hide_up);
        Animation showUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_up);
        hideUp.setDuration(700);
        showUp.setDuration(700);
        hideUp.setInterpolator(new AccelerateInterpolator());
        showUp.setInterpolator(new AccelerateInterpolator());
        hideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainPager.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        writeStory.setVisibility(View.VISIBLE);
writeStory.startAnimation(showUp);
        mainPager.startAnimation(hideUp);


    }



    void createStory(){
        String id = Long.toString(System.currentTimeMillis());
        String story_id = umail.replaceAll("\\W","_")+"_:"+id;
        String transaction_id = umail.replaceAll("\\W","_")+"_"+id;
        Story story = new Story(story_id,transaction_id,"created","created",null,null,uref);
        submitStory(story);
        Log.d("Story","Created");

    }


    void submitStory(final Story story) {

Log.d("Story","Submitted");

        paymentDialog = new ProgressDialog(StorySubmitActivity.this);
        paymentDialog.setIndeterminate(false);
        paymentDialog.setMessage("Please Wait...");
        paymentDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paymentDialog.setCancelable(false);
        paymentDialog.show();
        database =  FirebaseDatabase.getInstance();
        database.getReference("test_client_id").addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        clientID =  (String) dataSnapshot.getValue();

        Log.d("ClientID",clientID);
        database.getReference("test_client_secret").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clientSecret  = (String)dataSnapshot.getValue();
                Log.d("ClientSecret",clientSecret);
                getAccessToken(story);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
databaseError.toException().printStackTrace();
            handleException(databaseError.toException());
            }
        });



    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        databaseError.toException().printStackTrace();
        handleException(databaseError.toException());
    }
});
    }



    void getAccessToken(final Story story){


        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder().addEncoded("client_id",clientID).addEncoded("client_secret",clientSecret).addEncoded("grant_type", "client_credentials").build();

        try{
            okhttp3.Request request = new okhttp3.Request.Builder().addHeader("content-type","application/x-www-form-urlencoded").url("https://test.instamojo.com/oauth2/token/").post(body).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
handleException(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        startPayment(object.getString("access_token"),story);
Log.d("Access Token",object.getString("access_token"));

                    }catch (Exception e){
                        e.printStackTrace();
                        handleException(e);
                    }
                }
            });

        }catch(Exception e)

        {
            handleException(e);
            e.printStackTrace();
        }

    }


    void startPayment(String access_token, final Story story){

Log.d("Story",story.toMap().toString());
        Order order = new Order(access_token,story.transaction_id,uname,fragment.email.getText().toString(),"8520926489","100","SOCM");
        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(Order order, Exception error) {

                if (error != null) {
                     handleException(error);
                    return;
                }
               underSubmission = story;
                startPreCreatedUI(order);
            }
        });

        request.execute();
    }


    void startPreCreatedUI(Order order){

        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

          if (orderID != null && transactionID != null && paymentID != null) {
                underSubmission.payment_id = paymentID;
                underSubmission.order_id = orderID;

                postPaymentJob(underSubmission);
                String ord  = "Order ID:"+orderID+":::TRANS:"+transactionID+"::::PAYID :"+paymentID;
                Log.d("order",ord);
                Toast.makeText(getApplicationContext(),"Order ID:"+orderID+":::TRANS:"+transactionID+"::::PAYID :"+paymentID,Toast.LENGTH_LONG).show();


            } else {
handleException(new PaymentCancelledException());
            }
        }
    }

    void postPaymentJob(final Story story){

        Log.d("post_payment","started");

        database.getReference("test_api_key").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                api_key = (String) dataSnapshot.getValue();
Log.d("api_key",api_key);

                database.getReference("test_auth_token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        auth_token = (String)dataSnapshot.getValue();
                        Log.d("auth_token",auth_token);
validatePayment(auth_token,api_key,story);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
handleException(databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handleException(databaseError.toException());
            }
        });
    }

    void validatePayment(String auth_token, String api_key, final Story story){

        //Log.d("Validating",story.payment_id+"::Auth"+auth_token+"::Key"+api_key);
        OkHttpClient client = new OkHttpClient();
      okhttp3.Request request = new okhttp3.Request.Builder().url("https://test.instamojo.com/api/1.1/payments/"+story.payment_id+"/").addHeader("X-Api-Key",api_key).addHeader("X-Auth-Token",auth_token).get().build();
   client.newCall(request).enqueue(new Callback() {
       @Override
       public void onFailure(Call call, IOException e) {
         e.printStackTrace();
handleException(e);
       }

       @Override
       public void onResponse(Call call, Response response) throws IOException {
try{
    JSONObject resp = new JSONObject(response.body().string());

    Log.d("Response",resp.toString());
    if(resp.getBoolean("success")){
        JSONObject payment = resp.getJSONObject("payment");
if(payment.getString("status").equals("Credit")){
    onPaymentSuccess(story);
}else {
    handleException(new PaymentFailException());
}
    }

}catch (Exception e){
  handleException(e);
}
       }
   });
    }


    void onPaymentSuccess(Story story){
paymentDialog.cancel();

        story.transaction_status="paid";
        story.submission_status="submitted";

        database.getReference().child("submissions").child(story.story_id).setValue(story.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError==null){
                    writeStory.setVisibility(View.GONE);
                    findViewById(R.id.final_view).setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    next.setText("Generate Certificate");
                }
                else {
                   handleException(databaseError.toException());
                }
            }
        });

    }
class PaymentFailException extends Exception{}
class NoInternetException extends Exception{}
class ConnectionTimeoutException extends Exception{}
class PaymentCancelledException extends Exception{}



void handleException(Exception e){
    if(e instanceof NoInternetException){
        Toast.makeText(getApplicationContext(),"Please Check your Network Connection",Toast.LENGTH_LONG).show();
        return;
    }
    if(e instanceof ConnectionTimeoutException){
        Toast.makeText(getApplicationContext(),"Please Check your Nerwork Connection",Toast.LENGTH_LONG).show();
        return;
    }
    if(e instanceof PaymentCancelledException){
      OnPaymentCancelled();
        return;
    }
    if(e instanceof PaymentFailException){
        OnPaymentCancelled();
        return;
    }

    if(e instanceof UnknownHostException){
        handleException(new NoInternetException());
        return;
    }
    if(e instanceof SocketTimeoutException){
        handleException(new ConnectionTimeoutException());
        return;
    }
    else {
        Toast.makeText(getApplicationContext(),"UnKnown Error Occcurred",Toast.LENGTH_LONG).show();
        FirebaseCrash.report(e);
        return;
    }


}

void OnPaymentCancelled(){
paymentDialog.cancel();
payStatView.setImageResource(R.drawable.ic_sad);
    payStatText.setText("Payment Failed..!");
    writeStory.setVisibility(View.GONE);
    findViewById(R.id.final_view).setVisibility(View.VISIBLE);


    }
    }

