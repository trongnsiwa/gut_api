package com.ecommerce.gut.service.impl;

import java.util.Optional;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ColorSizeRepository;
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
  private ColorSizeRepository colorSizeRepository;

  @Override
  public Color getColorById(Long id) {
    return colorRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.info("Color {} is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
        });
  }

  @Override
  public boolean createColor(Color color) throws CreateDataFailException, DuplicateDataException {
    try {
      boolean isUniqueName = colorRepository.existsByName(color.getName());
      if (isUniqueName) {
        LOGGER.info("Color name {} is already taken", color.getName());
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      }

      boolean isUniqueSource = colorRepository.existsBySource(color.getSource());
      if (isUniqueSource) {
        LOGGER.info("Color source {} is already taken", color.getSource());
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
      }

      colorRepository.save(color);
    } catch (DuplicateDataException e) {
      if (e.getMessage().equals(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN)) {
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      } else {
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
      }
    } catch (Exception e) {
      LOGGER.info("Fail to create color {}", color.getName());
      throw new CreateDataFailException(ErrorCode.ERR_COLOR_CREATED_FAIL);
    }

    return true;
  }

  @Override
  public Color updateColor(Color color, Long id)
      throws UpdateDataFailException, DuplicateDataException, DataNotFoundException {
    try {
      Optional<Color> oldColor = colorRepository.findById(id);
      if (!oldColor.isPresent()) {
        LOGGER.info("Color {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      }

      Optional<Color> colorWithName = colorRepository.findByName(color.getName());
      if (colorWithName.isPresent() && !id.equals(colorWithName.get().getId())) {
        LOGGER.info("Color name {} is already taken", color.getName());
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      }

      Optional<Color> colorWithSource = colorRepository.findBySource(color.getSource());
      if (colorWithSource.isPresent() && !id.equals(colorWithSource.get().getId())) {
        LOGGER.info("Color source {} is already taken", color.getSource());
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
      }

      var newCategory = oldColor.get();
      newCategory.setName(color.getName());
      newCategory.setSource(color.getSource());

      return colorRepository.save(newCategory);
    } catch (DataNotFoundException e) {
      throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
    } catch (DuplicateDataException e) {
      if (e.getMessage().equals(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN)) {
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_NAME_ALREADY_TAKEN);
      } else {
        throw new DuplicateDataException(ErrorCode.ERR_COLOR_SOURCE_ALREADY_TAKEN);
      }
    } catch (Exception e) {
      LOGGER.info("Fail to update color {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_COLOR_UPDATED_FAIL);
    }
  }

  @Override
  public boolean deleteColor(Long id)
      throws DeleteDataFailException, RestrictDataException, DataNotFoundException {
    try {
      boolean existedColorId = colorRepository.existsById(id);
      if (!existedColorId) {
        LOGGER.info("Color {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      }

      boolean stillJoining = colorSizeRepository.existsJoiningColor(id);
      if (stillJoining) {
        throw new RestrictDataException(ErrorCode.ERR_PRODUCT_STILL_HAVE_COLOR);
      }

      colorRepository.deleteById(id);
    } catch (DataNotFoundException e) {
      throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
    } catch (RestrictDataException e) {
      throw new RestrictDataException(ErrorCode.ERR_PRODUCT_STILL_HAVE_COLOR);
    } catch (Exception e) {
      LOGGER.info("Fail to delete color {}", id);
      throw new DeleteDataFailException(ErrorCode.ERR_COLOR_DELETED_FAIL);
    }

    return true;
  }

}
