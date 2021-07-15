package com.ecommerce.gut.service;

import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface ColorService {

  Color getColorById(Long id);
  
  boolean createColor(Color color) throws CreateDataFailException, DuplicateDataException;

  Color updateColor(Color color, Long id) throws UpdateDataFailException, DuplicateDataException, DataNotFoundException;

  boolean deleteColor(Long id) throws DeleteDataFailException, RestrictDataException, DataNotFoundException;

}
