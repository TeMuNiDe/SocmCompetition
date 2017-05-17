package com.vitech.socmcompetition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import static android.content.Intent.ACTION_VIEW;

public class CertificateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        final String name = getIntent().getStringExtra("name");

        String html   = "<html><p>This is to Certify that <b><i>"+name+"</i></b> has participated in the Competition  <b>Most Inspiring Story</b></p></html>";

        WebView certView = (WebView)findViewById(R.id.cert_view);
        certView.loadData(html,"text/html","UTF-8");

findViewById(R.id.cert_save).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(final View v) {
      final File imag =  certify(name);

        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imag.getAbsolutePath()}, new String[]{"application/pdf"} , new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Intent view = new Intent(ACTION_VIEW);

                view.setDataAndType(Uri.fromFile(imag),"application/pdf");
                startActivity(Intent.createChooser(view,"View With..."));
            }
        });

    }
});
    }


public  File certify(String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,getPackageName())!= PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }


        String html   = "<html><p>This is to Certify that <b><i>"+name+"</i></b> has participated in the Competition  <b>Most Inspiring Story</b></p></html>";

        File docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    File certificate = new File(docs.getPath() + "/Certificate_SOCM.pdf");
        if(!docs.exists()){
            docs.mkdirs();
        }

        try {


            if(!certificate.exists()){
                certificate.createNewFile();
            }

            OutputStream certStream = new FileOutputStream(certificate);

            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc,certStream);
            doc.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer,doc,new StringReader(html));

            writer.flush();
            doc.close();
            certStream.close();


        }catch (Exception e){
            e.printStackTrace();
        }


return certificate;
    }
}
