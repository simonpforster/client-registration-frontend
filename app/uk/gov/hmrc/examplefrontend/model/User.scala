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

package uk.gov.hmrc.examplefrontend.model

import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, Forms}
import uk.gov.hmrc.examplefrontend.common.{ErrorMessages, UserClientProperties}

import scala.util.matching.Regex


case class UserName(name: String)

object UserNameForm {

  val submitForm: Form[UserName] =
    Form(
      mapping(
        UserClientProperties.name -> Forms.text.verifying(ErrorMessages.nameFormError, _.nonEmpty)
      )(UserName.apply)(UserName.unapply)
    )
}

case class UserBusinessName(business: String)

object UserBusinessNameForm {
  val submitForm: Form[UserBusinessName] =
    Form(
      mapping(
        UserClientProperties.businessName -> Forms.text.verifying(ErrorMessages.businessNameFormError, _.nonEmpty)
      )(UserBusinessName.apply)(UserBusinessName.unapply)
    )
}

case class UserContactNumber(contact: String)

object UserContactNumberForm {

  val regEx: Regex = """^[0-9]{10}$|^[0-9]{11}$""".r

  val contactNumberCheckConstraint: Constraint[String] = Constraint("contactNumberRegex")({
    case regEx() => Valid
    case _ => Invalid(ErrorMessages.contactNumberFormError)
  })

  val submitForm: Form[UserContactNumber] =
    Form(
      mapping(
        UserClientProperties.contactNumber -> Forms.text.verifying(contactNumberCheckConstraint)
      )(UserContactNumber.apply)(UserContactNumber.unapply)
    )
}

case class UserProperty(propertyNumber: String, postcode: String) {

  def encode(): String = {
    propertyNumber + "/" + postcode
  }
}

object UserProperty {
  def decode(x: String): UserProperty = {
    val (propertyNumber, postcode): (String, String) = x.split("/").toList match {
      case propertyNumber :: postcode :: _ => (propertyNumber, postcode)
    }
    UserProperty(propertyNumber = propertyNumber, postcode = postcode)
  }
}

object UserPropertyForm {

  val regEx: Regex = """(?:[A-Za-z]\d ?\d[A-Za-z]{2})|(?:[A-Za-z][A-Za-z\d]\d ?\d[A-Za-z]{2})|(?:[A-Za-z]{2}\d{2} ?\d[A-Za-z]{2})|(?:[A-Za-z]\d[A-Za-z] ?\d[A-Za-z]{2})|(?:[A-Za-z]{2}\d[A-Za-z] ?\d[A-Za-z]{2})""".stripMargin.r

  val postcodeCheckConstraint: Constraint[String] = Constraint("postcodeRegex")({
    case regEx() => Valid
    case _ => Invalid(ErrorMessages.postcodeFormError)
  })

  val submitForm: Form[UserProperty] =
    Form(
      mapping(
        UserClientProperties.propertyNumber -> Forms.text.verifying(ErrorMessages.propertyNumberFormError, _.nonEmpty),
        UserClientProperties.postcode -> Forms.text.verifying(postcodeCheckConstraint)
      )(UserProperty.apply)(UserProperty.unapply)
    )
}

case class UserBusinessType(businessType: String)

object UserBusinessTypeForm {
  val submitForm: Form[UserBusinessType] =
    Form(
      mapping(
        UserClientProperties.businessType -> Forms.text.verifying(ErrorMessages.businessTypeFormError, _.nonEmpty),
      )(UserBusinessType.apply)(UserBusinessType.unapply)
    )
}

case class UserPassword(password: String, passwordCheck: String)

object UserPasswordForm {
  val submitForm: Form[UserPassword] =
    Form(
      mapping(
        UserClientProperties.password -> Forms.text.verifying(ErrorMessages.passwordFormError, _.length >= 10),
        UserClientProperties.passwordCheck -> Forms.text
      )(UserPassword.apply)(UserPassword.unapply)
    )
}

case class User(name: String,
                businessName: String,
                contactNumber: String,
                propertyNumber: String,
                postcode: String,
                businessType: String,
                password: String)



