package com.ecommerce.gut.service;

import java.util.List;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface ColorService {

  Color getColorById(Long id);

  List<Color> getAllColors();

  List<Color> getColorsPerPage(Integer pageNum, Integer pageSize, String sortBy);

  List<Color> searchByName(Integer pageNum, Integer pageSize, String sortBy, String name);

  Long countColors();

  Long countColorsByName(String name);
  
  boolean createColor(Color color) throws CreateDataFailException, DuplicateDataException;

  Color updateColor(Color color, Long id) throws UpdateDataFailException, DuplicateDataException, DataNotFoundException;

  boolean deleteColor(Long id) throws DeleteDataFailException, RestrictDataException, DataNotFoundException;

}
