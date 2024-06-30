package com.ukpatel.expense.tracker.common.constant;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public final class CmnConstants {

    public static final Short STATUS_ACTIVE = 1;
    public static final Short STATUS_INACTIVE = 0;
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String USER_SESSION_INFO = "USER_SESSION_INFO";
    public static final String PUBLIC_END_POINTS = "/auth/**";
    public static final String[] PUBLIC_END_POINT_EXCEPTIONS = new String[]{
            "/auth/change-password",
            "/auth/logout",
            "/auth/forgot-password"
    };


    // Prevent instantiation
    private CmnConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static UserSessionInfo getUserSessionInfo() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return (UserSessionInfo) request.getAttribute(USER_SESSION_INFO);
    }

    public static UserMst getLoggedInUser() {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = new UserMst();
        loggedInUser.setUserId(userSessionInfo.getUserDTO().getUserId());
        return loggedInUser;
    }
}
