package com.emon.exampleXMLtoPDF.demoInvoice;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.emon.exampleXMLtoPDF.R;
import com.emon.exampleXMLtoPDF.dummyList.DummyContent;
import com.emon.exampleXMLtoPDF.dummyList.DummyItemRecyclerViewAdapter;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DemoInvoiceFragment extends Fragment {

    private static final String TAG = "InvoiceFragment";
    // Vistas de la factura
    TextView customer_shop_name_tv, customer_address_tv, customer_phone_tv,
            customer_order_date_tv, our_delivery_date_tv;
    ImageView ivLogo;

    private View finalInvoiceViewToPrint;

    public DemoInvoiceFragment() {
    }


    /**
     * IMPORTANTE: Solo necesitamos imprimir un LinearLayout llamado "invoice_layout", no todo el RelativeLayout.
     * Por lo tanto, necesitamos crear una vista como "finalInvoiceViewToPrint" que se imprimirá, ignorando
     * la parte restante del diseño principal.
     *
     * @param root la vista principal que contiene el diseño de la factura. Necesitamos crear una
     *             vista final de la factura a partir de ella (View root), que se imprimirá, ignorando el resto.
     * @return la vista final de la factura generada a partir de la vista principal (generate_invoice_btn
     * porque no queremos imprimirlo. Solo imprimimos la parte principal de la factura).
     */
    private View createInvoiceViewFromRootView(View root) {

        finalInvoiceViewToPrint = root.findViewById(R.id.invoice_layout);
        RecyclerView invoice_rv = finalInvoiceViewToPrint.findViewById(R.id.invoice_rv);
        customer_shop_name_tv = finalInvoiceViewToPrint.findViewById(R.id.customer_shop_name_tv);
        customer_address_tv = finalInvoiceViewToPrint.findViewById(R.id.customer_address_tv);
        customer_phone_tv = finalInvoiceViewToPrint.findViewById(R.id.customer_phone_tv);
        customer_order_date_tv = finalInvoiceViewToPrint.findViewById(R.id.customer_order_date_tv);
        our_delivery_date_tv = finalInvoiceViewToPrint.findViewById(R.id.our_delivery_date_tv);
        customer_shop_name_tv.setText(Html.fromHtml("<b>Nombre:</b> Demo shop name"));
        customer_address_tv.setText(Html.fromHtml("<b>Address:</b> " + "Demo shop address"));
        customer_phone_tv.setText(Html.fromHtml("<b>Cell No:</b> " + "1234567"));
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        final String orderDate = format.format(1617976800);
        final String deliveryDate = format.format(Calendar.getInstance().getTime());
        customer_order_date_tv.setText(Html.fromHtml("<b>Order Date:</b> " + orderDate));
        our_delivery_date_tv.setText(Html.fromHtml("<b>Delivery Date:</b> " + deliveryDate));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        invoice_rv.setLayoutManager(layoutManager);
        ivLogo=finalInvoiceViewToPrint.findViewById(R.id.iv_logo);
        //logo
        Glide.with(this).load("https://lh6.ggpht.com/9SZhHdv4URtBzRmXpnWxZcYhkgTQurFuuQ8OR7WZ3R7fyTmha77dYkVvcuqMu3DLvMQ=w30").into(ivLogo);
        invoice_rv.setAdapter(new DummyItemRecyclerViewAdapter(DummyContent.ITEMS));

        return finalInvoiceViewToPrint;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_demo_invoice, container, false);
        Button generate_invoice_btn = root.findViewById(R.id.generate_invoice_btn);

        finalInvoiceViewToPrint = createInvoiceViewFromRootView(root);

        generate_invoice_btn.setOnClickListener(v -> {
           generatePdf();
        });
        return root;
    }

    public void generatePdf() {
        PdfGenerator.getBuilder()
                .setContext(requireActivity())
                .fromViewSource()
                .fromView(finalInvoiceViewToPrint)
                /* "fromLayoutXML()" toma una variedad de recursos de diseño.
                 * También puede invocar aquí el método "fromLayoutXMLList()", que toma una lista de recursos de diseño en lugar de una matriz. */
                /* Toma el tamaño de página predeterminado como A4,A5. También puede configurar el tamaño de página personalizado en píxeles
                 * by calling ".setCustomPageSize(int widthInPX, int heightInPX)" here. */
                .setFileName("demo-invoice")
                /* Es el nombre del archivo */
                .setFolderNameOrPath("demo-invoice-folder/")

                 /* Es el nombre de la carpeta. Si configura el nombre de la carpeta como este patrón (FolderA/FolderB/FolderC), then
                 * FolderA creates first.Then FolderB inside FolderB and also FolderC inside the FolderB and finally
                  * el archivo pdf llamado "Test-PDF.pdf" se almacenará dentro de la Carpeta B. */
                .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                /* It true then the generated pdf will be shown after generated. */
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
                        Log.d(TAG, "onFailure: " + failureResponse.getErrorMessage());
                        /* Si el pdf no se genera por un error, entonces descubrirás el motivo.
                         * de esta respuesta de error. */
                        //Toast.makeText(MainActivity.this, "Failure : "+failureResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), "" + failureResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void showLog(String log) {
                        super.showLog(log);
                        Log.d(TAG, "log: " + log);
                        /*Muestra registros de eventos dentro del proceso de generación del pdf*/
                    }

                    @Override
                    public void onStartPDFGeneration() {

                    }

                    @Override
                    public void onFinishPDFGeneration() {

                    }

                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
                        /* Si el PDF se genera correctamente, encontrará SuccessResponse
                         * que contiene el documento Pdf, el archivo y la ruta (donde se almacena el pdf generado)*/
                        //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Success: " + response.getPath());

                    }
                });

    }

}