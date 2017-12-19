/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.multiutil.exception;

/**
 *
 * @author huongnt
 */
public class ApplicationException extends Exception {

    private int errorCode;

    public ApplicationException(int message) {
        super(String.valueOf(message));
        this.errorCode = message;
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

}
