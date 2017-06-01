package com.vitech.socmcompetition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_END;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_RETRY;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_RULES_AND_REGULATIONS;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_STORY;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_T_AND_C;
import static com.vitech.socmcompetition.StorySubmitActivity.NAV_STATE.NAV_USER_DETAILS;


public class StorySubmitActivity extends AppCompatActivity {

    public static final MediaType JSON    = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TEXT    = MediaType.parse("text/plain; charset=utf-8");

NoTouchPager mainPager;
 MainPageAdapter adapter;
    String uname = "NO_NAME",umail = "NO_EMAIL",uref="NO_REF",uphone="0000000000",college = " ";
    UserDetailsFragment fragment;
    ProgressDialog paymentDialog;
    String clientID,clientSecret,api_key,auth_token;
    FirebaseDatabase database;
    Story underSubmission;
    Button next;
    ImageView payStatView;
    EditText storyTitle;
    AlertDialog.Builder prompt;
    WebView storyContent;
    View writeStory;
    TextView payStatText;

    String client_id_ref = "";
    String client_secret_ref = "";
    String api_key_ref = "";
    String auth_token_ref ="";
    String access_token_url  ="";
    String payment_url = "";
    String payment_amount = "";
    int min_lines =0;
enum FLAG_PAYMENT{
    BEFORE_PAYMENT,AFTER_PAYMENT;
}
enum PAYMENT_TARGET{
    TARGET_TEST,TARGET_RELEASE,TARGET_PAYMENT_TEST;
}
PAYMENT_TARGET target = PAYMENT_TARGET.TARGET_RELEASE;
FLAG_PAYMENT flag_payment = FLAG_PAYMENT.BEFORE_PAYMENT;
    public class Story{

        String reference;
        String story_id;
        String transaction_id;
        String submission_status;
        String transaction_status;
        String order_id;
        String payment_id;
        String title;
        String content;


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
       NAV_USER_DETAILS,NAV_T_AND_C,NAV_RULES_AND_REGULATIONS,NAV_STORY,NAV_END,NAV_RETRY;
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
        editor.getSettings().setUseWideViewPort(true);

        prompt = new AlertDialog.Builder(this);
        switch (target){
            case TARGET_TEST:Instamojo.setBaseUrl("https://test.instamojo.com/");
                client_id_ref="test_client_id";
                client_secret_ref = "test_client_secret";
                api_key_ref = "test_api_key";
                auth_token_ref = "test_auth_token";
                payment_url = "https://test.instamojo.com/api/1.1/payments/";
                access_token_url = "https://test.instamojo.com/oauth2/token/";
                payment_amount = "100";
                min_lines=10;
                break;
            case TARGET_RELEASE: client_id_ref="client_id_socm";
                client_secret_ref = "client_secret_socm";
                api_key_ref = "api_key_socm";
                auth_token_ref = "auth_token_socm";
                payment_url = "https://www.instamojo.com/api/1.1/payments/";
                access_token_url = "https://www.instamojo.com/oauth2/token/";
                payment_amount = "100";
                min_lines  = 100;
                break;
            case TARGET_PAYMENT_TEST: client_id_ref="client_id";
                client_secret_ref = "client_secret";
                api_key_ref = "api_key";
                auth_token_ref = "auth_token";
                payment_url = "https://www.instamojo.com/api/1.1/payments/";
                access_token_url = "https://www.instamojo.com/oauth2/token/";
                payment_amount = "10";
                min_lines=10;
                break;

        }

        editor.loadUrl("file:///android_asset/ckeditor/index.html");
        writeStory = findViewById(R.id.write_story_view);
        storyTitle = (EditText)findViewById(R.id.story_title);
        storyContent = editor;
        mainPager = (NoTouchPager)findViewById(R.id.main_pager);
        adapter = new MainPageAdapter(getSupportFragmentManager());
        mainPager.setAdapter(adapter);
        next = (Button)findViewById(R.id.button);

