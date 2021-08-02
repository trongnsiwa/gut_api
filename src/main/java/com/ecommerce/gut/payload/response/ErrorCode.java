package com.ecommerce.gut.payload.response;

public class ErrorCode {
  
  // AUTH
  public static final String ERR_ROLE_NOT_FOUND = "ERR_ROLE_NOT_FOUND";
  public static final String ERR_EMAIL_ALREADY_TAKEN = "ERR_EMAIL_ALREADY_TAKEN";
  public static final String ERR_USER_CREATED_FAIL = "ERR_USER_CREATED_FAIL";
  public static final String ERR_LOGIN_FAIL = "ERR_LOGIN_FAIL";

  // CATEGORY
  public static final String ERR_CATEGORY_PARENT_NOT_FOUND = "ERR_CATEGORY_PARENT_NOT_FOUND";
  public static final String ERR_CATEGORY_NOT_FOUND = "ERR_CATEGORY_NOT_FOUND";
  public static final String ERR_CATEGORY_PARENT_ALREADY_TAKEN = "ERR_CATEGORY_PARENT_ALREADY_TAKEN";
  public static final String ERR_CATEGORY_ALREADY_TAKEN = "ERR_CATEGORY_ALREADY_TAKEN";
  public static final String ERR_PARENT_NAME_EXISTED = "ERR_PARENT_NAME_EXISTED";
  public static final String ERR_CATEGORY_NAME_EXISTED = "ERR_CATEGORY_NAME_EXISTED";
  public static final String ERR_CATEGORY_STILL_IN_PARENT = "ERR_CATEGORY_STILL_IN_PARENT";
  public static final String ERR_PRODUCT_STILL_IN_CATEGORY = "ERR_PRODUCT_STILL_IN_CATEGORY";
  public static final String ERR_CATEGORY_PARENT_CREATED_FAIL = "ERR_CATEGORY_PARENT_CREATED_FAIL";
  public static final String ERR_CATEGORY_CREATED_FAIL = "ERR_CATEGORY_CREATED_FAIL";
  public static final String ERR_CATEGORY_PARENT_UPDATED_FAIL = "ERR_CATEGORY_PARENT_UPDATED_FAIL";
  public static final String ERR_CATEGORY_UPDATED_FAIL = "ERR_CATEGORY_UPDATED_FAIL";
  public static final String ERR_CATEGORY_PARENT_DELETED_FAIL = "ERR_CATEGORY_PARENT_DELETED_FAIL";
  public static final String ERR_CATEGORY_DELETED_FAIL = "ERR_CATEGORY_DELETED_FAIL";
  public static final String ERR_CATEGORY_PARENT_LOADED_FAIL = "CATEGORY_PARENT_LOADED_FAIL";
  public static final String ERR_CATEGORY_LOADED_FAIL = "CATEGORY_LOADED_FAIL";

  // COLOR
  public static final String ERR_COLOR_NOT_FOUND = "ERR_COLOR_NOT_FOUND";
  public static final String ERR_COLOR_ALREADY_EXISTED = "ERR_COLOR_ALREADY_EXISTED";
  public static final String ERR_COLOR_NAME_ALREADY_TAKEN = "ERR_COLOR_NAME_ALREADY_TAKEN";
  public static final String ERR_COLOR_SOURCE_ALREADY_TAKEN = "ERR_COLOR_SOURCE_ALREADY_TAKEN";
  public static final String ERR_PRODUCT_STILL_HAVE_COLOR = "ERR_PRODUCT_STILL_HAVE_COLOR";
  public static final String ERR_COLOR_LOADED_FAIL = "ERR_COLOR_LOADED_FAIL";
  public static final String ERR_COLOR_CREATED_FAIL = "ERR_COLOR_CREATED_FAIL";
  public static final String ERR_COLOR_UPDATED_FAIL = "ERR_COLOR_UPDATED_FAIL";
  public static final String ERR_COLOR_DELETED_FAIL = "ERR_COLOR_DELETED_FAIL";

