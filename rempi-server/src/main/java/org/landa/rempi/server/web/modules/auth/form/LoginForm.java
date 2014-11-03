package org.landa.rempi.server.web.modules.auth.form;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class LoginForm {

    @NotNull
    @Size(min = 2, max = 10, message = "Username kötelező")
    private String username;

    @NotNull
    @Size(min = 2, max = 10, message = "Pass req")
    private String password;

    @Valid
    private Details details = new Details();

    private boolean rememberMe = true;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * @return the rememberMe
     */
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     * @param rememberMe
     *            the rememberMe to set
     */
    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(final Details details) {
        this.details = details;
    }

    public class Details {
        @NotNull
        @Size(min = 1, max = 255, message = "Message req")
        private String welcomeMessage;

        public String getWelcomeMessage() {
            return welcomeMessage;
        }

        public void setWelcomeMessage(final String welcomeMessage) {
            this.welcomeMessage = welcomeMessage;
        }

    }

    public UsernamePasswordToken asToken() {
        final UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        return token;
    }

}
