package yuan.example.akka.wordCount

import akka.actor._
import akka.routing.RoundRobinRouter

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.collection.mutable.HashMap
import scala.concurrent.duration._

object WcWithAkka extends App{

  //execute this method
  calcultate()

  //all messages
  sealed trait wcMessage
  case class FinalRes(du:Duration,res:String=null) extends wcMessage

  case object Map extends wcMessage
  case class Task(splitedData:Array[String]) extends wcMessage
  case class Reduce(partData:mutable.HashMap[String,Int]) extends wcMessage

  def calcultate(): Unit ={
    //create system actor
    val system = ActorSystem("WordCountwithakka")
    println("begin")

    //create listener
    val listener = system.actorOf(Props[Listener],name = "WCListener")
    println("listener inited")

    //create master
    val master= system.actorOf(Props(new Master(listener,4)),name="WCMaster")
    println("master inited")

    //begin to calculate
    println("begin to calculate")
    master ! Map

  }

  class Listener extends Actor{
    override def receive= {
      case FinalRes(du,res) =>{
        println("\n\tCalculation time: \t%s".format(du))
        println("\n\tRes: \t%s".format(res))
        context.system.shutdown()
      }

    }
  }

  class Master(listener: ActorRef,numWorker : Int=4) extends Actor{
    //original data
    val data = "hello world hello this is my first akka app plz use scala Cause scala is really smart " +
      "I have used it so far in two real projects very successfully. both are in the near real-time traffic " +
      "information field (traffic as in cars on highways), distributed over several nodes, integrating messages"

    val dataArray =  data.split(" ")
    val sumSize = dataArray.size
    val jobPerWork = sumSize/numWorker

    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(numWorker)), name = "workerRouter")

    var totalRes = new mutable.HashMap[String,Int]
    var receivedMsg = 0
    val start: Long = System.currentTimeMillis

    def receive = {
      case Map =>{
        println("begin to map")
        for(i <- 1 to numWorker){
          var endIndex = 0
          if (i ==numWorker){
            endIndex = sumSize-1
          }else{
            endIndex = i*jobPerWork
          }

          val sendData =  dataArray.slice((i-1)*jobPerWork,endIndex)
          println("send worker "+i+" to work")
          workerRouter ! Task(sendData)
        }
      }

      case Reduce(partData) =>{
        println("received one res")
        totalRes = mergeRes(totalRes,partData)

        receivedMsg = receivedMsg + 1
        if (receivedMsg == numWorker){
          println("all job finished")
          listener ! FinalRes(du = (System.currentTimeMillis - start).millis,totalRes.toList.toString())
          // Stops this actor and all its supervised children
          context.stop(self)
        }
      }

    }


    def mergeRes(allData:mutable.HashMap[String,Int],partData:mutable.HashMap[String,Int]):mutable.HashMap[String,Int]={

      partData.foreach(a => {
        val numExisted = allData.getOrElse(a._1,0)
        val newNum = numExisted + a._2
        allData.put(a._1,newNum)
      })
      allData
    }
  }

  class Worker() extends Actor{

    def wordCount(data:Array[String]):HashMap[String,Int]={
      val count = new mutable.HashMap[String,Int]()
      data.foreach(word =>{
        val numExisted = count.getOrElse(word,0)
        val newNum = numExisted + 1
        count.put(word,newNum)
      })

      count
    }

    override def receive= {

      case Task(splitedData) =>{
        println("one to work")
        sender ! Reduce(wordCount(splitedData))
      }

    }
  }





}
