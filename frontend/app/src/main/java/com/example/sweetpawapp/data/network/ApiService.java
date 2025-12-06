package com.example.sweetpawapp.data.network;

import com.example.sweetpawapp.data.model.notification.NotificationByDate;
import com.example.sweetpawapp.data.model.notification.NotificationMarkReadResponse;
import com.example.sweetpawapp.data.model.notification.NotificationResponse;
import com.example.sweetpawapp.data.model.order.OrderDetailResponse;
import com.example.sweetpawapp.data.model.order.Orders;
import com.example.sweetpawapp.data.model.order.OrdersListResponse;
import com.example.sweetpawapp.data.model.order.PaymentResponse;
import com.example.sweetpawapp.data.model.order.PaymentStatusResponse;
import com.example.sweetpawapp.data.model.product.FavoriteResponse;
import com.example.sweetpawapp.data.model.product.FilterProductRequest;
import com.example.sweetpawapp.data.model.product.FilterProductResponse;
import com.example.sweetpawapp.data.model.cart.UpdateCartQuantityRequest;
import com.example.sweetpawapp.data.model.cart.UpdateCartQuantityResponse;
import com.example.sweetpawapp.data.model.product.GetProductDetailResponse;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteRequest;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteResponse;
import com.example.sweetpawapp.data.model.review.AddReviewRequest;
import com.example.sweetpawapp.data.model.review.AddReviewResponse;
import com.example.sweetpawapp.data.model.review.DeleteRatingRequest;
import com.example.sweetpawapp.data.model.review.ReviewOrderListResponse;
import com.example.sweetpawapp.data.model.review.UpdateReviewRequest;
import com.example.sweetpawapp.data.model.user.AddAddressResponse;
import com.example.sweetpawapp.data.model.user.DeleteAddressRequest;
import com.example.sweetpawapp.data.model.user.SetDefaultAddressRequest;
import com.example.sweetpawapp.data.model.user.UpdateAddressRequest;
import com.example.sweetpawapp.data.model.user.UpdateAddressResponse;
import com.example.sweetpawapp.data.model.user.UpdateProfileRequest;
import com.example.sweetpawapp.data.model.user.User;
import com.example.sweetpawapp.data.model.auth.GoogleLoginRequest;
import com.example.sweetpawapp.data.model.auth.GoogleLoginResponse;
import com.example.sweetpawapp.data.model.auth.GoogleRegisterRequest;
import com.example.sweetpawapp.data.model.auth.GoogleRegisterResponse;
import com.example.sweetpawapp.data.model.auth.LoginRequest;
import com.example.sweetpawapp.data.model.auth.LoginResponse;
import com.example.sweetpawapp.data.model.auth.RegisterRequest;
import com.example.sweetpawapp.data.model.auth.RegisterResponse;
import com.example.sweetpawapp.data.model.cart.AddToCartRequest;
import com.example.sweetpawapp.data.model.cart.AddToCartResponse;
import com.example.sweetpawapp.data.model.cart.GetCartResponse;
import com.example.sweetpawapp.data.model.cart.RemoveCartRequest;
import com.example.sweetpawapp.data.model.cart.RemoveCartResponse;
import com.example.sweetpawapp.data.model.order.OrderRequest;
import com.example.sweetpawapp.data.model.order.OrderResponse;
import com.example.sweetpawapp.data.model.order.PreviewOrderRequest;
import com.example.sweetpawapp.data.model.order.PreviewOrderResponse;
import com.example.sweetpawapp.data.model.product.ProductResponse;
import com.example.sweetpawapp.data.model.product.SearchProductResponse;
import com.example.sweetpawapp.data.model.user.ChangePasswordRequest;
import com.example.sweetpawapp.data.model.user.ChangePasswordResponse;
import com.example.sweetpawapp.data.model.user.CheckEmailAndSendOtpRequest;
import com.example.sweetpawapp.data.model.user.CheckEmailAndSendOtpResponse;
import com.example.sweetpawapp.data.model.user.ResetPasswordRequest;
import com.example.sweetpawapp.data.model.user.ResetPasswordResponse;
import com.example.sweetpawapp.data.model.user.VerifyOtpRequest;
import com.example.sweetpawapp.data.model.user.VerifyOtpResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // --- AUTH APIs ---
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    @POST("/api/auth/google-login")
    Call<GoogleLoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);
    @POST("/api/auth/google-register")
    Call<GoogleRegisterResponse> registerWithGoogle(@Body GoogleRegisterRequest request);


    // --- USER APIs ---
    @POST("api/user/checkemail-sendotp")
    Call<CheckEmailAndSendOtpResponse> checkEmailAndSendOtp(@Body CheckEmailAndSendOtpRequest request);
    @POST("api/user/verify-otp")
    Call<VerifyOtpResponse> verifyOtp(@Body VerifyOtpRequest request);
    @POST("api/user/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);
    @PATCH("api/user/change-password")
    Call<ChangePasswordResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
    @GET("api/user/me")
    Call<User> getProfile(@Header("Authorization") String token);
    //Cập nhật thông tin
    @PATCH("api/user/me")
    Call<User> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);
    @PUT("api/user/{id}/add-address")
    Call<AddAddressResponse> addAddress(@Header("Authorization") String token,@Path("id") String userId, @Body User.DiaChi address);

    @PATCH("api/user/{id}/address/default")
    Call<AddAddressResponse> setDefaultAddress(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Body SetDefaultAddressRequest request
    );

    // Xóa địa chỉ theo index
    @HTTP(method = "DELETE", path = "api/user/{id}/address/deleteAddress", hasBody = true)
    Call<AddAddressResponse> deleteAddress(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Body DeleteAddressRequest request
    );

    //Cập nhật địa chỉ
    @PATCH("api/user/{id}/address/updateAddress")
    Call<UpdateAddressResponse> updateAddress(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Body UpdateAddressRequest request
    );


    // --- PRODUCT APIs ---
    @GET("api/products/category/{categoryName}")
    Call<ProductResponse> getProductsByCategory(@Path("categoryName") String categoryName);
    @GET("api/products/search")
    Call<SearchProductResponse> searchProducts(@Query("q") String keyword);
    @POST("api/products/filter")
    Call<FilterProductResponse> filterProducts(@Body FilterProductRequest request);
    // Lấy chi tiết sản phẩm
    @GET("api/products/{productId}")
    Call<GetProductDetailResponse> getProductDetail(@Path("productId") String productId);
    // Sản phẩm yêu thích
    @POST("api/favorite/toggle")
    Call<ToggleFavoriteResponse> toggleFavorite(@Header("Authorization") String token, @Body ToggleFavoriteRequest request);

    @GET("api/favorite")
    Call<FavoriteResponse> getFavorites(@Header("Authorization") String token);


    // --- CART APIs ---
    @GET("/api/cart/{userId}")
    Call<GetCartResponse> getCart(@Header("Authorization") String token, @Path("userId") String userId);

    @POST("/api/cart/add")
    Call<AddToCartResponse> addToCart(@Header("Authorization") String token, @Body AddToCartRequest request);

    //Xóa 1 sản phẩm khỏi giỏ hàng
    @DELETE("/api/cart/clear/{productId}") //Dung lại GetCartResponse do trùng cấu trúc
    Call<RemoveCartResponse> removeItemFromCart(@Header("Authorization") String token, @Path("productId") String productId);

    @HTTP(method = "DELETE", path = "/api/cart/remove", hasBody = true)
    Call<RemoveCartResponse> removeSelectedItems(@Header("Authorization") String token, @Body RemoveCartRequest request);

    //Cập nhật số lượng của sản phẩm trong giỏ
    @PUT("/api/cart/quantity")
    Call<UpdateCartQuantityResponse> updateCartQuantity(@Header("Authorization") String token, @Body UpdateCartQuantityRequest request);


    // --- ORDER APIs ---
    // Lấy danh sách đơn hàng theo userId
    @GET("api/orders/my-orders")
    Call<OrdersListResponse> getUserOrders(@Header("Authorization") String token);
    @POST("api/orders/preview")
    Call<PreviewOrderResponse> previewOrder(@Header("Authorization") String token, @Body PreviewOrderRequest request);

    @POST("api/orders")
    Call<OrderResponse> order (@Header("Authorization") String token, @Body OrderRequest request);
    @GET("/api/orders/detail/{orderId}")
    Call<OrderDetailResponse> getOrderDetail(@Header("Authorization") String token, @Path("orderId") String orderId);


    // --- PAYMENT APIs ---
    @POST("/api/payment/create-payment/{orderId}")
    Call<PaymentResponse> createPayment(@Header("Authorization") String token, @Path("orderId") String orderId);

    @GET("/api/payment/status/{orderId}")
    Call<PaymentStatusResponse> getPaymentStatus(@Header("Authorization") String token, @Path("orderId") String orderId);


    // --- RATING APIs ---
    @GET("/api/ratings")
    Call<ReviewOrderListResponse> getReviewsByProduct(@Header("Authorization") String token, @Query("productId") String productId);
    @GET("/api/ratings")
    Call<ReviewOrderListResponse> getAllReviews(@Header("Authorization") String token);
    @POST("api/ratings/createrating")
    Call<AddReviewResponse> createRating(@Header("Authorization") String token, @Body AddReviewRequest request);
    @PUT("api/ratings/updaterating")
    Call<AddReviewResponse> updateRating(@Header("Authorization") String token, @Body UpdateReviewRequest request);
    @HTTP(method = "DELETE", path = "/api/ratings/deleterating", hasBody = true)
    Call<AddReviewResponse> deleteRating(@Header("Authorization") String token, @Body DeleteRatingRequest request);


    // --- NOTIFICATION APIs ---
    @GET("/api/notification/")
    Call<NotificationByDate> getNotifications(@Header("Authorization") String token);
    @GET("/api/notification/unread")
    Call<NotificationResponse> getUnreadNotifications(@Header("Authorization") String token);
    @POST("/api/notification/mark-as-read/{notificationId}")
    Call<NotificationMarkReadResponse> markAsRead(@Header("Authorization") String token, @Path("notificationId") String notificationId);
}
