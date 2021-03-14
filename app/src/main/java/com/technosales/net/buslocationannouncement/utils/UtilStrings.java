package com.technosales.net.buslocationannouncement.utils;

public class UtilStrings {

    public static final String SHARED_PREFERENCES = "shared_prefs";
    public static final String SHARED_PREFERENCES_HELPER = "shared_prefs_helper";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String LATITUDETEST = "latitudeTest";
    public static final String LONGITUDETEST = "longitudeTest";
    public static final String CURRENT_ORDER = "current_order";
    public static final String DEVICE_ID = "device_id";
    public static final String ROUTE_ID = "route_id";
    public static final String TOTAL_TICKETS = "total_tickets";
    public static final String TOTAL_COLLECTIONS = "total_collections";
    public static final String TOTAL_COLLECTIONS_CARD = "total_collections_card";
    public static final String TOTAL_COLLECTIONS_BY_CARD = "total_collections_by_card";
    public static final String TOTAL_COLLECTIONS_BY_CASH = "total_collections_by_cash";
    public static final String TOTAL_COLLECTIONS_BY_QR = "total_collections_by_qr";
    public static final String DATE_TIME = "date_time";
    public static final String DATA_SENDING = "data_sent";
    public static final String LAST_DATA_ID = "last_data_id";
    public static final String ROUTE_NAME = "route_name";
    public static final String DEVICE_NAME = "device_name";
    public static final String NAME_HELPER = "helper_name";
    public static final String AMOUNT_HELPER = "helper_balance";
    public static final String CONTACT_HELPER = "helper_contact";
    public static final String EMAIL_HELPER = "helper_email";
    public static final String ID_HELPER = "helper_id";
    public static final String SENT_TICKET = "sent_ticket";
    public static final String FORWARD = "rev_for"; /// save if bus is going along with order or returning
    public static final String ROUTE_TYPE = "route_type";
    public static final String MODE = "mode";
    public static final String ROUTE_LIST_SIZE = "list_size";
    public static final String CURRENT_ID = "current_id";



    //            public static final String MAIN_URL = "http://172.16.1.131:85/route_api/public/api/";//server
//    public static final String MAIN_URL = "http://202.52.240.149:85/route_api/public/api/";//test---8170613861
    public static final String MAIN_URL = "http://202.52.240.149:82/route_api_v2/public/api/";//production
    public static final String REGISTER_URL = MAIN_URL + "routeDevice";
    public static final String ROUTE_STATION = MAIN_URL + "getRouteStation";


    //            public static final String TICKET_URL = "http://172.16.1.131:85/routemanagement/api/";/// server

    public static final String TICKET_URL = "http://117.121.237.226:83/routemanagement/api/";/// production
    public static final String TICKET_URL_NEW = "http://202.52.240.148:8092/new_rm/api/";/// production

    //    public static final String TICKET_URL = "http://202.52.240.149:85/routemanagement/api/";////testServer


    public static final String TICKET_PRICE_LIST = "http://117.121.237.226:83/routemanagement/api/get_fare_by_device/";

    //        public static final String TICKET_PRICE_LIST = "http://172.16.1.131:85/routemanagement/api/get_fare";

    public static final String UPDATE_TICKET = TICKET_URL + "update_device_info";
    public static final String RESET_DEVICE = TICKET_URL + "reset_device";
    public static final String TICKET_POST = TICKET_URL + "store_ticket";
    public static final String TICKET_REGISTER_DEVICE = TICKET_URL + "register";
    //    public static final String ADVERTISEMENTS_URL = "http://172.16.1.131:85/routemanagement/api/adv/getAdvertisement/";
    public static final String ADVERTISEMENTS_URL = TICKET_URL + "adv/getAdvertisement/";


    public static final String SECRET_KEY = "L+poNDg7gEEwUVOfHPAmYlgmmHilt9GgpAEF44Dgb64=";


    //for passengers
    public static final String PASSENGER_REGISTER = TICKET_URL + "passenger/register";
    public static final String PASSENGER_NAME = "passenger_name";
    public static final String PASSENGER_OLD_BLNC = "passenger_old_blnc";
    public static final String PASSENGER_STATUS = "passenger_status";

    public static final String PASSENGER_CHECK_BALANCE = "passenger-check-balance";

    public static final String FARE = TICKET_URL + "fare"; //pay fare
    public static final String RECHARGE = TICKET_URL + "recharge";
    public static final int DEFAULT_ISSUE_AMOUNT = 100;


    public static final int RING_ROAD = 0;/// for ring road
    public static final int NON_RING_ROAD = 1;/// for non ring road

    public static final int MODE_1 = 1;//// normal mode---> starting
    public static final int MODE_2 = 2;//// location suggestion wrt prices
    public static final int MODE_3 = 3;//// price calculations with route


