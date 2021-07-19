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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.examplefrontend.Connector.RegistrationConnector
import uk.gov.hmrc.examplefrontend.common.{ErrorMessages, SessionKeys}
import uk.gov.hmrc.examplefrontend.config.ErrorHandler
import uk.gov.hmrc.examplefrontend.model.{User, UserProperty}
import uk.gov.hmrc.examplefrontend.views.html.{CRNPage, ResultPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SummaryController @Inject()(
                                   mcc: MessagesControllerComponents,
                                   resultPage: ResultPage,
                                   registrationConnector: RegistrationConnector,
                                   crnPage: CRNPage,
                                   error: ErrorHandler,
                                   implicit val executionContext: ExecutionContext
                                 )
  extends FrontendController(mcc) with I18nSupport {

  def Summary: Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      request.session
      val name: String = request.session.get(SessionKeys.name).getOrElse("")
      val business: String = request.session.get(SessionKeys.businessName).getOrElse("")
      val contact: String = request.session.get(SessionKeys.contactNumber).getOrElse("")
      val property: UserProperty = UserProperty.decode(request.session.get(SessionKeys.property).getOrElse(""))
      val businessType: String = request.session.get(SessionKeys.businessType).getOrElse("")
      val password: String = request.session.get(SessionKeys.password).getOrElse("")
      val result = User(name, business, contact, property.propertyNumber, property.postcode, businessType, password)

      Ok(resultPage(result))
    }
  }

  def SummarySubmit: Action[AnyContent] = Action async { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Future(Redirect(routes.RegistrationController.home()))
    } else {
      val name: String = request.session.get(SessionKeys.name).getOrElse("")
      val businessName: String = request.session.get(SessionKeys.businessName).getOrElse("")
      val contactNumber: String = request.session.get(SessionKeys.contactNumber).getOrElse("")
      val property: UserProperty = UserProperty.decode(request.session.get(SessionKeys.property).getOrElse(""))
      val businessType: String = request.session.get(SessionKeys.businessType).getOrElse("")
      val password: String = request.session.get(SessionKeys.password).getOrElse("")
      val user = User(name, businessName, contactNumber, property.propertyNumber, property.postcode, businessType, password)

      registrationConnector.create(user).map {
        case Some(client) => Ok(crnPage(client)).withSession(
          SessionKeys.crn -> client.crn,
          SessionKeys.name -> client.name,
          SessionKeys.businessName -> client.businessName,
          SessionKeys.postcode -> client.propertyNumber,
          SessionKeys.propertyNumber -> client.postcode,
          SessionKeys.contactNumber -> client.contactNumber,
          SessionKeys.businessType -> client.businessType
        )
        case _ => BadRequest
      }.recover {
        case _ => InternalServerError(error.standardErrorTemplate(
          pageTitle = ErrorMessages.pageTitle,
          heading = ErrorMessages.heading,
          message = ErrorMessages.message))
      }
    }
  }
}
