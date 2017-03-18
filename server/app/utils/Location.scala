package utils

/**
  * Represents a location on the web app
  */
abstract class Location

case object LogIn extends Location

case object SignUp extends Location

case object Home extends Location

case object ActivateAccount extends Location
