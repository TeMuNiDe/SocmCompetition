package com.vitech.socmcompetition;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements Serializable {

    private static final long serialVersionUID = 1705927040799295880L;

    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "(\\\\.)|[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"(\\\\\"|[^\"])*\")";
    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")";

    private static final String EMAIL_REGEX = "^\\s*?(.+)@(.+?)\\s*$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    private final boolean allowLocal;
    private final boolean allowTld;

    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator(false, false);

    private static final EmailValidator EMAIL_VALIDATOR_WITH_TLD = new EmailValidator(false, true);

    private static final EmailValidator EMAIL_VALIDATOR_WITH_LOCAL = new EmailValidator(true, false);


    /**
     * Singleton instance of this class, which does
     *  consider local addresses valid.
     */
    private static final EmailValidator EMAIL_VALIDATOR_WITH_LOCAL_WITH_TLD = new EmailValidator(true, true);


    public static EmailValidator getInstance() {
        return EMAIL_VALIDATOR;
    }


    public static EmailValidator getInstance(boolean allowLocal, boolean allowTld) {
        if(allowLocal) {
            if (allowTld) {
                return EMAIL_VALIDATOR_WITH_LOCAL_WITH_TLD;
            } else {
                return EMAIL_VALIDATOR_WITH_LOCAL;
            }
        } else {
            if (allowTld) {
                return EMAIL_VALIDATOR_WITH_TLD;
            } else {
                return EMAIL_VALIDATOR;
            }
        }
    }


    public static EmailValidator getInstance(boolean allowLocal) {
        return getInstance(allowLocal, false);
    }


    protected EmailValidator(boolean allowLocal, boolean allowTld) {
        super();
        this.allowLocal = allowLocal;
        this.allowTld = allowTld;
    }


    protected EmailValidator(boolean allowLocal) {
        super();
        this.allowLocal = allowLocal;
        this.allowTld = false;
    }
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }

        if (email.endsWith(".")) { // check this first - it's cheap!
            return false;
        }

        // Check the whole email address structure
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }
        return true;
    }


}




















