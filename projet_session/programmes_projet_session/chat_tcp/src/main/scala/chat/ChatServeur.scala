package chat

import akka.actor.{Actor, Props, ActorSystem, ActorRef, PoisonPill, Terminated}
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory
import java.net.{NetworkInterface, InetAddress}
import scala.collection.JavaConversions._
import scala.collection.convert.Wrappers.JEnumerationWrapper
import scala.collection.mutable.HashMap



object ChatSeveur extends App{
  println("Starting Akka Chat Server Actor")
  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("chatserver"))
  val server = system.actorOf(Props[Server], name = "chatserver")
  server ! StartUp
}

class Server extends Actor {
  val sessions = new HashMap[ActorRef, String]

  def receive={

    case StartUp =>
      println("Demarrage du serveur")

    case DemandeConnection(pseudo) =>{
      sessions += (sender->pseudo)
      context.watch(sender)
      println(pseudo+" s'est connecte !")
      broadcast(Recu(pseudo+" s'est connecte !", "Serveur"), null)
    }
      
    case Terminated(client) => {
      val pseudo:String = sessions.get(client).get
      sessions-=client
      println(pseudo+" s'est deconnecte !")
      broadcast(Recu(pseudo+ " s'est déconnecte", "Serveur"), sender)
    }

    case Deconnection => {
      val pseudo:String = sessions.get(sender).get
      sessions-=sender
      println(pseudo+" s'est deconnecte !")
      broadcast(Recu(pseudo+ " s'est déconnecte", "Serveur"), sender)
    }
              
    case NouveauMessage(message) => {
      if (sessions.contains(sender)){
        println(message)
        broadcast(Recu(message, sessions.get(sender).get), null)
      }
      else{
        sender ! Recu("Rejoignez avec la commande /re", "Serveur")
      }
    }
  }
  
  def broadcast(message:Messages, sender:ActorRef)={
    sessions.foreach {
      case(k,v)=> if (k!=sender) k ! message
      }
  }
}
