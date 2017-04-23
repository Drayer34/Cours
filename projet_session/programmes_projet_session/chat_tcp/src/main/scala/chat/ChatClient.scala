package chat

import akka.actor.PoisonPill
import akka.actor._
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory
import java.net.{NetworkInterface, InetAddress}
import scala.collection.JavaConversions._
import akka.actor.Props
import scala.collection.convert.Wrappers.JEnumerationWrapper
import scala.io.StdIn





object ChatClient {
  
  def main(args:Array[String]) {

    val username = StdIn.readLine("sasir username : ")
    
    val clientConfig = ConfigFactory.load.getConfig("chatclient")
    val defaultConfig = ConfigFactory.load.getConfig("chatclient")
    val completeConfig = clientConfig.withFallback(defaultConfig)

    
    val system = ActorSystem("chat", completeConfig)

    
    val serverconfig = ConfigFactory.load.getConfig("chatserver")
    val serverAddress = serverconfig.getString("akka.remote.netty.tcp.hostname")
    val serverPort = serverconfig.getString("akka.remote.netty.tcp.port")
    val serverPath = s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver"
    val serveur = system.actorSelection(serverPath)

    
    println("/re pour rejoindre, /deco pour se déconnecter et /exit pour partir")
    var client = system.actorOf(Props(new Client(username)), name=username)
    
     Iterator.continually(StdIn.readLine()).takeWhile(_ != "/exit").foreach { msg =>
      msg match {

        case "/re" => 
          serveur.tell(DemandeConnection(username), client)

        case "/deco" =>
          serveur.tell(Deconnection, client)

          //client ! Deconnection 
          
        case _ => 
          serveur.tell(NouveauMessage(msg), client)
      }
    }

    client ! PoisonPill
    system.terminate()
    
  }
  
  class Client(val pseudo:String) extends Actor {

	  def receive={
			case Recu(msg, emt) => println("["+emt+"]" +" : " + msg)

			//case Deconnection => self ! PoisonPill
    }
  }
}