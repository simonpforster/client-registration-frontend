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
import uk.gov.hmrc.examplefrontend.common.Utils.loggedInCheck
import uk.gov.hmrc.examplefrontend.model.{UserName, UserNameForm}
import uk.gov.hmrc.examplefrontend.views.html.NameInputPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.Future

class NameController @Inject()(
                                mcc: MessagesControllerComponents,
                                nameInputPage: NameInputPage
                              )
  extends FrontendController(mcc) {

  def InputName(isUpdate:Boolean): Action[AnyContent] = Action { implicit request =>
    loggedInCheck{ () =>
        val form: Form[UserName] = request.session.get(SessionKeys.name).fold(UserNameForm.submitForm.fill(UserName(""))) { name =>
          UserNameForm.submitForm.fill(UserName(name))
        }
        Ok(nameInputPage(form,isUpdate))
      }
  }

  def SubmitInputName(isUpdate:Boolean): Action[AnyContent] = Action { implicit request =>
    loggedInCheck { () =>
      if (isUpdate) {
        UserNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
          BadRequest(nameInputPage(formWithErrors, isUpdate))
        }, { formData =>
          Redirect(routes.SummaryController.Summary(isUpdate)).withSession(request.session + (SessionKeys.name -> formData.name))
        })
      } else {
        UserNameForm.submitForm.bindFromRequest().fold({ formWithErrors =>
          BadRequest(nameInputPage(formWithErrors, isUpdate))
        }, { formData =>
          Redirect(routes.BusinessNameController.InputBusinessName(isUpdate)).withSession(request.session + (SessionKeys.name -> formData.name))
        })
      }
    }
  }
}