    //Payment modes
    public static final String PAYMENT_CARD = "CARD";
    public static final String PAYMENT_CASH = "CASH";
    public static final String PAYMENT_QR = "QR";


    public static final String TRANSACTION_TYPE_PAYMENT = "PAYMENT";
    public static final String TRANSACTION_TYPE_LOAD = "LOAD";
    public static final String STATUS = "S";

    public static final int TYPE_ADV = 0;
    public static final int TYPE_NOTICE = 1;

    public static final String FIRST_RUN = "first_run";

    public static final int PRINTING_TEXT_SIZE = 30;
    public static final String PRICE_VALUE = "price_value";
    public static final String TICKET_TYPE = "ticket_type";
    public static final String DISCOUNT_TYPE = "discountType";
    public static final String RECHARGE_ITR = "recharge";
    public static final String STATION_NAME = "station_name";
    public static final String TOTAL_DISTANCE = "total_distance";
    public static final String TOGETOFF = "toGetOff";
    public static final String NEAREST_PLACE = "nearest_name";
    public static final String network = "कृपया तपाईंको वाइफाइ जडान जाँच गर्नुहोस्";
    public static final String USERID = "user_id";


    //    new API Calls date 10/2/2020
    public static final String NEW_BASE_URL = "http://202.52.240.146:96/api/v1.0/";
    public static final String NEW_PASSENGER_REGISTER = "registration/register-card";
    public static final String NEW_ACCOUNT_TYPE = "utility/account-type";
    public static final String NEW_TRANSACTION = "transaction/transaction";
    public static final String UPDATE_DEVICE_INFO = "registration/update-device-info";
    public static final String CARD_BLOCK = "registration/block-card";
    public static final String GET_CARD_BLOCK = "block-card";
    public static final String CARD_REISSUE = "registration/reissue-card";
    public static final String TOKEN = "auth/token";

    public static final String TRANSATION_STATEMENT = "transaction/transaction-statement";
    public static final String UPDATE_PASSENGER_COUNT = "update-passenger-number";


    public static final String SOURCE = "source";

    public static final String PLACE = "PLACE";
    public static final String PRICE = "PRICE";
    public static final String POSITION = "position";

    public static final int CUSTOMERID = 12;
    public static final int CUSTOMER_AMT =13;
    public static final int CUSTOMER_HASH = 14;
    public static final int CUSTOMER_TRANSACTION_NO =28;


    //    first transaction
    public static final int FIRST_TRANSACTION_ID = 24;
    public static final int FIRST_TRANSACTION_AMT = 25;
    public static final int FIRST_TRANSACTION_HASH = 26;

    //    second transaction
    public static final int SECOND_TRANSACTION_ID = 36;
    public static final int SECOND_TRANSACTION_AMT = 37;
    public static final int SECOND_TRANSACTION_HASH = 38;


    public static final int SECTOR_TRAILER_CUSTOMER_DETAILS =15;
    public static final int SECTOR_TRAILER_CUSTOMER_FIRST_TRANSACTION =27;
    public static final int SECTOR_TRANSACTION_NO = 31;
    public static final int SECTOR_TRAILER_CUSTOMER_SECOND_TRANSACTION =21;



    public static final int SECTOR_CUSTOMER=3;
    public static final int SECTOR_TRANSATION =7;
    public static final int SECTOR_FIRST_TRANSATION = 6;
    public static final int SECTOR_SECOND_TRANSATION =9;

    public static final byte[] KEY_DEFAULT =
            {(byte) 0xFF, (byte) 0xDB, (byte) 0x59, (byte) 0xCF, (byte) 0xF2, (byte) 0xF3,
                    (byte) 0x08, (byte) 0x77, (byte) 0x8F, (byte) 0xFF,
                    (byte) 0x2C, (byte) 0x1F, (byte) 0xEF, (byte) 0x93, (byte) 0xAA, (byte) 0x13};

    public static final byte[] KEY_A = {(byte) 0xFF, (byte) 0xDB, (byte) 0x59, (byte) 0xCF, (byte) 0xF2, (byte) 0xF3};
    public static final byte[] ACCESS_BITS = {(byte) 0x08, (byte) 0x77, (byte) 0x8F, (byte) 0xFF};
    public static final byte[] KEY_B = {(byte) 0x2C, (byte) 0x1F, (byte) 0xEF, (byte) 0x93, (byte) 0xAA, (byte) 0x13};

    public static final long CONNECTION_TIME_OUT = 500;
    public static final String REFRESH_TOKEN ="helper_login/refresh_token" ;

    public static final String NULL = "";

    public static final String USER_NUMBER ="userNum";
    public static final String TEST ="TEST";

    public static final String CALL_REGISTER_CHECK = "http://202.52.240.149:82/";
    public static final String LOCATION_CHANGE = "location_change";


    public static final String TOTAL_PASSENGERS = "total_passengers";
}