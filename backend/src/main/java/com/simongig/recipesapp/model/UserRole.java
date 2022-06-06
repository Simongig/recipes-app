package com.simongig.recipesapp.model;

import org.springframework.data.annotation.Id;

public class UserRole {

   @Id
   private String name;

   public UserRole() {
   }

   public UserRole(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
