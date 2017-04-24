package chat

import akka.actor.Actor
import scala.collection.mutable.HashMap
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.PoisonPill
import akka.actor.Terminated
import akka.actor.actorRef2Scala


abstract class Message
case class DemandeConnection(pseudo:String) extends Message
case class Kick(pseudo:ActorRef)extends Message
case class Envoi(message:String)extends Message
case class Recu(message:String, emetteur:String)extends Message
case class NouveauMessage(message:String) extends Message

case object Deconnection 
case object ConnectionServeur


class Server extends Actor {
	val sessions = new HashMap[ActorRef, String]

		def receive={
			case DemandeConnection(pseudo) =>{
				sessions += (sender()->pseudo)
				context.watch(sender())
				broadcast(Recu(pseudo+" s'est connecté !", "Serveur"))
			}
			
			case Terminated(client) => {
			  val pseudo:String = sessions.get(client).get
			  sessions-=client
			  broadcast(Recu(pseudo+ " s'est déconnecté", "Serveur"))
			}
			  
			case Kick(client) => sessions-=client
			
			case NouveauMessage(message) => broadcast(Recu(message, sessions.get(sender()).get))
	}
	
	def broadcast(message:Message)={
	  sessions.foreach {
	    case(k,v)=> k ! message
	    }
	}
}

class Client(val pseudo:String, val serveurActuel:ActorRef) extends Actor {


		def receive={
			case ConnectionServeur => {
						serveurActuel ! DemandeConnection(pseudo)
			}
			case Deconnection  => self ! PoisonPill
			case Envoi(message) =>
			  serveurActuel ! NouveauMessage(message)
			
			case Recu(msg, emt) => println("[CLient de "+pseudo+"]["+emt+"]" +" : " + msg)
   }
}

object main extends App {

	val systeme = ActorSystem("Chat")
			val serveur = systeme.actorOf(Props[Server], name="ServeurCopain")
			val antoine = systeme.actorOf(Props(new Client("Antoine", serveur)), name="antoine")
			val vincent = systeme.actorOf(Props(new Client("Vincent", serveur)), name="vincent")
			val kevin = systeme.actorOf(Props(new Client("Kevin", serveur)), name="kevin")
			antoine ! ConnectionServeur
			vincent ! ConnectionServeur
			kevin ! ConnectionServeur
			antoine ! Envoi("Salut tous le monde")
			vincent ! Envoi("Bye")
			Thread.sleep(100)
			vincent ! Deconnection
			
			//antoine ! Deconnection
}