package models

/**
  * Model for a password (the password isn't in the user model because a user 
  * can authenticate by an other mean and therefore doesn't need one).
  */
case class Password(userId: Long, password: String)


