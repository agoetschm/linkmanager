package models

/**
  * User data used to log in
  */
case class UserLogInData(username: String, password: String)


/**
  * User data used to sign up
  */
case class UserSignUpData(username: String, email: String, password: String)
