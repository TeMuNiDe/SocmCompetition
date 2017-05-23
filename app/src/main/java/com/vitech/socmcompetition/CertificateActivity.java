package com.vitech.socmcompetition;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.itextpdf.text.Document;

import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;

import static android.content.Intent.ACTION_VIEW;


public class CertificateActivity extends AppCompatActivity {
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String story_id = getIntent().getStringExtra("story_id");
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please Wait...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        try {
            DatabaseReference reference = database.getReference("submissions").child(story_id).child("transaction_status");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        if (dataSnapshot.getValue().equals("paid")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    generateCertificate();
                                }
                            });

                        }
                        else {
                            displayErrorMessage();
                        }
                    }catch (Exception e){
                        handleException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
handleException(databaseError.toException());
                }
            });
        }catch (Exception e){
handleException(e);
        }
    }

 void    handleException(Exception e){
     dialog.cancel();
     if(e instanceof UnknownHostException){
         Toast.makeText(getApplicationContext(),"Please Check your Network Connection",Toast.LENGTH_LONG).show();

         return;
     }
     if(e instanceof SocketTimeoutException){
         Toast.makeText(getApplicationContext(),"Please Check your Network Connection",Toast.LENGTH_LONG).show();

         return;
     }
     if(e instanceof SocketException){
         Toast.makeText(getApplicationContext(),"Please Check your Network Connection",Toast.LENGTH_LONG).show();

         return;
     }
     else {
         Toast.makeText(getApplicationContext(),"UnKnown Error Occcurred",Toast.LENGTH_LONG).show();
         displayErrorMessage();
         FirebaseCrash.report(e);
         return;
     }

 }


 void displayErrorMessage(){
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        WebView certView = (WebView)findViewById(R.id.cert_view);
        certView.loadUrl("file:///android_asset/error.html");
    }
});

 }

public void generateCertificate() {



    final String name = getIntent().getStringExtra("name");
   final String collegeName = getIntent().getStringExtra("college");
try {
    final String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());



    InputStream stream = getAssets().open("certificate_preview.html");
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
    StringBuilder preview_string =  new StringBuilder();
    String buffer;
    while ((buffer=reader.readLine())!=null){
        preview_string.append(buffer);
    }
    String fromFile  = preview_string.toString();

   String html = fromFile.replace("%NAME%",name).replace("%COLLEGE%",collegeName).replace("%DATE%",date);

    WebView certView = (WebView) findViewById(R.id.cert_view);
    certView.getSettings().setUseWideViewPort(true);
     certView.getSettings().setLoadWithOverviewMode(true);

   certView.setPadding(0, 0, 0, 0);
   certView.setInitialScale(getScale());

    certView.setWebChromeClient(new WebChromeClient(){

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d("message",consoleMessage.message());

            return super.onConsoleMessage(consoleMessage);

        }
    });
    certView.loadDataWithBaseURL("file:///android_asset/certificate_preview.html",html,"text/html","UTF-8",null);



    findViewById(R.id.cert_save).setVisibility(View.VISIBLE);
    dialog.dismiss();
    findViewById(R.id.cert_save).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            v.setVisibility(View.GONE);
            Log.d("Dialog", "Shown");

            findViewById(R.id.cert_load).setVisibility(View.VISIBLE);
            final File imag = certify(name, collegeName,date);

            if(imag!=null) {
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imag.getAbsolutePath()}, new String[]{"application/pdf"}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Intent view = new Intent(ACTION_VIEW);
                        callBack(true);
                        view.setDataAndType(Uri.fromFile(imag), "application/pdf");
                        startActivity(Intent.createChooser(view, "View With..."));
                    }
                });
            }else {
                callBack(false);
            }

        }
    });

}catch (Exception e){
    FirebaseCrash.report(e);
    e.printStackTrace();
}
}


protected   File certify(String name,String collegeName,String date){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return null;
            }
        }

            File docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File certificate = new File(docs.getPath() + "/Certificate_SOCM.pdf");
            if (!docs.exists()) {
                docs.mkdirs();
            }
            try {
                if (!certificate.exists()) {
                    certificate.createNewFile();
                }
                OutputStream certStream = new FileOutputStream(certificate);
                Document doc = new Document(PageSize.A4);
                PdfWriter writer = PdfWriter.getInstance(doc, certStream);
                doc.open();
                InputStream ims = getAssets().open("certificate_template.jpg");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                PdfContentByte canvas = writer.getDirectContentUnder();
                image.scaleAbsolute(PageSize.A4);
                image.setAbsolutePosition(0, 0);
                canvas.addImage(image);


                BaseFont font = BaseFont.createFont("assets/fonts/arial.ttf", BaseFont.WINANSI, false);

                PdfContentByte nameWriter = writer.getDirectContent();
                nameWriter.saveState();
                nameWriter.beginText();
                nameWriter.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
                nameWriter.setLineWidth(1.5f);
                nameWriter.setFontAndSize(font, 24.0f);
                nameWriter.setTextMatrix(150, 427);
                nameWriter.showText(name);
                nameWriter.endText();
                nameWriter.restoreState();


                PdfContentByte collegeWriter = writer.getDirectContent();
                collegeWriter.saveState();
                collegeWriter.beginText();
                collegeWriter.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
                collegeWriter.setLineWidth(1.5f);
                collegeWriter.setFontAndSize(font, 24.0f);
                collegeWriter.setTextMatrix(50, 397);
                collegeWriter.showText(collegeName);
                collegeWriter.endText();
                collegeWriter.restoreState();


                PdfContentByte dateWriter = writer.getDirectContent();
                dateWriter.saveState();
                dateWriter.beginText();
                dateWriter.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
                dateWriter.setLineWidth(1.5f);
                dateWriter.setFontAndSize(font, 24.0f);
                dateWriter.setTextMatrix(400, 287);
                dateWriter.showText(date);
                dateWriter.endText();
                dateWriter.restoreState();


                doc.close();
                certStream.close();


            } catch (Exception e) {
                e.printStackTrace();
            }


            return certificate;

    }

    @Override
    public void onBackPressed() {
        finish();

    }
    void callBack(final boolean stat){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(stat) {
                    Toast.makeText(getApplicationContext(), "Saved in Documents", Toast.LENGTH_LONG).show();
                }
                findViewById(R.id.cert_load).setVisibility(View.GONE);
                findViewById(R.id.cert_save).setVisibility(View.VISIBLE);
            }
        });

    }


    private int getScale(){
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width)/new Double(2480);
        val = val * 100d;
        return val.intValue();
    }
}
