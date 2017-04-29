
class ClientException(msg: String) extends Exception(msg)

case class UPickleException(msg: String) extends ClientException(msg)

case class AjaxException(msg: String) extends ClientException(msg)
