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
import uk.gov.hmrc.examplefrontend.common.{ErrorMessages, SessionKeys, UrlKeys, UserClientProperties}
import uk.gov.hmrc.examplefrontend.config.ErrorHandler
import uk.gov.hmrc.examplefrontend.controllers.RegistrationController
import uk.gov.hmrc.examplefrontend.model.{Client, User, UserProperty}
import uk.gov.hmrc.examplefrontend.views.html._

import scala.concurrent.Future


class RegistrationControllerSpec extends AbstractTest {

  private val fakeRequestSubmitSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.submitPath)
  private val fakeRequestName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.nameInputPath)
  private val fakeRequestSubmitProperty: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.propertyInputPath)
  private val fakeRequestSubmitName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.nameInputPath)
  private val fakeRequestSubmitBusinessName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "POST",
    path = UrlKeys.businessInputPath)

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
    contactNumber = "01111111111111",
    propertyNumber = "TestPropertyNumber",
    postcode = "TestAddress",
    businessType = "Private Limited",
    password = "TestPassword")
  private val client: Client = Client(
    crn = "CRN",
    name = user.name,
    businessName = user.businessName,
    contactNumber = user.contactNumber,
    propertyNumber = user.propertyNumber,
    postcode = user.postcode,
    businessType = user.businessType,
    arn = Option("Arn"))
  private val fakeRequestSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.summaryPath)
    .withSession(
      SessionKeys.name -> user.name,
      SessionKeys.businessName -> user.businessName,
      SessionKeys.contactNumber -> user.contactNumber,
      SessionKeys.property -> UserProperty(user.propertyNumber, user.postcode).encode(),
      SessionKeys.businessType -> user.businessType,
      SessionKeys.password -> user.password)
  private val htmlContentType: String = "text/html"

  "GET /NameInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputName(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputName(fakeRequestName)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputName(fakeRequestName.withSession(SessionKeys.crn -> client.crn))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputName(
        fakeRequestName.withSession(SessionKeys.name -> user.name))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("name").`val` shouldBe user.name
    }
  }

  "GET/BusinessNameInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputBusinessName(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputBusinessName(fakeRequestName)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputBusinessName(fakeRequestName.withSession(SessionKeys.crn -> client.crn))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputBusinessName(
        fakeRequestName.withSession(SessionKeys.businessName -> user.businessName))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("business-name").`val` shouldBe user.businessName
    }
  }

  "GET/ContactInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputContactNumber(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputContactNumber(fakeRequestName)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputContactNumber(fakeRequestName.withSession(SessionKeys.crn -> client.crn))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputContactNumber(
        fakeRequestName.withSession(SessionKeys.contactNumber -> user.contactNumber))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("contact-number").`val` shouldBe user.contactNumber
    }
  }

  "GET/BusinessType" should {
    "return 200" in {
      val result: Future[Result] = controller.InputBusinessType(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputBusinessType(fakeRequestName)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputBusinessType(
        fakeRequestName.withSession(SessionKeys.crn -> client.crn))
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "GET/PropertyNumberInput" should {
    "return 200" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName.withSession(SessionKeys.crn -> client.crn))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestName.withSession(
        SessionKeys.property -> UserProperty(user.propertyNumber, user.postcode).encode()))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("property-number").`val` shouldBe user.propertyNumber
      doc.getElementById("postcode").`val` shouldBe user.postcode
    }
  }

  "GET/PasswordInput" should {
    "return 200" in {
      val result = controller.InputPassword(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputPassword(fakeRequestName)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputPassword(fakeRequestName.withSession(SessionKeys.crn -> client.crn))
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
      contentType(result) shouldBe Some(htmlContentType)
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("name-value").text() shouldBe user.name
      doc.getElementById("business-name-value").text() shouldBe user.businessName
      doc.getElementById("contact-value").text() shouldBe user.contactNumber
      doc.getElementById("property-value").text().contains(user.propertyNumber) shouldBe true
      doc.getElementById("property-value").text().contains(user.postcode) shouldBe true
      doc.getElementById("business-type-value").text() shouldBe user.businessType

    }
    "return redirect home" in {
      val result: Future[Result] = controller.Summary(fakeRequestSummary.withSession(SessionKeys.crn -> client.crn))
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "Redirect home page" should {
    "return 303" in {
      val result: Future[Result] = controller.home(fakeRequestSummary)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "Redirect Dashboard" should {
    "return 303" in {
      val result: Future[Result] = controller.dashboard(fakeRequestName)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "POST/SubmitNameInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withSession(
        SessionKeys.crn -> client.crn, SessionKeys.name -> client.name))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.name -> user.name))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.name -> user.name))
      session(result).get(SessionKeys.name).getOrElse("") shouldBe user.name
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.name -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "POST/SubmitBusinessNameInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withSession(
        SessionKeys.crn -> client.crn,
        SessionKeys.businessName -> client.businessName))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withFormUrlEncodedBody(
        UserClientProperties.businessName -> user.businessName))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withFormUrlEncodedBody(
        UserClientProperties.businessName -> user.businessName))
      session(result).get(SessionKeys.businessName).getOrElse("") shouldBe user.businessName
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.businessName -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "POST/SubmitContactInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withSession(
        SessionKeys.crn -> client.crn,
        SessionKeys.contactNumber -> client.contactNumber))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.contactNumber -> user.contactNumber))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.contactNumber -> user.contactNumber))
      session(result).get(SessionKeys.contactNumber).getOrElse("") shouldBe user.contactNumber
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.contactNumber -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "POST/SubmitPropertyInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputProperty(fakeRequestSubmitProperty.withSession(
        SessionKeys.crn -> client.crn,
        SessionKeys.property -> UserProperty(client.propertyNumber, client.postcode).encode()))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputProperty(fakeRequestSubmitProperty.withFormUrlEncodedBody(
        UserClientProperties.propertyNumber -> user.propertyNumber,
        UserClientProperties.postcode -> user.postcode))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result = controller.SubmitInputProperty(fakeRequestSubmitProperty.withFormUrlEncodedBody(
        UserClientProperties.propertyNumber -> user.propertyNumber,
        UserClientProperties.postcode -> user.postcode))
      session(result).get(SessionKeys.property).getOrElse("") shouldBe UserProperty(user.propertyNumber, user.postcode).encode()
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputProperty(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.propertyNumber -> "",
        UserClientProperties.postcode -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "POST/SubmitInputBusinessType" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputBusinessType(fakeRequestSubmitProperty.withSession(
        SessionKeys.crn -> client.crn,
        SessionKeys.businessType -> client.businessType))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputBusinessType(fakeRequestSubmitProperty.withFormUrlEncodedBody(
        UserClientProperties.businessType -> client.businessType))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result = controller.SubmitInputBusinessType(fakeRequestSubmitProperty.withFormUrlEncodedBody(
        UserClientProperties.businessType -> client.businessType))
      session(result).get(SessionKeys.businessType).getOrElse("") shouldBe user.businessType
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputBusinessType(fakeRequestSubmitName.withFormUrlEncodedBody())
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "POST/SubmitPasswordInput" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputPassword(fakeRequestSubmitProperty.withSession(
        SessionKeys.crn -> client.crn,
        SessionKeys.password -> user.password,
        UserClientProperties.passwordCheck -> user.password))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.password -> user.password,
        UserClientProperties.passwordCheck -> user.password))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.password -> user.password,
        UserClientProperties.passwordCheck -> user.password))
      session(result).get(SessionKeys.password).getOrElse("") shouldBe user.password
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.password -> "",
        UserClientProperties.passwordCheck -> user.password))
      status(result) shouldBe Status.BAD_REQUEST
    }
    "return Bad Request with passwords not matching" in {
      val result: Future[Result] = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody(
        UserClientProperties.password -> "badpass",
        UserClientProperties.passwordCheck -> user.password))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "POST/SubmitSummary" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitName.withSession(
        SessionKeys.crn -> client.crn,
        SessionKeys.name -> user.name,
        SessionKeys.businessName -> user.businessName,
        SessionKeys.contactNumber -> user.contactNumber,
        SessionKeys.businessType -> user.businessType,
        SessionKeys.password -> user.password,
        SessionKeys.property -> (user.propertyNumber + "/" + user.postcode)))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      when(connector.create(any())) thenReturn Future.successful(Some(client))
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitSummary.withSession(
        SessionKeys.name -> user.name,
        SessionKeys.businessName -> user.businessName,
        SessionKeys.contactNumber -> user.contactNumber,
        SessionKeys.businessType -> user.businessType,
        SessionKeys.password -> user.password,
        SessionKeys.property -> (user.propertyNumber + "/" + user.postcode)))
      status(result) shouldBe Status.OK
    }
    "return Bad Request" in {
      when(connector.create(any())) thenReturn Future.successful(None)
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitSummary.withSession(
        SessionKeys.name -> user.name,
        SessionKeys.businessName -> user.businessName,
        SessionKeys.contactNumber -> user.contactNumber,
        SessionKeys.businessType -> user.businessType,
        SessionKeys.password -> user.password,
        SessionKeys.property -> (user.propertyNumber + "/" + user.postcode)))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "Error Handler" should {
    "catch unknown response" in {
      when(connector.create(any())) thenReturn Future.failed(new RuntimeException(""))
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitSummary.withSession(
        SessionKeys.name -> user.name,
        SessionKeys.businessName -> user.businessName,
        SessionKeys.contactNumber -> user.contactNumber,
        SessionKeys.businessType -> user.businessType,
        SessionKeys.password -> user.password,
        SessionKeys.property -> (user.propertyNumber + "/" + user.postcode)))
      val doc = Jsoup.parse(contentAsString(result))
      doc.title() shouldBe ErrorMessages.pageTitle
    }
  }
}
