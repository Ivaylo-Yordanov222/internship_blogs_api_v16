package com.ivaylo.blog.utility.validators;

import com.ivaylo.blog.models.UserRegisterRequest;
import com.ivaylo.blog.utility.enums.ValidationResult;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ivaylo.blog.utility.BlogsConstants.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.SUCCESS;

public interface UserRegisterValidator extends Function<UserRegisterRequest, ValidationResult> {

    static UserRegisterValidator isNameEqualsNull(){
        return user -> user.getUsername() != null?
                SUCCESS:
                NAME_IS_MANDATORY;
    }
    static UserRegisterValidator isValidNameLength(){
        return user -> user.getUsername().length() >= USERNAME_MIN_LENGTH
                && user.getUsername().length() <= USERNAME_MAX_LENGTH?
                SUCCESS:
                NAME_MUST_BE_BETWEEN;
    }
    static UserRegisterValidator isValidName(){
        return user -> {
            Pattern pattern = Pattern.compile(USERNAME_REGEX);
            Matcher matcher = pattern.matcher(user.getUsername());
            return matcher.matches()?SUCCESS:NAME_MUST_HAVE_THESE_SYMBOLS;
        };
    }
    static UserRegisterValidator isEmailEqualsToNull(){
        return user -> user.getEmail() != null?
                SUCCESS:
                EMAIL_IS_MANDATORY;
    }
    static UserRegisterValidator isValidEmail(){
        return user -> {
                Pattern pattern = Pattern.compile(EMAIL_REGEX);
                Matcher matcher = pattern.matcher(user.getEmail());
                return matcher.matches()?SUCCESS:EMAIL_IS_NOT_VALID;
        };
    }
    static UserRegisterValidator isPasswordEqualsToNull(){
        return user -> user.getPassword() != null?
                SUCCESS:
                PASSWORD_IS_MANDATORY;
    }
    static UserRegisterValidator isValidPasswordLength(){
        return user -> user.getPassword().length() >= PASSWORD_MIN_LENGTH
                && user.getPassword().length() <= PASSWORD_MAX_LENGTH?
                SUCCESS:PASSWORD_MUST_BE_BETWEEN;
    }
    static UserRegisterValidator isValidPassword(){
        return user -> {
            Pattern pattern = Pattern.compile(PASSWORD_REGEX);
            Matcher matcher = pattern.matcher(user.getPassword());
            return matcher.matches()?SUCCESS:PASSWORD_MUST_HAVE_THESE_SYMBOLS;
        };
    }

    default UserRegisterValidator and(UserRegisterValidator other){
        return user -> {
          ValidationResult result = this.apply(user);
          return result.equals(SUCCESS)? other.apply(user): result;
        };
    }
}
