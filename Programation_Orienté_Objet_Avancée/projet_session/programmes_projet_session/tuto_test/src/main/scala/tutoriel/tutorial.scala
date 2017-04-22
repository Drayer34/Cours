package tutoriel

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.Future


  class SimpleActor extends Actor {
    import context._
    implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

    def receive = {
      case s:String => println(self.path+" ; String : "+s)
      case i:Int => println("Int : "+i)
      case AskNameMessage => sender ! "Antoine"
      case _ => context.actorSelection("akka://SimpleSystem/user/SimpleActor2").resolveOne().onComplete {
        case Success(actorRef) => actorRef ! "coucou"
        case Failure(ex) => println("user/" + "somename" + " does not exist")
      }
    }
    
    def foo = println("method foo lel")
  }

object tutorial extends App {


  val system = ActorSystem("SimpleSystem")
  val actor = system.actorOf(Props[SimpleActor], "SimpleActor")
  val actor2 = system.actorOf(Props[SimpleActor], "SimpleActor2")
  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))
  
  actor ! "hey dude" 
  actor ! 42
  actor ! system
  actor2 ! 42
}