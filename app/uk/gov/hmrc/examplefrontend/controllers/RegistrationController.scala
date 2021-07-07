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

package uk.gov.hmrc.examplefrontend.controllers


import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, ValidationError}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.examplefrontend.Connector.RegistrationConnector
import uk.gov.hmrc.examplefrontend.model._
import uk.gov.hmrc.examplefrontend.views.html._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class RegistrationController @Inject()(mcc: MessagesControllerComponents, nameInputPage: NameInputPage, businessNameInputPage: BusinessNameInputPage, contactNumberInputPage: ContactNumberInputPage, propertyInputPage: PropertyInputPage, businessTypeInputPage: BusinessTypeInputPage, passwordInputPage: PasswordInputPage, resultPage: ResultPage, registrationConnector: RegistrationConnector, crnPage: CRNPage) extends FrontendController(mcc) with I18nSupport {


  def InputName: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      val form: Form[UserName] = request.session.get("name").fold(UserNameForm.submitForm.fill(UserName(""))) { name =>
        UserNameForm.submitForm.fill(UserName(name))
      }
      Ok(nameInputPage(form))
    }
  }

  def SubmitInputName: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      UserNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(nameInputPage(formWithErrors))
      }, { formData =>
        Redirect(routes.RegistrationController.InputBusinessName()).withSession(request.session + ("name" -> formData.name))
      })
    }
  }


  def InputBusinessName: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      val form: Form[UserBusinessName] = request.session.get("business").fold(UserBusinessNameForm.submitForm.fill(UserBusinessName(""))) { business => UserBusinessNameForm.submitForm.fill(UserBusinessName(business))
      }
      Ok(businessNameInputPage(form))
    }
  }

  def SubmitInputBusinessName: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      UserBusinessNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(businessNameInputPage(formWithErrors))
      }, { formData =>
        Redirect(routes.RegistrationController.InputContactNumber()).withSession(request.session + ("businessName" -> formData.business))
      })
    }
  }

  def InputContactNumber: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      val form: Form[UserContactNumber] = request.session.get("contactNumber").fold(UserContactNumberForm.submitForm.fill(UserContactNumber(""))) { contactNumber => UserContactNumberForm.submitForm.fill(UserContactNumber(contactNumber))
      }
      Ok(contactNumberInputPage(form))
    }
  }

  def SubmitInputContactNumber: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      UserContactNumberForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(contactNumberInputPage(formWithErrors))
      }, { formData =>
        Redirect(routes.RegistrationController.InputProperty()).withSession(request.session + ("contactNumber" -> formData.contact))
      })
    }
  }

  def InputProperty: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      val form: Form[UserProperty] = request.session.get("propertyNumber" + "postcode").fold(UserPropertyForm.submitForm.fill(UserProperty("", ""))) { property =>
        UserPropertyForm.submitForm.fill(UserProperty.decode(property))
      }
      Ok(propertyInputPage(form))
    }
  }

  def SubmitInputProperty: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      UserPropertyForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(propertyInputPage(formWithErrors))
      }, { formData =>
        Redirect(routes.RegistrationController.InputBusinessType()).withSession(request.session + ("property" -> formData.encode()))
      })
    }
  }

  def InputBusinessType: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      val form: Form[UserBusinessType] = request.session.get("propertyType").fold(UserBusinessTypeForm.submitForm.fill(UserBusinessType(""))) { business =>
        UserBusinessTypeForm.submitForm.fill(UserBusinessType(business))
      }
      Ok(businessTypeInputPage(form))
    }
  }

  def SubmitInputBusinessType: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      UserBusinessTypeForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(businessTypeInputPage(formWithErrors))
      }, { formData =>
        Redirect(routes.RegistrationController.InputPassword()).withSession(request.session + ("businessType" -> formData.businessType))
      })
    }
  }

  def InputPassword: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      val form: Form[UserPassword] = request.session.get("propertyType").fold(UserPasswordForm.submitForm.fill(UserPassword("", ""))) { password =>
        UserPasswordForm.submitForm.fill(UserPassword(password, password))
      }
      Ok(passwordInputPage(UserPasswordForm.submitForm.fill(UserPassword("", ""))))
    }
  }

  def SubmitInputPassword: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      UserPasswordForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(passwordInputPage(formWithErrors))
      }, { formData =>
        formData.password match {
          case formData.passwordCheck => Redirect(routes.RegistrationController.Summary()).withSession(request.session + ("password" -> formData.password))
          case _ => BadRequest(passwordInputPage(UserPasswordForm.submitForm.fill(UserPassword("", "")).withError("passwordCheck", "password does not match")))
        }
      })
    }
  }

  def Summary: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("crn").isDefined) {Redirect(routes.RegistrationController.home())} else {
      request.session
      val name: String = request.session.get("name").getOrElse("")
      val business: String = request.session.get("businessName").getOrElse("")
      val contact: String = request.session.get("contactNumber").getOrElse("")
      val property: UserProperty = UserProperty.decode(request.session.get("property").getOrElse(""))
      val businessType: String = request.session.get("businessType").getOrElse("")
      val password: String = request.session.get("password").getOrElse("")
      val result = User(name, business, contact, property.propertyNumber, property.postcode, businessType, password)

      Ok(resultPage(result))
    }
  }

  def SummarySubmit: Action[AnyContent] = Action async { implicit request =>
    if (request.session.get("crn").isDefined) {Future(Redirect(routes.RegistrationController.home()))} else {
      val name: String = request.session.get("name").getOrElse("")
      val businessName: String = request.session.get("businessName").getOrElse("")
      val contactNumber: String = request.session.get("contactNumber").getOrElse("")
      val property: UserProperty = UserProperty.decode(request.session.get("property").getOrElse(""))
      val businessType: String = request.session.get("businessType").getOrElse("")
      val password: String = request.session.get("password").getOrElse("")
      val user = User(name, businessName, contactNumber, property.propertyNumber, property.postcode, businessType, password)

      registrationConnector.create(user).map {
        case Some(client) => Ok(crnPage(client)).withSession("crn" -> client.crn, "name" -> client.name)
        case _ => BadRequest

      }
    }
  }

  def home: Action[AnyContent] = Action { implicit request =>
    Redirect("http://localhost:9008/example-frontend/")
  }

  def dashboard: Action[AnyContent] = Action { implicit request =>
    Redirect("http://localhost:9008/example-frontend/dashboard")
  }
}