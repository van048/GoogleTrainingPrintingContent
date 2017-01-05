package cn.ben.googletrainingprintingcontent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;

public class PrintCustomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_custom);
    }

    private void doPrint() {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = getString(R.string.app_name) + " Document";

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new MyPrintDocumentAdapter(this),
                null); //
    }

    public void startWork(@SuppressWarnings("UnusedParameters") View view) {
        doPrint();
    }

    private class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        private PrintedPdfDocument mPdfDocument;
        private final Context mContext;
        private int totalPages;
        private final SparseIntArray writtenPagesArray;

        public MyPrintDocumentAdapter(Context context) {
            mContext = context;
            writtenPagesArray = new SparseIntArray();
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

            // Create a new PdfDocument with the requested page attributes
            mPdfDocument = new PrintedPdfDocument(mContext, newAttributes);

            // Respond to cancellation request
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            // Compute the expected number of printed pages
            totalPages = computePageCount(newAttributes);

            if (totalPages > 0) {
                // Return print information to print framework
                PrintDocumentInfo info = new PrintDocumentInfo
                        // TODO: 2017/1/5 The document name which may be shown to the user and is the file name if the content it describes is saved as a PDF. Cannot be empty.
                        .Builder("print_output.pdf")
                        // A print service may use normal paper to print the content instead of dedicated photo paper.
                        // Also it may use a lower quality printing process as the content is not as sensitive to print quality variation as a photo is.
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalPages)
                        .build();
                // Content layout reflow is complete
                callback.onLayoutFinished(info, true);
            } else {
                // Otherwise report an error to the print framework
                callback.onLayoutFailed("Page count calculation failed.");
            }
        }

        // TODO: 2017/1/5 调用时机
        @Override
        public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            // Iterate over each page of the document,
            // check if it's in the output range.
            for (int i = 0; i < totalPages; i++) {
                // Check to see if this page is in the output range.
                if (containsPage(pageRanges, i)) {
                    // If so, add it to writtenPagesArray. writtenPagesArray.size()
                    // is used to compute the next output page index.
                    // TODO: 2017/1/5
                    writtenPagesArray.append(writtenPagesArray.size(), i);
                    PdfDocument.Page page = mPdfDocument.startPage(i);

                    // check for cancellation
                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        mPdfDocument.close();
                        mPdfDocument = null;
                        return;
                    }

                    // Draw page content for printing
                    drawPage(page, i + 1);

                    // Rendering is complete, so page can be finalized.
                    mPdfDocument.finishPage(page);
                }
            }

            // Write PDF document to file
            try {
                mPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                mPdfDocument.close();
                mPdfDocument = null;
            }
            // TODO: 2017/1/5
//            PageRange[] writtenPages = computeWrittenPages();
            PageRange[] writtenPages = pageRanges.clone();
            // Signal the print framework the document is complete
            callback.onWriteFinished(writtenPages);
        }

        // TODO: 2017/1/5
        private PageRange[] computeWrittenPages() {
            return new PageRange[0];
        }

        private boolean containsPage(PageRange[] pageRanges, int i) {
            for (PageRange pageRange : pageRanges) {
                if (i >= pageRange.getStart() && i <= pageRange.getEnd()) return true;
            }
            return false;
        }

        private int computePageCount(PrintAttributes printAttributes) {
            int itemsPerPage = 4; // default item count for portrait mode

            PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
            if (pageSize != null && !pageSize.isPortrait()) {
                // Six items per page in landscape orientation
                itemsPerPage = 6;
            }

            // Determine number of print items
            int printItemCount = getPrintItemCount();

            return (int) Math.ceil((double) printItemCount / itemsPerPage);
        }

        private int getPrintItemCount() {
            // TODO: 2017/1/5
            return 16;
        }

        private void drawPage(PdfDocument.Page page, int index) {
            Canvas canvas = page.getCanvas();

            // units are in points (1/72 of an inch)
            int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(36);
            canvas.drawText("Test Title " + index, leftMargin, titleBaseLine, paint);

            paint.setTextSize(11);
            canvas.drawText("Test paragraph", leftMargin, titleBaseLine + 25, paint);

            paint.setColor(Color.BLUE);
            canvas.drawRect(100, 100, 172, 172, paint);
        }
    }
}
