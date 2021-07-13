package com.ecommerce.gut.service.impl;

import java.util.Optional;
import com.ecommerce.gut.dto.ErrorCode;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ProductColorSizeRepository;
import com.ecommerce.gut.service.ColorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColorServiceImpl implements ColorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ColorServiceImpl.class);

  @Autowired
  private ColorRepository colorRepository;

  @Autowired
  private ProductColorSizeRepository productColorSizeRepository;

  @Override
  public Color getColorById(Long id) {
    return colorRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.info("Color %d is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
        });
  }

  @Override
  public boolean createColor(Color color) throws CreateDataFailException {
    try {
      Optional<Color> existedColor = colorRepository.findById(color.getId());
      if (existedColor.isPresent()) {
        LOGGER.info("Color %d is already taken", color.getId());
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_ALREADY_EXISTED);
      }

      boolean isUniqueName = colorRepository.existsByName(color.getName());
      if (isUniqueName) {
        LOGGER.info("Color name %d is already taken", color.getName());
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      }

      colorRepository.save(color);
    } catch (Exception e) {
      LOGGER.info("Fail to create color %d", color.getId());
      throw new CreateDataFailException(ErrorCode.ERR_COLOR_CREATED_FAIL);
    }

    return true;
  }

  @Override
  public Color updateColor(Color color, Long id) throws UpdateDataFailException {
    try {
      Optional<Color> oldColor = colorRepository.findById(id);
      if (!oldColor.isPresent()) {
        LOGGER.info("Color %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      }

      boolean isUniqueName = colorRepository.existsByName(color.getName());
      if (isUniqueName) {
        LOGGER.info("Color name %d is already taken", color.getName());
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      }

      var newCategory = oldColor.get();
      newCategory.setName(color.getName());
      newCategory.setSource(color.getSource());

      return colorRepository.save(newCategory);
    } catch (Exception e) {
      LOGGER.info("Fail to update color %d", id);
      throw new UpdateDataFailException(ErrorCode.ERR_COLOR_UPDATED_FAIL);
    }
  }

  @Override
  public boolean deleteColor(Long id) throws DeleteDataFailException {
    try {
      boolean existedColorId = colorRepository.existsById(id);
      if (!existedColorId) {
        LOGGER.info("Color %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      }

      boolean stillJoining = productColorSizeRepository.existsJoiningColor(id);
      if (stillJoining) {
        throw new RestrictDataException(ErrorCode.ERR_PRODUCT_STILL_HAVE_COLOR);
      }

      colorRepository.deleteById(id);
    } catch (Exception e) {
      LOGGER.info("Fail to delete color %d", id);
      throw new DeleteDataFailException(ErrorCode.ERR_COLOR_DELETED_FAIL);
    }

    return true;
  }

}
