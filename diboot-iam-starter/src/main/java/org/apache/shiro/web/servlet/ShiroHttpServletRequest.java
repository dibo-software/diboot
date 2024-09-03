//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.shiro.web.servlet;

import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.util.TokenUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DisabledSessionException;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.util.WebUtils;

import java.security.Principal;

public class ShiroHttpServletRequest extends HttpServletRequestWrapper {
    public static final String COOKIE_SESSION_ID_SOURCE = "cookie";
    public static final String URL_SESSION_ID_SOURCE = "url";
    public static final String REFERENCED_SESSION_ID = ShiroHttpServletRequest.class.getName() + "_REQUESTED_SESSION_ID";
    public static final String REFERENCED_SESSION_ID_IS_VALID = ShiroHttpServletRequest.class.getName() + "_REQUESTED_SESSION_ID_VALID";
    public static final String REFERENCED_SESSION_IS_NEW = ShiroHttpServletRequest.class.getName() + "_REFERENCED_SESSION_IS_NEW";
    public static final String REFERENCED_SESSION_ID_SOURCE = ShiroHttpServletRequest.class.getName() + "REFERENCED_SESSION_ID_SOURCE";
    public static final String IDENTITY_REMOVED_KEY = ShiroHttpServletRequest.class.getName() + "_IDENTITY_REMOVED_KEY";
    public static final String SESSION_ID_URL_REWRITING_ENABLED = ShiroHttpServletRequest.class.getName() + "_SESSION_ID_URL_REWRITING_ENABLED";
    protected ServletContext servletContext;
    protected HttpSession session;
    protected boolean httpSessions;

    public ShiroHttpServletRequest(HttpServletRequest wrapped, ServletContext servletContext, boolean httpSessions) {
        super(wrapped);
        this.servletContext = servletContext;
        this.httpSessions = httpSessions;
    }

    public boolean isHttpSessions() {
        return this.httpSessions;
    }

    public String getRemoteUser() {
        Object scPrincipal = this.getSubjectPrincipal();
        String remoteUser;
        if (scPrincipal != null) {
            if (scPrincipal instanceof String) {
                return (String)scPrincipal;
            }

            if (scPrincipal instanceof Principal) {
                remoteUser = ((Principal)scPrincipal).getName();
            } else {
                remoteUser = scPrincipal.toString();
            }
        } else {
            remoteUser = super.getRemoteUser();
        }

        return remoteUser;
    }

    protected Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    protected Object getSubjectPrincipal() {
        Object userPrincipal = null;
        Subject subject = this.getSubject();
        if (subject != null) {
            userPrincipal = subject.getPrincipal();
        }

        return userPrincipal;
    }

    public boolean isUserInRole(String s) {
        Subject subject = this.getSubject();
        boolean inRole = subject != null && subject.hasRole(s);
        if (!inRole) {
            inRole = super.isUserInRole(s);
        }

        return inRole;
    }

    public Principal getUserPrincipal() {
        Object scPrincipal = this.getSubjectPrincipal();
        Object userPrincipal;
        if (scPrincipal != null) {
            if (scPrincipal instanceof Principal) {
                userPrincipal = (Principal)scPrincipal;
            } else {
                userPrincipal = new ObjectPrincipal(scPrincipal);
            }
        } else {
            userPrincipal = super.getUserPrincipal();
        }

        return (Principal)userPrincipal;
    }

    public String getRequestedSessionId() {
        String requestedSessionId = null;
        if (this.isHttpSessions()) {
            requestedSessionId = super.getRequestedSessionId();
        } else {
            Object sessionId = this.getAttribute(REFERENCED_SESSION_ID);
            if (sessionId != null) {
                requestedSessionId = sessionId.toString();
            }
        }

        return requestedSessionId;
    }

    public HttpSession getSession(boolean create) {
        HttpSession httpSession;
        if (this.isHttpSessions()) {
            httpSession = super.getSession(false);
            if (httpSession == null && create) {
                if (!WebUtils.isSessionCreationEnabled(this)) {
                    throw this.newNoSessionCreationException();
                }

                httpSession = super.getSession(create);
            }
        } else {
            Subject subject;
            try {
                subject = this.getSubject();
            } catch (Exception e) {
                HttpServletRequest httpRequest = (HttpServletRequest)getRequest();
                String currentToken = TokenUtils.getRequestToken(httpRequest);
                if (V.notEmpty(currentToken)) {
                    String cachedUserInfo = TokenUtils.getCachedUserInfoStr(currentToken);
                    if (cachedUserInfo != null) {
                        SecurityManager securityManager = ThreadContext.getSecurityManager();
                        if (securityManager == null) {
                            SecurityUtils.setSecurityManager(ContextHolder.getBean(SecurityManager.class));
                        }
                        IamAuthToken authToken = new IamAuthToken(cachedUserInfo);
                        authToken.setAuthtoken(currentToken);
                        authToken.setValidPassword(false);
                        IamSecurityUtils.getSubject().login(authToken);
                    }
                } else {
                    throw new UnavailableSecurityManagerException(e.getMessage());
                }
                subject = this.getSubject();
            }

            boolean existing = subject.getSession(false) != null;
            if (this.session == null || !existing) {
                Session shiroSession = this.getSubject().getSession(create);
                if (shiroSession != null) {
                    this.session = new ShiroHttpSession(shiroSession, this, this.servletContext);
                } else if (this.session != null) {
                    this.session = null;
                }

                if (shiroSession != null && !existing) {
                    this.setAttribute(REFERENCED_SESSION_IS_NEW, Boolean.TRUE);
                }
            }

            httpSession = this.session;
        }

        return httpSession;
    }

    private DisabledSessionException newNoSessionCreationException() {
        String msg = "Session creation has been disabled for the current request.  This exception indicates that there is either a programming error (using a session when it should never be used) or that Shiro's configuration needs to be adjusted to allow Sessions to be created for the current request.  See the " + DisabledSessionException.class.getName() + " JavaDoc for more.";
        return new DisabledSessionException(msg);
    }

    public HttpSession getSession() {
        return this.getSession(true);
    }

    public boolean isRequestedSessionIdValid() {
        if (this.isHttpSessions()) {
            return super.isRequestedSessionIdValid();
        } else {
            Boolean value = (Boolean)this.getAttribute(REFERENCED_SESSION_ID_IS_VALID);
            return value != null && value.equals(Boolean.TRUE);
        }
    }

    public boolean isRequestedSessionIdFromCookie() {
        if (this.isHttpSessions()) {
            return super.isRequestedSessionIdFromCookie();
        } else {
            String value = (String)this.getAttribute(REFERENCED_SESSION_ID_SOURCE);
            return value != null && value.equals("cookie");
        }
    }

    public boolean isRequestedSessionIdFromURL() {
        if (this.isHttpSessions()) {
            return super.isRequestedSessionIdFromURL();
        } else {
            String value = (String)this.getAttribute(REFERENCED_SESSION_ID_SOURCE);
            return value != null && value.equals("url");
        }
    }

    /** @deprecated */
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return this.isRequestedSessionIdFromURL();
    }

    private class ObjectPrincipal implements Principal {
        private Object object;

        ObjectPrincipal(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return this.object;
        }

        public String getName() {
            return this.getObject().toString();
        }

        public int hashCode() {
            return this.object.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof ObjectPrincipal) {
                ObjectPrincipal op = (ObjectPrincipal)o;
                return this.getObject().equals(op.getObject());
            } else {
                return false;
            }
        }

        public String toString() {
            return this.object.toString();
        }
    }
}