  // SIZE
  public static final String ERR_SIZE_NOT_FOUND = "ERR_SIZE_NOT_FOUND";

  // PRODUCT
  public static final String ERR_PRODUCT_NOT_FOUND = "ERR_PRODUCT_NOT_FOUND"; 
  public static final String ERR_PRODUCT_ALREADY_TAKEN = "ERR_PRODUCT_ALREADY_TAKEN"; 
  public static final String ERR_NOT_EXIST_TWO_SAME_COLORS = "ERR_NOT_EXIST_TWO_SAME_COLORS";
  public static final String ERR_PRODUCT_LOADED_FAIL = "ERR_PRODUCT_LOADED_FAIL";
  public static final String ERR_PRODUCT_CREATED_FAIL = "ERR_PRODUCT_CREATED_FAIL";
  public static final String ERR_PRODUCT_UPDATED_FAIL = "ERR_PRODUCT_UPDATED_FAIL";
  public static final String ERR_PRODUCT_DELETED_FAIL = "ERR_PRODUCT_DELETED_FAIL";
  public static final String ERR_PRODUCT_IMAGES_REPLACED_FAIL = "ERR_PRODUCT_IMAGES_REPLACED_FAIL";
  public static final String ERR_PRODUCT_REVIEW_ADDED_FAIL = "ERR_PRODUCT_REVIEW_ADDED_FAIL";
  public static final String ERR_PRODUCT_ADDED_TO_PARENT = "ERR_PRODUCT_ADDED_TO_PARENT";

  // USER
  public static final String ERR_USER_NOT_FOUND = "ERR_USER_NOT_FOUND"; 
  public static final String ERR_USER_PROFILE_LOADED_FAIL = "ERR_USER_PROFILE_LOADED_FAIL"; 
  public static final String ERR_USER_PROFILE_EDITED_FAIL = "ERR_USER_PROFILE_EDITED_FAIL"; 
  public static final String ERR_CURRENT_USER_PROFILE_EDITED_FAIL = "ERR_CURRENT_USER_PROFILE_PROFILE";
  public static final String ERR_USER_DELETED_FAIL = "ERR_USER_DELETED_FAIL";
  public static final String ERR_USER_DEACTIVATED_FAIL = "ERR_USER_DEACTIVATED_FAIL";
  public static final String ERR_USER_ACTIVATED_FAIL = "ERR_USER_ACTIVATED_FAIL";
  public static final String ERR_USER_ROLES_CHANGED_FAIL = "ERR_USER_ROLES_CHANGED_FAIL";
  public static final String ERR_LOAD_USERS_FAIL = "ERR_LOAD_USERS_FAIL";

  // CART
  public static final String ERR_CURRENT_QUANTITY_LOWER = "ERR_CURRENT_QUANTITY_LOWER";
  public static final String ERR_OUT_OF_STOCK = "ERR_OUT_OF_STOCK";
  public static final String ERR_ADD_TO_CART_FAIL = "ERR_ADD_TO_CART_FAIL";
  public static final String ERR_REMOVE_ITEM_FAIL = "ERR_REMOVE_ITEM_FAIL";
  public static final String ERR_UPDATE_ITEM_QUANTITY_FAIL = "ERR_UPDATE_ITEM_QUANTITY_FAIL";
  public static final String ERR_CLEAR_CART_FAIL = "ERR_CLEAR_CART_FAIL";
  public static final String ERR_CART_NOT_FOUND = "ERR_CART_NOT_FOUND";
  public static final String ERR_ITEM_CART_NOT_FOUND = "ERR_ITEM_CART_NOT_FOUND";

  // CONVERTER
  public static final String ERR_DATA_CONVERT_FAIL = "ERR_DATA_CONVERT_FAIL";

  // BRAND
  public static final String ERR_BRAND_NOT_FOUND = "ERR_BRAND_NOT_FOUND"; 
  

}
