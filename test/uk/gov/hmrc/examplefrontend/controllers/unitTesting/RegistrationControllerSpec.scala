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
import org.mockito.Mockito.mock
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, session, status}
import uk.gov.hmrc.examplefrontend.Connector.RegistrationConnector
import uk.gov.hmrc.examplefrontend.controllers.RegistrationController
import uk.gov.hmrc.examplefrontend.model.User
import uk.gov.hmrc.examplefrontend.views.html.{BusinessNameInputPage, BusinessTypeInputPage, CRNPage, ContactNumberInputPage, NameInputPage, PasswordInputPage, PropertyInputPage, ResultPage}


class RegistrationControllerSpec extends AbstractTest {

  private val fakeRequestName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET","/NameInput").withSession()
  private val fakeRequestSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET","/Summary").withSession("name"->"jake","businessName"->"jakeBusiness","contactNumber"->"000","address"->"testadress","postcode"->"testpostcode","businessType"->"businessType","password"->"testpassword")
  private val fakeRequestSubmitName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET","/NameInput").withSession("name"-> "jake")
  private val fakeRequestSubmitBusinessName:FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST","/BusinessNameInput")
  private val user:User = User("TestFullName","TestNameOfBusiness","TestContactNumber","TestPropertyNumber","TestAddress","TestPostCode","TestPassword")



  val name:NameInputPage = app.injector.instanceOf(classOf[NameInputPage])
  val businessName:BusinessNameInputPage = app.injector.instanceOf(classOf[BusinessNameInputPage])
  val contactNumber:ContactNumberInputPage = app.injector.instanceOf(classOf[ContactNumberInputPage])
  val property:PropertyInputPage = app.injector.instanceOf(classOf[PropertyInputPage])
  val businessType: BusinessTypeInputPage = app.injector.instanceOf(classOf[BusinessTypeInputPage])
  val password: PasswordInputPage = app.injector.instanceOf(classOf[PasswordInputPage])
  val result: ResultPage = app.injector.instanceOf(classOf[ResultPage])
  val crn: CRNPage = app.injector.instanceOf(classOf[CRNPage])
  val connector: RegistrationConnector = mock(classOf[RegistrationConnector])

  private val controller = new RegistrationController(Helpers.stubMessagesControllerComponents(),name,businessName,contactNumber,property,businessType,password,result,connector,crn)

  "GET /NameInput" should {
    "return 200" in {
      val result = controller.InputName(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in{
      val result = controller.InputName(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("Name Value")
    }
  }

  "GET/BusinessNameInput" should{
    "return 200" in {
      val result = controller.InputBusinessName(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in{
      val result = controller.InputBusinessName(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("BusinessNameValue")
    }
  }

  "GET/ContactInput" should{
    "return 200" in {
      val result = controller.InputContactNumber(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in{
      val result = controller.InputContactNumber(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("ContactNumberValue")
    }
  }

  "GET/PropertyNumberInput" should{
    "return 200" in {
      val result = controller.InputProperty(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in{
      val result = controller.InputProperty(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("PropertyNumberValue")
    }
  }

  "GET/PasswordInput" should{
    "return 200" in {
      val result = controller.InputPassword(fakeRequestName)
      status(result) shouldBe Status.OK
    }
    "return html" in{
      val result = controller.InputPassword(fakeRequestName)
      contentType(result) shouldBe Some("text/html")
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("PasswordValue")
    }
  }

  "GET/Summary" should{
    "return 200" in {
      val result = controller.Summary(fakeRequestSummary)
      status(result) shouldBe Status.OK
    }
    "return html" in{
      val result = controller.Summary(fakeRequestSummary)
      contentType(result) shouldBe Some("text/html")
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("NameValue")
      doc.getElementById("BusinessNameValue")
    }
  }

  "POST/SubmitNameInput" should{
    "retutn 303" in {
      val result = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody("name" -> "Jake"))
      status(result) shouldBe 303
    }
    "return html" in{
      val result = controller.SubmitInputName(fakeRequestSubmitName.withFormUrlEncodedBody("name" -> "Jake"))
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("BusinessNameValue")
    }
  }

  "POST/SubmitBusinessNameInput" should{
    "retutn 303" in {
      val result = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withFormUrlEncodedBody("businessName" -> "Jake"))
      status(result) shouldBe 303
    }
    "return html" in{
      val result = controller.SubmitInputBusinessName(fakeRequestSubmitBusinessName.withFormUrlEncodedBody("businessName" -> "Jake"))
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("BusinessNameValue")
      session(result).get("businessName").getOrElse("") shouldBe ("Jake")
    }
  }

  "POST/SubmitContactInput" should{
    "retutn 303" in {
      val result = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody("contactNumber" -> "000"))
      status(result) shouldBe 303
    }
    "return html" in{
      val result = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody("contactNumber" -> "000"))
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("PropertyNumberValue")
      session(result).get("contactNumber").getOrElse("") shouldBe ("000")
    }
  }

  "POST/SubmitPropertyInput" should{
    "retutn 303" in {
      val result = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody("propertyNumber" -> "10", "address" -> "London"))
      status(result) shouldBe 303
    }
    "return html" in{
      val result = controller.SubmitInputContactNumber(fakeRequestSubmitName.withFormUrlEncodedBody("propertyNumber" -> "10", "address" -> "London"))
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("PropertyNumberValue")
    }
  }

  "POST/SubmitPasswordInput" should{
    "retutn 303" in {
      val result = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody("password" -> "pass"))
      status(result) shouldBe 303
    }
    "return html" in{
      val result = controller.SubmitInputPassword(fakeRequestSubmitName.withFormUrlEncodedBody("password" -> "pass"))
      val doc=Jsoup.parse(contentAsString(result))
      doc.getElementById("NameValue")
      session(result).get("password").getOrElse("") shouldBe ("pass")
    }
  }

}
