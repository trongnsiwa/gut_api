package com.ecommerce.gut.service.impl;

import java.util.List;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.service.PSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PSizeServiceImpl implements PSizeService {

  @Autowired
  PSizeRepository pSizeRepository;

  @Override
  public List<PSize> getAllSizes() {
    return pSizeRepository.findAll();
  }
  
}
