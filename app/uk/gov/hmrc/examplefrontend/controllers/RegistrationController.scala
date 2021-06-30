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
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.examplefrontend.Connector.RegistrationConnector
import uk.gov.hmrc.examplefrontend.model._
import uk.gov.hmrc.examplefrontend.views.html._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global


class RegistrationController @Inject()(mcc: MessagesControllerComponents, NameInputPage: NameInputPage, BusinessNameInputPage: BusinessNameInputPage, ContactNumberInputPage: ContactNumberInputPage, PropertyInputPage: PropertyInputPage, BusinessTypeInputPage: BusinessTypeInputPage, PasswordInputPage: PasswordInputPage, ResultPage:ResultPage,RegistrationConnector: RegistrationConnector, CRNPage:CRNPage) extends FrontendController(mcc) with I18nSupport {

  def InputName: Action[AnyContent] = Action { implicit request =>
    val form: Form[UserName] = request.session.get("name").fold(UserNameForm.submitForm.fill(UserName(""))) { name =>
      UserNameForm.submitForm.fill(UserName(name))
    }
    Ok(NameInputPage(form))
  }

  def SubmitInputName: Action[AnyContent] = Action { implicit request =>
    UserNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
      BadRequest(NameInputPage(formWithErrors))
    }, { formData =>
      Redirect(routes.RegistrationController.InputBusinessName()).withSession(request.session + ("name" -> formData.name))
    })
  }


  def InputBusinessName: Action[AnyContent] = Action { implicit request =>
    val form: Form[UserBusinessName] = request.session.get("business").fold(UserBusinessNameForm.submitForm.fill(UserBusinessName(""))) { business => UserBusinessNameForm.submitForm.fill(UserBusinessName(business))
    }
    Ok(BusinessNameInputPage(form))
  }

  def SubmitInputBusinessName: Action[AnyContent] = Action { implicit request =>
    UserBusinessNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
      BadRequest(BusinessNameInputPage(formWithErrors))
    }, { formData =>
      Redirect(routes.RegistrationController.InputContactNumber()).withSession(request.session + ("businessName" -> formData.business))
    })
  }

  def InputContactNumber: Action[AnyContent] = Action { implicit request =>
    val form: Form[UserContactNumber] = request.session.get("contactNumber").fold(UserContactNumberForm.submitForm.fill(UserContactNumber(""))) { contactNumber => UserContactNumberForm.submitForm.fill(UserContactNumber(contactNumber))
    }
    Ok(ContactNumberInputPage(form))
  }

  def SubmitInputContactNumber: Action[AnyContent] = Action { implicit request =>
    UserContactNumberForm.submitForm.bindFromRequest().fold({ formWithErrors =>
      BadRequest(ContactNumberInputPage(formWithErrors))
    }, { formData =>
      Redirect(routes.RegistrationController.InputProperty()).withSession(request.session + ("contactNumber" -> formData.contact))
    })
  }

  def InputProperty: Action[AnyContent] = Action { implicit request =>
    val form: Form[UserProperty] = request.session.get("propertyNumber" + "postcode").fold(UserPropertyForm.submitForm.fill(UserProperty("", ""))) { property =>
      UserPropertyForm.submitForm.fill(UserProperty.decode(property))
    }
    Ok(PropertyInputPage(form))
  }

  def SubmitInputProperty = Action { implicit request =>
    UserPropertyForm.submitForm.bindFromRequest().fold({ formWithErrors =>
      BadRequest(PropertyInputPage(formWithErrors))
    }, { formData =>
      Redirect(routes.RegistrationController.InputBusinessType()).withSession(request.session + ("property" -> formData.encode()))
    })
  }

  def InputBusinessType: Action[AnyContent] = Action { implicit request =>
    val form: Form[UserBusinessType] = request.session.get("propertyType").fold(UserBusinessTypeForm.submitForm.fill(UserBusinessType(""))) { business =>
      UserBusinessTypeForm.submitForm.fill(UserBusinessType(business))
    }
    Ok(BusinessTypeInputPage(form))
  }

  def SubmitInputBusinessType: Action[AnyContent] = Action { implicit request =>
    UserBusinessTypeForm.submitForm.bindFromRequest().fold({ formWithErrors =>
      BadRequest(BusinessTypeInputPage(formWithErrors))
    }, { formData =>
      Redirect(routes.RegistrationController.InputPassword()).withSession(request.session + ("businessType" -> formData.businessType))
    })
  }

  def InputPassword: Action[AnyContent] = Action { implicit request =>
    val form: Form[UserPassword] = request.session.get("password").fold(UserPasswordForm.submitForm.fill((UserPassword("")))) { password =>
      UserPasswordForm.submitForm.fill(UserPassword(password))
    }
    Ok(PasswordInputPage(form))
  }

  def SubmitInputPassword: Action[AnyContent] = Action { implicit request =>
    UserPasswordForm.submitForm.bindFromRequest().fold({ formWithErrors =>
      BadRequest(PasswordInputPage(formWithErrors))
    }, { formData =>
      Redirect(routes.RegistrationController.Summary()).withSession(request.session + ("password" -> formData.password))
    })
  }

    def Summary: Action[AnyContent] = Action { implicit request =>
      request.session
      val name:String = request.session.get("name").getOrElse("")
      val business:String  = request.session.get("businessName").getOrElse("")
      val contact:String = request.session.get("contactNumber").getOrElse("")
      val property: UserProperty = UserProperty.decode(request.session.get("property").getOrElse(""))
      val businessType:String = request.session.get("businessType").getOrElse("")
      val password:String = request.session.get("password").getOrElse("")
      val result = User(name,business,contact,property.propertyNumber,property.postcode,businessType,password)

      Ok(ResultPage(result))
    }

    def SummarySubmit: Action[AnyContent] = Action async { implicit request =>
      val name:String = request.session.get("name").get
      val businessName:String = request.session.get("businessName").get
      val contactNumber:String = request.session.get("contactNumber").get
      val property:UserProperty = UserProperty.decode(request.session.get("property").getOrElse(""))
      val businessType:String = request.session.get("businessType").get
      val password:String = request.session.get("password").get
        val user = User(name,businessName,contactNumber,property.propertyNumber,property.postcode,businessType,password)

      RegistrationConnector.create(user).map {
        case Some(client) => Ok(CRNPage(client)).withSession("crn"->client.crn)
        case _ => BadRequest

      }
    }

     def home: Action[AnyContent] = Action  {implicit request =>
       Redirect("http://localhost:9008/example-frontend/")
     }

    def dashboard: Action[AnyContent] = Action {implicit request =>
      Redirect("http://localhost:9008/example-frontend/dashboard")
    }
  }