        payStatView = (ImageView)findViewById(R.id.pay_stat);
        payStatText = (TextView)findViewById(R.id.message_state);


        storyContent.addJavascriptInterface(new StoryGrabberInterface(new StoryObtainedListener() {
            @Override
            public void onStoryObtained(final String[] content) {


                if(countWords(content[0])>=min_lines){
                    underSubmission.content = content[1];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadStory(underSubmission);
                        }
                    });
                }
                else {
                   prompt.setMessage("Write at least 100 words").show();

                }
            }
        }),"Storygrabber");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTitle("Registration");
                mainPager.setCurrentItem(1);
                next.setVisibility(View.VISIBLE);
            }
        },2000);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentNav){
                    case NAV_USER_DETAILS:if(verifyUserInput()){
                        mainPager.setCurrentItem(2,true);
                        setTitle("Terms and Conditions");
                        next.setText("Agree");
                        currentNav = NAV_T_AND_C;

                    }else{
                        prompt.setMessage("Invalid Details...!");
                        prompt.show();
                    }
                    break;
                    case NAV_T_AND_C:mainPager.setCurrentItem(3,true);setTitle("Rules and Regulations");currentNav = NAV_RULES_AND_REGULATIONS;next.setText("I Know");break;
                    case NAV_RULES_AND_REGULATIONS:hidePagerAndShowStorySubmit();next.setText("Submit");currentNav = NAV_STORY;break;
                    case NAV_STORY:createStory();break;
                    case NAV_RETRY:retryDatabaseInsert(underSubmission);break;
                    case NAV_END:startActivity(new Intent(getApplicationContext(),CertificateActivity.class).putExtra("name",uname).putExtra("story_id",underSubmission.story_id).putExtra("college",college));
                }

            }
        });
    }

    boolean verifyUserInput(){

        fragment =(UserDetailsFragment) adapter.fragmentMap.get(1);

        if(fragment.name.getText().toString().equals("")||fragment.email.getText().toString().equals("")||fragment.phone.getText().toString().length()<10||fragment.college.getText().toString().equals("")){
            return false;
        }
        else {

            uname = fragment.name.getText().toString();
            umail = fragment.email.getText().toString();
            uref = fragment.reference.getText().toString();
            uphone = fragment.phone.getText().toString();
            college = fragment.college.getText().toString();
if(isValidEmail(umail)){
    Log.d("email","valid");
    return true;
}
else{
    return false;
}

        }
    }
    boolean isValidEmail(String email){


        return EmailValidator.getInstance().isValid(email);
    }
    void hidePagerAndShowStorySubmit(){
        setTitle("Story");
        next.setText("Submit");
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
        buildStoryWithContent(story);
        Log.d("Story","Created");

    }


    void submitStory(final Story story) {

        database =  FirebaseDatabase.getInstance();
        database.getReference(client_id_ref).addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        clientID =  (String) dataSnapshot.getValue();


        database.getReference(client_secret_ref).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clientSecret  = (String)dataSnapshot.getValue();

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
            okhttp3.Request request = new okhttp3.Request.Builder().addHeader("content-type","application/x-www-form-urlencoded").url(access_token_url).post(body).build();
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
        Order order = new Order(access_token,story.transaction_id,uname,fragment.email.getText().toString(),uphone,payment_amount,"SOCM");
        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(Order order,final Exception error) {

                if (error != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleException(error);
                        }
                    });

                    return;
                }else {
                    underSubmission = story;
                    startPreCreatedUI(order);
                }
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

        flag_payment = FLAG_PAYMENT.AFTER_PAYMENT;
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

          if (orderID != null && transactionID != null && paymentID != null) {
                underSubmission.payment_id = paymentID;
                underSubmission.order_id = orderID;

                postPaymentJob(underSubmission);

              //  Toast.makeText(getApplicationContext(),"Order ID:"+orderID+":::TRANS:"+transactionID+"::::PAYID :"+paymentID,Toast.LENGTH_LONG).show();


            } else {
handleException(new PaymentCancelledException());
            }
        }
    }

    void postPaymentJob(final Story story){

        Log.d("post_payment","started");

        database.getReference(api_key_ref).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                api_key = (String) dataSnapshot.getValue();

                database.getReference(auth_token_ref).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        auth_token = (String)dataSnapshot.getValue();
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
      okhttp3.Request request = new okhttp3.Request.Builder().url(payment_url+story.payment_id+"/").addHeader("X-Api-Key",api_key).addHeader("X-Auth-Token",auth_token).get().build();
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


    void onPaymentSuccess(final Story story){

        story.transaction_status="paid";
        story.submission_status="submitted";
        final Story localStory  = story;
     OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(TEXT,new JSONObject(localStory.toMap()).toString());
        okhttp3.Request request = new okhttp3.Request.Builder().addHeader("address",story.story_id).addHeader("Content-Type","text/plain").post(body).url("https://socmcompetetion.firebaseapp.com/addToDatabase").build();
client.newCall(request).enqueue(new Callback() {
    @Override
    public void onFailure(Call call,final IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                paymentSuccessButDatabaseError(e,story);
            }
        });

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
if(response.code()==200){
    underSubmission= story;
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            showSuccessFull();
        }
    });

}
else {
    paymentSuccessButDatabaseError(new Exception(),story);
}
    }
});
    }
