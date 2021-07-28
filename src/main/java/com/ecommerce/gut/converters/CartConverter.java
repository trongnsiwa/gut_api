package com.ecommerce.gut.converters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.CartDTO;
import com.ecommerce.gut.dto.CartItemDTO;
import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.SizeDTO;
import com.ecommerce.gut.entity.Cart;
import com.ecommerce.gut.entity.CartItem;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.Image;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.exception.ConvertEntityDTOException;
import com.ecommerce.gut.payload.response.ErrorCode;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartConverter {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CartConverter.class);

  @Autowired
  private ModelMapper modelMapper;

  public CartDTO convertCartToDto(Cart cart) throws ConvertEntityDTOException {
    try {
      CartDTO cartDTO = new CartDTO();
      cartDTO.setId(cart.getId());
      cartDTO.setUserId(cart.getUser().getId());
      
      List<CartItemDTO> cartItems = cart.getCartItems()
          .stream()
          .map(this::convertCartItemToDto)
          .collect(Collectors.toList());
      cartDTO.setCartItems(cartItems);

      return cartDTO;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Cart to CartDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public CartItemDTO convertCartItemToDto(CartItem cartItem) throws ConvertEntityDTOException {
    try {
      CartItemDTO cartItemDTO = new CartItemDTO();
      cartItemDTO.setId(cartItem.getId());
      cartItemDTO.setProductId(cartItem.getProduct().getId());
      cartItemDTO.setProductName(cartItem.getProduct().getName());
      cartItemDTO.setPrice(cartItem.getPrice());

      Optional<ProductImage> firstImage = cartItem.getProduct().getProductImages().stream().findFirst();
      if (firstImage.isPresent()) {
        Image img = firstImage.get().getImage();
        ProductImageDTO imageDTO = new ProductImageDTO(img.getId(), img.getImageUrl(), img.getTitle(), firstImage.get().getColorCode());
        cartItemDTO.setImage(imageDTO);
      }

      cartItemDTO.setColor(convertColorToDto(cartItem.getColorSize().getColor()));
      cartItemDTO.setSize(convertSizeToDto(cartItem.getColorSize().getSize()));

      cartItemDTO.setAmount(cartItem.getAmount());

      return cartItemDTO;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert CartItem to CartItemDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public ColorDTO convertColorToDto(Color color) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(color, ColorDTO.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Color to ColorDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public SizeDTO convertSizeToDto(PSize pSize) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(pSize, SizeDTO.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert PSize to SizeDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

}
