package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.EditUserRequest
import com.rxplayer.rxplayer.security.SecurityUserAdapter
import com.rxplayer.rxplayer.dto.input.SignupRequest
import com.rxplayer.rxplayer.dto.output.created.CreatedUserDTO
import com.rxplayer.rxplayer.dto.output.find.FindUserDTO
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.exception.InvalidOperationException
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.UserRepository
import com.rxplayer.rxplayer.util.AuthUtil
import com.rxplayer.rxplayer.validator.SignUpRequestValidator
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.reactive.function.server.*

@Service
class UserHandler(
    private val passwordEncoder: PasswordEncoder,
    private val signUpValidator: SignUpRequestValidator,
    private val userRepository: UserRepository
){
    suspend fun save(request: ServerRequest): ServerResponse {
        val signup = request.awaitBody<SignupRequest>()
        val errors = BeanPropertyBindingResult(signup, SignupRequest::class.java.name)
        signUpValidator.validate(signup, errors)

        if(errors.hasFieldErrors()){
            val customErrors: MutableMap<String, String?> = HashMap()
            for(error in errors.fieldErrors){
                customErrors[error.field] = error.code
            }

            return ServerResponse.badRequest()
                .bodyValueAndAwait(customErrors)
        }

        val user = User(
            username = signup.username,
            email = signup.email,
            password = passwordEncoder.encode(signup.password))

        val savedUser = userRepository.save(user)
        val dto = CreatedUserDTO(savedUser.id, savedUser.username, savedUser.profilePicture)
        return ServerResponse.status(HttpStatus.CREATED)
            .bodyValueAndAwait(dto)
    }

    @PreAuthorize("#serverRequest.pathVariable('id') == authentication.principal.user.id")
    suspend fun edit(serverRequest: ServerRequest): ServerResponse {
        val body = serverRequest.awaitBody<EditUserRequest>()
        val exists = userRepository.existsByEmail(body.email)

        if(exists){
            throw InvalidOperationException("Can update your email to ${body.email} because it's already in use")
        }

        val user = AuthUtil.getAuthenticatedUserFromRequest(serverRequest).apply {
            username = body.username
            email = body.email
        }

        val editedUser = userRepository.save(user)
        val dto = FindUserDTO(editedUser.id, editedUser.username, editedUser.email, editedUser.profilePicture)
        return ServerResponse.status(HttpStatus.OK)
            .bodyValueAndAwait(dto)
    }

    suspend fun findMyself(serverRequest: ServerRequest): ServerResponse {
        val user = AuthUtil.getAuthenticatedUserFromRequest(serverRequest)
        val dto = FindUserDTO(user.id, user.username, user.email, user.profilePicture)
        return ServerResponse.ok().bodyValueAndAwait(dto)
    }

    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val user = userRepository.findById(id)

        return user?.let {
            val dto = FindUserDTO(it.id, it.username, it.username, it.profilePicture)
            ServerResponse.ok().bodyValueAndAwait(dto)
        } ?: ServerResponse.notFound().buildAndAwait()
    }

}