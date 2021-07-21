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
import uk.gov.hmrc.examplefrontend.model.{UserBusinessName, UserBusinessNameForm}
import uk.gov.hmrc.examplefrontend.views.html.BusinessNameInputPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class BusinessNameController @Inject()(
                                        mcc: MessagesControllerComponents,
                                        businessNameInputPage: BusinessNameInputPage
                                      )
  extends FrontendController(mcc) {

  def InputBusinessName(isUpdate:Boolean): Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      val form: Form[UserBusinessName] = request.session.get(SessionKeys.businessName).fold(UserBusinessNameForm.submitForm.fill(UserBusinessName(""))) {
        business => UserBusinessNameForm.submitForm.fill(UserBusinessName(business))
      }
      Ok(businessNameInputPage(form,isUpdate))
    }
  }

  def SubmitInputBusinessName(isUpdate:Boolean): Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      if(isUpdate){
        UserBusinessNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
          BadRequest(businessNameInputPage(formWithErrors,isUpdate))
        }, { formData =>
          Redirect(routes.SummaryController.Summary(isUpdate)).withSession(request.session + (SessionKeys.businessName -> formData.business))
        })
      }else {
        UserBusinessNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
          BadRequest(businessNameInputPage(formWithErrors,isUpdate))
        }, { formData =>
          Redirect(routes.ContactNumberController.InputContactNumber(isUpdate=false)).withSession(request.session + (SessionKeys.businessName -> formData.business))
        })
      }
    }
  }


}
