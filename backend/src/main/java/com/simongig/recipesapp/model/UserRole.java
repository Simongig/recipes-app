package com.simongig.recipesapp.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;

public class UserRole {

   public enum RoleName {
      ROLE_USER,
      ROLE_ADMIN,
      ROLE_EDITOR
   }

   @Id
   @BsonId
   private String id;
   private RoleName name;

   public UserRole() {
   }

   public UserRole(@JsonProperty("name") @NonNull RoleName name) {
      this.id = new ObjectId().toString();
      this.name = name;
   }

   public String getId() {
      return id;
  }

  public void setId(String id) {
      this.id = id;
  }

   public RoleName getName() {
      return name;
   }

   public void setName(RoleName name) {
      this.name = name;
   }
}
