package com.example.sweetpawapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.user.AddAddressResponse;
import com.example.sweetpawapp.data.model.user.CheckEmailAndSendOtpResponse;
import com.example.sweetpawapp.data.model.user.ResetPasswordResponse;
import com.example.sweetpawapp.data.model.user.UpdateAddressResponse;
import com.example.sweetpawapp.data.model.user.User;
import com.example.sweetpawapp.data.model.user.VerifyOtpResponse;
import com.example.sweetpawapp.data.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    // LiveData để UI observe
    private final MutableLiveData<User> userProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User.DiaChi>> addressList = new MutableLiveData<>();
    private final MutableLiveData<AddAddressResponse> addAddressResult = new MutableLiveData<>();
    private final MutableLiveData<AddAddressResponse> deleteAddressResult = new MutableLiveData<>();
    private final MutableLiveData<AddAddressResponse> setDefaultResult = new MutableLiveData<>();
    private final MutableLiveData<UpdateAddressResponse> updateAddressResult = new MutableLiveData<>();

    // quản lý observer
    private Observer<User> userObserver;

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<CheckEmailAndSendOtpResponse> checkEmailAndSendOtp(String email) {
        return userRepository.checkEmailAndSendOtp(email);
    }

    public LiveData<VerifyOtpResponse> verifyOtp(String email, String otp) {
        return userRepository.verifyOtp(email, otp);
    }

    public LiveData<ResetPasswordResponse> resetPassword(String email, String newPass, String confirmPass) {
        return userRepository.resetPassword(email, newPass, confirmPass);
    }

    public LiveData<User> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public void fetchUserProfile(String token) {
        LiveData<User> apiResponse = userRepository.getUserProfile(token);

        if (userObserver != null) {
            apiResponse.removeObserver(userObserver);
        }
        userObserver = user -> {
            if (user != null) {
                userProfileLiveData.postValue(user);
            } else {
                userProfileLiveData.postValue(null);
            }
        };

        apiResponse.observeForever(userObserver);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (userObserver != null) {
            userRepository.getUserProfile("").removeObserver(userObserver);
        }

    }

    public LiveData<User> updateProfile(String fullName, String email, String phone) {
        return userRepository.updateProfile(fullName, email, phone);
    }

    // ===== Lấy danh sách địa chỉ =====
    public void fetchAddressList() {
        userRepository.getUserAddress(addressList);
    }

    public LiveData<List<User.DiaChi>> getAddressList() {
        return addressList;
    }

    // ===== Thêm địa chỉ =====
    public void addAddress(String userId, User.DiaChi address) {
        userRepository.addAddress(userId, address, addAddressResult);
    }

    public LiveData<AddAddressResponse> getAddAddressResult() {
        return addAddressResult;
    }

    // ===== Xóa địa chỉ =====
    public void deleteAddress(String userId, int index) {
        userRepository.deleteAddress(userId, index, deleteAddressResult);
    }

    public LiveData<AddAddressResponse> getDeleteAddressResult() {
        return deleteAddressResult;
    }

    // ===== Set địa chỉ mặc định =====
    public void setDefaultAddress(String userId, int index) {
        userRepository.setDefaultAddress(userId, index, setDefaultResult);
    }

    public LiveData<AddAddressResponse> getSetDefaultResult() {
        return setDefaultResult;
    }

    public void updateAddress(String userId, int index, User.DiaChi newAddress) {
        userRepository.updateAddress(userId, index, newAddress, updateAddressResult);
    }

    public LiveData<UpdateAddressResponse> getUpdateAddressResult() {
        return updateAddressResult;
    }

}

