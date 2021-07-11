package com.ecommerce.gut.service.impl;

import java.util.Locale;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ProductColorSizeRepository;
import com.ecommerce.gut.service.ColorService;
import com.ecommerce.gut.util.CustomResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ColorServiceImpl implements ColorService {

  @Autowired
  private ColorRepository colorRepository;

  @Autowired
  private CustomResponseEntity customResponseEntity;

  @Autowired
  private ProductColorSizeRepository productColorSizeRepository;

  @Autowired
  private MessageSource messages;

  @Autowired
  private HttpServletRequest request;

  @Override
  public Color getColorById(Long id) {
    return colorRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("color.message.notFound", null, request.getLocale()), id)));
  }

  @Override
  public ResponseEntity<?> addColor(Color color) {

    Locale locale = request.getLocale();

    Optional<Color> existedColor = colorRepository.findById(color.getId());
    if (existedColor.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("color.message.alreadyExisted", null, locale), color.getId()), HttpStatus.CONFLICT);
    }

    boolean isUniqueName = colorRepository.existsByName(color.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(
          String.format(messages.getMessage("color.message.nameAlreadyTaken", null, locale), color.getName()),
          HttpStatus.CONFLICT);
    }

    colorRepository.save(color);
    return customResponseEntity.generateMessageResponseEntity(
        String.format(messages.getMessage("color.message.addSucc", null, locale), color.getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> updateColor(Color color, Long id) {

    Locale locale = request.getLocale();

    Optional<Color> oldColor = colorRepository.findById(id);
    if (!oldColor.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("color.message.notFound", null, locale), id));
    }

    boolean isUniqueName = colorRepository.existsByName(color.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(
          String.format(messages.getMessage("color.message.nameAlreadyTaken", null, locale), color.getName()),
          HttpStatus.CONFLICT);
    }

    var newCategory = oldColor.get();
    newCategory.setName(color.getName());
    newCategory.setSource(color.getSource());
    
    colorRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("color.message.updateSucc", null, locale), id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteColor(Long id) {

    Locale locale = request.getLocale();

    boolean existedColorId = colorRepository.existsById(id);
    if (!existedColorId) {
      throw new CustomNotFoundException(String.format(messages.getMessage("color.message.notFound", null, locale), id));
    }

    boolean stillJoining = productColorSizeRepository.existsJoiningColor(id);
    if (stillJoining) {
      return customResponseEntity.generateMessageResponseEntity(messages.getMessage("color.message.productStillHave", null, locale), HttpStatus.CONFLICT);
    }

    colorRepository.deleteById(id);

    return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("color.message.delSucc", null, locale), id), HttpStatus.OK);
  }
  
}
