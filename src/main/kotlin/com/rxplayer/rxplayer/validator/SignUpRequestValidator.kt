package com.rxplayer.rxplayer.validator

import com.rxplayer.rxplayer.dto.input.SignupRequest
import com.rxplayer.rxplayer.repositories.UserRepository
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils

@Component
class SignUpRequestValidator(private val userRepository: UserRepository){

    suspend fun validate(target: SignupRequest, errors: Errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Username is required")
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Password is required")
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Email is required")
        val (username, password, email) = target

        if(username.length < 5) errors.rejectValue("username", "Username must be at least 5 characters long.")
        if(password.length < 8) errors.rejectValue("password", "Password must be at least 8 characters long.")
        validateEmail(email, errors)
        validateIfExists(username, email, errors)
    }

    private fun validateEmail(email: String, errors: Errors){
        if(!Regex("^[a-zA-Z][a-zA-Z-_]+@[a-zA-Z]+(\\.[a-zA-Z]+)+$").matches(email)){
            errors.rejectValue(
                "email",
                "Invalid email address $email eg: rxplayer@gmail.com")
        }
    }

    private suspend fun validateIfExists(username: String, email: String, errors: Errors){
        val usernameExists = userRepository.existsByUsername(username)
        if (usernameExists){
            errors.rejectValue("username", "This username is already taken")
        }

        val emailExists = userRepository.existsByEmail(email)
        if(emailExists){
            errors.rejectValue("email", "There is an account already with this email")
        }
    }
}