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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.examplefrontend.common.SessionKeys
import uk.gov.hmrc.examplefrontend.model.{UserProperty, UserPropertyForm}
import uk.gov.hmrc.examplefrontend.views.html.PropertyInputPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class PropertyController @Inject()(
                                    mcc: MessagesControllerComponents,
                                    propertyInputPage: PropertyInputPage
                                  )
  extends FrontendController(mcc) {

  def InputProperty(isUpdate:Boolean): Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      val form: Form[UserProperty] = request.session.get(SessionKeys.property)
        .fold(UserPropertyForm.submitForm.fill(UserProperty("", ""))) {
        property => UserPropertyForm.submitForm.fill(UserProperty.decode(property))
      }
      Ok(propertyInputPage(form,isUpdate))
    }
  }

  def SubmitInputProperty(isUpdate:Boolean): Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      UserPropertyForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(propertyInputPage(formWithErrors,isUpdate))
      },{ formData => if(isUpdate) {
        Redirect(routes.SummaryController.Summary(isUpdate)).withSession(request.session + (SessionKeys.property -> formData.encode()))
      }
        else {
        Redirect(routes.BusinessTypeController.InputBusinessType(isUpdate))
          .withSession(request.session + (SessionKeys.property -> formData.encode()))
      }

      })
    }
  }

}