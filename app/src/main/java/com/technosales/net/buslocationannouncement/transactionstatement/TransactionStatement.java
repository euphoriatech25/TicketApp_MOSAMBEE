package com.technosales.net.buslocationannouncement.transactionstatement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.technosales.net.buslocationannouncement.paxsupport.printer.Device;
import com.technosales.net.buslocationannouncement.paxsupport.printer.ReceiptPrintParam;
import com.technosales.net.buslocationannouncement.printlib.Printer;
import com.technosales.net.buslocationannouncement.printlib.RxUtils;
import com.technosales.net.buslocationannouncement.PrintListenerImpl;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionStatement extends BaseActivity implements ITransactionStatement.View {
    TextView fromDate, toDate;
    EditText userMobNo;
    LinearLayout getUserInfo, getUserStatement,statementRequestLayout, userStatementList;
    Button transactionStatement,printTransaction;
    TranStatementPresenterImpl tranStatementPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statement);
        setUpToolbar("लेनदेन बयान", true);

        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        userMobNo = findViewById(R.id.userMob);
        getUserInfo = findViewById(R.id.getUserInfo);
        transactionStatement = findViewById(R.id.transactionStatement);
        printTransaction = findViewById(R.id.printTransaction);
        statementRequestLayout = findViewById(R.id.statementRequestLayout);
        userStatementList = findViewById(R.id.userStatementList);
        getUserStatement = findViewById(R.id.getUserStatement);
        tranStatementPresenter = new TranStatementPresenterImpl(this, new TranStatementControllerImpl());

        transactionStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tranStatementPresenter.requestTransaction();
                GeneralUtils.hideKeyboard(TransactionStatement.this);
            }
        });
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionStatement.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        int month = monthOfYear + 1;
                        fromDate.setText(year + "/" + month + "/" + dayOfMonth);
                        Log.e("TAG", "onDateSet: " + newDate);
                        System.out.print(dateFormatter.format(newDate.getTime()));
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionStatement.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        int month = monthOfYear + 1;
                        toDate.setText(year + "/" + month + "/" + dayOfMonth);

                        Log.e("TAG", "onDateSet: " + newDate);
                        System.out.print(dateFormatter.format(newDate.getTime()));
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

    }


    @Override
    public String getMobileNo() {
        return userMobNo.getText().toString();
    }

    @Override
    public String getFromDate() {
        String fromDateTv = fromDate.getText().toString();
        if (fromDateTv != null && !fromDateTv.equalsIgnoreCase("")) {
            return fromDateTv;
        } else {
            Toast.makeText(this, "Please add From Date", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public String getToDate() {
        String toDateTv = toDate.getText().toString();
        if (toDateTv != null && !toDateTv.equalsIgnoreCase("")) {
            return toDateTv;
        } else {
            Toast.makeText(this, "Please add To Date", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void setMobileError() {
        GeneralUtils.setError(userMobNo);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TransactionStatement.this, TicketAndTracking.class));
    }

    @Override
    public void onSuccessSt(TransactionStatementModel transactionModel) {
        String valueFinal = "Customer Transaction Statement \n\n";
        List<String>value=new ArrayList<>();
        List<TransactionStatementModel.Datum> result=transactionModel.getData();
      if(transactionModel.getData().size()>0) {
          getUserInfo.setVisibility(View.GONE);
          getUserStatement.setVisibility(View.VISIBLE);

          for (int i = 0; i < transactionModel.getData().size(); i++) {
              View itemView = LayoutInflater.from(TransactionStatement.this).inflate(R.layout.transaction_statement_list, userStatementList, false);
              TextView ticketId, transactionMediumTv, transactionAmt, transactionDate;
              ticketId = itemView.findViewById(R.id.ticketId);
              transactionAmt = itemView.findViewById(R.id.transactionAmt);
              transactionMediumTv = itemView.findViewById(R.id.transactionMedium);
              transactionDate = itemView.findViewById(R.id.transactionDate);
              ticketId.setText("Ticket Id :-" + String.valueOf(result.get(i).getTicketId()));
              transactionAmt.setText("Ticket Amount :-" + result.get(i).getTransactionAmount());
              transactionMediumTv.setText("Payment Medium :-" + result.get(i).getTransactionMedium());
              transactionDate.setText("Payment Date :-" + result.get(i).getDeviceTime());
              String printString = ("Ticket Id :-"+result.get(i).getId()+"\n" + "Ticket Amount :-"+result.get(i).getTransactionAmount() +"\n" + "Payment Medium  :-"+ result.get(i).getTransactionMedium() +"\n" + "Payment Date :-"+result.get(i).getDeviceTime());
              value.add(printString);
              userStatementList.addView(itemView);
          }

          for (int i = 0; i < 5; i++) {
              valueFinal = valueFinal + "\n\n" + value.get(i);
          }

          String finalValueFinal = valueFinal;
          printTransaction.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if(finalValueFinal.length()>0){
                      paraPrint(finalValueFinal);
                  }
              }
          });
      }else {
          Toast.makeText(this, "यो कार्डको शून्य लेनदेन छ।", Toast.LENGTH_SHORT).show();
      }
    }


    @Override
    public void onFailure(String message) {

    }

    @Override
    public void noInternetConnection() {

    }

    @Override
    public void connectionTimeOut() {

    }

    @Override
    public void showProgressBar(boolean showpBar) {

    }

    @Override
    public void unKnownError() {

    }

    @Override
    public void setFromDateError() {
        Toast.makeText(this, "Please choose From date", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setToDateError() {
        Toast.makeText(this, "Please choose To date", Toast.LENGTH_SHORT).show();
    }
    public void paraPrint(String printData) {
        RxUtils.runInBackgroud(new Runnable() {
            @Override
            public void run() {
                ReceiptPrintParam receiptPrintParam = new ReceiptPrintParam();
                String printType = "error";
                if (GeneralUtils.needBtPrint()) {
                    Printer.printA60Receipt("", "", printType);
                } else {
                    receiptPrintParam.print(printData, new PrintListenerImpl(TransactionStatement.this));
                    Device.beepOk();
                }
            }
        });
    }
}
