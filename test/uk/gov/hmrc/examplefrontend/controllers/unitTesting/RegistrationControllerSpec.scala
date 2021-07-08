/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.examplefrontend.controllers.unitTesting

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, session, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.examplefrontend.Connector.RegistrationConnector
import uk.gov.hmrc.examplefrontend.config.ErrorHandler
import uk.gov.hmrc.examplefrontend.controllers.RegistrationController
import uk.gov.hmrc.examplefrontend.model.{Client, User}
import uk.gov.hmrc.examplefrontend.views.html._

import scala.concurrent.Future


class RegistrationControllerSpec extends AbstractTest {


  private val fakeRequestSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = "/summary")
    .withSession(
      "name" -> "jake",
      "businessName" -> "jakeBusiness",
      "contactNumber" -> "000",
      "property" -> "postcode/propertyNumber",
      "businessType" -> "testbusinessType",
      "password" -> "testpassword")
  private val fakeRequestSubmitSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = "/summary-submit")
  private val fakeRequestName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = "/name-input")
  private val fakeRequestSubmitProperty: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = "/property-input")
    .withSession(
      "property" -> "postcode/propertyNumber")
  private val fakeRequestSubmitName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = "/name-input").withSession("name" -> "jake")
  private val fakeRequestSubmitBusinessName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "POST",
    path = "/business-input")


  val name: NameInputPage = app.injector.instanceOf(classOf[NameInputPage])
  val businessName: BusinessNameInputPage = app.injector.instanceOf(classOf[BusinessNameInputPage])
  val contactNumber: ContactNumberInputPage = app.injector.instanceOf(classOf[ContactNumberInputPage])
  val property: PropertyInputPage = app.injector.instanceOf(classOf[PropertyInputPage])
  val businessType: BusinessTypeInputPage = app.injector.instanceOf(classOf[BusinessTypeInputPage])
  val password: PasswordInputPage = app.injector.instanceOf(classOf[PasswordInputPage])
  val result: ResultPage = app.injector.instanceOf(classOf[ResultPage])
  val crn: CRNPage = app.injector.instanceOf(classOf[CRNPage])
  val error: ErrorHandler = app.injector.instanceOf(classOf[ErrorHandler])
  val connector: RegistrationConnector = mock(classOf[RegistrationConnector])

  private val controller = new RegistrationController(
    mcc = Helpers.stubMessagesControllerComponents(),

    nameInputPage = name,
    businessNameInputPage = businessName,
    contactNumberInputPage = contactNumber,
    propertyInputPage = property,
    businessTypeInputPage = businessType,
    passwordInputPage = password,
    resultPage = result,
    registrationConnector = connector,
    crnPage = crn,
    error = error
  )

  private val user: User = User(
    name = "TestFullName",
    businessName = "TestNameOfBusiness",
    contactNumber = "TestContactNumber",
    propertyNumber = "TestPropertyNumber",
    postcode = "TestAddress",
    businessType = "TestPostCode",
    password = "TestPassword")
  private val client: Client = Client(
    crn = "CRN",
    name = "TestFullName",
    businessName = "TestNameOfBusiness",
    contactNumber = "TestContactNumber",
    propertyNumber = 10,
    postcode = "TestAddress",
    businessType = "TestPostCode",
    arn = Option("Arn"))

  "GET /NameInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputName(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputName(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputName(fakeRequestName.withSession("crn" -> "CRNTEST"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputName(fakeRequestName.withSession("name" -> "testName"))
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("name").`val` shouldBe "testName"
    }
  }

  "GET/BusinessNameInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputBusinessName(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputBusinessName(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("BusinessNameValue")
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputBusinessName(fakeRequestName.withSession("crn" -> "CRNTEST"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputBusinessName(fakeRequestName.withSession("businessName" -> "testName"))
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("business-name").`val` shouldBe "testName"
    }
  }

  "GET/ContactInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputContactNumber(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputContactNumber(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("ContactNumberValue")
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputContactNumber(fakeRequestName.withSession("crn" -> "CRNTEST"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputContactNumber(fakeRequestName.withSession("contactNumber" -> "01000000000"))
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("contact-number").`val` shouldBe "01000000000"
    }
  }

  "GET/BusinessType" should {
    "return 200" in {
      val result: Future[Result] = controller.InputBusinessType(fakeRequestName)
      status(result) shouldBe 200
    }

    "return html" in {
      val result: Future[Result] = controller.InputBusinessType(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("businessType1")
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputBusinessType(fakeRequestName.withSession("crn" -> "CRNTEST"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputBusinessType(fakeRequestName.withSession("businessType" -> "business-other"))
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("business-other").`val` shouldBe "Other"
    }
  }

  "GET/PropertyNumberInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("PropertyNumberValue")
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName.withSession("crn" -> "CRNTEST"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName.withSession("property" -> "404/test"))
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("property-number").`val` shouldBe "404"
      doc.getElementById("postcode").`val` shouldBe "test"
    }
  }

  "GET/PasswordInput" should {
    "return 200" in {
      val result = controller.InputPassword(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputPassword(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("PasswordValue")
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputPassword(fakeRequestName.withSession("crn" -> "CRNTEST"))
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "GET/Summary" should {
    "return 200" in {
      val result: Future[Result] = controller.Summary(fakeRequestSummary)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.Summary(fakeRequestSummary)
      contentType(result) shouldBe Some("text/html")
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("NameValue")
      doc.getElementById("BusinessNameValue")
    }
    "return redirect home" in {
      val result: Future[Result] = controller.Summary(fakeRequestSummary.withSession("crn" -> "CRNTEST"))
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "Redirect home page" should {
    "return 303" in {
      val result: Future[Result] = controller.home(fakeRequestSummary)
      status(result) shouldBe 303
    }
  }

  "Redirect Dashboard" should {
    "return 303" in {
      val result: Future[Result] = controller.dashboard(fakeRequestName)
      status(result) shouldBe 303
    }
  }


  "POST/SubmitNameInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withSession("crn" -> "CRNTEST", "name" -> "Jake"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody("name" -> "Jake"))
      status(result) shouldBe 303
    }
    "return html" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody("name" -> "Jake"))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("BusinessNameValue")
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody("name" -> ""))
      status(result) shouldBe 400
    }
  }

  "POST/SubmitBusinessNameInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withSession("crn" -> "CRNTEST", "businessName" -> "Jake"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withFormUrlEncodedBody("businessName" -> "Jake"))
      status(result) shouldBe 303
    }
    "return html" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withFormUrlEncodedBody("businessName" -> "Jake"))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("BusinessNameValue")
      session(result).get("businessName").getOrElse("") shouldBe "Jake"
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitName.withFormUrlEncodedBody("businessName" -> ""))
      status(result) shouldBe 400
    }
  }

  "POST/SubmitContactInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withSession("crn" -> "CRNTEST", "contactNumber" -> "01895635898"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody("contactNumber" -> "01895635898"))
      status(result) shouldBe 303
    }
    "return html" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody("contactNumber" -> "01895635898"))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("PropertyNumberValue")
      session(result).get("contactNumber").getOrElse("") shouldBe "01895635898"

    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody("contactNumber" -> ""))
      status(result) shouldBe 400
    }
  }

  "POST/SubmitPropertyInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputProperty(fakeRequestSubmitProperty.withSession("crn" -> "CRNTEST", "propertyNumber" -> "10", "postcode" -> "London"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputProperty(fakeRequestSubmitProperty.withFormUrlEncodedBody("propertyNumber" -> "10", "postcode" -> "London"))
      status(result) shouldBe 303
    }
    "return html" in {
      val result = controller.SubmitInputProperty(fakeRequestSubmitProperty.withFormUrlEncodedBody("propertyNumber" -> "10", "postcode" -> "London"))
      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementById("businessType1")
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputProperty(fakeRequestSubmitName.withFormUrlEncodedBody("propertyNumber" -> "", "postcode" -> ""))
      status(result) shouldBe 400
    }
  }

  "POST/SubmitInputBusinessType" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputBusinessType(fakeRequestSubmitProperty.withSession("crn" -> "CRNTEST", "businessType" -> "other"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputBusinessType(fakeRequestSubmitProperty.withFormUrlEncodedBody("businessType" -> "other"))
      status(result) shouldBe 303
    }
    "return html" in {
      val result = controller.SubmitInputBusinessType(fakeRequestSubmitProperty.withFormUrlEncodedBody("businessType" -> "other"))
      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementById("password")
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputBusinessType(fakeRequestSubmitName.withFormUrlEncodedBody())
      status(result) shouldBe 400
    }
  }

  "POST/SubmitPasswordInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputPassword(fakeRequestSubmitProperty.withSession("crn" -> "CRNTEST", "password" -> "pass", "passwordCheck" -> "pass"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody("password" -> "pass", "passwordCheck" -> "pass"))
      status(result) shouldBe 303
    }
    "return html" in {
      val result = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody("password" -> "pass", "passwordCheck" -> "pass"))
      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementById("NameValue")
      session(result).get("password").getOrElse("") shouldBe "pass"
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody("password" -> "", "passwordCheck" -> "pass"))
      status(result) shouldBe 400
    }
    "return Bad Request with missmatch password" in {
      val result: Future[Result] = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody("password" -> "badpass", "passwordCheck" -> "pass"))
      status(result) shouldBe 400
    }
  }

  "POST/SubmitSummary" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitName.withSession("crn" -> "CRNTEST", "name" -> user.name, "businessName" -> user.businessName, "contactNumber" -> user.contactNumber, "businessType" -> user.businessType, "password" -> user.password, "property" -> (user.propertyNumber + "/" + user.postcode)))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      when(connector.create(any())) thenReturn Future.successful(Some(client))
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitSummary.withSession("name" -> user.name, "businessName" -> user.businessName, "contactNumber" -> user.contactNumber, "businessType" -> user.businessType, "password" -> user.password, "property" -> (user.propertyNumber + "/" + user.postcode)))
      status(result) shouldBe 200
    }
    "return Bad Request" in {
      when(connector.create(any())) thenReturn Future.successful(None)
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitSummary.withSession("name" -> user.name, "businessName" -> user.businessName, "contactNumber" -> user.contactNumber, "businessType" -> user.businessType, "password" -> user.password, "property" -> (user.propertyNumber + "/" + user.postcode)))
      status(result) shouldBe 400
    }
  }

  "Error Handler" should {
    "catch unknown response" in {
      when(connector.create(any())) thenReturn Future.failed(new RuntimeException(""))
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitSummary.withSession("name" -> user.name, "businessName" -> user.businessName, "contactNumber" -> user.contactNumber, "businessType" -> user.businessType, "password" -> user.password, "property" -> (user.propertyNumber + "/" + user.postcode)))
      val doc = Jsoup.parse(contentAsString(result))
      doc.title() shouldBe "Something went wrong"
    }
  }
}
