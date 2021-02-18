package com.technosales.net.buslocationannouncement.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.TicketBusApp;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.pojo.PriceList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class GeneralUtils {
    private static final String[] BT_PRINT_DEVICE = {"A60", "Aries8", "Aries6"};


    public static Bitmap mergeToPin(Bitmap firstImage, Bitmap secondImage) {
            Bitmap result = Bitmap.createBitmap(firstImage.getWidth() + secondImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(firstImage, 0f, 0f, null);
            canvas.drawBitmap(secondImage, firstImage.getWidth(), 0f, null);
            return result;
    }

    public static ApiError convertErrors(ResponseBody response){
        Converter<ResponseBody, ApiError> converter = ServerConfigNew.retrofit().responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError apiError = null;
        try {
            apiError = converter.convert(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiError;
    }

    public static boolean isValidEmailId(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
    public static void setError(EditText editText) {
        editText.setError("Field required");
        editText.requestFocus();
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
            return true;

        return false;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public static boolean isNetworkAvailableCheck() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) TicketBusApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static boolean needBtPrint() {
        for (String s : BT_PRINT_DEVICE) {
            if (s.equals(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }
    public static void showProgressBar(boolean showProgressBar, ProgressBar progressBar) {
        if (showProgressBar) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    public static String ByteArrayToHexString(byte [] inarray) {
        Log.i("nfcread:", "ByteArrayToHexString: ");
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
            out += ':';


        }
        out = out.substring(0,out.length()-1); //remove : from end
        Log.i("nfcread:", out);


        return out;
    }

    public static float calculateDistance(Double startLat, Double startLng, Double endLat, Double endLng) {
        float distance;
        Location startingLocation = new Location("starting point");
        startingLocation.setLatitude(startLat);
        startingLocation.setLongitude(startLng);

        //Get the target location
        Location endingLocation = new Location("ending point");
        endingLocation.setLatitude(endLat);
        endingLocation.setLongitude(endLng);

        distance = startingLocation.distanceTo(endingLocation);


        return distance;
    }
  public static String bcdToStr(byte[] string){
      String newData = null;
      try {
          newData = new String(string, "UTF-8");
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      return newData;
  }

    public static List<PriceList> priceCsv(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.price);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-16"), 100);
            try {
                while ((line = br.readLine()) != null) {

                    String[] priceDistance = line.split("-");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.PRICE_VALUE, priceDistance[0]);
                    contentValues.put(DatabaseHelper.PRICE_MIN_DISTANCE, priceDistance[1]);
                    contentValues.put(DatabaseHelper.PRICE_DISTANCE, priceDistance[2]);
                    databaseHelper.insertPrice(contentValues);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return databaseHelper.priceLists(false);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static String getDate() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        return df.format(c.getTime());
    }

    public static String getFullDate() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-M-dd");
        return df.format(c.getTime());
    }
    public static String getTicketDate() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MMdd");
        return df.format(c.getTime());
    }
    public static String getTicketTime() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HHmmss");
        return df.format(c.getTime());
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }

    public static String getUnicodeNumber(String number) {
        String unicodeChar = "";
        for (int i = 0; i < number.length(); i++) {
            char character = number.charAt(i);
            String valueOfchar = String.valueOf(character);
            if (valueOfchar.equals("1")) {
                valueOfchar = "१";
            } else if (valueOfchar.equals("2")) {
                valueOfchar = "२";
            } else if (valueOfchar.equals("3")) {
                valueOfchar = "३";
            } else if (valueOfchar.equals("4")) {
                valueOfchar = "४";
            } else if (valueOfchar.equals("5")) {
                valueOfchar = "५";
            } else if (valueOfchar.equals("6")) {
                valueOfchar = "६";
            } else if (valueOfchar.equals("7")) {
                valueOfchar = "७";
            } else if (valueOfchar.equals("8")) {
                valueOfchar = "८";
            } else if (valueOfchar.equals("9")) {
                valueOfchar = "९";
            } else if (valueOfchar.equals("0")) {
                valueOfchar = "०";
            }

            unicodeChar = unicodeChar + valueOfchar;

        }

        return unicodeChar;
    }
    public static boolean isScreenOrientationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    public static String getUnicodeReverse(String number) {
        String unicodeChar = "";
        for (int i = 0; i < number.length(); i++) {
            char character = number.charAt(i);
            String valueOfchar = String.valueOf(character);
            if (valueOfchar.equals("१")) {
                valueOfchar = "1";
            } else if (valueOfchar.equals("२")) {
                valueOfchar = "2";
            } else if (valueOfchar.equals("३")) {
                valueOfchar = "3";
            } else if (valueOfchar.equals("४")) {
                valueOfchar = "4";
            } else if (valueOfchar.equals("५")) {
                valueOfchar = "5";
            } else if (valueOfchar.equals("६")) {
                valueOfchar = "6";
            } else if (valueOfchar.equals("७")) {
                valueOfchar = "7";
            } else if (valueOfchar.equals("८")) {
                valueOfchar = "8";
            } else if (valueOfchar.equals("९")) {
                valueOfchar = "9";
            } else if (valueOfchar.equals("०")) {
                valueOfchar = "0";
            }

            unicodeChar = unicodeChar + valueOfchar;

        }

        return unicodeChar;
    }

    public static String getNepaliMonth(String valueOfchar) {
        String nepali_month = "";
        if (valueOfchar.equals("1")) {
            nepali_month = "बैशाख";
        } else if (valueOfchar.equals("2")) {
            nepali_month = "जेठ";
        } else if (valueOfchar.equals("3")) {
            nepali_month = "आषाढ";
        } else if (valueOfchar.equals("4")) {
            nepali_month = "साउन";
        } else if (valueOfchar.equals("5")) {
            nepali_month = "भाद्र";
        } else if (valueOfchar.equals("6")) {
            nepali_month = "आश्विन";
        } else if (valueOfchar.equals("7")) {
            nepali_month = "कार्तिक";
        } else if (valueOfchar.equals("8")) {
            nepali_month = "मंसिर";
        } else if (valueOfchar.equals("9")) {
            nepali_month = "पौष";
        } else if (valueOfchar.equals("10")) {
            nepali_month = "माघ";
        } else if (valueOfchar.equals("11")) {
            nepali_month = "फाल्गुण";
        } else if (valueOfchar.equals("12")) {
            nepali_month = "चैत्र";
        }

        Log.i("getNepaliDate", "" + nepali_month);
        return nepali_month;
    }

    public static void createTicketFolder() {
        File ticketFolder = new File(Environment.getExternalStorageDirectory() + "/TicketData");
        if (!ticketFolder.isDirectory())
            ticketFolder.mkdirs();
    }

    public static void writeInTxt(File txtFile, String data) {
        try {
            FileOutputStream fOut = new FileOutputStream(txtFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public static long getDelayTime(int charLength) {
        return charLength / 12 * 1000 + 3000;
    }

    public static enum EPaddingPosition {
        /**
         * padding left
         */
        PADDING_LEFT,
        /**
         * padding right
         */
        PADDING_RIGHT
    }
    public static byte[] strToBcd(String str, EPaddingPosition paddingPosition) throws IllegalArgumentException {
        if (str == null || paddingPosition == null) {
             throw new IllegalArgumentException("strToBcd input arg is null");
        }
        int len = str.length();
        int mod = len % 2;
        if (mod != 0) {
            if (paddingPosition == EPaddingPosition.PADDING_RIGHT) {
                str = str + "0";
            } else {
                str = "0" + str;
            }
            len = str.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = str.getBytes();
        int j, k;
        for (int p = 0; p < str.length() / 2; p++) {
            if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else if ((abt[2 * p] >= 'A') && (abt[2 * p] <= 'Z')) {
                j = abt[2 * p] - 'A' + 0x0a;
            } else {
                j = abt[2 * p] - '0';
            }

            if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else if ((abt[2 * p + 1] >= 'A') && (abt[2 * p + 1] <= 'Z')) {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            } else {
                k = abt[2 * p + 1] - '0';
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

}