class PaymentFailException extends Exception{}
class NoInternetException extends Exception{}
class ConnectionTimeoutException extends Exception{}
class PaymentCancelledException extends Exception{}
class PaymentSuccessButInsertionErrorException extends Exception{
    Story story;
    public PaymentSuccessButInsertionErrorException(Story story){
        this.story = story;
    }

}

void showSuccessFull(){
    paymentDialog.cancel();
    setTitle("Stories of Common Man");
    writeStory.setVisibility(View.GONE);
    findViewById(R.id.final_view).setVisibility(View.VISIBLE);
    next.setVisibility(View.VISIBLE);
    currentNav = NAV_END;

    next.setText("Generate Certificate");
}



void paymentSuccessButDatabaseError(Exception error,final Story local){
    FirebaseCrash.report(error);
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            paymentDialog.cancel();

            writeStory.setVisibility(View.GONE);
            findViewById(R.id.final_view).setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            currentNav = NAV_RETRY;
            underSubmission= local;
            next.setText("Generate Certificate");
        }
    });

}

void retryDatabaseInsert(final Story story){
    paymentDialog.show();
    OkHttpClient client = new OkHttpClient();

    RequestBody body = RequestBody.create(TEXT,new JSONObject(story.toMap()).toString());
    okhttp3.Request request = new okhttp3.Request.Builder().addHeader("address",story.story_id).addHeader("Content-Type","text/plain").post(body).url("https://socmcompetetion.firebaseapp.com/addToDatabase").build();
    client.newCall(request).enqueue(new Callback() {

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                underSubmission = story;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    startActivity(new Intent(getApplicationContext(),CertificateActivity.class).putExtra("name",uname).putExtra("story_id",underSubmission.story_id).putExtra("college",college));

                    }
                });
            } else {
                handleException(new PaymentSuccessButInsertionErrorException(story));
            }
        }

        @Override
        public void onFailure(Call call, final IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleException(new PaymentSuccessButInsertionErrorException(story));
                }
            });

        }
    });
}




