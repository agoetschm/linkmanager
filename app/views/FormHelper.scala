package views

/**
  * Created by agoetschm on 8/10/16.
  */
object FormHelper {

  import views.html.helper.FieldConstructor
  import views.html.inputHelper

  implicit val customFieldHelper = FieldConstructor(inputHelper.f)
}
