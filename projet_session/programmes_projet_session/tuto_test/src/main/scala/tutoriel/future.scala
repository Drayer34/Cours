package tutoriel

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

import scala.util.Failure
import scala.util.Success
import java.util.concurrent.TimeUnit
import scala.io.StdIn

case object AskNameMessage

class TestActor extends Actor {
  import context._
  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))
  
	def receive = {
	  case AskNameMessage => sender ! "Antoine"
	  case _ => println("Eh... Don't know what to do !")
	}
}

object future extends App {
	// create the system and actor
	val system = ActorSystem("AskTestSystem")
	val myActor = system.actorOf(Props[TestActor], name = "myActor")

	// (1) this is one way to "ask" another actor
	implicit val timeout = Timeout(5 seconds)
	val future = myActor ? AskNameMessage
	val result = Await.result(future, timeout.duration).asInstanceOf[String]
	println(result)

	// (2) this is a slightly different way to ask another actor
	val future2: Future[String] = ask(myActor, AskNameMessage).mapTo[String]
	val result2 = Await.result(future2, 1 second)
	println(result2)

	system.terminate()
	
}