void handleException(final  Exception e){
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if(e instanceof PaymentSuccessButInsertionErrorException){
                paymentDialog.cancel();

                PaymentSuccessButInsertionErrorException exception = (PaymentSuccessButInsertionErrorException)e;
                writeStory.setVisibility(View.GONE);
                findViewById(R.id.final_view).setVisibility(View.VISIBLE);
                payStatView.setImageResource(R.drawable.ic_sad);
                payStatText.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                payStatText.setText(getResources().getString(R.string.payment_issue)+exception.story.payment_id);
                return;
            }
            if(e instanceof NoInternetException){
                Toast.makeText(getApplicationContext(),"Please Check your Network Connection",Toast.LENGTH_LONG).show();
                switch (flag_payment){
                    case AFTER_PAYMENT:OnPaymentCancelled();

                }
                return;
            }
            if(e instanceof ConnectionTimeoutException){
                Toast.makeText(getApplicationContext(),"Please Check your Network Connection",Toast.LENGTH_LONG).show();
                switch (flag_payment){
                    case AFTER_PAYMENT:OnPaymentCancelled();

                }                return;
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
            if(e instanceof SocketException){
                handleException(new NoInternetException());
                return;
            }

            else {
                e.printStackTrace();
                FirebaseCrash.report(e);
                Toast.makeText(getApplicationContext(),"UnKnown Error Occcurred",Toast.LENGTH_LONG).show();
                switch (flag_payment){
                    case AFTER_PAYMENT:OnPaymentCancelled();

                }
                return;
            }


        }
    });


}

void OnPaymentCancelled(){

paymentDialog.cancel();
payStatView.setImageResource(R.drawable.ic_sad);
    payStatText.setText("Payment Failed..!");
    writeStory.setVisibility(View.GONE);
    next.setVisibility(View.GONE);
    findViewById(R.id.final_view).setVisibility(View.VISIBLE);


    }

  void buildStoryWithContent(Story story){

      story.title = storyTitle.getText().toString();
      underSubmission = story;


     storyContent.setWebChromeClient(new WebChromeClient(){
          @Override
          public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
              Log.d("console_message",consoleMessage.message()+":::"+consoleMessage.lineNumber());
              return super.onConsoleMessage(consoleMessage);
          }
      });



      if(story.title.equals("")){
          prompt.setMessage("Empty title").show();
      }
      else {
          storyContent.loadUrl("javascript:getStory()");
      }
  }





class StoryGrabberInterface{
StoryObtainedListener listener;

    public StoryGrabberInterface(StoryObtainedListener listener){
        this.listener = listener;
    }

     @JavascriptInterface
    public void getStoryContent(String text,String html){
         Log.d("JAVASCRIPT","Working");
         listener.onStoryObtained(new String[]{text,html});

    }


}
    public interface StoryObtainedListener {
        void onStoryObtained(String[] content);
    }


    public int countWords(String s){

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {

            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                          } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;

            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }

    void uploadStory(final Story story){
      OkHttpClient client = new OkHttpClient();

        Log.d("Story","Submitted");
        paymentDialog = new ProgressDialog(StorySubmitActivity.this);
        paymentDialog.setIndeterminate(false);
        paymentDialog.setMessage("Please Wait...");
        paymentDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paymentDialog.setCancelable(false);
        paymentDialog.show();

        RequestBody body = RequestBody.create(JSON,buildPost(story.title,story.content,uname+","+umail,story.story_id));
        okhttp3.Request post = new okhttp3.Request.Builder().addHeader("Authorization", " Basic c3Rvcmllc29mY29tbW9ubWFuOndldHQgY1RpeCBDQkpaIFVrTUkgRGRVNiBINkNG").addHeader("Content-Type","application/json").url("http://"+getResources().getString(R.string.site_ip)+"/wp-json/wp/v2/posts").post(body).build();

        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
if(response.code()== HttpURLConnection.HTTP_CREATED){

    submitStory(story);

}
            else {
    Log.d("Code Error",response.code()+"");
    onFailure(call,new IOException());
            }

            }
        });
    }
  String buildPost(String title,String data,String author,String id){
        JSONObject object = new JSONObject();
        String content  = data +"\n\n\nBy : \t"+author;
        try {
            object.put("author","1").put("title",title+"("+id+")").put("content",content);
        }catch (Exception e){
            e.printStackTrace();
        }
        return object.toString();
    }

}

