package com.simongig.recipesapp.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;


public class UserRole {

   @Id
   @BsonId
   private String id;
   private String name;

   public UserRole() {
   }

   public UserRole(@JsonProperty("name") @NonNull String name) {
      this.id = new ObjectId().toString();
      this.name = name;
   }

   public String getId() {
      return id;
  }

  public void setId(String id) {
      this.id = id;
  }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
