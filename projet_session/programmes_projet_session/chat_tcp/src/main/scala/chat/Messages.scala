package chat

import akka.actor.ActorRef


abstract class Messages

case object Deconnection extends Messages
case object StartUp extends Messages

case class DemandeConnection(pseudo:String) extends Messages
case class Recu(message:String, emetteur:String)extends Messages
case class NouveauMessage(message:String) extends Messages

