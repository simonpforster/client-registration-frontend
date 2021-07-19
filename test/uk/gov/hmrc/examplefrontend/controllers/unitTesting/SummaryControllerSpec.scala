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
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.http.Status
import play.api.test.FakeRequest
import uk.gov.hmrc.examplefrontend.Connector.RegistrationConnector
import uk.gov.hmrc.examplefrontend.common.{ErrorMessages, SessionKeys, UrlKeys}
import uk.gov.hmrc.examplefrontend.config.ErrorHandler
import uk.gov.hmrc.examplefrontend.controllers.SummaryController
import uk.gov.hmrc.examplefrontend.model.{Client, User, UserProperty}
import uk.gov.hmrc.examplefrontend.views.html.{CRNPage, ResultPage}

import scala.concurrent.{ExecutionContext, Future}

class SummaryControllerSpec extends AbstractTest {

  val result: ResultPage = app.injector.instanceOf[ResultPage]
  val crn: CRNPage = app.injector.instanceOf[CRNPage]
  val error: ErrorHandler = app.injector.instanceOf[ErrorHandler]
  val connector: RegistrationConnector = mock(classOf[RegistrationConnector])
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  private val controller = new SummaryController(
    mcc = stubMessagesControllerComponents(),
    resultPage = result,
    registrationConnector = connector,
    crnPage = crn,
    error = error,
    executionContext = executionContext
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
  private val htmlContentType: String = "text/html"

  private val fakeRequestSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.summaryPath)
    .withSession(
      SessionKeys.name -> user.name,
      SessionKeys.businessName -> user.businessName,
      SessionKeys.contactNumber -> user.contactNumber,
      SessionKeys.property -> UserProperty(user.propertyNumber, user.postcode).encode(),
      SessionKeys.businessType -> user.businessType,
      SessionKeys.password -> user.password
    )
  private val fakeRequestSubmitSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "POST",
    path = UrlKeys.submitPath
  )

  "Summary GET" should {
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

  "SubmitSummary POST" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SummarySubmit(fakeRequestSubmitSummary.withSession(
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
