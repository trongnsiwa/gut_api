package com.ecommerce.gut.service;

import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface ColorService {

  Color getColorById(Long id);
  
  boolean createColor(Color color) throws CreateDataFailException;

  Color updateColor(Color color, Long id) throws UpdateDataFailException;

  boolean deleteColor(Long id) throws DeleteDataFailException;

}
