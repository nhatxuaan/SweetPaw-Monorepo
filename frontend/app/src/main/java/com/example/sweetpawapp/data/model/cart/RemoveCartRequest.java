package com.example.sweetpawapp.data.model.cart;

import java.util.List;

public class RemoveCartRequest {
    private List<String> selectedItems;

    public RemoveCartRequest(List<String> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }
}
