package com.ivaylo.blog.utility.validators;

import com.ivaylo.blog.models.UserLoginRequest;
import com.ivaylo.blog.utility.enums.ValidationResult;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ivaylo.blog.utility.BlogsConstants.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.SUCCESS;

public interface UserLoginValidator extends Function<UserLoginRequest, ValidationResult> {

    static UserLoginValidator isEmailLoginEqualsToNull(){
        return user -> user.getEmail() != null?
                SUCCESS:
                EMAIL_IS_MANDATORY;
    }
    static UserLoginValidator isValidLoginEmail(){
        return user -> {
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            Matcher matcher = pattern.matcher(user.getEmail());
            return matcher.matches()?SUCCESS:EMAIL_IS_NOT_VALID;
        };
    }
    static UserLoginValidator isPasswordLoginEqualsToNull(){
        return user -> user.getPassword() != null?
                SUCCESS:
                PASSWORD_IS_MANDATORY;
    }
    static UserLoginValidator isValidLoginPasswordLength(){
        return user -> user.getPassword().length() >= PASSWORD_MIN_LENGTH
                && user.getPassword().length() <= PASSWORD_MAX_LENGTH?
                SUCCESS:PASSWORD_MUST_BE_BETWEEN;
    }
    static UserLoginValidator isValidLoginPassword(){
        return user -> {
            Pattern pattern = Pattern.compile(PASSWORD_REGEX);
            Matcher matcher = pattern.matcher(user.getPassword());
            return matcher.matches()?SUCCESS:PASSWORD_MUST_HAVE_THESE_SYMBOLS;
        };
    }

    default UserLoginValidator and(UserLoginValidator other){
        return user -> {
            ValidationResult result = this.apply(user);
            return result.equals(SUCCESS)? other.apply(user): result;
        };
    }
}
