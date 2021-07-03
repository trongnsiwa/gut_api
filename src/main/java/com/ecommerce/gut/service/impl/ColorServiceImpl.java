package com.ecommerce.gut.service.impl;

import java.util.Optional;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.service.ColorService;
import com.ecommerce.gut.util.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ColorServiceImpl implements ColorService {

  @Autowired
  ColorRepository colorRepository;

  @Autowired
  CustomResponseEntity customResponseEntity;

  @Override
  public Color getColorById(Integer id) {
    return colorRepository.findById(id).orElseThrow(() -> new CustomNotFoundException(String.format("Color %d", id)));
  }

  @Override
  public ResponseEntity<?> addColor(Color color) {
    Optional<Color> existedColor = colorRepository.findById(color.getId());
    if (existedColor.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Color %d is already existed.", color.getId()), HttpStatus.CONFLICT);
    }

    boolean isUniqueName = colorRepository.existsByName(color.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(
          String.format("Color name %s is already taken.", color.getName()),
          HttpStatus.CONFLICT);
    }

    colorRepository.save(color);
    return customResponseEntity.generateMessageResponseEntity(
        String.format("Add new color %s successful!", color.getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> updateColor(Color color, Optional<Integer> id) {
    if (!id.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide color Id.", HttpStatus.BAD_REQUEST);
    }

    Optional<Color> oldColor = colorRepository.findById(id.get());
    if (!oldColor.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Color %d is not found.", id.get()), HttpStatus.NOT_FOUND);
    }

    boolean isUniqueName = colorRepository.existsByName(color.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(
          String.format("Color name %s is already taken.", color.getName()),
          HttpStatus.CONFLICT);
    }

    Color newCategory = oldColor.get();
    newCategory.setName(color.getName());
    newCategory.setSource(color.getSource());
    
    colorRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity( String.format("Update color %d successful!", id.get()), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteColor(Optional<Integer> id) {
    if (!id.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide color Id.", HttpStatus.BAD_REQUEST);
    }
    
    boolean existedColorId = colorRepository.existsById(id.get());
    if (!existedColorId) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category group %d is not found.", id.get()), HttpStatus.NOT_FOUND);
    }

    boolean stillJoining = colorRepository.existsJoiningColor(id.get());
    if (stillJoining) {
      return customResponseEntity.generateMessageResponseEntity("There are some products still have this color.", HttpStatus.CONFLICT);
    }

    colorRepository.deleteById(id.get());

    return customResponseEntity.generateMessageResponseEntity(String.format("Delete color %d successful.", id.get()), HttpStatus.OK);
  }
  
}
