/*
 * Module : servicesMonitor-api
 * File   : KnowledgeAgent.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

import java.time.Instant

import akka.actor.{ActorRef, PoisonPill, Props, Stash}
import brave.play.ZipkinTraceServiceLike
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
//import org.apache.http.params.HttpConnectionParams
import org.json4s.DefaultFormats
import play.api.libs.json.Json
import scalaj.http.{Http, HttpResponse}

import scala.collection.mutable.HashMap
import scala.language.dynamics

abstract class KnowledgeAgent(override implicit val tracer: ZipkinTraceServiceLike)  extends BaseServiceActor with LazyLogging with Stash  {


  val myURI=self.path.name
  var curConfig:Config=ConfigFactory.empty


  type WorkerSpec=Config
  val managingAgent:ActorRef=self
  var knowledgeObjectSpecs:HashMap[String, Config]= HashMap.empty[String, Config]
  var knowledgeObjects:HashMap[String, KnowledgeObject]=HashMap.empty[String, KnowledgeObject]
  var knowledgeObjectsState:HashMap[String, KnowledgeObjectState]=HashMap.empty[String, KnowledgeObjectState]

  // plugins
  var myPlugIns:HashMap[String, Any]=HashMap.empty[String, Any]
  // these are links of my siblings that I need to communicate directly to them
  var peerLinks:HashMap[String, ActorRef]=HashMap.empty[String, ActorRef]

  var myLinks:HashMap[String, ActorRef]=HashMap.empty[String, ActorRef]//???

  private var trackChangeHistory:Boolean = false
  private var trackUsageHistory:Boolean = false
  private var trackPerformance:Boolean= false

  private var workerSpecs:HashMap[String, WorkerSpec]= HashMap.empty[String, WorkerSpec]

  val workerAgents:HashMap[String, ActorRef]=HashMap.empty[String, ActorRef]

  private var mgmtAgentLinks:HashMap[String, ActorRef]=HashMap.empty[String, ActorRef]

  val mycontext = context

  override def preStart(): Unit = ()
  override def postStop(): Unit = ()
  //TODO private ckp
  def  initializeWorkers() = {
    try {
      createWorkerAgents()
      sendInitializeToWorkerAgents()
    }catch {
      case ex:Exception =>
         ex.printStackTrace()
    }
  }


   def sendInitializeToWorkerAgents() = {
    println("sendInitializeToWorkerAgents ",curConfig )
    if (curConfig.hasPath("workers")) {

      val configlist = curConfig.getConfigList("workers")
      //logger.info("in ", myURI,configlist )
      if ( configlist.size() > 0) {
        var itr = configlist.iterator()
        while (itr.hasNext) {
          val workerElement = itr.next()
          if (workerElement.hasPath("name")) {
            val name: String = workerElement.getString("name")
            workerSpecs(name) = workerElement
          }
        }
      }
    }

    for( workerUID <- workerAgents.keys){

      // get name from workerUID
      val name=   UIDGenerator.getNameFromUID(workerUID)

      if (workerSpecs.contains(name)) {
        val workerSpec = workerSpecs(name).asInstanceOf[Config]



        var configPath = ""

        if (workerSpec.hasPath("config")) {

          configPath = workerSpec.getString("config")

        }

        val links: HashMap[String, ActorRef] = HashMap.empty[String, ActorRef]

        if (workerSpec.hasPath("peers")) {
          val peers = workerSpec.getConfigList("peers")
          if (peers.size() > 0) {
            var itr = peers.iterator()
            while (itr.hasNext) {
              val peerElement = itr.next()
              if (peerElement.hasPath("name")) {
                val name: String = peerElement.getString("name")
                val peerUID = URIGenerator.get("worker", name)
                if (workerAgents.contains(peerUID))
                  links(peerUID) = workerAgents(peerUID) //assumption peers are within the siblings
                if (peerLinks.contains(peerUID))
                  links(peerUID) = peerLinks(peerUID) // or from ancestor links
              }
            }
          }
        }
println("Sending ",configPath," To worker ",workerUID)
        workerAgents(workerUID) ! BaseInitialize(configPath, links)
      }
    }
  }



  private def getWorker(name:String):Option[ActorRef] ={
    if(workerAgents.contains(name)) Option(workerAgents(name)) else None
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    context.children foreach {
      child ⇒
        context.unwatch(child)
        context.stop(child)
    }
    postStop()
  }
  override def postRestart(reason: Throwable): Unit = {
    preStart()
  }

  context.become(initialState)

  def preInitialize(): Unit ={

  }

  def preShutdown():Unit={
    // can be overridden
  }


  def shutdownWorkers() ={
    for(worker <-  workerAgents.values) {
      //   worker! Shutdown
    }
  }

  def shutdownSelf() ={
    preShutdown()
    self ! PoisonPill

  }


  private def switchState()={
    postInitialize()
    context.become(runningState)
    unstashAll()
  }

  def postInitialize()={
    //  logger.info("In knowledgeagent post initialize")
    //Globals.trace.record(childmsg1, "post initialize    ")
  }

  def createWorkerAgents()={
    println("CreateWorkerAgents" +curConfig)
    val configlist = curConfig.getConfigList("workers")
    println(configlist)
    if(!configlist.isEmpty) {
      var itr=configlist.iterator()
      while (itr.hasNext) {

        var workerElement = itr.next()
        // must have name
        var name:String=""
        if (!workerElement.hasPath("name"))
          logger.info("No name in ",workerElement)
        else {
          name = workerElement.getString("name")
          //must have provider
          var provider:String = workerElement.getString("provider")
          var instance="single"
          if(workerElement.hasPath("instance")){
            //TODO      configPath=workerElement.getString("instance")
          }

          instance match {
            case "single" => // create agent
              val uid = URIGenerator.get("worker", name)
              println("in Single  ",provider)
              println(" uid ",uid)

              if (!workerAgents.contains(uid)){

                println("Starting ",provider,uid)
            //    val workerAgent = context.actorOf(Props(Class.forName("com.diwo.test.DataAggregatorActor"),tracer),name="TestChildActor")


                val  workerAgent = context.actorOf(Props(Class.forName(provider),tracer), name = uid)
                println("Started ",provider,uid)
                workerAgents(uid) = workerAgent
              }

            case "multiple" =>
              // generate List of UID's
              var urischeme = "cuboidid"
              if (workerElement.hasPath("urischeme")) {
                urischeme = workerElement.getString("urischeme")
              }
              val uidList = URIGenerator.getList("worker", name, urischeme)
              for (uid <- uidList) {
                val workerAgent = context.actorOf(Props(Class.forName(provider)), name = uid)
                workerAgents(uid) = workerAgent
              }
          }

        }
      }
    }
  }
  private var koIndex:HashMap[String, KnowledgeObject]=HashMap.empty[String, KnowledgeObject]

  //private def initializeKnowledgeObjects()={
  //TODO ckp
  def initializeKnowledgeObjects()={

    val configlist = curConfig.getConfigList("knowledge")
    println(configlist)
    var itr=configlist.iterator()
    while (itr.hasNext) {
      val knowledgeElement = itr.next().asInstanceOf[Config]
      val name:String = knowledgeElement.getString("name")

      knowledgeObjectSpecs(name) = knowledgeElement
      val uri= URIGenerator.get("object", name)
      knowledgeObjectsState(uri) =  new KnowledgeObjectState("na")
    println("initializeKnowledgeObjects " ,uri,name)
    }

    //InitializeKnowledgeObjects(koSpecs:HashMap[String, Config])=>{
    val koSpecs = knowledgeObjectSpecs

    var responseObjects:HashMap[String, KnowledgeObject]=HashMap.empty[String, KnowledgeObject]
    var koRefs : HashMap[String,KnowledgeObject]= HashMap.empty[String,KnowledgeObject]

    var pendingKoInitializations=0
println("knowledgeObjectSpecs ",knowledgeObjectSpecs)

    for (specKey <- koSpecs.keys) {
      var spec: Config = koSpecs(specKey)
      val name = spec.getString("name")
      val uri = URIGenerator.get("object", name)
      //see if the knowledge object already exists
      if (koIndex.contains(uri)) {
        responseObjects(uri) = koIndex(uri)
      } else {
        // create an instance of the knowledge Object
        pendingKoInitializations += 1

        if (spec.hasPath("metaData")) {
          var configName  = spec.getString("metaData")
          var instanceId  = spec.getString("instanceId")
          var metaDataObj = getKnowledgeObject( configName,instanceId )
          println(metaDataObj.ko_id)
          if ( metaDataObj.ko_id.length > 0) {
            koIndex(uri) = metaDataObj
            koIndex(uri).setContent (metaDataObj)
            responseObjects(uri) = metaDataObj
          }
        }

      }
      //   logger.info("Sending to ",sender().path.name," KnowledgeObjectsReady ")
      self ! KnowledgeObjectsReady(responseObjects)
    }
    if (koSpecs.size == 0){
      if (  curConfig.hasPath("workers")){
        println("All knowledge objects are ready now starting workers")
        initializeWorkers()
      }
      switchState()
    }
  }

  private def getKnowledgeObject (configName:String, instanceId:String ):  KnowledgeObject ={
    implicit val formats: DefaultFormats = DefaultFormats
    var unixTimestamp: Long = Instant.now.getEpochSecond
    val monitorService = ConfigFactory.load().getString("KMMService")
    var requestBody = Json.parse("{\"serviceName\":\"SchedulerService\",\"commandName\": \"getMetadata\", \"configName\":\"" + configName + "\", \"instanceId\":\"" + instanceId  +"\" }")
    var requestDirective = Json.parse("{\"requestId\":\"12345\", \"requestor\": \"HIM\", \"requestTS\": 1560344948 }")
    var responseDirective = Json.parse("{\"response\":\"default\" }")
    var req = ServiceRequest(requestDirective, requestBody, requestDirective)

    var jsonString = Json.toJson(req).toString()
    println("getKnolwedgeObject Request",jsonString)


    var response: HttpResponse[String] = Http(monitorService).postData(jsonString.toString).timeout(connTimeoutMs = 2000, readTimeoutMs = 10000).header("content-type", "application/json").asString
    var mo: MetaDataObject = new MetaDataObject

    if (response.isSuccess) {
      println(response.body.toString)
      // check for empty
      if (! response.body.toString.equals("[]")) {
        mo.addKO(configName, Json.parse(response.body))
      }
      else {
        println("metadata "+configName + " not found")
      }
    }
    mo
  }
  private def insertPlugIns()={
    val pluglist = curConfig.getConfigList("plugins")
    var itr=pluglist.iterator()
    while (itr.hasNext) {
      val plugElement = itr.next()
      val name:String = plugElement.getString("name")
      val provider:String=plugElement.getString("provider")
      myPlugIns(name) =  Class.forName(provider)
    }
  }


  override def receive: Receive = initialState

  def initialState:Receive = {

    case BaseInitialize(configpath:String, links:HashMap[String, ActorRef]) => {
      stash()

      if(configpath != ""){
        try {
          curConfig = ConfigFactory.parseResources(configpath) //ConfigFactory.load().getConfig(configpath)
        }
        catch {
          case ex: Exception =>
            println(ex.getMessage)
        }
      }

      peerLinks=links

      // hook for adding extra initialization logic
      preInitialize()

      if(!curConfig.isEmpty){
        if(curConfig.hasPath("plugins")){
          insertPlugIns()
        }

        if(curConfig.hasPath("knowledge")){
          println(" calling initializeKnowledgeObjects")
          initializeKnowledgeObjects()
        } else if(curConfig.hasPath("workers")) {
          println(" calling initializeWorkers")
          initializeWorkers()
          switchState()
        }

      }
    }

    case KnowledgeObjectsReady(koRefs:HashMap[String, KnowledgeObject]) =>{
      // initialize Knowledge References
      // all knowledge objects must be initialized, before workers are created
      println(myURI+" in KnowledgeObjects ready")

      for( key <- koRefs.keys ){
        println(myURI+" in KnowledgeObjects "+key + " ready")

        // update state
        //          knowledgeObjectsState key
        // update reference
        knowledgeObjects(key) = koRefs(key)
        knowledgeObjectsState(key) =  new KnowledgeObjectState("ready")
      }

      var notAvailable= knowledgeObjectsState.size

      for( k <-  knowledgeObjectsState ) {
        if (! k._2.asInstanceOf[KnowledgeObjectState].getState().equals("na"))
          notAvailable = notAvailable -1
      }
      // logger.info(myURI+" in KnowledgeObjects "+notAvailable)


      //all knowledge objects are ready , we can now initialize the workers and switch state.
      if( (notAvailable == 0) && ( curConfig.hasPath("workers") )){
        println("All knowledge objects are ready now starting workers")
        initializeWorkers()
        switchState()
      }else{
        println( notAvailable + "  number of  objects are not ready to start workers")
      }
    }

    case x => if (extendedInitialState.isDefinedAt(x)) extendedInitialState(x)

    case _=> logger.info("Unknown message in KnowledgeAgent")
      stash()


  }
  var extendedInitialState: Receive={
    case _ =>      //Globals.trace.record(childmsg1, "received msg   ")
  }
  var extendedRunningState: Receive={
    case ForwardToWorker(name,msgtoforward)=>
      logger.info(" ForwardToWorker")
      val uid = URIGenerator.get("worker", name)
      workerAgents(uid) ! msgtoforward
    case _ =>
  }



  def runningState:Receive={

    case ForwardToWorker(name,msgtoforward)=>
      logger.info(" ForwardToWorker")
      val uid = URIGenerator.get("worker", name)
      workerAgents(uid) ! msgtoforward


    case x => if (extendedRunningState.isDefinedAt(x)) extendedRunningState(x)

  }


}
