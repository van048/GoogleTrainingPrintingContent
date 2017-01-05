package cn.ben.googletrainingprintingcontent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class PrintHtmlActivity extends AppCompatActivity {
    private static final String TAG = PrintHtmlActivity.class.getSimpleName();
    private static final String PRINT_DOCUMENT_NAME = "benyang";
    // TODO: 2017/1/5  
    private WebView mWebView;
    private ArrayList<PrintJob> mPrintJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_html);

        mPrintJobs = new ArrayList<>();
    }

    private void doWebViewPrint() {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            // True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "page finished loading " + url);
                createWebPrintJob(view);
                mWebView = null;
            }
        });

        /*
        baseUrl the URL to use as the page's base URL. If null defaults to 'about:blank'. todo
        data a String of data in the given encoding
        mimeType the MIMEType of the data, e.g. 'text/html'. If null, defaults to 'text/html'.
        encoding the encoding of the data
        historyUrl the URL to use as the history entry. If null defaults to 'about:blank'. If non-null, this must be a valid URL. todo
         */
        // Generate an HTML document on the fly:
//        String htmlDocument = "<html><body><h1>Test Content</h1><p>Testing, " +
//                "testing, testing...</p></body></html>";
//        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        /*
        If you want to include graphics in the page, place the graphic files in
        the assets/ directory of your project and specify a base URL in the first
        parameter of the loadDataWithBaseURL() method, as shown in the following code example:
         */
        String htmlBody = "<html><head><title>TITLE!!!</title></head>";
        htmlBody += "<body><h1>Image?</h1><img src=\"icon.png\" /></body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/images/", htmlBody,
                "text/HTML", "UTF-8", null);

        /*
        You can also load a web page for printing by replacing the loadDataWithBaseURL() method with loadUrl() as shown below.
         */
        // Print an existing web page (remember to request INTERNET permission!):
//        webView.loadUrl("http://developer.android.com/about/index.html");

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(PRINT_DOCUMENT_NAME);

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        // Save the job object for later status checking
        mPrintJobs.add(printJob);
    }

    @SuppressWarnings("UnusedParameters")
    public void printHtmlDocs(View view) {
        doWebViewPrint();
    }

    @SuppressWarnings("UnusedParameters")
    public void printCustom(View view) {
        startActivity(new Intent(this, PrintCustomActivity.class));
    }
}
