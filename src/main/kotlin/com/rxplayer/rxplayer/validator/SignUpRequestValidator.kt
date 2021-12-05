package com.rxplayer.rxplayer.validator

import com.rxplayer.rxplayer.dto.input.SignupRequest
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

@Component
class SignUpRequestValidator : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return SignupRequest::class.java == clazz
    }

    override fun validate(target: Any, errors: Errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Username is required")
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Password is required")
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Email is required")

        val (username, password, email) = target as SignupRequest
        if(username.length < 5){
            errors.rejectValue("username", "Username must be at least 5 characters long.")
        }

        if(password.length < 8) {
            errors.rejectValue("password", "Password must be at least 8 characters long.")
        }

        if(!Regex("^[a-zA-Z][a-zA-Z-_]+@[a-zA-Z]+(\\.[a-zA-Z]+)+$").matches(email)){
            errors.rejectValue(
                "email",
                "Invalid email address $email eg: rxplayer@gmail.com")
        }
    }
}