package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.user_defined;

/**
 * Created by lafran on 10/24/16.
 */

public class UserCategory {
  private int id_user_category;
  private int user_id;
  private int parentCategoryId;
  private String name;
  private String color;
  private String colorName;
  private String deleted_at;
  private String updated_at;
  private String created_at;

  private Category parentCategory;

  public UserCategory(){

  }

  public UserCategory(user_defined data) {
    this.id_user_category = data.id;
    this.user_id = data.user_id;
    this.parentCategoryId = data.category_id;
    this.name = data.name;
    this.color = data.color;
    this.colorName = data.color_name;
    this.deleted_at = data.deleted_at;
    this.updated_at = data.updated_at;
    this.created_at = data.created_at;
  }

  public int getId_user_category() {
    return id_user_category;
  }

  public UserCategory setId_user_category(int id_user_category) {
    this.id_user_category = id_user_category;
    return this;
  }

  public int getUser_id() {
    return user_id;
  }

  public UserCategory setUser_id(int user_id) {
    this.user_id = user_id;
    return this;
  }

  public int getParentCategoryId() {
    return parentCategoryId;
  }

  public UserCategory setParentCategoryId(int parentCategoryId) {
    this.parentCategoryId = parentCategoryId;
    return this;
  }

  public String getColor() {
    return color;
  }

  public UserCategory setColor(String color) {
    this.color = color;
    return this;
  }

  public String getColorName() {
    return colorName;
  }

  public UserCategory setColorName(String colorName) {
    this.colorName = colorName;
    return this;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public UserCategory setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public UserCategory setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public String getCreated_at() {
    return created_at;
  }

  public UserCategory setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public String getName() {
    return name;
  }

  public UserCategory setName(String name) {
    this.name = name;
    return this;
  }

  public Category getParentCategory() {
    return parentCategory;
  }

  public UserCategory setParentCategory(Category parentCategory) {
    this.parentCategory = parentCategory;
    return this;
  }
}
