package com.ecommerce.gut.payload.response;

public class SuccessCode {
  
  // AUTH
  public static final String USER_SIGNUP_SUCCESS = "USER_SIGNUP_SUCCESS";
  public static final String USER_LOGIN_SUCCESS = "USER_LOGIN_SUCCESS";

  // CATEGORY
  public static final String CATEGORY_CREATED_SUCCESS = "CATEGORY_CREATED_SUCCESS";
  public static final String CATEGORY_UPDATED_SUCCESS = "CATEGORY_UPDATED_SUCCESS";
  public static final String CATEGORY_DELETED_SUCCESS = "CATEGORY_DELETED_SUCCESS";
  public static final String CATEGORY_PARENT_CREATED_SUCCESS = "CATEGORY_PARENT_CREATED_SUCCESS";
  public static final String CATEGORY_PARENT_UPDATED_SUCCESS = "CATEGORY_PARENT_UPDATED_SUCCESS";
  public static final String CATEGORY_PARENT_DELETED_SUCCESS = "CATEGORY_PARENT_DELETED_SUCCESS";
  public static final String CATEGORY_PARENT_LOADED_SUCCESS = "CATEGORY_PARENT_LOADED_SUCCESS";
  public static final String CATEGORY_LOADED_SUCCESS = "CATEGORY_LOADED_SUCCESS";

  // COLOR
  public static final String COLOR_CREATED_SUCCESS = "COLOR_CREATED_SUCCESS";
  public static final String COLOR_UPDATED_SUCCESS = "COLOR_UPDATED_SUCCESS";
  public static final String COLOR_DELETED_SUCCESS = "COLOR_DELETED_SUCCESS";
  public static final String COLOR_LOADED_SUCCESS = "ERR_COLOR_LOADED_SUCCESS";

  // PRODUCT
  public static final String PRODUCT_CREATED_SUCCESS = "PRODUCT_CREATED_SUCCESS";
  public static final String PRODUCT_DELETED_SUCCESS = "PRODUCT_DELETED_SUCCESS";
  public static final String PRODUCT_UPDATED_SUCCESS = "PRODUCT_UPDATED_SUCCESS";
  public static final String PRODUCT_IMAGES_DELETED_SUCCESS = "PRODUCT_IMAGES_DELETED_SUCCESS";
  public static final String PRODUCT_IMAGES_UPDATED_SUCCESS = "PRODUCT_IMAGES_UPDATED_SUCCESS";
  public static final String PRODUCT_REVIEW_ADDED_SUCCESS = "PRODUCT_REVIEW_ADDED_SUCCESS";
  public static final String PRODUCT_LOADED_SUCCESS = "PRODUCT_LOADED_SUCCESS";
  public static final String PRODUCTS_LOADED_SUCCESS = "PRODUCTS_LOADED_SUCCESS";
  
  // USER
  public static final String USER_PROFILE_EDITED_SUCCESS = "USER_PROFILE_EDITED_SUCCESS";
  public static final String CURRENT_USER_PROFILE_EDITED_SUCCESS = "CURRENT_USER_PROFILE_EDITED_SUCCESS";
  public static final String USER_DELETED_SUCCESS = "USER_DELETED_SUCCESS";
  public static final String USER_DEACTIVATED_SUCCESS = "USER_DEACTIVATED_SUCCESS";
  public static final String USER_ACTIVATED_SUCCESS = "USER_ACTIVATED_SUCCESS";
  public static final String USER_ROLES_CHANGED_SUCCESS = "USER_ROLES_CHANGED_SUCCESS";
  public static final String USER_LOADED_SUCCESS = "USER_LOADED_SUCCESS";

  // CART
  public static final String ADD_TO_CART_SUCCESS = "ADD_TO_CART_SUCCESS";
  public static final String REMOVE_ITEM_SUCCESS = "REMOVE_ITEM_SUCCESS";
  public static final String UPDATE_ITEM_QUANTITY_SUCCESS = "UPDATE_ITEM_QUANTITY_SUCCESS";
  public static final String CLEAR_CART_SUCCESS = "CLEAR_CART_SUCCESS";
  public static final String LOAD_CART_SUCCESS = "LOAD_CART_SUCCESS";

}
