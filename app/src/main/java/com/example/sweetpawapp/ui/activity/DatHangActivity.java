package com.example.sweetpawapp.ui.activity;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.order.DataOrder;
import com.example.sweetpawapp.data.model.user.Address;
import com.example.sweetpawapp.data.model.cart.CartItem;
import com.example.sweetpawapp.data.model.order.OrderRequest;
import com.example.sweetpawapp.data.model.order.PreviewOrderRequest;
import com.example.sweetpawapp.data.model.user.User;
import com.example.sweetpawapp.ui.adapter.DatHangAdapter;
import com.example.sweetpawapp.ui.adapter.PhuongThucAdapter;
import com.example.sweetpawapp.ui.viewmodel.OrderViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatHangActivity extends AppCompatActivity {
    private static final String TAG = "Order";
    private RecyclerView rcvSelected;
    private TextView tvTongTien, edtDiaChi, tvApDung, tvThongBao, tvPhiGH, tvGiam, tvTong, tvTienTT, txtNgayKhac, tvThongBaoKM ;
    private EditText edtTen, edtSDT, editGhiChu, editKM;
    private ImageView imgEditDiaChi;
    private Spinner spnPhuongThuc;
    CardView cvHomNay, cvNgayKhac;
    CardView gio1, gio2, gio3, gio4, gio5, gio6;
    List<CardView> gioList = new ArrayList<>();
    private List<Address> diaChiList;
    private List<CardView> ngayList = new ArrayList<>();
    private Button btnDatHang;

    private User.DiaChi diaChi;
    private ArrayList<CartItem> selectedItems;
    private int total;


    private DecimalFormat df = new DecimalFormat("#,### đ");
    private final ActivityResultLauncher<Intent> someActivityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Address selected = data.getParcelableExtra("selectedAddress");
                                if(selected != null){
                                    edtDiaChi.setText(selected.getFullDiaChi());
                                    String sdt = edtSDT.getText().toString().trim();
                                    if (coThongTinDayDu(sdt, diaChi)) {
                                        Log.d(TAG, "Đủ thông tin, gọi API preview...");
                                        OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

                                        // Gọi preview thật
                                        previewOrderIfPossible(selectedItems, diaChi, orderViewModel);

                                        // Lắng nghe kết quả API
                                        orderViewModel.getPreviewOrderResponse().observe(this, response -> {
                                            if (response != null && response.getData() != null) {
                                                int ship = response.getData().getShippingFee();
                                                int discount = response.getData().getDiscountAmount();
                                                int totalPrice = response.getData().getTotal();

                                                tvPhiGH.setText(df.format(ship));
                                                tvGiam.setText(df.format(discount));
                                                tvTong.setText(df.format(totalPrice));
                                                tvThongBao.setText("Tính toán đơn hàng thành công!");
                                                tvTienTT.setText(tvTong.getText());
                                            } else {
                                                tvThongBao.setText("Không thể tính phí giao hàng.");
                                            }
                                        });
                                    }

                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dat_hang_activity);
        // Lấy dữ liệu từ Intent
        selectedItems = getIntent().getParcelableArrayListExtra("selectedItems");
        total = getIntent().getIntExtra("totalPrice", 0);
        initUI();
        // Load thong tin tu pref
        loadUserInfoFromPrefs();

        setupEvents();
    }

    private void loadUserInfoFromPrefs() {
        PreferencesManager prefs = PreferencesManager.getInstance(this);

        // Lấy thông tin cơ bản
        String hoTen = prefs.getUserName();
        String sdt = prefs.getUserPhone();
        String addressJson = prefs.getUserAddress();

        // Set dữ liệu lên UI
        if (hoTen != null && !hoTen.isEmpty()) {
            edtTen.setText(hoTen);
        }

        if (sdt != null && !sdt.isEmpty()) {
            edtSDT.setText(sdt);
        }

        if (addressJson != null && !addressJson.isEmpty()) {
            try {
                Log.d(TAG, "Address JSON: " + addressJson);
                Type listType = new TypeToken<ArrayList<User.DiaChi>>() {}.getType();
                List<User.DiaChi> addressList = new Gson().fromJson(addressJson, listType);

                if (addressList != null && !addressList.isEmpty()) {
                    Log.d(TAG, "Parsed address list size: " + addressList.size());
                    for (User.DiaChi dc : addressList) {
                        Log.d("DatHangActivity", "Địa chỉ load: " + dcToString(dc));
                    }

                    User.DiaChi diaChiMacDinh = null;
                    for (User.DiaChi dc : addressList) {
                        if (dc.isMacDinh()) {
                            diaChiMacDinh = dc;
                            break;
                        }
                    }
                    if (diaChiMacDinh == null) diaChiMacDinh = addressList.get(0);

                    // Hiển thị lên UI
                    String fullAddress = dcToString(diaChiMacDinh);
                    edtDiaChi.setText(fullAddress);
                    Log.d(TAG, "Hiển thị địa chỉ: " + fullAddress);
                } else {
                    Log.w(TAG, "addressList trống sau khi parse!");
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi parse JSON địa chỉ", e);
            }
        }


        Log.d(TAG, "Load user from prefs: " + hoTen + " | " + sdt);
    }

    private String dcToString(User.DiaChi dc) {
        if (dc == null) return "";
        return dc.getSoNha() + ", " +
                dc.getTenDuong() + ", " +
                dc.getPhuongXa() + ", " +
                dc.getQuanHuyen() + ", " +
                dc.getThanhPho();
    }

    private void initUI(){
        rcvSelected = findViewById(R.id.rcvSelected);
        tvTongTien = findViewById(R.id.tvTongTien);
        spnPhuongThuc = findViewById(R.id.spnPhuongThuc);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        tvApDung = findViewById(R.id.tvApDung);
        tvThongBao = findViewById(R.id.tvThongBao);
        tvPhiGH = findViewById(R.id.tvPhiGH);
        tvGiam = findViewById(R.id.tvGiam);
        tvTong = findViewById(R.id.tvTong);
        tvTienTT = findViewById(R.id.tvTienTT);
        edtTen = findViewById(R.id.edtTen);
        edtSDT = findViewById(R.id.edtSDT);
        editGhiChu = findViewById(R.id.editGhiChu);
        editKM = findViewById(R.id.editKM);
        tvThongBaoKM = findViewById(R.id.tvThongBaoKM);
        imgEditDiaChi = findViewById(R.id.imgEditDiaChi);
        btnDatHang = findViewById(R.id.btnDatHang);
        cvHomNay = findViewById(R.id.cvhomNay);
        cvNgayKhac = findViewById(R.id.cvNgayKhac);
        txtNgayKhac = findViewById(R.id.txtNgayKhac);
        ngayList.add(cvHomNay);
        ngayList.add(cvNgayKhac);
        gio1 = findViewById(R.id.gio1).getParent() instanceof CardView ? (CardView) ((View)findViewById(R.id.gio1)).getParent() : null;
        gio2 = findViewById(R.id.gio2).getParent() instanceof CardView ? (CardView) ((View)findViewById(R.id.gio2)).getParent() : null;
        gio3 = findViewById(R.id.gio3).getParent() instanceof CardView ? (CardView) ((View)findViewById(R.id.gio3)).getParent() : null;
        gio4 = findViewById(R.id.gio4).getParent() instanceof CardView ? (CardView) ((View)findViewById(R.id.gio4)).getParent() : null;
        gio5 = findViewById(R.id.gio5).getParent() instanceof CardView ? (CardView) ((View)findViewById(R.id.gio5)).getParent() : null;
        gio6 = findViewById(R.id.gio6).getParent() instanceof CardView ? (CardView) ((View)findViewById(R.id.gio6)).getParent() : null;
        gioList.add(gio1);
        gioList.add(gio2);
        gioList.add(gio3);
        gioList.add(gio4);
        gioList.add(gio5);
        gioList.add(gio6);

        // Dữ liệu cho spinner
        String[] phuongThuc = {"Thanh toán khi nhận hàng", "Chuyển khoản"};

        int[] icons = {
                R.drawable.ic_cod,   // icon tiền mặt
                R.drawable.ic_bank   // icon ngân hàng
        };
        // Adapter
        PhuongThucAdapter adapterPT = new PhuongThucAdapter(this, phuongThuc, icons);
        spnPhuongThuc.setAdapter(adapterPT);
        spnPhuongThuc.setPopupBackgroundResource(R.drawable.bg_edittext_border);

        rcvSelected.setLayoutManager(new LinearLayoutManager(this));

    }


    private void setupEvents(){
        if(selectedItems != null && !selectedItems.isEmpty()){
            DatHangAdapter adapter = new DatHangAdapter(selectedItems);
            rcvSelected.setLayoutManager(new LinearLayoutManager(this));
            rcvSelected.setAdapter(adapter);
            rcvSelected.setVisibility(View.VISIBLE);
        }



        tvTongTien.setText(df.format(total));

        // Trong DatHangActivity
        imgEditDiaChi.setOnClickListener(v -> {

            PreferencesManager prefs = PreferencesManager.getInstance(this);
            String addressJson = prefs.getUserAddress(); // lấy danh sách địa chỉ từ SharedPreferences

            Intent intent = new Intent(DatHangActivity.this, DiaChiActivity.class);
            intent.putExtra("addresses_json", addressJson);
            someActivityResultLauncher.launch(intent);
        });
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());

        View.OnClickListener ngayClickListener = v -> {
            for (CardView card : ngayList) {
                card.setCardBackgroundColor(Color.parseColor("#FFE8E9")); // reset màu
            }

            ((CardView) v).setCardBackgroundColor(Color.parseColor("#FE4B5E"));

            // Nếu người dùng chọn "Ngày khác" thì mở lịch
            if (v.getId() == R.id.cvNgayKhac) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(selectedYear, selectedMonth, selectedDay);
                            // Ngày được chọn luôn là ngày tương lai (vì đã chặn hôm nay & quá khứ)

                            String ngayChon = String.format("%02d/%02d/%d",
                                    selectedDay, selectedMonth + 1, selectedYear);
                            txtNgayKhac.setText(ngayChon);
                            enableAllTimeSlots(); // tất cả khung giờ đều khả dụng
                        },
                        year, month, day
                );
                // Không cho chọn hôm nay và quá khứ
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_MONTH, 1);
                datePickerDialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());

                datePickerDialog.show();
            } else {
                txtNgayKhac.setText("Ngày khác"); // reset khi chọn “Hôm nay”
                disablePastTimeSlots();
            }
        };



        for (CardView card : ngayList) {
            card.setOnClickListener(ngayClickListener);
        }

        View.OnClickListener gioClickListener = v -> {
            for (CardView card : gioList) {
                card.setCardBackgroundColor(Color.parseColor("#FFE8E9"));
            }
            ((CardView) v).setCardBackgroundColor(Color.parseColor("#FE4B5E"));
        };

        for (CardView card : gioList) {
            if (card != null) card.setOnClickListener(gioClickListener);
        }

        //Lấy dữ liệu người dùng
        PreferencesManager prefs = PreferencesManager.getInstance(this);
        User user = new User();


        String addressJson = prefs.getUserAddress();
        List<User.DiaChi> diaChiList = new ArrayList<>();

        if (addressJson != null && !addressJson.isEmpty()) {
            Type listType = new TypeToken<List<User.DiaChi>>() {}.getType();
            diaChiList = new Gson().fromJson(addressJson, listType);
        }
        user.setDiaChiList(diaChiList);

        // Chọn địa chỉ mặc định
        //User.DiaChi diaChi = null;
        if (user.getDiaChiList() != null && !user.getDiaChiList().isEmpty()) {
            for (User.DiaChi dc : user.getDiaChiList()) {
                if (dc.isMacDinh()) {
                    diaChi = dc;
                    break;
                }
            }
            if (diaChi == null) diaChi = user.getDiaChiList().get(0);
        }


        String sdt = edtSDT.getText().toString().trim();

        if (coThongTinDayDu(sdt, diaChi)) {
            Log.d(TAG, "Đủ thông tin, gọi API preview...");
            OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

            // Gọi preview thật
            previewOrderIfPossible(selectedItems, diaChi, orderViewModel);

            // Lắng nghe kết quả API
            orderViewModel.getPreviewOrderResponse().observe(this, response -> {
                if (response != null && response.getData() != null) {
                    int ship = response.getData().getShippingFee();
                    int discount = response.getData().getDiscountAmount();
                    int totalPrice = response.getData().getTotal();
                    
                    tvPhiGH.setText(df.format(ship));
                    tvGiam.setText(df.format(discount));
                    tvTong.setText(df.format(totalPrice));
                    tvThongBao.setText("Tính toán đơn hàng thành công!");
                    tvTienTT.setText(tvTong.getText());
                } else {
                    tvThongBao.setText("Không thể tính phí giao hàng.");
                }
            });
        }

        //Xử lý áp dụng khuyến mãi
        tvApDung.setOnClickListener(v->{
            String maKM = editKM.getText().toString().trim();
            //Gọi lại API previewOrderIfPossible
            if (selectedItems == null || selectedItems.isEmpty()) {
                Toast.makeText(this, "Không có sản phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy ViewModel
            OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

            // GỌI LẠI API PREVIEW
            previewOrderIfPossible(selectedItems, diaChi, orderViewModel);

            // LẮNG NGHE KẾT QUẢ
            orderViewModel.getPreviewOrderResponse().observe(this, response -> {
                if (response != null && response.getData() != null) {

                    int ship = response.getData().getShippingFee();
                    int discount = response.getData().getDiscountAmount();
                    int totalPrice = response.getData().getTotal();

                    tvPhiGH.setText(df.format(ship));
                    tvGiam.setText(df.format(discount));
                    tvTong.setText(df.format(totalPrice));
                    tvTienTT.setText(df.format(totalPrice));

                    tvThongBaoKM.setText("Áp dụng mã thành công!");
                } else {
                    tvThongBaoKM.setText("Mã giảm giá không hợp lệ!");
                    tvGiam.setText("0");
                }
            });
            if(maKM.isEmpty()){
                Toast.makeText(this, "Nhập mã khuyến mãi để áp dụng", Toast.LENGTH_SHORT).show();
                tvThongBaoKM.setVisibility(View.GONE);
            }
            else{
                tvThongBaoKM.setVisibility(View.VISIBLE);
            }

        });
    


        //Xu ly dathang
        btnDatHang.setOnClickListener(v ->{
            final User.DiaChi currentAddress = this.diaChi;
            // Lấy thông tin cơ bản
            String ten = edtTen.getText().toString().trim();
            String ghiChu = editGhiChu.getText().toString().trim();

            //Lấy ngày giao
            String ngayGiao;
            if (txtNgayKhac.getText().toString().equals("Ngày khác")) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                ngayGiao = String.format("%02d/%02d/%d", day, month, year);
            } else {
                ngayGiao = txtNgayKhac.getText().toString();
            }
            Log.d(TAG, "Ngày giao hàng: " + ngayGiao);

            // Lấy giờ giao (CardView nào đang có màu đỏ)
            String gioGiao = "";
            for (CardView card : gioList) {
//                if (card != null && card.getBackground() instanceof ColorDrawable) {
//                    ColorDrawable bg = (ColorDrawable) card.getBackground();
//                    if (bg.getColor() == Color.parseColor("#FE4B5E")) {
//                        View child = ((ViewGroup) card).getChildAt(0);
//                        if (child instanceof TextView) {
//                            gioGiao = ((TextView) child).getText().toString();
//                        }
//                        break;
//                    }
//                }
                if (card == null) continue;

                int currentColor = card.getCardBackgroundColor().getDefaultColor();

                if (currentColor == Color.parseColor("#FE4B5E")) {
                    TextView label = findTextViewInViewGroup(card);
                    if (label != null) {
                        gioGiao = label.getText().toString();
                    }
                    break;
                }
            }
            Log.d(TAG, "Giờ giao hàng: " + gioGiao);


            // Phương thức thanh toán
            String phuongThuc = spnPhuongThuc.getSelectedItem().toString();
            Log.d("PhuongThuc", phuongThuc);

            List<OrderRequest.Item> items = new ArrayList<>();
            for (CartItem item : selectedItems) {
                items.add(new OrderRequest.Item(
                        item.getProductId(),
                        item.getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        200
                ));
            }
            int idPay;
            if (phuongThuc.equals("Thanh toán khi nhận hàng")){
                idPay = 2;
            }
            else{
                idPay = 1;
            }

            if (ten.isEmpty() || sdt.isEmpty() || currentAddress == null || ngayGiao.isEmpty() || gioGiao.trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin giao hàng!", Toast.LENGTH_SHORT).show();
                return;
            }
            String discount_code = editKM.getText().toString().trim();

            // Tạo request dùng thông tin từ diaChi mặc định
            OrderRequest request = new OrderRequest(
                    ten,
                    sdt,
                    currentAddress.getSoNha() + " " + currentAddress.getTenDuong(),
                    currentAddress.getPhuongXa(),
                    currentAddress.getQuanHuyen(),
                    currentAddress.getThanhPho(),
                    ghiChu,
                    items,
                    idPay,
                    discount_code
            );

            Log.d("DatHang", "➡ Gửi request tạo đơn hàng: " + new Gson().toJson(request));

            // Gọi ViewModel xử lý đặt hàng
            OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
            orderViewModel.orderRepository(request);

            // Quan sát kết quả trả về
            orderViewModel.getOrderResponse().observe(this, response -> {
                if (response != null && response.getData() != null && response.getData().getCode() == 200) {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

                    String orderId = response.getData().getOrderId();
                    String tienTT = tvTienTT.getText().toString();
                    if(phuongThuc.equals("Chuyển khoản")){
                        Log.d(TAG, "order" + orderId);
                        Intent intent = new Intent(DatHangActivity.this, PaymentActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("tienTT", tienTT);
                        startActivity(intent);
                    }

                    finish();
                } else {
                    Toast.makeText(this, "Không thể đặt hàng. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });

        });

    }

    private void previewOrderIfPossible(ArrayList<CartItem> selectedItems, User.DiaChi diaChi, OrderViewModel orderViewModel) {

        if (selectedItems == null || selectedItems.isEmpty() || diaChi == null) {
            Log.w(TAG, "Thiếu sản phẩm hoặc địa chỉ, không thể gọi API preview");
            return;
        }

        // 1️ Chuyển danh sách CartItem thành danh sách Item (theo API yêu cầu)
        List<PreviewOrderRequest.Item> items = new ArrayList<>();
        for (CartItem item : selectedItems) {
            items.add(new PreviewOrderRequest.Item(
                    item.getProductId(),   // ID sản phẩm
                    item.getName(), // Tên sản phẩm
                    item.getQuantity(),    // Số lượng
                    item.getPrice(),       // Giá
                    200      // Trọng lượng
            ));
        }

        // 2 Lấy thông tin địa chỉ giao hàng
        User user = new User();

        PreferencesManager prefs = PreferencesManager.getInstance(this);
        user.setId(prefs.getUserId());
        user.setHoTen(prefs.getUserName());
        user.setSoDienThoai(prefs.getUserPhone());

        String toName = user.getHoTen();
        String toPhone = user.getSoDienThoai();
        String toAddress = diaChi.getSoNha() + " " + diaChi.getTenDuong() + ", "
                + diaChi.getPhuongXa() + ", " + diaChi.getQuanHuyen();
        String toWard = diaChi.getPhuongXa();
        String toDistrict = diaChi.getQuanHuyen();
        String toProvince = diaChi.getThanhPho();
        String note = editGhiChu.getText().toString().trim();
        String maKM = editKM.getText().toString().trim();

        // 3  Tạo request đầy đủ
        PreviewOrderRequest request = new PreviewOrderRequest(
                toName,
                toPhone,
                toAddress,
                toWard,
                toDistrict,
                toProvince,
                note,
                items,
                maKM
        );

        // Log ra để xem JSON gửi lên có đúng không
        Log.d("DatHangActivity", "Gửi request preview order: " + new Gson().toJson(request));

        // 4 Gọi API qua ViewModel
        orderViewModel.previewOrder(request);


    }
    private boolean coThongTinDayDu(String sdt, User.DiaChi diaChi) {
        return sdt != null
                && !sdt.trim().isEmpty()
                && diaChi != null;
    }

    private void disablePastTimeSlots() {
        // 6 khung giờ cố định
        int[] startHours = {9, 10, 11, 13, 14, 15};
        int[] endHours   = {10, 11, 12, 14, 15, 16};

        // Lấy giờ hiện tại
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        Log.d("DisableSlot", "Giờ hiện tại: " + currentHour + ":" + currentMinute);

        for (int i = 0; i < gioList.size(); i++) {
            CardView card = gioList.get(i);
            if (card == null) continue;

            // Tìm TextView hiển thị trong CardView
            TextView label = null;
            if (card.getChildCount() > 0 && card.getChildAt(0) instanceof TextView) {
                label = (TextView) card.getChildAt(0);
            } else {
                // Nếu layout có TextView lồng sâu hơn (trong LinearLayout chẳng hạn)
                label = findTextViewInViewGroup(card);
            }

            int end = endHours[i];

            // Nếu đã qua khung giờ => disable
            if (currentHour >= end) {
                card.setEnabled(false);
                card.setCardBackgroundColor(Color.parseColor("#E0E0E0")); // màu xám
                if (label != null) label.setTextColor(Color.GRAY);
                Log.d("DisableSlot", "Tắt khung giờ: " + (label != null ? label.getText() : "unknown"));
            } else {
                // Giờ còn hợp lệ => enable
                card.setEnabled(true);
                card.setCardBackgroundColor(Color.parseColor("#FFE8E9")); // màu hồng nhạt
                if (label != null) label.setTextColor(Color.BLACK);
            }
        }
    }

    // Tìm TextView trong ViewGroup nếu TextView nằm sâu hơn 1 cấp
    private TextView findTextViewInViewGroup(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof TextView) {
                return (TextView) child;
            } else if (child instanceof ViewGroup) {
                TextView result = findTextViewInViewGroup((ViewGroup) child);
                if (result != null) return result;
            }
        }
        return null;
    }
    private void enableAllTimeSlots() {
        for (CardView card : gioList) {
            if (card == null) continue;
            card.setEnabled(true);
            card.setCardBackgroundColor(Color.parseColor("#FFE8E9"));
            TextView label = findTextViewInViewGroup(card);
            if (label != null) label.setTextColor(Color.BLACK);
        }
    }


}
