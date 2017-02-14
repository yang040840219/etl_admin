import org.scalatest._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

/**
  * Created by yxl on 17/1/23.
  */


case class Resident(name: String, age: Int, role: Option[String])
case class Location(lat: Double, long: Double)
case class Place(name: String, location: Location, residents: Seq[Resident])

class JsonSpec  extends FlatSpec with Matchers with
  OptionValues with Inside with Inspectors {

  behavior of "play json lib"


  //implicit val residentFormat = Json.format[Resident]


  it should "test1 reads writes with macro" in {

    // macro 方式
    implicit val residentReads = Json.reads[Resident]
    implicit val residentWrites = Json.writes[Resident]

    val resident = Resident(name="Fiver", age=4, role=None)
    val residentJson: JsValue = Json.toJson(resident)

    println(residentJson)

  }

  it should "test2 reads writes with manual" in {

    implicit val locationReads: Reads[Resident] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "age").read[Int] and
        (JsPath \ "role").readNullable[String]
      )(Resident.apply _)


    val jsonString: JsValue = Json.parse(
      """{
        "name" : "Fiver",
        "age" : 4
      }""")

    val residentFromJson: JsResult[Resident] = Json.fromJson[Resident](jsonString)

    residentFromJson match {
      case JsSuccess(r: Resident, path: JsPath) => println("Name: " + r.name)
      case e: JsError => println("Errors: " + JsError.toJson(e).toString())
    }

  }

  it should "test3 string parser" in {

    val json: JsValue = Json.parse("""
                  {
                    "name" : "Watership Down",
                    "location" : {
                      "lat" : 51.235685,
                      "long" : -1.309197
                    },
                    "residents" : [ {
                      "name" : "Fiver",
                      "age" : 4,
                      "role" : null
                    }, {
                      "name" : "Bigwig",
                      "age" : 6,
                      "role" : "Owsla"
                    } ]
                    }
                  """)

    println(json)

  }

  it should "test4 class construction" in {
    val json: JsValue = JsObject(Seq(
      "name" -> JsString("Watership Down"),
      "location" -> JsObject(Seq("lat" -> JsNumber(51.235685), "long" -> JsNumber(-1.309197))),
      "residents" -> JsArray(Seq(
        JsObject(Seq(
          "name" -> JsString("Fiver"),
          "age" -> JsNumber(4),
          "role" -> JsNull
        )),
        JsObject(Seq(
          "name" -> JsString("Bigwig"),
          "age" -> JsNumber(6),
          "role" -> JsString("Owsla")
        ))
      ))
    ))
    println("json:" + json)
    (JsPath \ "name")(json) should equal(Seq(JsString("Watership Down")))
  }

  it should "test5 class construction simple" in {
    val json: JsValue = Json.obj(
      "name" -> "Watership Down",
      "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
      "residents" -> Json.arr(
        Json.obj(
          "name" -> "Fiver",
          "age" -> 4,
          "role" -> JsNull
        ),
        Json.obj(
          "name" -> "Bigwig",
          "age" -> 6,
          "role" -> "Owsla"
        )
      )
    )

    (JsPath \ "name")(json) should equal(Seq(JsString("Watership Down")))
  }

  it should "test6 parse implicit writes" in {

    implicit val locationWrites = new Writes[Location] {
      def writes(location: Location) = Json.obj(
        "lat" -> location.lat,
        "long" -> location.long
      )
    }

    implicit val residentWrites = new Writes[Resident] {
      def writes(resident: Resident) = Json.obj(
        "name" -> resident.name,
        "age" -> resident.age,
        "role" -> resident.role
      )
    }

    implicit val placeWrites = new Writes[Place] {
      def writes(place: Place) = Json.obj(
        "name" -> place.name,
        "location" -> place.location ,
        "residents" -> place.residents // 隐式转换
      )
    }

    val place = Place(
      "Watership Down",
      Location(51.235685, -1.309197),
      Seq(
        Resident("Fiver", 4, None),
        Resident("Bigwig", 6, Some("Owsla"))
      )
    )
    val json = Json.toJson(place)
    println(json)

  }

  it should "test7 parse with case class" in {

    implicit val locationWrites: Writes[Location] = (
      (JsPath \ "lat").write[Double] and
        (JsPath \ "long").write[Double]
      )(unlift(Location.unapply))

    implicit val residentWrites: Writes[Resident] = (
      (JsPath \ "name").write[String] and
        (JsPath \ "age").write[Int] and
        (JsPath \ "role").writeNullable[String]
      )(unlift(Resident.unapply))

    implicit val placeWrites: Writes[Place] = (
      (JsPath \ "name").write[String] and
        (JsPath \ "location").write[Location] and
        (JsPath \ "residents").write[Seq[Resident]]
      )(unlift(Place.unapply))

    val place = Place(
      "Watership Down",
      Location(51.235685, -1.309197),
      Seq(
        Resident("Fiver", 4, None),
        Resident("Bigwig", 6, Some("Owsla"))
      )
    )
    val json = Json.toJson(place)
    println(json)
  }

  it should "test8 traversing json structure" in {

    val json: JsValue = Json.parse("""
                  {
                    "name" : "Watership Down",
                    "location" : {
                      "lat" : 51.235685,
                      "long" : -1.309197
                    },
                    "residents" : [ {
                      "name" : "Fiver",
                      "age" : 4,
                      "role" : null
                    }, {
                      "name" : "Bigwig",
                      "age" : 6,
                      "role" : "Owsla"
                    } ]
                    }
                  """)
    val lat = (json \ "location" \ "lat").get
    val readableString: String = Json.prettyPrint(json)
    println(readableString)
    lat should equal(JsNumber(51.235685))
  }

  it should "test9 complex reads" in {

    // 分成两个
    val locationReadsBuilder =
      (JsPath \ "lat").read[Double].and((JsPath \ "long").read[Double])

    implicit def locationReads = locationReadsBuilder.apply(Location.apply _)

    val json:JsValue = Json.parse("""{"lat" : 51.235685, "long" : -1.309197}""")

    val jsResult = Json.fromJson[Location](json)

    jsResult match {
      case  JsSuccess(location:Location,path:JsPath) => {
           println(location)
      }
      case e:JsError => println(e)
    }

  }

  it should "test10 complex writes" in {
    implicit val locationWrites: Writes[Location] = (
      (JsPath \ "lat").write[Double] and
        (JsPath \ "long").write[Double]
      )(unlift(Location.unapply))

    val location = Location(51.235685,-1.309197)

    val jsValue = Json.toJson(location)

    println(Json.prettyPrint(jsValue))


  }
}
