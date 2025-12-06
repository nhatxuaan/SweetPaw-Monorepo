package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

public class DataOrder {
        @SerializedName("orderId")
        private String orderId;

        @SerializedName("order_code")
        private String orderCode;

        @SerializedName("sort_code")
        private String sortCode;

        @SerializedName("trans_type")
        private String transType;

        @SerializedName("expected_delivery_time")
        private String expectedDeliveryTime;

        @SerializedName("fee")
        private Fee fee;

        @SerializedName("total_fee")
        private int totalFee;

        @SerializedName("total_price")
        private int totalPrice;

        public String getOrderId() {
            return orderId;
        }

        public String getOrderCode() {
            return orderCode;
        }

        public String getSortCode() {
            return sortCode;
        }

        public String getTransType() {
            return transType;
        }

        public String getExpectedDeliveryTime() {
            return expectedDeliveryTime;
        }

        public Fee getFee() {
            return fee;
        }

        public int getTotalFee() {
            return totalFee;
        }

        public int getTotalPrice() {
            return totalPrice;
        }

    public static class Fee {
        @SerializedName("main_service")
        private int mainService;

        @SerializedName("insurance")
        private int insurance;

        @SerializedName("cod_fee")
        private int codFee;

        @SerializedName("station_do")
        private int stationDo;

        @SerializedName("station_pu")
        private int stationPu;

        @SerializedName("return")
        private int returnFee;

        @SerializedName("r2s")
        private int r2s;

        @SerializedName("return_again")
        private int returnAgain;

        @SerializedName("coupon")
        private int coupon;

        @SerializedName("document_return")
        private int documentReturn;

        @SerializedName("double_check")
        private int doubleCheck;

        @SerializedName("double_check_deliver")
        private int doubleCheckDeliver;

        @SerializedName("pick_remote_areas_fee")
        private int pickRemoteAreasFee;

        @SerializedName("deliver_remote_areas_fee")
        private int deliverRemoteAreasFee;

        @SerializedName("pick_remote_areas_fee_return")
        private int pickRemoteAreasFeeReturn;

        @SerializedName("deliver_remote_areas_fee_return")
        private int deliverRemoteAreasFeeReturn;

        @SerializedName("cod_failed_fee")
        private int codFailedFee;

        public int getMainService() {
            return mainService;
        }
    }
}
