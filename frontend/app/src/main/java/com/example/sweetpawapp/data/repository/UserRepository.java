package com.example.sweetpawapp.data.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.user.AddAddressResponse;
import com.example.sweetpawapp.data.model.user.CheckEmailAndSendOtpRequest;
import com.example.sweetpawapp.data.model.user.CheckEmailAndSendOtpResponse;
import com.example.sweetpawapp.data.model.user.DeleteAddressRequest;
import com.example.sweetpawapp.data.model.user.ResetPasswordRequest;
import com.example.sweetpawapp.data.model.user.ResetPasswordResponse;
import com.example.sweetpawapp.data.model.user.SetDefaultAddressRequest;
import com.example.sweetpawapp.data.model.user.UpdateAddressRequest;
import com.example.sweetpawapp.data.model.user.UpdateAddressResponse;
import com.example.sweetpawapp.data.model.user.UpdateProfileRequest;
import com.example.sweetpawapp.data.model.user.User;
import com.example.sweetpawapp.data.model.user.VerifyOtpRequest;
import com.example.sweetpawapp.data.model.user.VerifyOtpResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;
    private static final String TAG = "User";

    public UserRepository() {
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }



    public LiveData<CheckEmailAndSendOtpResponse> checkEmailAndSendOtp(String email) {
        MutableLiveData<CheckEmailAndSendOtpResponse> liveData = new MutableLiveData<>();

        CheckEmailAndSendOtpRequest request = new CheckEmailAndSendOtpRequest(email);

        Log.d(TAG, "Gọi API checkEmailAndSendOtp với email: " + email);
        apiService.checkEmailAndSendOtp(request).enqueue(new Callback<CheckEmailAndSendOtpResponse>() {
            @Override
            public void onResponse(Call<CheckEmailAndSendOtpResponse> call, Response<CheckEmailAndSendOtpResponse> response) {
                Log.d(TAG, "Phản hồi từ server: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Thành công: " + response.body().isSuccess() + " | expiresIn: " + response.body().getExpiresIn());
                    liveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Lỗi: Response không hợp lệ hoặc body null");
                    CheckEmailAndSendOtpResponse error = new CheckEmailAndSendOtpResponse();
                    liveData.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<CheckEmailAndSendOtpResponse> call, Throwable t) {
                Log.e(TAG, "Thất bại khi gọi API");
                CheckEmailAndSendOtpResponse fail = new CheckEmailAndSendOtpResponse();
                liveData.postValue(fail);
            }
        });

        return liveData;
    }

    public LiveData<VerifyOtpResponse> verifyOtp(String email, String otp) {
        MutableLiveData<VerifyOtpResponse> data = new MutableLiveData<>();

        VerifyOtpRequest request = new VerifyOtpRequest(email, otp);
        Log.d(TAG, "Gửi yêu cầu verifyOtp: " + email + " - OTP: " + otp);

        apiService.verifyOtp(request).enqueue(new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Phản hồi thành công: " + response.code());
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Phản hồi lỗi: " + response.code());
                    VerifyOtpResponse errorResponse = new VerifyOtpResponse();
                    data.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi verifyOtp: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<ResetPasswordResponse> resetPassword(String email, String newPass, String confirmPass) {
        MutableLiveData<ResetPasswordResponse> liveData = new MutableLiveData<>();
        ResetPasswordRequest request = new ResetPasswordRequest(email, newPass, confirmPass);

        Log.d(TAG, "Gửi yêu cầu reset password cho email: " + email);

        apiService.resetPassword(request).enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Phản hồi thành công: success=" + response.body().isSuccess());
                    liveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Phản hồi lỗi hoặc body null. Code: " + response.code());
                    liveData.postValue(new ResetPasswordResponse(false));
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi gọi API reset password", t);
                liveData.postValue(new ResetPasswordResponse(false));
            }
        });

        return liveData;
    }
    public LiveData<User> getUserProfile(String token) {
        MutableLiveData<User> data = new MutableLiveData<>();
        Log.d(TAG, "Gọi API getProfile với token: " + token);

        apiService.getProfile("Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "API phản hồi: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    User profile = response.body();
                    saveUserToPreferences(profile);

                    // Log chi tiết user
                    Log.d(TAG, "Dữ liệu user nhận được:");
                    Log.d(TAG, "   ID: " + profile.getId());
                    Log.d(TAG, "   Họ tên: " + profile.getHoTen());
                    Log.d(TAG, "   Email: " + profile.getEmail());
                    Log.d(TAG, "   Số điện thoại: " + profile.getSoDienThoai());
                    Log.d(TAG, "   Số địa chỉ: " + (profile.getDiaChiList() != null ? profile.getDiaChiList().size() : 0));

                    if (profile.getDiaChiList() != null) {
                        for (User.DiaChi dc : profile.getDiaChiList()) {
                            Log.d(TAG, "   ➜ " + dc.getTenDiaChi() + ", " + dc.getSoNha() + " " + dc.getTenDuong());
                        }
                    }

                    data.postValue(profile);

                } else {
                    Log.w(TAG, "API trả về lỗi hoặc body null. Code=" + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi đọc errorBody", e);
                        }
                    }
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Lỗi khi gọi API getProfile: " + t.getMessage(), t);
                data.postValue(null);
            }
        });

        return data;
    }

    //Cập nhật thông tin
    public LiveData<User> updateProfile(String fullName, String email, String phone) {
        MutableLiveData<User> data = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        UpdateProfileRequest request = new UpdateProfileRequest(fullName, email, phone);

        apiService.updateProfile("Bearer " + token, request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveUserToPreferences(response.body());
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    // ===== Thêm địa chỉ =====
    public void addAddress(String userId, User.DiaChi address, MutableLiveData<AddAddressResponse> liveData) {
        Log.d(TAG, "===== [ADD ADDRESS API CALL] =====");
        Log.d(TAG, "UserID gửi lên: " + userId);
        Log.d(TAG, "Tên địa chỉ: " + address.getTenDiaChi());
        Log.d(TAG, "Số nhà: " + address.getSoNha());
        Log.d(TAG, "Đường: " + address.getTenDuong());
        Log.d(TAG, "Xã: " + address.getPhuongXa());
        Log.d(TAG, "Huyện: " + address.getQuanHuyen());
        Log.d(TAG, "Tỉnh: " + address.getThanhPho());
        Log.d(TAG, "Mặc định: " + address.isMacDinh());

        // Lấy token từ Preferences
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token null hoặc rỗng, không thể gọi API thêm địa chỉ");
            liveData.postValue(null);
            return;
        }

        apiService.addAddress("Bearer " + token, userId, address).enqueue(new Callback<AddAddressResponse>() {
            @Override
            public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {
                Log.d(TAG, "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Thêm địa chỉ thành công - message: " + response.body().getMessage());
                    String addressJson = new Gson().toJson(response.body().getDiaChi());
                    prefs.saveUserAddress(addressJson);
                    liveData.postValue(response.body());
                    // Hiển thị Toast
                    Toast.makeText(App.getAppContext(), "Thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();

                } else {
                    Log.e(TAG, "Lỗi thêm địa chỉ - body null hoặc response lỗi");
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                Log.e(TAG, "Gọi API thất bại: " + t.getMessage(), t);
                liveData.postValue(null);
            }
        });
    }


    // ===== Lấy danh sách địa chỉ từ profile =====
    public void getUserAddress(MutableLiveData<List<User.DiaChi>> liveData) {
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        apiService.getProfile("Bearer "+ token).enqueue(new Callback<com.example.sweetpawapp.data.model.user.User>() {
            @Override
            public void onResponse(Call<com.example.sweetpawapp.data.model.user.User> call, Response<com.example.sweetpawapp.data.model.user.User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "Gọi thành công api lấy ds địa chỉ");
                    liveData.postValue(response.body().getDiaChiList());
                } else {
                    liveData.postValue(null);
                    Log.e(TAG, "Gọi THẤT BẠI api lấy ds địa chỉ");
                }

            }

            @Override
            public void onFailure(Call<com.example.sweetpawapp.data.model.user.User> call, Throwable t) {
                liveData.postValue(null);
            }
        });
    }


    // ===== Set địa chỉ mặc định =====
    public void setDefaultAddress(String userId, int index, MutableLiveData<AddAddressResponse> liveData) {

        // Lấy token từ Preferences
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token null hoặc rỗng, không thể gọi API thêm địa chỉ");
            liveData.postValue(null);
            return;
        }
        apiService.setDefaultAddress("Bearer " + token, userId, new SetDefaultAddressRequest(index)).enqueue(new Callback<AddAddressResponse>() {
            @Override
            public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {
                Log.e(TAG, "Đã gọi API");
                if (response.isSuccessful() && response.body() != null) {
                    String addressJson = new Gson().toJson(response.body().getDiaChi());
                    prefs.saveUserAddress(addressJson);
                    liveData.postValue(response.body());
                    Log.d(TAG, "Cập nhật địa chỉ mặc định thành công!");
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
    }

    private void saveUserToPreferences(User user) {
        if (user == null) return;
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        prefs.saveUserId(user.getId());
        prefs.saveUserName(user.getHoTen());
        prefs.saveUserPhone(user.getSoDienThoai());

        // Lưu danh sách địa chỉ dưới dạng JSON
        if (user.getDiaChiList() != null && !user.getDiaChiList().isEmpty()) {
            String addressJson = new Gson().toJson(user.getDiaChiList());
            prefs.saveUserAddress(addressJson);
        } else {
            prefs.saveUserAddress("[]");
        }
        prefs.setLoggedIn(true);
    }

    // XÓA ĐỊA CHỈ
    public void deleteAddress(String userId, int index, MutableLiveData<AddAddressResponse> liveData) {

        Log.d(TAG, "===== [DELETE ADDRESS API CALL] =====");
        Log.d(TAG, "UserID: " + userId + " | Index cần xóa: " + index);

        // Lấy token
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token null hoặc rỗng, không thể xóa địa chỉ");
            liveData.postValue(null);
            return;
        }

        // Tạo body request
        DeleteAddressRequest request = new DeleteAddressRequest(index);

        apiService.deleteAddress("Bearer " + token, userId, request)
                .enqueue(new Callback<AddAddressResponse>() {
                    @Override
                    public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {
                        Log.d(TAG, "Response Code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {

                            AddAddressResponse res = response.body();
                            Log.d(TAG, "Xóa địa chỉ thành công - message: " + res.getMessage());

                            // Lưu danh sách địa chỉ mới vào Preferences
                            String addressJson = new Gson().toJson(res.getDiaChi());
                            prefs.saveUserAddress(addressJson);

                            liveData.postValue(res);
                        } else {
                            Log.e(TAG, "Xóa địa chỉ thất bại - body null hoặc lỗi");
                            liveData.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                        Log.e(TAG, "Gọi API xóa địa chỉ thất bại: " + t.getMessage(), t);
                        liveData.postValue(null);
                    }
                });
    }

    // Cập nhật địa chỉ
    public void updateAddress(String userId, int index, User.DiaChi newAddress, MutableLiveData<UpdateAddressResponse> liveData) {

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token null hoặc rỗng, không thể gọi API cập nhật địa chỉ");
            liveData.postValue(null);
            return;
        }

        UpdateAddressRequest request = new UpdateAddressRequest(index, newAddress);

        apiService.updateAddress("Bearer " + token, userId, request)
                .enqueue(new Callback<UpdateAddressResponse>() {
                    @Override
                    public void onResponse(Call<UpdateAddressResponse> call, Response<UpdateAddressResponse> response) {
                        if (response.isSuccessful()){
                            Log.e(TAG, "response.isSuccessful");
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            UpdateAddressResponse res = response.body();

                            // Lưu danh sách địa chỉ mới vào Preferences
                            if (res.getData() != null) {
                                String addressJson = new Gson().toJson(res.getData());
                                prefs.saveUserAddress(addressJson);
                                Log.d(TAG, "Đã lưu địa chỉ: " + addressJson);
                            }

                            liveData.postValue(res);
                            Log.d(TAG, "Cập nhật địa chỉ thành công - message: " + res.getMessage());
                        } else {
                            Log.e(TAG, "Cập nhật địa chỉ thất bại - body null hoặc lỗi, code: " + response.code());
                            liveData.postValue(null);
                        }

                    }

                    @Override
                    public void onFailure(Call<UpdateAddressResponse> call, Throwable t) {
                        Log.e(TAG, "Gọi API cập nhật địa chỉ thất bại: " + t.getMessage(), t);
                        liveData.postValue(null);
                    }
                });
    }



}
