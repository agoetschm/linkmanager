package models

import com.mohiva.play.silhouette.api.Identity

/**
  * User model
  */
case class User(id: Long, username: String, email: String, activated: Boolean) extends Identity





