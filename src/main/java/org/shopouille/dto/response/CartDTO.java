package org.shopouille.dto.response;

import java.util.List;

public class CartDTO {
    public Long id;
    public Long userId;
    public List<CartItemDTO> items;
}
